package sirius.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

public class Table{
	//Stores the class that each sample belongs to - should be posClass or negClass or thirdClass
	private List<String> classHeaderList = null;
	//Stores the indexes of posClass
	private Set<Integer> posClassIndex = new HashSet<Integer>();
	//Stores the indexes of negClass
	private Set<Integer> negClassIndex = new HashSet<Integer>();
	//Stores the indexes of thirdClass - Currently only used by DECORATE
	private Set<Integer> thirdClassIndex = new HashSet<Integer>();
	//Stores the header of the data - the name of each sample
	private List<String> headerList = new ArrayList<String>();
	//Stores the row of the data - the name of each probe
	private List<String> rowNameList = new ArrayList<String>();
	//Stores the data - Each row is the probe and each col is the sample
	private DoubleMatrix2D dataMatrix;	
	//Stores the String that identifies the Positive instances
	private String posClass = null;
	//Stores the String that identifies the Negative instances
	private String negClass = null;
	//Stores the String that identifies the ThirdClass instances
	private String thirdClass = null;
	//Stores the fileName where the table is created
	private String fileName = null;
	//Stores the fileLocation that generates the table
	private String fileLocation = null;
	/*
	 * Converts the probe list into entrezID list and takes the maximum expression for a gene (follows GSEA collapse)
	 * Those probes with empty entrezID will simply remains as probeID
	 */
	public Table convertToEntrezID(Map<String, Set<String>> probeID2EntrezIDSet, boolean collapse, ConversionInformation cinfo) throws IOException{
		return convertToEntrezID(probeID2EntrezIDSet, collapse, false, cinfo);
	}

	public Table convertToEntrezID(Map<String, Set<String>> probeID2EntrezIDSet, boolean collapse, boolean debug, ConversionInformation cinfo) throws IOException{
		/*
		 * Converts the probeIDList into entrezIDList
		 */
		if(collapse){
			return conversionWithCollapse(probeID2EntrezIDSet, debug, cinfo);
		}else{
			return conversionWithoutCollapse(probeID2EntrezIDSet, debug, cinfo);
		}
	}

	private Table conversionWithoutCollapse(Map<String, Set<String>> probeID2MembershipIDSet, boolean debug, ConversionInformation cinfo) throws IOException{
		Map<Integer, Integer> newIndex2OldIndexSet = cinfo.getNewIndex2OldIndex();
		List<String> membershipIDList = cinfo.getMembershipIDList();//Has repeats
		/*
		 * Populate the datamatrix according to the new entrezIDList by picking the max expression to represent
		 */
		DoubleMatrix2D newDataMatrix = new DenseDoubleMatrix2D(membershipIDList.size(), this.headerList.size());
		for(int r = 0; r < newDataMatrix.rows(); r++){
			for(int c = 0; c < newDataMatrix.columns(); c++){
				int i = newIndex2OldIndexSet.get(r);
				double v = this.dataMatrix.get(i, c);
				newDataMatrix.setQuick(r, c, v);
			}
		}
		return new Table(this.classHeaderList, this.posClassIndex, this.negClassIndex, this.headerList, membershipIDList, newDataMatrix, this.posClass, this.negClass,
				this.fileName, this.fileLocation);
	}

	private Table conversionWithCollapse(Map<String, Set<String>> probeID2MembershipIDSet, boolean debug, ConversionInformation cinfo) throws IOException{
		/*
		 * Populate the datamatrix according to the new entrezIDList by picking the max expression to represent
		 */
		List<String> membershipIDList = cinfo.getMembershipIDList();
		Map<Integer, Set<Integer>> newIndex2OldIndexSet = cinfo.getNewIndex2OldIndexSet();
		DoubleMatrix2D newDataMatrix = new DenseDoubleMatrix2D(membershipIDList.size(), this.headerList.size());
		for(int r = 0; r < newDataMatrix.rows(); r++){
			for(int c = 0; c < newDataMatrix.columns(); c++){
				double value = Double.MIN_VALUE;
				for(int i:newIndex2OldIndexSet.get(r)){
					double v = this.dataMatrix.get(i, c);
					if(v > value) value = v;
				}
				newDataMatrix.setQuick(r, c, value);
			}
		}
		return new Table(this.classHeaderList, this.posClassIndex, this.negClassIndex, this.headerList, membershipIDList, newDataMatrix, this.posClass, this.negClass,
				this.fileName, this.fileLocation);
	}

	public Table(List<String> classHeaderList, Set<Integer> posClassIndex, Set<Integer> negClassIndex, List<String> headerList, List<String> rowNameList,
			DoubleMatrix2D dataMatrix, String posClass, String negClass, String fileName, String fileLocation){
		this.classHeaderList = classHeaderList;
		this.posClassIndex = posClassIndex;
		this.negClassIndex = negClassIndex;
		this.headerList = headerList;
		this.rowNameList = rowNameList;
		this.dataMatrix = dataMatrix;
		this.posClass = posClass;
		this.negClass = negClass;
		this.fileName = fileName;
		this.fileLocation = fileLocation;
	}

	public File getGSEADataset() throws IOException{
		//First line = #1.2
		//Second line = (# of data rows) (tab) (# of data columns)
		//Third line = Name(tab)Description(tab)(sample 1 name)(tab)(sample 2 name) (tab) ... (sample N name)
		//Remainder = (gene name) (tab) (gene description) (tab) (col 1 data) (tab) (col 2 data) (tab) ... (col N data)
		File f = File.createTempFile("Table_Generated", ".gct");
		f.deleteOnExit();
		BufferedWriter output = new BufferedWriter(new FileWriter(f));
		output.write("#1.2"); output.newLine();
		output.write(this.getProbeSize() + "\t" + this.getSampleSize()); output.newLine();
		output.write("Name\tDescription");
		for(String s:this.getHeaderList()){
			output.write("\t" + s);
		}
		output.newLine();
		for(int r = 0; r < this.getProbeSize(); r++){
			output.write(this.getRowName(r) + "\t" + " ");
			for(int c = 0; c < this.getSampleSize(); c++){
				output.write("\t" + this.get(r, c));
			}
			output.newLine();
		}
		output.close();
		return f;
	}

	public File getGSEATemplate() throws IOException{
		//First line = (number of samples) (space) (number of classes) (space) 1
		//Second line = # (space) (class 0 name) (space) (class 1 name)
		//Third line = (sample 1 class) (space) (sample 2 class) (space) ... (sample N class)
		File f = File.createTempFile("Table_Generated", ".cls");
		f.deleteOnExit();
		BufferedWriter output = new BufferedWriter(new FileWriter(f));
		output.write(this.getSampleSize() + " 2 1"); output.newLine();
		output.write("# Pos Neg"); output.newLine();
		for(int i = 0; i < this.getSampleSize(); i++){
			if(this.posClassIndex.contains(i) && this.negClassIndex.contains(i)) {
				output.close();
				throw new Error("Belongs to both pos and neg class");
			}
			else if(this.posClassIndex.contains(i)) output.write("0 ");
			else if(this.negClassIndex.contains(i)) output.write("1 ");
			else {
				output.close();
				throw new Error("Do not belong to either pos or neg class");
			}
		}
		output.newLine();
		output.close();
		return f;
	}

	/*
	 * Used for Ranking Algorithm in Polaris
	 */
	//Stores the bin of genes - Each row is the probe and each cold is the sample
	private DoubleMatrix2D binMatrix = null;
	//Stores the rank of genes 
	private DoubleMatrix2D rankMatrix = null;
	private int numberOfBin = -1;

