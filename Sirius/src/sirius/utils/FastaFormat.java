package sirius.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FastaFormat{
	private String header;
	private String sequence;
	private Integer indexLocation = null;	

	public FastaFormat(FastaFormat f){
		this(f.getHeader(), f.getSequence(), f.getIndexLocation());
	}
	
	public FastaFormat(String header, String sequence, Integer indexLocation){
		this.header = header;
		this.sequence = sequence.trim().toUpperCase();
		this.indexLocation = indexLocation;
	}
	
	public FastaFormat(String header,String sequence){		
		this(header, sequence, null);
	}
	
	public void setSequence(String sequence){
		this.sequence = sequence;
	}

	public Object get(int col){
		if(col == 1)
			return header;
		else if(col == 2)
			return sequence.length();
		else //col == 3
			return getIndexLocation();
	}

	public String getHeader(){		
		return header;
	}
	
	public String peekSequence(){
		return this.peekSequence(80);
	}
	
	public String peekSequence(int firstX){
		if(firstX >= this.sequence.length()){
			return this.sequence;
		}else{
			return this.sequence.substring(0,firstX) + "..."; 
		}
	}

	public String getSequence(){
		return sequence;
	}

	public int getSequenceLength(){
		return sequence.length();
	}

	public int getUpstreamSequenceLength(){
		//only if index location is not -1
		if(getIndexLocation() == -1)
			return -1;
		else{
			return getIndexLocation();//because if +1 location is at index 10 then there are upstream length would be 10
		}
	}

	public int getDownstreamSequenceLength(){
		//only if index location is not -1
		if(getIndexLocation() == -1)
			return -1;
		else{
			//because if total length is 20 and +1 location is at index 10 then the downstream length would be 10
			return getSequenceLength() - getIndexLocation();
		}
	}

	public int getStartIndex(int windowFrom, boolean isPercentage){
		int targetLocationIndex = this.getIndexLocation();		
		if(isPercentage){
			windowFrom = (int)(windowFrom * sequence.length() / 100.0);
			if(windowFrom != 0)
				windowFrom -= 1;
		}
		if(targetLocationIndex == -1)
			return windowFrom;    	
		//non -1
		else{
			int startIndex;
			if(windowFrom < 0)//-ve				
				startIndex = targetLocationIndex + windowFrom;				
			else//+ve
				startIndex = targetLocationIndex + windowFrom - 1;

			if(startIndex < 0)				
				startIndex = 0;

			return startIndex;
		}    			
	}

	public int getEndIndex(int windowTo, boolean isPercentage){		
		int targetLocationIndex = this.getIndexLocation();		
		if(isPercentage){
			windowTo = (int)(windowTo * sequence.length() / 100.0);
			if(windowTo != 0)
				windowTo -= 1;
		}
		if(targetLocationIndex == -1){    		
			return windowTo;
		}
		//non -1
		else{
			int endIndex;
			if(windowTo < 0)//-ve
				endIndex = targetLocationIndex + windowTo;
			else//+ve
				endIndex = targetLocationIndex + windowTo - 1;

			if(endIndex + 1 > sequence.length())
				endIndex = sequence.length() - 1;
			return endIndex;
		}		
	}

	public void setIndexLocation(int indexLocation){
		int index1 = header.indexOf("+1_Index(");
		//if exists, just replace
		if(index1 != -1){
			int index2 = header.indexOf(")",index1);
			header = header.substring(0,index1) + " +1_Index(" + indexLocation + ")" + header.substring(index2+1);
		}			
		//add +1_Index to end of header
		else
			header += " +1_Index(" + indexLocation + ")";
		this.indexLocation = indexLocation;
	}
	
	public int getIndexLocation(){
		if(this.indexLocation != null) return this.indexLocation;
		int index1 = header.indexOf("+1_Index(");
		if(index1 != -1){			
			int index2 = header.indexOf(")",index1);
			int indexLocation = Integer.parseInt(header.substring(index1 + "+1_Index(".length(),index2));
			this.indexLocation = indexLocation;
			return indexLocation;
		}else{
			//auto set it to be -1
			header += " +1_Index(-1)";
			this.indexLocation = -1;
			return -1;
		}				
	}
	
	public int[] getPredictPositionForClassifierOne(int leftMostPosition, int rightMostPosition) throws Exception{
		if(leftMostPosition == 0 || rightMostPosition == 0)
			throw new Exception("LeftMostPosition OR RightMostPosition is 0!");
		int[] predictPosition = new int[2];		
		int index = getIndexLocation();
		int length = getSequence().length();
		if(leftMostPosition > 0){
			predictPosition[0] = -index;
			predictPosition[1] = (length - 1) - (rightMostPosition - 1) - (index - 1);
		}else{ //leftMostPosition < 0		
			if(index > -leftMostPosition)
				predictPosition[0] = - index - leftMostPosition;
			else
				predictPosition[0] = +1;

			if(rightMostPosition < 0)				
				predictPosition[1] = (length - 1) - (index - 1);
			else//rightMostPosition > 0								
				predictPosition[1] = (length - 1) - (rightMostPosition - 1) - (index - 1);			
		}
		return predictPosition;				
	}	
	
	public int getArraySize(int leftMostPosition, int rightMostPosition) throws Exception{
		//Try to understand this function more - EXCEPTION OCCURED
		int arraySize = -1;
		int predictPosition[] = getPredictPositionForClassifierOne(leftMostPosition,rightMostPosition);
		if(predictPosition[0] < 0 && predictPosition[1] > 0)
			arraySize = (predictPosition[0] * -1) + predictPosition[1];
		else if(predictPosition[0] > 0 && predictPosition[1] > 0)
			arraySize = predictPosition[1] - predictPosition[0] + 1;
		else if(predictPosition[0] < 0 && predictPosition[1] < 0)
			arraySize = (predictPosition[0] - predictPosition[1] - 1) * -1;
		return arraySize;
	}
	
	public int getPredictionFromForClassifierTwo(int leftMostPosition, int rightMostPosition, int setUpstream) 
	throws Exception{
		int predictPosition[] = getPredictPositionForClassifierOne(leftMostPosition,rightMostPosition);
		if(setUpstream == 0)
			throw new Exception("SetUpstream should not be 0");
		else if(setUpstream > 0)
			return predictPosition[0];
		else//setUpstream < 0
			return predictPosition[0] - setUpstream;
	}

	public static void undersample(List<FastaFormat> aList, List<FastaFormat> bList, double foldSize,
			int randNumber){
		List<FastaFormat> moreList;
		List<FastaFormat> lesserList;
		if(aList.size() == bList.size()) return;//No need to do anything
		else if(aList.size() < bList.size()){
			lesserList = aList;
			moreList = bList;
		}else{
			lesserList = bList;
			moreList = aList;
		}
		Random rand = new Random(randNumber);
		while(moreList.size() > (lesserList.size() * 2)){
			int index = rand.nextInt(moreList.size());
			moreList.remove(index);
		}
	}
	
	public static void oversample(List<FastaFormat> aList, List<FastaFormat> bList, boolean evenly, 
			int fold){
		List<FastaFormat> moreList;
		List<FastaFormat> lesserList;
		if(aList.size() == bList.size()) return;//No need to do anything
		else if(aList.size() < bList.size()){
			lesserList = aList;
			moreList = bList;
		}else{
			lesserList = bList;
			moreList = aList;
		}

		List<FastaFormat> tempList = new ArrayList<FastaFormat>();
		if(evenly){
			int times = moreList.size() * fold / lesserList.size();					
			for(int i = 0; i < times - 1; i++){
				for(int x = 0; x < lesserList.size(); x++){
					tempList.add(new FastaFormat(lesserList.get(x).getHeader(),
							lesserList.get(x).getSequence()));
				}
			}
		}else{
			while((lesserList.size() + tempList.size()) * fold < moreList.size()){			
				for(int x = 0; x < lesserList.size(); x++){
					tempList.add(new FastaFormat(lesserList.get(x).getHeader(), 
							lesserList.get(x).getSequence()));
					if(tempList.size() + lesserList.size() == moreList.size())
						break;
				}
			}
		}
		lesserList.addAll(tempList);
	}
}