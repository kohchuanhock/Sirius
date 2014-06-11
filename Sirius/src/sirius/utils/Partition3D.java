package sirius.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partition3D extends Partition{
	private List<Double> zOriginalList = new ArrayList<Double>();	
	private List<Double> zSortedList = new ArrayList<Double>();	
	public double zStart;
	public double zEnd;
	
	public Partition3D(double xS, double xE, double yS, double yE, double zS, double zE){
		this.xStart = xS;
		this.xEnd = xE;
		this.yStart = yS;
		this.yEnd = yE;
		this.zStart = zS;
		this.zEnd = zE;
	}
	
	public void add(double x, double y, double z){
		this.xOriginalList.add(x);
		this.yOriginalList.add(y);
		this.zOriginalList.add(z);
		this.xSortedList.add(x);
		this.ySortedList.add(y);
		this.zSortedList.add(z);
	}	
	
	public void sort(){
		Collections.sort(xSortedList);
		Collections.sort(ySortedList);
		Collections.sort(zSortedList);
	}
		
	public double getZStart(){return this.zStart;}
	public double getZEnd(){return this.zEnd;}
	public List<Double> getZSortedList(){return this.zSortedList;}
	public List<Double> getZOriginalList(){return this.zOriginalList;}
	
	public boolean isIdentical(){
		boolean identical = true;		
		for(int i = 0; i < xOriginalList.size() - 1; i++){
			double currentX = xOriginalList.get(i);
			double nextX = xOriginalList.get(i + 1);
			double currentY = yOriginalList.get(i);
			double nextY = yOriginalList.get(i + 1);
			double currentZ = zOriginalList.get(i);
			double nextZ = zOriginalList.get(i + 1);
			if(currentX != nextX || currentY != nextY || currentZ != nextZ){
				identical = false;
				break;
			}
		}
		return identical;
	}
	
	public String toString(){
		StringBuffer s = new StringBuffer();
		for(int i = 0; i < this.xOriginalList.size(); i++){
			s.append(this.xOriginalList.get(i) + ", ");
			s.append(this.yOriginalList.get(i) + ",");
			s.append(this.zOriginalList.get(i) + "\t");
		}
		return s.toString();
	}
	
	public List<Partition3D> partition(double xPartitionValue, double yPartitionValue, double zPartitionValue,
			double epsilon){		
		List<Partition3D> pList = new ArrayList<Partition3D>();
		pList.add(new Partition3D(xPartitionValue + epsilon, this.xEnd, yPartitionValue + epsilon, this.yEnd, 
				zPartitionValue + epsilon, this.zEnd));
		pList.add(new Partition3D(xPartitionValue + epsilon, this.xEnd, yPartitionValue + epsilon, this.yEnd,
				this.zStart, zPartitionValue));
		pList.add(new Partition3D(xPartitionValue + epsilon, this.xEnd, this.yStart, yPartitionValue, 
				zPartitionValue + epsilon, this.zEnd));		
		pList.add(new Partition3D(xPartitionValue + epsilon, this.xEnd, this.yStart, yPartitionValue,
				this.zStart, zPartitionValue));
		pList.add(new Partition3D(this.xStart, xPartitionValue, yPartitionValue + epsilon, this.yEnd, 
				zPartitionValue + epsilon, this.zEnd));
		pList.add(new Partition3D(this.xStart, xPartitionValue, yPartitionValue + epsilon, this.yEnd,
				this.zStart, zPartitionValue));
		pList.add(new Partition3D(this.xStart, xPartitionValue, this.yStart, yPartitionValue, 
				zPartitionValue + epsilon, this.zEnd));		
		pList.add(new Partition3D(this.xStart, xPartitionValue, this.yStart, yPartitionValue,
				this.zStart, zPartitionValue));
		/*
		 * Sort the points into their respective Partition
		 */
		for(int i = 0; i < xOriginalList.size(); i++){
			double x = xOriginalList.get(i);
			double y = yOriginalList.get(i);
			double z = zOriginalList.get(i);
			if(x >= xPartitionValue + epsilon){
				if(y >= yPartitionValue + epsilon){
					if(z >= zPartitionValue + epsilon){
						//x >, y >, z >
						pList.get(0).add(x, y, z);
					}else{
						//x >, y >, z <
						pList.get(1).add(x, y, z);
					}					
				}else{
					if(z >= zPartitionValue + epsilon){
						//x >, y <, z >
						pList.get(2).add(x, y, z);
					}else{
						//x >, y <, z <
						pList.get(3).add(x, y, z);
					}		
				}
			}else{
				if(y >= yPartitionValue + epsilon){
					if(z >= zPartitionValue + epsilon){
						//x <, y >, z >
						pList.get(4).add(x, y, z);
					}else{
						//x <, y >, z <
						pList.get(5).add(x, y, z);
					}		
				}else{
					if(z >= zPartitionValue + epsilon){
						//x <, y <, z >
						pList.get(6).add(x, y, z);
					}else{
						//x <, y <, z <
						pList.get(7).add(x, y, z);
					}		
				}
			}
		}
		return pList;
	}
	
	public List<Partition3D> partition(){
		/*
		 * Find mid-point such that divide into half
		 */
		this.sort();
		int centerIndex1 = (xSortedList.size() / 2) - 1;
		int centerIndex2 = xSortedList.size() / 2;		
		double xPartitionValue = (xSortedList.get(centerIndex1) + xSortedList.get(centerIndex2)) / 2.0;
		double yPartitionValue = (ySortedList.get(centerIndex1) + ySortedList.get(centerIndex2)) / 2.0;
		double zPartitionValue = (zSortedList.get(centerIndex1) + zSortedList.get(centerIndex2)) / 2.0;
		double epsilon = 1.0E-15;		
		/*
		 * Create four new partitions
		 */
		List<Partition3D> pList = partition(xPartitionValue, yPartitionValue, zPartitionValue, epsilon);
		if(isInvalidPartition(pList)){			
			pList = partition(xPartitionValue, yPartitionValue, zPartitionValue, epsilon * -1);			
		}
		return pList;
	}
	
	protected boolean isInvalidPartition(List<? extends Partition> pList){
		/*
		 * 
		 * Does after partition cause 3 partitions to be 0?
		 */		
		int countZero = 0;
		for(Partition p:pList) if(p.numOfPoints() == 0) countZero++;
		//Invalid if 7 or more have 0 points
		return (countZero >= 7) ? true:false;
	}

	public boolean isUniform(List<? extends Partition> pList){
		/*
		 * Test the partition for uniform distribution
		 */
		double expectedN = xOriginalList.size() / 8.0;
		double chiSquared = 0.0;
		for(Partition p:pList) chiSquared += Math.pow(expectedN - p.numOfPoints(), 2.0) / expectedN; 			 
		//14.067 is for 95% and df of 7
		return (14.067 >= chiSquared) ? true:false; 		
	}	
}