	public int getNumberOfBin(){
		//If Rank and Bin Matrix are not built, build them
		if(this.numberOfBin == -1){
			this.buildRankAndBinMatrix(-1);
		}
		return this.numberOfBin;
	}
	public int rows(){return this.dataMatrix.rows();}//Number of Genes
	public int columns(){return this.dataMatrix.columns();}//Number of Samples
	public String getFilename(){return this.fileName;}
	public String getPosClass(){return this.posClass;}//Pos class String
	public String getNegClass(){return this.negClass;}//Neg class String

	/*
	 * Build the rank matrix
	 * 1) Sort by value
	 * 2) Discretize into bins of size square root to the size of genes
	 */
	private void buildRankAndBinMatrix(int numberOfBin){
		this.rankMatrix = new DenseDoubleMatrix2D(this.dataMatrix.rows(), this.dataMatrix.columns());
		this.binMatrix = new DenseDoubleMatrix2D(this.dataMatrix.rows(), this.dataMatrix.columns());
		/*
		 * Find the ranks of genes
		 */
		for(int c = 0; c < this.dataMatrix.columns(); c++){
			DoubleMatrix1D dM = this.dataMatrix.viewColumn(c);
			List<IndexAndValue> iList = new ArrayList<IndexAndValue>();
			for(int i = 0; i < dM.size(); i++){
				iList.add(new IndexAndValue(i, dM.get(i)));
			}
			Collections.sort(iList, new SortByValue());
			for(int rank = 0; rank < iList.size(); rank++){
				this.rankMatrix.set(iList.get(rank).getIndex(), c, rank);
			}
		}
		/*
		 * Discretize the ranks - i.e. instead of splitting them into ranks of number of genes, make it to be square root of number of genes
		 */
		if(numberOfBin == -1)
			this.numberOfBin = (int) Math.sqrt(this.binMatrix.columns());
		else
			this.numberOfBin = numberOfBin;
		int sizeOfBin = (this.rankMatrix.rows() / this.numberOfBin) + 1;
		for(int r = 0; r < this.rankMatrix.rows(); r++){
			for(int c = 0; c < this.rankMatrix.columns(); c++){
				int rank = (int) this.rankMatrix.get(r, c);
				int bin = rank / sizeOfBin;
				this.binMatrix.set(r, c, bin);
			}
		}
	}

	/*
	 * Returns the bin of the genes for the positive or negative samples
	 */
	public int[] getBinMatrix(int geneRow, boolean isPos){
		if(this.numberOfBin == -1) this.buildRankAndBinMatrix(-1);
		Set<Integer> classIndex;
		if(isPos) classIndex = this.posClassIndex;
		else classIndex = this.negClassIndex;
		int[] d = new int[classIndex.size()];
		int i = 0;
		for(int index:classIndex){
			d[i++] = (int) this.binMatrix.get(geneRow, index);
		}
		return d;
	}

	/*
	 * Returns the rank of the genes for the positive or negative samples
	 */
	public int[] getRankMatrix(int geneRow, boolean isPos){
		if(this.numberOfBin == -1) this.buildRankAndBinMatrix(-1);
		Set<Integer> classIndex;
		if(isPos) classIndex = this.posClassIndex;
		else classIndex = this.negClassIndex;
		int[] d = new int[classIndex.size()];
		int i = 0;
		for(int index:classIndex){
			d[i++] = (int) this.rankMatrix.get(geneRow, index);
		}
		return d;
	}

	public double[] getRankMatrix(int geneRow){
		if(this.numberOfBin == -1) this.buildRankAndBinMatrix(-1);
		return this.rankMatrix.viewRow(geneRow).toArray();
	}

	public DoubleMatrix2D getBinMatrix(){
		if(this.numberOfBin == -1) this.buildRankAndBinMatrix(-1);
		return this.binMatrix;
	}

	public DoubleMatrix2D getRankMatrix(){
		if(this.numberOfBin == -1) this.buildRankAndBinMatrix(-1);
		return this.rankMatrix;
	}

	//Assumes number of columns imply sample size
	public int getSampleSize(){
		return this.headerList.size();		
	}

	public Set<Integer> getPosSampleSet(){return this.posClassIndex;}
	public Set<Integer> getNegSampleSet(){return this.negClassIndex;}
	public int getClassValue(int sampleIndex){
		if(this.posClassIndex.contains(sampleIndex)) return 0;
		else if(this.negClassIndex.contains(sampleIndex)) return 1;
		else throw new Error("Unknown Class");
	}
	public int getPosSampleSize(){return this.posClassIndex.size();}	
	public int getNegSampleSize(){return this.negClassIndex.size();}
	public int getThirdClassSampleSize(){return this.thirdClassIndex.size();}
	//Assumes number of rows imply genes
	public int getProbeSize(){return this.rowNameList.size();}
	public List<String> getHeaderList(){return this.headerList;}	
	public String getHeader(int index){return this.headerList.get(index);}
	public List<String> getRowName(){return this.rowNameList;}
	public String getRowName(int r){return this.rowNameList.get(r);}
	public void adjustRowName(int r, int noOfDuplicate){this.rowNameList.set(r, this.rowNameList.get(r) + "_" + noOfDuplicate);}
	public List<String> getClassHeader(){return this.classHeaderList;}
	public DoubleMatrix2D getData(){return this.dataMatrix;}
	public String getFileLocation(){return this.fileLocation;}
	public double get(int row, int col){
		return this.dataMatrix.get(row, col);
	}

	public double[] getRowArray(int row, boolean isPos, boolean useRank, boolean useBin){
		if(useRank && useBin) throw new Error("Both useRank and useBin cannot be true at the same time.");
		Set<Integer> iSet;
		if(isPos) iSet = this.getPosSampleSet();
		else iSet = this.getNegSampleSet();

		double[] dList = new double[iSet.size()];
		int count = 0;
		for(int col = 0; col < this.getSampleSize(); col++){
			if(iSet.contains(col)){
				if(useBin){
					dList[count++] = this.getBinMatrix().get(row, col);
				}else if(useRank){
					dList[count++] = this.getRankMatrix().get(row, col);
				}else{
					dList[count++] = this.get(row, col);
				}
			}
		}
		return dList;
	}

	public DoubleMatrix1D getDataRow(int row){
		return this.dataMatrix.viewRow(row);
	}

	public DoubleMatrix1D getDataCol(int col){		
		return this.dataMatrix.viewColumn(col);
	}

	public Instance getDataColAsInstance(int col, Set<Integer> selectedGeneSet){
		return this.getDataColAsInstance(col, selectedGeneSet, false);
	}

	public Instance getDataColAsInstance(int col, Set<Integer> selectedGeneSet, boolean useRankMatrix){
		//It should not matter whether posClass or negClass is used since it is not used
		return new Table(this.dataMatrix.viewColumn(col), this, this.negClass, useRankMatrix).getAsInstances(selectedGeneSet, useRankMatrix).instance(0);
	}

	public void show(){
		System.out.println("Pos Size: " + this.getPosSampleSize());
		System.out.println("Neg Size: " + this.getNegSampleSize());
		System.out.println("Probe Size: " + this.getProbeSize());
		System.out.println("Sample Size: " + this.getSampleSize());
		for(String h:this.headerList) System.out.print(h+"\t");
		System.out.println();
		System.out.println();
	}

	public void init(Table data){
		this.posClass = data.posClass;
		this.negClass = data.negClass;
		this.split();
	}


