package sirius.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Partition {
	protected List<Double> xOriginalList = new ArrayList<Double>();
	protected List<Double> yOriginalList = new ArrayList<Double>();
	protected List<Double> xSortedList = new ArrayList<Double>();
	protected List<Double> ySortedList = new ArrayList<Double>();	
	protected double xStart;
	protected double xEnd;
	protected double yStart;
	protected double yEnd;
	
	public abstract void sort();
	public abstract boolean isIdentical();
	public abstract String toString();
	public abstract List<? extends Partition> partition();
	protected abstract boolean isInvalidPartition(List<? extends Partition> pList);
	public abstract boolean isUniform(List<? extends Partition> pList);
	
	public int numOfPoints(){return this.xOriginalList.size();}
	public double getXStart(){return this.xStart;}
	public double getXEnd(){return this.xEnd;}
	public double getYStart(){return this.yStart;}
	public double getYEnd(){return this.yEnd;}
	public List<Double> getXSortedList(){return this.xSortedList;}
	public List<Double> getYSortedList(){return this.ySortedList;}
	public List<Double> getXOriginalList(){return this.xOriginalList;}
	public List<Double> getYOriginalList(){return this.yOriginalList;}
}