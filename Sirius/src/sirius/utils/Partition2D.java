package sirius.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partition2D extends Partition{
		
	public Partition2D(double xS, double xE, double yS, double yE){
		this.xStart = xS;
		this.xEnd = xE;
		this.yStart = yS;
		this.yEnd = yE;
	}
	
	public void add(double x, double y){
		this.xOriginalList.add(x);
		this.yOriginalList.add(y);
		this.xSortedList.add(x);
		this.ySortedList.add(y);
	}	
	
	public void sort(){
		Collections.sort(xSortedList);
		Collections.sort(ySortedList);
	}

	public boolean isIdentical(){
		boolean identical = true;		
		for(int i = 0; i < xOriginalList.size() - 1; i++){
			double currentX = xOriginalList.get(i);
			double nextX = xOriginalList.get(i + 1);
			double currentY = yOriginalList.get(i);
			double nextY = yOriginalList.get(i + 1);
			if(currentX != nextX || currentY != nextY){
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
			s.append(this.yOriginalList.get(i) + " ");
		}
		return s.toString();
	}
	
	public List<Partition2D> partition(double xPartitionValue, double yPartitionValue, double epsilon){		
		List<Partition2D> pList = new ArrayList<Partition2D>();
		pList.add(new Partition2D(xPartitionValue + epsilon, this.xEnd, yPartitionValue + epsilon, this.yEnd));
		pList.add(new Partition2D(xPartitionValue + epsilon, this.xEnd, this.yStart, yPartitionValue));
		pList.add(new Partition2D(this.xStart, xPartitionValue, yPartitionValue + epsilon, this.yEnd));
		pList.add(new Partition2D(this.xStart, xPartitionValue, this.yStart, yPartitionValue));
		/*
		 * Sort the points into their respective Partition
		 */
		for(int i = 0; i < xOriginalList.size(); i++){
			double x = xOriginalList.get(i);
			double y = yOriginalList.get(i);
			if(x >= xPartitionValue + epsilon){
				if(y >= yPartitionValue + epsilon){
					pList.get(0).add(x, y);
				}else{
					pList.get(1).add(x, y);
				}
			}else{
				if(y >= yPartitionValue + epsilon){
					pList.get(2).add(x, y);
				}else{
					pList.get(3).add(x, y);
				}
			}
		}
		return pList;
	}
	
	public List<Partition2D> partition(){
		/*
		 * Find mid-point such that divide into half
		 */
		this.sort();
		int centerIndex1 = (xSortedList.size() / 2) - 1;
		int centerIndex2 = xSortedList.size() / 2;		
		double xPartitionValue = (xSortedList.get(centerIndex1) + xSortedList.get(centerIndex2)) / 2.0;
		double yPartitionValue = (ySortedList.get(centerIndex1) + ySortedList.get(centerIndex2)) / 2.0;		
		double epsilon = 1.0E-15;		
		/*
		 * Create four new partitions
		 */
		List<Partition2D> pList = partition(xPartitionValue, yPartitionValue, epsilon);
		if(isInvalidPartition(pList)){			
			pList = partition(xPartitionValue, yPartitionValue, epsilon * -1);			
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
		//Invalid if 3 or more have 0 points
		return (countZero >= 3) ? true:false;
	}

	public boolean isUniform(List<? extends Partition> pList){
		/*
		 * Test the partition for uniform distribution
		 */
		double expectedN = xOriginalList.size() / 4.0;
		double chiSquared = 0.0;
		for(Partition p:pList) chiSquared += Math.pow(expectedN - p.numOfPoints(), 2.0) / expectedN; 			 
		//7.815 is for 95% and df of 3
		return (7.815 >= chiSquared) ? true:false; 		
	}
}