	public Table(Table data){
		//deep copy
		this.dataMatrix = (DenseDoubleMatrix2D) data.dataMatrix.copy();

		for(int x = 0; x < data.rowNameList.size(); x++){
			this.rowNameList.add(new String(data.rowNameList.get(x)));			
		}
		for(int y = 0; y < data.headerList.size(); y++){
			this.headerList.add(new String(data.headerList.get(y)));
		}
		this.init(data);
	}

	public Table(DoubleMatrix1D geneValue, Table data, String header){
		this(geneValue, data, header, false);
	}

	/*
	 * Create a Table with single column with all probes
	 */
	public Table(DoubleMatrix1D geneValue, Table data, String header, boolean useRankMatrix){
		this.dataMatrix = new DenseDoubleMatrix2D(geneValue.size(), 1);
		for(int x = 0; x < this.dataMatrix.rows(); x++){
			this.dataMatrix.setQuick(x, 0, geneValue.getQuick(x));
		}
		this.rowNameList = data.rowNameList;
		this.headerList.add(header);
		this.init(data);
		if(useRankMatrix) this.buildRankAndBinMatrix((int)Math.sqrt(data.columns()));
	}

	/*
	 * Create a Table with single column with only selected probes
	 */
	public Table(DoubleMatrix1D geneValue, Set<Integer> selectedGeneList, Table data, String header){
		this.dataMatrix = new DenseDoubleMatrix2D(selectedGeneList.size(), 1);
		int count = 0;
		for(int x = 0; x < data.dataMatrix.rows(); x++){
			if(selectedGeneList.contains(x))
				this.dataMatrix.setQuick(count++, 0, geneValue.getQuick(x));
		}
		for(int x = 0; x < data.rowNameList.size(); x++){
			if(selectedGeneList.contains(x))
				this.rowNameList.add(new String(data.rowNameList.get(x)));			
		}

		this.headerList.add(header);
		this.init(data);
	}

	public Table(DoubleMatrix2D geneValueList, List<String> headerList, Table data){
		this.dataMatrix = geneValueList;
		this.rowNameList = data.getRowName();
		this.headerList = headerList;

		this.init(data);
	}

	public Table(List<DoubleMatrix1D> geneValueList, List<String> headerList, Table data){
		this.dataMatrix = new DenseDoubleMatrix2D(geneValueList.get(0).size(), headerList.size());

		for(int row = 0; row < this.dataMatrix.rows(); row++){
			for(int col = 0; col < this.dataMatrix.columns(); col++){
				this.dataMatrix.setQuick(row, col, geneValueList.get(col).getQuick(row));
			}
		}
		this.rowNameList = data.getRowName();

		this.headerList = headerList;

		this.init(data);
	}

	public Table(Table data, int sampleIndex, boolean toRemove){
		if(toRemove){
			/*
			 * Copies all the samples except one
			 */
			this.dataMatrix = new DenseDoubleMatrix2D(data.rows(), data.columns() - 1);
			for(int row = 0; row < data.dataMatrix.rows(); row++){
				int c = 0;
				for(int col = 0; col < data.dataMatrix.columns(); col++){
					if(col != sampleIndex)
						this.dataMatrix.setQuick(row, c++, data.dataMatrix.getQuick(row, col));
				}
			}
		}else{
			/*
			 * Copies only one sample
			 */
			this.dataMatrix = new DenseDoubleMatrix2D(data.rows(), 1);
			for(int row = 0; row < this.dataMatrix.rows(); row++){
				this.dataMatrix.setQuick(row, 0, data.dataMatrix.getQuick(row, sampleIndex));
			}
		}

		for(int x = 0; x < data.rowNameList.size(); x++){
			this.rowNameList.add(new String(data.rowNameList.get(x)));			
		}
		for(int y = 0; y < data.headerList.size(); y++){
			if(toRemove){
				if(y != sampleIndex)
					this.headerList.add(new String(data.headerList.get(y)));
			}else{
				if(y == sampleIndex)
					this.headerList.add(new String(data.headerList.get(y)));
			}

		}
		this.init(data);
	}
	
	/*
	 * Construct a new table with only the selected Genes
	 */
	public Table(Table data, List<Integer> selectedGeneList){
		//deep copy
		this.dataMatrix = new DenseDoubleMatrix2D(selectedGeneList.size(), data.dataMatrix.columns());
		for(int i = 0; i < selectedGeneList.size(); i++){
			DoubleMatrix1D row = data.getDataRow(selectedGeneList.get(i));
			for(int j = 0; j < row.size(); j++){
				this.dataMatrix.set(i, j, row.get(j));
			}
			String rowName = new String(data.rowNameList.get(selectedGeneList.get(i)));
			this.rowNameList.add(rowName);
		}

		for(int y = 0; y < data.headerList.size(); y++){
			this.headerList.add(new String(data.headerList.get(y)));
		}
		this.init(data);
	}
	
	/*
	 * Construct a new Table with only the selected samples
	 */
	public Table(List<Integer> selectedSampleList, Table data){
		//deep copy
		this.dataMatrix = new DenseDoubleMatrix2D(data.dataMatrix.rows(), selectedSampleList.size());
		for(int i = 0; i < selectedSampleList.size(); i++){
			DoubleMatrix1D column = data.getDataCol(selectedSampleList.get(i));
			for(int j = 0; j < column.size(); j++){
				this.dataMatrix.set(j, i, column.get(j));
			}
		}
		this.rowNameList = data.rowNameList;

		for(int y = 0; y < selectedSampleList.size(); y++){
			this.headerList.add(new String(data.headerList.get(selectedSampleList.get(y))));
		}
		this.init(data);
	}

	private Table(List<String> headerList, List<String> rowNameList, DoubleMatrix2D dataMatrix,
			String posClass, String negClass){
		this.headerList = new ArrayList<String>(headerList);
		this.rowNameList = new ArrayList<String>(rowNameList);
		this.dataMatrix = dataMatrix;
		this.posClass = new String(posClass);
		this.negClass = new String(negClass);
		this.split();
	}

	/*
	 * This is used for cross-validation
	 * 
	 * Given a list of Table, combine them into a Table with an index skipped
	 */
	public Table(List<Table> dataList, int skipIndex){
		//deep copy
		this.posClass = dataList.get(0).posClass;
		this.negClass = dataList.get(0).negClass;
		//Populate the rowNameList
		for(int x = 0; x < dataList.get(0).rowNameList.size(); x++){
			this.rowNameList.add(new String(dataList.get(0).rowNameList.get(x)));
		}
		for(int i = 0; i < dataList.size(); i++){
			//Checking to ensure all have the same posClass and negClass
			if(i == skipIndex) continue;
			if(this.posClass.equals(dataList.get(i).posClass) == false ||
					this.negClass.equals(dataList.get(i).negClass) == false){
				throw new Error("Not all Tables have same posClass and negClass");
			}
			//Populate the headerList
			Table table = dataList.get(i);
			for(int y = 0; y < table.headerList.size(); y++){
				this.headerList.add(new String(table.headerList.get(y)));
			}
		}
		//Populate the dataMatrix
		this.dataMatrix = new DenseDoubleMatrix2D(this.rowNameList.size(), this.headerList.size());
		for(int i = 0; i < this.rowNameList.size(); i++){
			int col = 0;
			for(int k = 0; k < dataList.size(); k++){
				if(k == skipIndex) continue;
				Table table = dataList.get(k);
				for(int j = 0; j < table.dataMatrix.columns(); j++, col++){
					double d = table.dataMatrix.get(i, j);
					this.dataMatrix.set(i, col, d);
				}
			}
		}
		this.split();		
	}

	public Table(String fileLocation, boolean hasRowHeader, String delimiter){
		this(fileLocation, hasRowHeader, delimiter, null, null);
	}

	/*
	 * Constructor prepared for MAQC datasets
	 */
	public Table(String fileLocation){
		this(fileLocation, true, ",", "Pos", "Neg");
	}

	public Table(String fileLocation, boolean hasRowHeader, String delimiter, String posClass, String negClass){
		this(fileLocation, hasRowHeader, delimiter, posClass, negClass, ' ', null, null, false);
	}

	public Table(String fileLocation, boolean hasRowHeader, String delimiter, String posClass, String negClass, boolean useRankMatrix){
		this(fileLocation, hasRowHeader, delimiter, posClass, negClass, ' ', null, null, useRankMatrix);
	}

	public Table(String fileLocation, boolean hasRowHeader, String delimiter,
			String posClass, String negClass, char skipChar){
		this(fileLocation, hasRowHeader, delimiter, posClass, negClass, skipChar, null, null, false);
	}

	public Table(String fileLocation, boolean hasRowHeader, String delimiter, String posClass, String negClass, 
			char skipChar, String classHeaderString, String classHeaderDelimiter, boolean useRankMatrix){
		/*
		 * Reading in the input file and populate the values
		 */
		try{
			int index = 0;
			while(fileLocation.indexOf(File.separator, index + 1) != -1){
				index = fileLocation.indexOf(File.separator, index + 1);
			}
			this.fileLocation = fileLocation;
			this.fileName = fileLocation.substring(index+1);
			this.posClass = posClass;
			this.negClass = negClass;
			BufferedReader input = new BufferedReader(new FileReader(fileLocation));
			String line = null;
			String[] s;
			if(skipChar != ' '){
				while(true){
					line = input.readLine();
					if(classHeaderString != null && line.contains(classHeaderString)){
						this.classHeaderList = new ArrayList<String>();						
						StringTokenizer st = new StringTokenizer(line, classHeaderDelimiter);
						st.nextToken();//classHeaderString
						while(st.hasMoreTokens()){
							this.classHeaderList.add(st.nextToken());
						}
					}
					if(line.length() > 0 && line.charAt(0) != skipChar){
						break;
					}
				}
			}			
			if(hasRowHeader){
				if(line == null)
					line = input.readLine();
				s = line.split(delimiter);				
				//Ignore the first item
				for(int x = 1; x < s.length; x++) {
					this.headerList.add(s[x]);
				}
				line = input.readLine();
			}			
			if(line == null) line = input.readLine();
			List<List<Double>> dListList = new ArrayList<List<Double>>();
			while(true){
				if(line == null) break;
				s = line.split(delimiter);			
				this.rowNameList.add(s[0]);
				List<Double> rowData = new ArrayList<Double>();
				for(int x = 1; x < s.length; x++){
					try{						
						rowData.add(Double.parseDouble(s[x]));
					}catch(NumberFormatException e){						
						if(s[x].equalsIgnoreCase(delimiter) == false && s[x].length() > 0){
							rowData.add(Double.NaN);
						}
					}
				}
				dListList.add(rowData);				
				line = input.readLine();
				if(skipChar != ' '){
					while(true){
						if(line == null || (line.length() > 0 && skipChar != line.charAt(0)))
							break;
						else
							line = input.readLine();
					}
				}
			}			
			input.close();
			this.dataMatrix = new DenseDoubleMatrix2D(this.rowNameList.size(), this.headerList.size());
			for(int i = 0; i < dListList.size(); i++){
				for(int j = 0; j < dListList.get(i).size(); j++){
					this.dataMatrix.set(i, j, dListList.get(i).get(j));
				}
			}
			this.split();			
			/*
			 * Probe ID - Adjust ProbeID so that they are unique
			 */
			this.adjustProbeName();
			if(useRankMatrix) this.buildRankAndBinMatrix(-1);
		}catch(Exception e){e.printStackTrace();}
	}	

	private void adjustProbeName(){
		Map<String, Integer> probeIDSet = new HashMap<String, Integer>();
		for(int r = 0; r < this.getProbeSize(); r++){
			String s = this.getRowName(r);
			if(probeIDSet.containsKey(s) == false){
				probeIDSet.put(s, 1);
			}else{
				probeIDSet.put(s, probeIDSet.get(s) + 1);
				this.adjustRowName(r, probeIDSet.get(s));
			}
		}
	}
	
	
	public DoubleMatrix2D generateClassC2DMatrix(int numberOfClassC, Random rand, List<double[]> msdList){
		/*
		 * Generate class C
		 */
		DoubleMatrix2D dataMatrix = new DenseDoubleMatrix2D(this.rows(), numberOfClassC);
		for(int i = 0; i < numberOfClassC; i++){//For each sample
			/*
			 * Let class C be either pos or neg depending on the usage
			 */
			for(int k = 0; k < this.rows(); k++){//For each gene
				double randValue = rand.nextGaussian() * msdList.get(k)[1];
				double value;
				if(rand.nextBoolean()){
					//Add
					value = msdList.get(k)[0] + randValue;
				}else{
					//Subtract
					value = msdList.get(k)[0] - randValue;
				}
				dataMatrix.setQuick(k, i, value);
			}
		}
		return dataMatrix;
	}
	/*
	 * Generate new Table with features based on mean and standard deviation
	 */
	public Table(Table data, Random rand, boolean useRankMatrix, DoubleMatrix2D classCMatrix, boolean keepPosOriginal, boolean useBootstrap){
		this.fileLocation = null;
		this.fileName = null;
		this.rowNameList = data.rowNameList;
		this.posClass = data.posClass;
		this.negClass = data.negClass;
		int index = 0;
		int originalSize;
		if(keepPosOriginal){
			originalSize = data.posClassIndex.size();
		}else{
			originalSize = data.negClassIndex.size();
		}
		this.dataMatrix = new DenseDoubleMatrix2D(data.rows(), originalSize * 2);
		if(useBootstrap == false){
			/*
			 * Copy over the original
			 */
			for(int i = 0; i < data.columns(); i++){//For each sample
				if((data.posClassIndex.contains(i) && keepPosOriginal) || (data.negClassIndex.contains(i) && keepPosOriginal == false)){
					index++;
	//				System.out.println(data.getHeaderList().get(i));
					this.headerList.add(data.getHeaderList().get(i));
					DoubleMatrix1D geneValues = data.getDataCol(i);
					for(int k = 0; k < geneValues.size(); k++){//For each gene
						this.dataMatrix.setQuick(k, index, geneValues.get(k));
					}
				}
			}
		}else{
			/*
			 * Do bootstrap on the original 
			 */
			while(index < originalSize){
				int i = rand.nextInt(data.columns());
				if((data.posClassIndex.contains(i) && keepPosOriginal) || (data.negClassIndex.contains(i) && keepPosOriginal == false)){
					index++;
					this.headerList.add(data.getHeaderList().get(i));
					DoubleMatrix1D geneValues = data.getDataCol(i);
					for(int k = 0; k < geneValues.size(); k++){//For each gene
						this.dataMatrix.setQuick(k, index, geneValues.get(k));
					}
				}
			}
		}
		/*
		 * Generate class C
		 */
		for(int i = 0; i < originalSize; i++, index++){//For each sample
			if(keepPosOriginal) this.headerList.add(this.negClass);
			else this.headerList.add(this.posClass);
			for(int k = 0; k < this.rows(); k++){//For each gene
				double v = classCMatrix.get(k, i);
				this.dataMatrix.setQuick(k, index, v);
			}
		}
		this.split();
		if(useRankMatrix) this.buildRankAndBinMatrix(-1);
	}

	/*
	 * Generate new Table with noise added for relative noise
	 */
	public Table(Table data, int duplicateSize, double noiseSize, double noisePercentage, Random rand, boolean useGaussian, boolean addNoiseToFirstSample, 
			boolean useRankMatrix, List<Double> covList){
		this.fileLocation = null;
		this.fileName = null;
		this.rowNameList = data.rowNameList;
		this.posClass = data.posClass;
		this.negClass = data.negClass;
		int index = 0;
		this.dataMatrix = new DenseDoubleMatrix2D(data.rows(), data.columns() * duplicateSize);
		for(int i = 0; i < data.columns(); i++){//For each sample
			for(int j = 0; j < duplicateSize; j++){//Multiple them by duplicateSize
				this.headerList.add(data.getHeaderList().get(i) + "_" + j);
				DoubleMatrix1D geneValues = data.getDataCol(i);
				for(int k = 0; k < geneValues.size(); k++){//For each gene
					if(j == 0 && addNoiseToFirstSample == false){
						//Do not add noise for the first sample
						this.dataMatrix.setQuick(k, index, geneValues.getQuick(k));
					}else{
						//Add noise to the rest
						double value = geneValues.getQuick(k);
						if(rand.nextDouble() < noisePercentage){
							//Add noise only to x percentage - could have made this more precise but not really needed. I think.
							double randValue;

							if(useGaussian) randValue = rand.nextGaussian();
							else randValue = rand.nextDouble();

							double tempValue = value * noiseSize * randValue * covList.get(k);

							if(rand.nextBoolean()){
								//Add
								value += tempValue;
							}else{
								//Subtract
								value -= tempValue;
							}
						}
						this.dataMatrix.setQuick(k, index, value);
					}
				}
				index++;
			}
		}
		this.split();
		if(useRankMatrix) this.buildRankAndBinMatrix(-1);
	}

	/*
	 * Generate new Table with noise added for absolute noise
	 */
	public Table(Table data, int duplicateSize, double noiseSize, double noisePercentage, Random rand, boolean useGaussian, boolean addNoiseToFirstSample, 
			boolean useRankMatrix){
		this.fileLocation = null;
		this.fileName = null;
		this.rowNameList = data.rowNameList;
		this.posClass = data.posClass;
		this.negClass = data.negClass;
		int index = 0;
		this.dataMatrix = new DenseDoubleMatrix2D(data.rows(), data.columns() * duplicateSize);
		for(int i = 0; i < data.columns(); i++){//For each sample
			for(int j = 0; j < duplicateSize; j++){//Multiple them by duplicateSize
				this.headerList.add(data.getHeaderList().get(i) + "_" + j);
				DoubleMatrix1D geneValues = data.getDataCol(i);
				for(int k = 0; k < geneValues.size(); k++){
					if(j == 0 && addNoiseToFirstSample == false){
						//Do not add noise for the first sample
						this.dataMatrix.setQuick(k, index, geneValues.getQuick(k));
					}else{
						//Add noise to the rest
						double value = geneValues.getQuick(k);
						if(rand.nextDouble() < noisePercentage){
							//Add noise only to x percentage - could have made this more precise but not really needed. I think.
							double randValue;

							if(useGaussian) randValue = rand.nextGaussian();
							else randValue = rand.nextDouble();

							if(rand.nextBoolean()){
								//Add
								value += value * noiseSize * randValue;
							}else{
								//Subtract
								value -= value * noiseSize * randValue;
							}
						}
						this.dataMatrix.setQuick(k, index, value);
					}
				}
				index++;
			}
		}
		this.split();
		if(useRankMatrix) this.buildRankAndBinMatrix(-1);
	}

	/*
	 * Generate a Table from Instances
	 */
	public Table(Instances inst, String posClass, String negClass, List<String> headerList){
		this.headerList = headerList;
		//Remove the class attribute
		this.dataMatrix = new DenseDoubleMatrix2D(inst.numAttributes() - 1, this.headerList.size());
		for(int r = 0; r < inst.numAttributes() - 1; r++){
			this.rowNameList.add(inst.attribute(r).name());
			for(int c = 0; c < inst.numInstances(); c++){
				this.dataMatrix.setQuick(r, c, inst.instance(c).value(r));
			}
		}
		this.posClass = posClass;
		this.negClass = negClass;
		this.split();
	}

	public void shuffleClassLabels(Random rand){
		int sampleSize = this.getSampleSize();
		int posSize = this.getPosSampleSize();
		List<Integer> iList = new ArrayList<Integer>();
		for(int i = 0; i < sampleSize; i++) iList.add(i);
		
		this.posClassIndex = new HashSet<Integer>();
		this.negClassIndex = new HashSet<Integer>();
		while(this.posClassIndex.size() != posSize){
			/*
			 * Randomly pick an available index and add to posClassIndex
			 */
			int i = rand.nextInt(iList.size());
			int index = iList.get(i);
			if(this.posClassIndex.contains(index)) throw new Error("Cannot be!");
			this.posClassIndex.add(index);
			iList.remove(i);
		}
		for(int i = 0; i < iList.size(); i++){
			int index = iList.get(i);
			if(this.negClassIndex.contains(index)) throw new Error("Cannot be!");
			if(this.posClassIndex.contains(index)) throw new Error("Cannot be!");
			this.negClassIndex.add(index);
		}
		if(this.posClassIndex.size() + this.negClassIndex.size() != sampleSize) throw new Error("This should not have happened!");
	}
	
	public void outputTable(){
		File f = this.writeToFileAsCSV();
		this.fileLocation = f.getAbsolutePath();
		this.fileName = f.getName();
	}

	private void split(){
		/*
		 * Fill up posClassIndex and negClassIndex
		 */
		if(this.posClass == null || this.negClass == null){
			//Does not have class information
			return;
		}
		List<Integer> classList = this.getClassList();
		if(this.posClassIndex.size() > 0) this.posClassIndex = new HashSet<Integer>();
		if(this.negClassIndex.size() > 0) this.negClassIndex = new HashSet<Integer>();
		if(this.thirdClassIndex.size() > 0) this.thirdClassIndex = new HashSet<Integer>();
		for(int y = 0; y < classList.size(); y++){				
			switch(classList.get(y)){
			//Pos
			case 0: this.posClassIndex.add(y); break;
			//Neg
			case 1: this.negClassIndex.add(y); break;
			//ThirdClass
			case 2: this.thirdClassIndex.add(y); break;
			default: throw new Error("Unknown Class: " + classList.get(y));
			}
		}		
	}

	public DoubleMatrix1D getClassListAsDoubleMatrix1D(){
		DoubleMatrix1D dm = new DenseDoubleMatrix1D(this.headerList.size());
		if(this.posClass == null || this.negClass == null) 
			throw new Error("PosClassString: " + this.posClass + "\tNegClassString: " + this.negClass);	
		for(int i = 0; i < this.headerList.size(); i++){
			if(this.headerList.get(i).contains(this.posClass)){
				dm.set(i, 0.0);				
			}else if(this.headerList.get(i).contains(this.negClass)){
				dm.set(i, 1.0);				
			}else{
				throw new Error("Unknown Class: " + this.headerList.get(i) + 
						"\tPos: " + posClass + "\tNeg: " + negClass);
			}
		}
		return dm;
	}

	public double[] getClassListAsDoubleArray(){
		/*
		 * Assume binary class
		 */
		if(this.posClass == null || this.negClass == null) 
			throw new Error("PosClassString: " + this.posClass + "\tNegClassString: " + this.negClass);		
		double[] bList = new double[this.headerList.size()];
		for(int i = 0; i < this.headerList.size(); i++){
			String h = this.headerList.get(i);
			if(h.contains(this.posClass)){
				bList[i] = 0.0;
			}else if(h.contains(this.negClass)){
				bList[i] = 1.0;

			}else{
				throw new Error("Unknown Class: " + h + "\tPos: " + posClass + "\tNeg: " + negClass);
			}
		}
		return bList;
	}

	private double[] convert2Array(List<Double> dList){
		double[] d = new double[dList.size()];
		for(int i = 0; i < dList.size(); i++){
			d[i] = dList.get(i);
		}
		return d;
	}

	public double[] getClassListAsArray(){
		List<Double> dList = this.getClassListAsDouble();
		return convert2Array(dList);
	}

	public List<Double> getClassListAsDouble(){
		/*
		 * Assume binary class
		 * Returns the class labels as double
		 */
		/*
		 * Can no longer use this because I want to be able to shuffle class labels
		 * if(this.posClass == null || this.negClass == null) 
			throw new Error("PosClassString: " + this.posClass + "\tNegClassString: " + this.negClass);		
		List<Double> bList = new ArrayList<Double>();
		for(String h:this.headerList){
			if(h.contains(this.posClass)){
				bList.add(0.0);
			}else if(h.contains(this.negClass)){
				bList.add(1.0);

			}else{
				throw new Error("Unknown Class: " + h + "\tPos: " + posClass + "\tNeg: " + negClass);
			}
		}
		return bList;*/
		
		List<Double> bList = new ArrayList<Double>();
		for(int i = 0; i < this.getSampleSize(); i++){
			if(this.posClassIndex.contains(i)) bList.add(0.0);
			else if(this.negClassIndex.contains(i)) bList.add(1.0);
			else if(this.thirdClassIndex.contains(i)) bList.add(2.0);
			else throw new Error("Unknown Class! Index " + i + " is not found in neither pos or negClassIndex");
		}
		return bList;
	}

	public List<Integer> getClassList(){
		/*
		 * Assume binary class
		 */
		List<String> header;
		if(this.classHeaderList == null){
			//Uses the name of sample as a clue to which class each sample belongs to
			header = this.headerList;
		}else{
			header = this.classHeaderList;
		}
		if(this.posClass == null || this.negClass == null) 
			throw new Error("PosClassString: " + this.posClass + "\tNegClassString: " + this.negClass);		
		List<Integer> bList = new ArrayList<Integer>();
		for(String h:header){
			if((h.contains(this.posClass) && h.contains(this.negClass)) || (this.thirdClass != null && h.contains(this.posClass) && h.contains(this.thirdClass)) || 
					(this.thirdClass != null && h.contains(this.thirdClass) && h.contains(this.negClass))){
				throw new Error("Ambiguous Class Label: " + h + "\tPos: " + this.posClass + "\tNeg: " + this.negClass + "\tThird: " + this.thirdClass);
			}else if(h.contains(this.posClass)){
				bList.add(0);
			}else if(h.contains(this.negClass)){
				bList.add(1);
			}else if(h.contains(this.thirdClass)){
				bList.add(2);
			}else{
				throw new Error("Unknown Class Label: " + h + "\tPos: " + this.posClass + "\tNeg: " + this.negClass);
			}
		}
		return bList;
	}

	public StringBuffer toRCodeAsClassVector(String name){
		//if(this.rClassVector != null) return this.rClassVector;
		StringBuffer code = new StringBuffer();
		List<Integer> classVector = this.getClassList();
		for(int x = 0; x < classVector.size(); x++){
			if(x != 0){
				code.append(",");
			}else{
				code.append(name + " = c(");
			}
			code.append(classVector.get(x));
		}
		code.append(");");
		//this.rClassVector = code;
		return code;
	}

	public StringBuffer[] toRCodeAsMatrix(String name){
		/*
		 * Return a StringBuffer that will run as R code to convert the table into dataframe
		 * with the given name 'name'
		 * Split the R code into 2 parts
		 * 1) Only the pos data
		 * 2) Only the neg data
		 */	
		StringBuffer[] rCode = new StringBuffer[2];
		/*if(this.rPosMatrix != null && this.rNegMatrix != null){
			rCode[0] = this.rPosMatrix;
			rCode[1] = this.rNegMatrix;
			return rCode;
		}*/
		rCode[0] = new StringBuffer();
		rCode[1] = new StringBuffer();
		for(int x = 0; x < this.rowNameList.size(); x++){
			for(int y = 0; y < this.headerList.size(); y++){
				boolean first = true;
				if(this.posClassIndex.contains(y)){
					if(first == false){
						rCode[0].append(",");
					}else{
						first = false;
						rCode[0].append("p" + x + " = c(");
					}				
					rCode[0].append(this.dataMatrix.get(x,y));
				}
			}
			for(int y = 0; y < this.headerList.size(); y++){
				if(this.negClassIndex.contains(y)){
					boolean first = true;
					if(first == false){
						rCode[1].append(",");
					}else{
						first = false;
						rCode[1].append("n" + x + " = c(");
					}
					rCode[1].append(this.dataMatrix.get(x, y));
				}
			}
			rCode[0].append(");");
			rCode[1].append(");");			
		}		

		for(int x = 0; x < this.rowNameList.size(); x++){
			if(x != 0){
				rCode[0].append(",");
				rCode[1].append(",");
			}else{
				rCode[0].append("p" + name + " = rbind(");
				rCode[1].append("n" + name + " = rbind(");		
			}
			rCode[0].append("p" + x);
			rCode[1].append("n" + x);
		}
		rCode[0].append(");");
		rCode[1].append(");");
		return rCode;
	}

	public StringBuffer toRCodeAsMatrix(String name, boolean setRowName, boolean setColName){
		/*
		 * Return a StringBuffer that will run as R code to convert the table into dataframe
		 * with the given name 'name'
		 */
		//if(this.rMatrix != null) return this.rMatrix;
		StringBuffer code = new StringBuffer();
		for(int x = 0; x < this.rowNameList.size(); x++){
			for(int y = 0; y < this.headerList.size(); y++){
				if(y != 0){
					code.append(",");
				}else{
					code.append("x" + x + " = c(");
				}
				code.append(this.dataMatrix.get(x,y));
			}
			code.append(");");
			//code.append("print(\"x" + x + "\");");
		}		
		for(int x = 0; x < this.rowNameList.size(); x++){
			if(x != 0){
				code.append(",");
			}else{
				code.append(name + " = rbind(");		
			}
			code.append("x" + x);
		}
		code.append(");");		
		if(setRowName){			
			for(int x = 0; x < this.rowNameList.size(); x++){
				if(x != 0){
					code.append(",");
				}else{
					code.append("rownames(" + name + ") = c(");
				}
				code.append("\"" + this.rowNameList.get(x) + "\"");
			}
			code.append(");");
		}
		if(setColName){
			for(int x = 0; x < this.headerList.size(); x++){
				if(x != 0){
					code.append(",");
				}else{
					code.append("colnames(" + name + ") = c(");
				}
				code.append("\"" + this.headerList.get(x) + "\"");
			}
			code.append(");");
		}
		//this.rMatrix = code;
		return code;
	}	

	private void addAbsoluteNoise(int row, int col, double percentageSizeOfNoise, Random r){
		/*
		 * Add absolute noise to the original data
		 */
		double originalValue = this.dataMatrix.get(row, col);
		double noise = r.nextDouble() * originalValue * percentageSizeOfNoise;
		//		double noise = this.dataMatrix.get(row, col) * percentageSizeOfNoise;
		if(r.nextBoolean()){
			this.dataMatrix.set(row, col, originalValue + noise);			
		}else{
			this.dataMatrix.set(row, col, originalValue - noise);
		}		
	}

	private void addRelativeNoise(int row, int col, double percentageSizeOfNoise, Random r, List<Double> covList){
		/*
		 * Add relative noise to the original data
		 */
		double originalValue = this.dataMatrix.get(row, col);
		double noise = r.nextDouble() * originalValue * percentageSizeOfNoise * covList.get(row);
		if(r.nextBoolean()){
			this.dataMatrix.set(row, col, originalValue + noise);			
		}else{
			this.dataMatrix.set(row, col, originalValue - noise);
		}		
	}

	public Table addNoise(double percentageToAddNoise, double percentageSizeOfNoise, Random rand, List<Double> covList){
		/*
		 * Add noise to a clone and returns the perturbed data without affecting the original data
		 */
		if(percentageToAddNoise < 0 || percentageToAddNoise > 1.0 || 
				percentageSizeOfNoise < 0 || percentageSizeOfNoise > 2.0){
			throw new Error("percentageToAddNoise: " + percentageToAddNoise + 
					"\npercentageSizeOfNoise: " + percentageSizeOfNoise);			
		}
		int totalNumber = this.headerList.size() * this.rowNameList.size();
		int numberOfNoiseToAdd = (int)(totalNumber * percentageToAddNoise);
		Set<Integer> fullSet = new HashSet<Integer>();
		for(int x = 0; x < totalNumber; x++){
			fullSet.add(x);
		}
		Set<Integer> selectedSet = new HashSet<Integer>();		
		for(Iterator<Integer> i = fullSet.iterator(); selectedSet.size() < numberOfNoiseToAdd;){
			selectedSet.add(i.next());
		}
		Table perturbedData = new Table(this);		
		int size = perturbedData.rowNameList.size();
		for(Iterator<Integer> i = selectedSet.iterator(); i.hasNext();){
			int num = i.next();
			int row = num % size;
			int col = num / size;		
			if(covList == null) perturbedData.addAbsoluteNoise(row, col, percentageSizeOfNoise, rand);
			else perturbedData.addRelativeNoise(row, col, percentageSizeOfNoise, rand, covList);
		}
		return perturbedData;
	}

	public Table bootstrap(double percentage, boolean withReplacement, Random r){
		if(percentage < 0 || percentage > 1){			
			throw new Error("percentage: " + percentage);			
		}
		int numberToChoose = (int)(percentage * this.headerList.size());
		List<Integer> headerIndexList = new ArrayList<Integer>();
		if(withReplacement){
			while(headerIndexList.size() < numberToChoose){
				headerIndexList.add(r.nextInt(this.headerList.size()));
			}
		}else if(percentage == 1.0){
			//Maintain the order
			for(int x = 0; x < this.headerList.size(); x++){
				headerIndexList.add(x);
			}
		}else{
			Set<Integer> fullSet = new HashSet<Integer>();
			for(int x = 0; x < this.headerList.size(); x++){
				fullSet.add(x);
			}
			for(Iterator<Integer> i = fullSet.iterator(); headerIndexList.size() < numberToChoose;){
				headerIndexList.add(i.next());
			}			
		}
		List<String> headerList = new ArrayList<String>();
		for(int i:headerIndexList) headerList.add(this.headerList.get(i));
		List<String> rowNameList = new ArrayList<String>();
		for(String s:this.rowNameList) rowNameList.add(new String(s));

		DenseDoubleMatrix2D dataMatrix = new DenseDoubleMatrix2D(rowNameList.size(), headerIndexList.size());
		for(int x = 0; x < rowNameList.size(); x++){
			int j = 0;
			for(int i:headerIndexList){
				dataMatrix.set(x, j, this.dataMatrix.get(x, i));
				j++;
			}
		}
		Table t = new Table(headerList, rowNameList, dataMatrix, this.posClass, this.negClass);
		while(t.getPosSampleSize() < 2 || t.getNegSampleSize() < 2){
			t = this.bootstrap(percentage, withReplacement, r);
		}
		return t;
	}

	public Table smoothBootstrap(double percentage, boolean withReplacement,
			double percentageToAddNoise, double percentageSizeOfNoise, Random rand){
		return smoothBootstrap(percentage, withReplacement, 
				percentageToAddNoise, percentageSizeOfNoise, rand, null);
	}

	public Table smoothBootstrap(double percentage, boolean withReplacement, 
			double percentageToAddNoise, double percentageSizeOfNoise, Random rand, List<Double> covList){
		//Generate a bootstrap sample then add noise to it
		return this.bootstrap(percentage, withReplacement, rand).addNoise(percentageToAddNoise, percentageSizeOfNoise, rand, covList);		
	}
	
	public Instances getAsInstances(Set<Integer> includeGeneIndex, boolean useRankMatrix){
		return this.getAsInstancesEfficient(includeGeneIndex, useRankMatrix, false);
	}

	public Instances getAsInstances(Set<Integer> includeGeneIndex, boolean useRankMatrix, boolean containClassC){
		System.out.println("getAsInstances Start...");
		Instances i = this.getAsInstancesEfficient(includeGeneIndex, useRankMatrix, containClassC);
		System.out.println("getAsInstances End!");
		return i;
	}

	public File writeToFileAsCSV(){
		try{			
			File f = File.createTempFile("Polaris_Generated_", ".csv");	
			f.deleteOnExit();
			return this.writeToFileAsCSV(f);
		}catch(IOException e){
			System.err.println("Unable to create file at Table.writeToFileAsCSV");
			e.printStackTrace(); 
			return null;
		}
	}

	public File writeToFileAsCSV(File outputFileLocation){
		try{			
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFileLocation));
			output.write("Probe");
			for(String header:this.headerList) output.write("," + header);
			output.newLine();
			for(int i = 0; i < this.rowNameList.size(); i++){				
				output.write(this.rowNameList.get(i));
				for(int j = 0; j < this.headerList.size(); j++){					
					output.write("," + this.dataMatrix.get(i, j));
				}
				output.newLine();
			}
			output.close();
			return outputFileLocation;
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	public Instances getAsInstancesEfficient(Set<Integer> includeGeneSet, boolean useRankMatrix){
		System.out.println("getAsInstancesEfficient start");
		Instances i = getAsInstancesEfficient(includeGeneSet, useRankMatrix, false);
		System.out.println("getAsInstancesEfficient end");
		return i;
	}

	public Instances getAsInstancesEfficient(Set<Integer> includeGeneSet, boolean useRankMatrix, boolean containThirdClass){
		/*
		 * Returns Instances without writing to file then convert
		 */
		//http://weka.wikispaces.com/Programmatic+Use
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(4);
		for(int i = 0; i < includeGeneSet.size(); i++){
			fvWekaAttributes.addElement(new Attribute("Numeric" + i));
		}
		// Declare the class attribute along with its values
		FastVector fvClassVal;
		if(containThirdClass) fvClassVal = new FastVector(3);
		else fvClassVal = new FastVector(2);
		fvClassVal.addElement("pos");
		fvClassVal.addElement("neg");
		if(containThirdClass) fvClassVal.addElement("classC");
		Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
		fvWekaAttributes.addElement(ClassAttribute);
		// Create an empty training set
		Instances instances = new Instances("Rel", fvWekaAttributes, this.getSampleSize());           
		// Set class index
		instances.setClassIndex(fvWekaAttributes.size() - 1);

		/*
		 * For ARFF format
		 * Each row is a sample and Each column is a probe
		 */
		Set<Integer> posSampleSet = this.posClassIndex;
		Set<Integer> negSampleSet = this.negClassIndex;
		Set<Integer> classCSampleSet = this.thirdClassIndex;
		if(useRankMatrix && this.numberOfBin == -1){
			this.buildRankAndBinMatrix(-1);
		}
		//For each sample
		for(int x = 0; x < this.headerList.size(); x++){
			Instance instance = new Instance(fvWekaAttributes.size());
			//For each gene
			int i = 0;//keep counts of included
			for(int y = 0; y < this.rowNameList.size(); y++){
				if(includeGeneSet == null || includeGeneSet.contains(y)){
					if(useRankMatrix == false){
						instance.setValue((Attribute)fvWekaAttributes.elementAt(i), this.dataMatrix.get(y,x));
					}else{
						instance.setValue((Attribute)fvWekaAttributes.elementAt(i), this.rankMatrix.get(y, x));
					}
					i++;
				}
			}
			if(posSampleSet.contains(x)) instance.setValue((Attribute)fvWekaAttributes.elementAt(i), "pos");
			else if(negSampleSet.contains(x)) instance.setValue((Attribute)fvWekaAttributes.elementAt(i), "neg");
			else if(containThirdClass && classCSampleSet.contains(x)) instance.setValue((Attribute)fvWekaAttributes.elementAt(i), "classC");
			else throw new Error("Sample does not belong to either class");
			instances.add(instance);
		}
		return instances;
	}

	/*
	 * Obtain a randomly chosen folds
	 */
	public List<List<Integer>> splitForCrossValidationInt(int fold, boolean stratify, int randNumber){
		if(this.posClass == null || this.negClass == null){
			throw new Error("Class Information not set - posClass == null || negClass == null");
		}
		Random rand = new Random(randNumber);
		List<List<Integer>> foldIndexList = new ArrayList<List<Integer>>();
		for(int i = 0; i < fold; i++) foldIndexList.add(new ArrayList<Integer>());
		if(stratify){
			//Randomly assign the index of samples to different folds but keep the class distribution
			//in each fold
			List<Integer> posList = new ArrayList<Integer>();
			List<Integer> negList = new ArrayList<Integer>();
			obtainClassIndex(posList, negList);
			randomlyAssign(foldIndexList, posList, rand);
			randomlyAssign(foldIndexList, negList, rand);
		}else{
			//Randomly assign the index of samples to different folds			
			List<Integer> originalList = new ArrayList<Integer>();
			for(int i = 0; i < this.headerList.size(); i++) originalList.add(i);			
			randomlyAssign(foldIndexList, originalList, rand);
		}
		return foldIndexList;
	}

	public static Table getTable(List<Integer> selectedSampleList, Table data){
		return new Table(selectedSampleList, data);
	}

	public static List<Integer> getFoldIndex(List<List<Integer>> iListList, int foldNum, boolean isTrain){
		/*
		 * Obtain the indices for training or test data based on the split
		 */
		List<Integer> iList = new ArrayList<Integer>();
		for(int i = 0; i < iListList.size(); i++){
			if(i == foldNum){
				if(isTrain == false)//Requesting for Test data
					for(int index:iListList.get(i)) iList.add(index);
			}else{
				if(isTrain == true)//Requesting for Train data
					for(int index:iListList.get(i)) iList.add(index);
			}
		}
		return iList;
	}

	public List<Table> splitForCrossValidation(int fold, boolean stratify, int randNumber){		
		List<List<Integer>> foldIndexList = this.splitForCrossValidationInt(fold, stratify, randNumber);
		List<Table> tableList = new ArrayList<Table>();
		for(int i = 0; i < foldIndexList.size(); i++){
			List<String> newHeaderList = new ArrayList<String>();
			List<Integer> indexList = foldIndexList.get(i);			
			DoubleMatrix2D dataMatrix = new DenseDoubleMatrix2D(this.rowNameList.size(), indexList.size());
			for(int j = 0; j < this.rowNameList.size(); j++){
				int k = 0;
				for(int index:indexList){
					dataMatrix.set(j, k, this.dataMatrix.get(j, index));
					if(j == 0){
						newHeaderList.add(this.headerList.get(index));
					}
					k++;
				}			
			}			
			tableList.add(new Table(newHeaderList, this.rowNameList,
					dataMatrix, this.posClass, this.negClass));
		}
		return tableList;
	}

	private void obtainClassIndex(List<Integer> posList, List<Integer> negList){
		for(int i = 0; i < this.headerList.size(); i++){
			String s = this.headerList.get(i);
			if(s.contains(this.posClass) && s.contains(this.negClass)){
				throw new Error("Matches both: " + s + "\t" + this.posClass + "\t" + this.negClass);
			}else if(this.headerList.get(i).contains(this.posClass)){
				posList.add(i);
			}else if(this.headerList.get(i).contains(this.negClass)){
				negList.add(i);
			}else{
				throw new Error("Does not match any");
			}
		}			
	}

	private void randomlyAssign(List<List<Integer>> foldIndexList, List<Integer> indexList, Random rand){
		int fold = foldIndexList.size();
		int count = 0;		
		while(indexList.size() > 0){
			int i = rand.nextInt(indexList.size());
			int index = indexList.get(i);
			indexList.remove(i);
			foldIndexList.get(count).add(index);
			count++;
			if(count == fold) count = 0;
		}
	}

	public void transpose(){
		Algebra a = new Algebra();
		this.dataMatrix = a.transpose(this.dataMatrix);
		this.split();		
	}

	public boolean isPosSample(int col){
		if(this.posClassIndex.contains(col)) return true;
		else if(this.negClassIndex.contains(col)) return false;
		else throw new Error("Index not found in neither pos or neg classes: " + col);
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("PosIndex (" + this.posClassIndex.size() + "): ");
		for(int i:this.posClassIndex) sb.append(i + ", ");
		sb.append("\r\n");
		
		sb.append("NegIndex (" + this.negClassIndex.size() + "): ");
		for(int i:this.negClassIndex) sb.append(i + ", ");
		sb.append("\r\n");
		
		sb.append("Pos:Neg ratio = " + Utils.roundDouble(((this.posClassIndex.size() + 0.0) / this.negClassIndex.size()), 4) + "\r\n");
		
		sb.append("Row Size: " + this.rows() + "\r\n");
		sb.append("Column Size: " + this.columns() + "\r\n");
		return sb.toString();
	}
}

class IndexAndValue{
	private int index;
	private double value;

	public IndexAndValue(int i, double v){
		this.index = i;
		this.value = v;
	}

	public int getIndex(){return this.index;}
	public double getValue(){return this.value;}
}

class SortByValue implements Comparator<IndexAndValue>{	
	public int compare(IndexAndValue probe1, IndexAndValue probe2){
		if(Double.isNaN(probe1.getValue())){
			return 1;
		}else if(Double.isNaN(probe2.getValue())){
			return -1;
		}else if(probe1.getValue() > probe2.getValue()){
			return -1;
		}else if(probe1.getValue() < probe2.getValue()){
			return 1;
		}else{
			return 0;
		}		
	}
}



