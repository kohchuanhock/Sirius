package sirius.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cern.jet.stat.Gamma;

public class ContinuousMI {
	public static double MIUsingRecursivePartitioning(List<Double> xList, List<Double> yList){		
		/*
		 * Initialize
		 */		
		Partition2D originalPartition = new Partition2D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		for(int i = 0; i < xList.size(); i++){
			//Remove missing/unknown/infinite values
			if(xList.get(i).isNaN() || xList.get(i).isInfinite() ||
					yList.get(i).isNaN() || yList.get(i).isInfinite()){
				continue;
			}
			originalPartition.add(xList.get(i), yList.get(i));
		}
		originalPartition.sort();
		//activeList implies that these partition have yet to be uniform
		List<Partition2D> activeList = originalPartition.partition();
		//uniformList implies that these partition are uniform and need not partition further
		List<Partition2D> uniformList = new ArrayList<Partition2D>();		
		while(activeList.size() > 0){//still have partitions to checks			
			Partition2D currentP = activeList.get(0);
			activeList.remove(0);
			if(currentP.numOfPoints() > 0){
				if(currentP.isIdentical() == false){
					List<Partition2D> pList = currentP.partition();
					if(currentP.isUniform(pList)){				
						uniformList.add(currentP);
					}else{
						for(Partition2D p:pList){
							if(p.numOfPoints() > 0){
								activeList.add(p);
							}
						}							
					}
				}else{//All points in the partition are identical, cannot partition further
					uniformList.add(currentP);
				}				
			}
		}		
		return computeMI(originalPartition, uniformList);
	}
	
	public static double MIUsingRecursivePartitioning(List<Double> xList, List<Double> yList, 
			List<Double> zList){
		/*
		 * Initialize
		 */		
		Partition3D originalPartition = new Partition3D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY);
		for(int i = 0; i < xList.size(); i++){
			//Remove missing/unknown/infinite values
			if(xList.get(i).isNaN() || xList.get(i).isInfinite() ||
					yList.get(i).isNaN() || yList.get(i).isInfinite() ||
					zList.get(i).isNaN() || zList.get(i).isInfinite()){
				continue;
			}
			originalPartition.add(xList.get(i), yList.get(i), zList.get(i));
		}
		originalPartition.sort();
		//activeList implies that these partition have yet to be uniform
		List<Partition3D> activeList = originalPartition.partition();
		//uniformList implies that these partition are uniform and need not partition further
		List<Partition3D> uniformList = new ArrayList<Partition3D>();		
		while(activeList.size() > 0){//still have partitions to checks			
			Partition3D currentP = activeList.get(0);
			activeList.remove(0);
			if(currentP.numOfPoints() > 0){
				if(currentP.isIdentical() == false){
					List<Partition3D> pList = currentP.partition();
					if(currentP.isUniform(pList)){				
						uniformList.add(currentP);
					}else{
						for(Partition3D p:pList){
							if(p.numOfPoints() > 0){
								activeList.add(p);
							}
						}
					}
				}else{//All points in the partition are identical, cannot partition further					
					uniformList.add(currentP);
				}				
			}
		}
		double a = computeMI(originalPartition, uniformList);		
		//double b = computeMI(uniformList, xList, yList, zList, 2);
		//System.out.println(a + "\t" + b);
		return a;
	}
	
	public static double ConditionalMIUsingRecursivePartitioning(List<Double> xList, List<Double> yList,
			List<Double> zList){
		/*
		 * Initialize
		 */		
		Partition3D originalPartition = new Partition3D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 
				Double.POSITIVE_INFINITY);
		for(int i = 0; i < xList.size(); i++){
			//Remove missing/unknown/infinite values
			if(xList.get(i).isNaN() || xList.get(i).isInfinite() ||
					yList.get(i).isNaN() || yList.get(i).isInfinite() ||
					zList.get(i).isNaN() || zList.get(i).isInfinite()){
				continue;
			}
			originalPartition.add(xList.get(i), yList.get(i), zList.get(i));
		}
		originalPartition.sort();
		//activeList implies that these partition have yet to be uniform
		List<Partition3D> activeList = originalPartition.partition();
		//uniformList implies that these partition are uniform and need not partition further
		List<Partition3D> uniformList = new ArrayList<Partition3D>();		
		while(activeList.size() > 0){//still have partitions to checks			
			Partition3D currentP = activeList.get(0);
			activeList.remove(0);
			if(currentP.numOfPoints() > 0){
				if(currentP.isIdentical() == false){
					List<Partition3D> pList = currentP.partition();
					if(currentP.isUniform(pList)){				
						uniformList.add(currentP);
					}else{
						for(Partition3D p:pList){
							if(p.numOfPoints() > 0){
								activeList.add(p);
							}
						}
					}
				}else{//All points in the partition are identical, cannot partition further					
					uniformList.add(currentP);
				}				
			}
		}
		double a = computeCMI(originalPartition, uniformList);		
		//double b = computeMI(uniformList, xList, yList, zList, 2);
		//System.out.println(a + "\t" + b);
		return a;
	}
	
	private static double computeMI(Partition2D originalPartition, List<Partition2D> uniformList){
		List<Double> xSortedList = originalPartition.getXSortedList();
		List<Double> ySortedList = originalPartition.getYSortedList();
		double mi = 0.0;
		double sum = xSortedList.size();
		for(Partition2D a:uniformList){
			if(a.numOfPoints() > 0){
				double px = getElementsBetween(xSortedList, a.getXStart(), a.getXEnd());
				double py = getElementsBetween(ySortedList, a.getYStart(), a.getYEnd());
				double pxy = a.numOfPoints();
				if(px != 0.0 && py != 0.0) mi += pxy * Math.log((pxy * sum) / (px * py));
			}
		}
		mi /= sum;
		return mi / Math.log(2);
	}	
	
	private static double computeMI(Partition3D originalPartition, List<Partition3D> uniformList){
		List<Double> xList = originalPartition.getXOriginalList();
		List<Double> yList = originalPartition.getYOriginalList();
		List<Double> zList = originalPartition.getZOriginalList();
		double mi = 0.0;
		double sum = xList.size();
		for(Partition3D a:uniformList){
			if(a.numOfPoints() > 0){
				double pz = getElementsBetween(zList, a.getZStart(), a.getZEnd());
				double pxy = getElementsBetween(xList, yList, a.getXStart(), a.getXEnd(),
						a.getYStart(), a.getYEnd());
				double pxyz = a.numOfPoints();
				if(pxy != 0.0 && pz != 0.0){					
					mi += (pxyz * Math.log((pxyz * sum) / (pxy * pz )));
				}
			}
		}
		mi /= sum;
		return mi / Math.log(2);
	}
	
	private static double computeCMI(Partition3D originalPartition, List<Partition3D> uniformList){
		List<Double> xList = originalPartition.getXOriginalList();
		List<Double> yList = originalPartition.getYOriginalList();
		List<Double> zList = originalPartition.getZOriginalList();
		double mi = 0.0;
		double sum = xList.size();
		for(Partition3D a:uniformList){
			if(a.numOfPoints() > 0){
				double pz = getElementsBetween(zList, a.getZStart(), a.getZEnd());
				double pxz = getElementsBetween(xList, zList, a.getXStart(), a.getXEnd(),
						a.getZStart(), a.getZEnd());
				double pyz = getElementsBetween(yList, zList, a.getYStart(), a.getYEnd(),
						a.getZStart(), a.getZEnd());
				double pxyz = a.numOfPoints();
				if(pxz != 0.0 && pyz != 0.0 && pz != 0.0){					
					mi += (pxyz * Math.log((pz * pxyz) / (pxz * pyz)));
				}
			}
		}
		mi /= sum;
		return mi / Math.log(2);
	}	
		
	public static double computeMI(List<Partition2D> uniformList, List<Double> xList, List<Double> yList){
		/*
		 * Compute MI using partitions and via discreteMI
		 */
		Set<Double> xBoundsSet = new HashSet<Double>();
		Set<Double> yBoundsSet = new HashSet<Double>();
		for(int i = 0; i < uniformList.size(); i++){
			xBoundsSet.add(uniformList.get(i).getXEnd());
			yBoundsSet.add(uniformList.get(i).getYEnd());
		}
		List<Double> sortedXBounds = new ArrayList<Double>();
		List<Double> sortedYBounds = new ArrayList<Double>();
		sortedXBounds.addAll(xBoundsSet);
		sortedYBounds.addAll(yBoundsSet);
		Collections.sort(sortedXBounds);
		Collections.sort(sortedYBounds);
		double[] xBounds = new double[sortedXBounds.size()];		
		double[] yBounds = new double[sortedYBounds.size()];
		for(int i = 0; i < sortedXBounds.size(); i++) xBounds[i] = sortedXBounds.get(i);
		for(int i = 0; i < sortedYBounds.size(); i++) yBounds[i] = sortedYBounds.get(i);	
		double[][] XY = new double[sortedXBounds.size()][sortedYBounds.size()];
		for(int i = 0; i < xList.size(); i++){
			XY[getBoundedBy(xList.get(i), xBounds)][getBoundedBy(yList.get(i), yBounds)]++;
		}
		return DiscreteMI.mutualInformation(XY);
	}
	
	public static double computeMI(List<Partition3D> uniformList, List<Double> xList, List<Double> yList,
			List<Double> zList, int conditionalIndex){
		/*
		 * Compute MI using partitions and via discreteMI
		 */
		Set<Double> xBoundsSet = new HashSet<Double>();
		Set<Double> yBoundsSet = new HashSet<Double>();
		Set<Double> zBoundsSet = new HashSet<Double>();
		for(int i = 0; i < uniformList.size(); i++){
			xBoundsSet.add(uniformList.get(i).getXEnd());
			yBoundsSet.add(uniformList.get(i).getYEnd());
			zBoundsSet.add(uniformList.get(i).zEnd);
		}
		List<Double> sortedXBounds = new ArrayList<Double>();
		List<Double> sortedYBounds = new ArrayList<Double>();
		List<Double> sortedZBounds = new ArrayList<Double>();
		sortedXBounds.addAll(xBoundsSet);
		sortedYBounds.addAll(yBoundsSet);
		sortedZBounds.addAll(zBoundsSet);
		Collections.sort(sortedXBounds);
		Collections.sort(sortedYBounds);
		Collections.sort(sortedZBounds);
		double[] xBounds = new double[sortedXBounds.size()];		
		double[] yBounds = new double[sortedYBounds.size()];
		double[] zBounds = new double[sortedZBounds.size()];
		for(int i = 0; i < sortedXBounds.size(); i++) xBounds[i] = sortedXBounds.get(i);
		for(int i = 0; i < sortedYBounds.size(); i++) yBounds[i] = sortedYBounds.get(i);
		for(int i = 0; i < sortedZBounds.size(); i++) zBounds[i] = sortedZBounds.get(i);
		System.out.println(sortedXBounds.size() + "\t" + 
				sortedYBounds.size() + "\t" + sortedZBounds.size());
		double[][][] XYZ = new double[sortedXBounds.size()][sortedYBounds.size()][sortedZBounds.size()];
		for(int i = 0; i < xList.size(); i++){
			XYZ[getBoundedBy(xList.get(i), xBounds)][getBoundedBy(yList.get(i), yBounds)]
			                                         [getBoundedBy(zList.get(i), zBounds)]++;
		}
		return DiscreteMI.conditionalMutualInformation(XYZ, conditionalIndex);
	}
	
	private static int getElementsBetween(List<Double> dList, double start, double end){
		int num = 0;
		for(int i = 0; i < dList.size(); i++){
			double d = dList.get(i); 
			if(d >= start && d <= end) num++;			
		}
		return num;
	}
	
	private static int getElementsBetween(List<Double> xList, List<Double> yList, 
			double xStart, double xEnd, double yStart, double yEnd){
		int num = 0;
		for(int i = 0; i < xList.size(); i++){
			double x = xList.get(i);
			double y = yList.get(i);
			if(x >= xStart && x <= xEnd && y >= yStart && y <= yEnd) num++;
		}
		return num;
	}
	
 	public static double SUUsingCellucciMethod(List<Double> xList, List<Double> yList, boolean modified){
 		return DiscreteMI.SU(partitionUsingCellucciMethod(xList,yList,modified));
 	}
 	
 	public static double SUUsingCellucciMethod(double[] xList, double[] yList, boolean modified){
 		return DiscreteMI.SU(partitionUsingCellucciMethod(xList,yList,modified));
 	}
 	
 	public static double NormalizedMIUsingCellucciMethod(double[] xList, double[] yList, boolean modified){
 		return DiscreteMI.normalizedMutualInformation(partitionUsingCellucciMethod(xList,yList,modified));
 	}
 	
 	public static double NormalizedMIUsingCellucciMethod(List<Double> xList, List<Double> yList, 
 			boolean modified){
 		return DiscreteMI.normalizedMutualInformation(partitionUsingCellucciMethod(xList,yList,modified));
 	}
 	
 	public static double NormalizedConditionalMIUsingCellucciMethod(
 			double[] xList, double[] yList, double[] zList, boolean modified){
 		return DiscreteMI.normalizedConditionalMutualInformation(
 				partitionUsingCellucciMethod(xList, yList, zList, modified), 2);
 	}
 	
 	public static double NormalizedConditionalMIUsingCellucciMethod(
 			List<Double> xList, List<Double> yList, List<Double> zList, boolean modified){
 		return DiscreteMI.normalizedConditionalMutualInformation(
 				partitionUsingCellucciMethod(xList, yList, zList, modified), 2);
 	}
 	
 	public static double IGUsingCellucciMethod(double[] xList, double[] yList, boolean modified){
 		return DiscreteMI.informationGain(partitionUsingCellucciMethod(xList,yList,modified), 1);
 	}
	
 	public static double IGUsingCellucciMethod(List<Double> xList, List<Double> yList, boolean modified){
 		return DiscreteMI.informationGain(partitionUsingCellucciMethod(xList,yList,modified), 1);
 	}
 	
	public static double MIUsingCellucciMethod(List<Double> xList, List<Double> yList, boolean modified){
		return DiscreteMI.mutualInformation(partitionUsingCellucciMethod(xList,yList,modified));
	}
	
	public static double MIUsingCellucciMethod(double[] xList, double[] yList, boolean modified){
		return DiscreteMI.mutualInformation(partitionUsingCellucciMethod(xList,yList,modified));
	}
	
 	public static double MIUsingCellucciMethod(List<Double> xList, List<Double> yList, 
 			List<Double> zList, boolean modified){
 		return DiscreteMI.mutualInformation(partitionUsingCellucciMethod(xList,yList,zList, modified));
 	}
 	
 	public static double ConditionalMIUsingCellucciMethod(double[] xList, double[] yList, double[] zList,
 			boolean modified){ 		 		
 		return DiscreteMI.conditionalMutualInformation(
				partitionUsingCellucciMethod(xList,yList,zList, modified), 2);
 	}
 	
	public static double ConditionalMIUsingCellucciMethod(
			List<Double> xList, List<Double> yList, List<Double> zList, boolean modified){		
		return DiscreteMI.conditionalMutualInformation(
				partitionUsingCellucciMethod(xList,yList,zList, modified), 2);
	}
	
	public static double[][] partitionUsingCellucciMethod(double[] x, double[] y, 
			boolean modified){
		List<Double> xList = new ArrayList<Double>();
		List<Double> yList = new ArrayList<Double>();
		for(double d:x) xList.add(d);
		for(double d:y) yList.add(d);		
		return partitionUsingCellucciMethod(xList, yList, modified);
	}
	
	public static double[][] partitionUsingCellucciMethod(List<Double> xList, List<Double> yList, 
			boolean modified){
		List<Double> originalXList = new ArrayList<Double>();
		List<Double> originalYList = new ArrayList<Double>();
		List<Double> sortedXList = new ArrayList<Double>();
		List<Double> sortedYList = new ArrayList<Double>();
		for(int i = 0; i < xList.size(); i++){
			if(xList.get(i).isNaN() || xList.get(i).isInfinite() ||
					yList.get(i).isNaN() || yList.get(i).isInfinite()){
				continue;
			}
			originalXList.add(xList.get(i));
			originalYList.add(yList.get(i));
			sortedXList.add(xList.get(i));
			sortedYList.add(yList.get(i));
		}		
		Collections.sort(sortedXList);
		Collections.sort(sortedYList);
		
		int divisor;
		if(modified){
			divisor = (int)Math.sqrt(originalXList.size());
			if(divisor < 5) divisor = 5;
		}else{
			divisor = 5;
		}		
		int Ne = (int) Math.sqrt(originalXList.size() / divisor);		
		double[] xBounds = getBounds(sortedXList, Ne);
		double[] yBounds = getBounds(sortedYList, Ne);
		double[][] XY = new double[Ne][Ne];
		for(int i = 0; i < originalXList.size(); i++){
			int xIndex = getBoundedBy(originalXList.get(i), xBounds);
			int yIndex = getBoundedBy(originalYList.get(i), yBounds);
			XY[xIndex][yIndex]++;
		}		
		return XY;
	}
	
	public static void main(String[] args){
		double d = Double.NaN;
		if(Double.isNaN(d)) System.out.println("True");
		System.out.println(d);
		d = Double.NEGATIVE_INFINITY;
		if(Double.isInfinite(d)) System.out.println("True");
		System.out.println(d);
		d = Double.POSITIVE_INFINITY;
		System.out.println(d);
	}
	
	public static double[][][] partitionUsingCellucciMethod(double[] x, double[] y, double[] z,
			boolean modified){
		List<Double> xList = new ArrayList<Double>();
 		List<Double> yList = new ArrayList<Double>();
 		List<Double> zList = new ArrayList<Double>();
 		
 		for(double d:x) xList.add(d);
 		for(double d:y) yList.add(d);
 		for(double d:z) zList.add(d);
 		
 		return partitionUsingCellucciMethod(xList, yList, zList, modified);
	}
	
	public static double[][][] partitionUsingCellucciMethod(List<Double> xList, List<Double> yList, 
			List<Double> zList, boolean modified){
		List<Double> originalXList = new ArrayList<Double>();
		List<Double> originalYList = new ArrayList<Double>();
		List<Double> originalZList = new ArrayList<Double>();
		List<Double> sortedXList = new ArrayList<Double>();		
		List<Double> sortedYList = new ArrayList<Double>();
		List<Double> sortedZList = new ArrayList<Double>();
		for(int i = 0; i < xList.size(); i++){
			if(xList.get(i).isNaN() || xList.get(i).isInfinite() ||
					yList.get(i).isNaN() || yList.get(i).isInfinite() ||
					zList.get(i).isNaN() || zList.get(i).isInfinite()){
				continue;
			}
			originalXList.add(xList.get(i));
			originalYList.add(yList.get(i));
			originalZList.add(zList.get(i));
			sortedXList.add(xList.get(i));
			sortedYList.add(yList.get(i));
			sortedZList.add(zList.get(i));
		}		
		Collections.sort(sortedXList);
		Collections.sort(sortedYList);
		Collections.sort(sortedZList);
		
		int Ne;
		if(modified){
			int divisor = (int) Math.pow(originalXList.size(), 1.0/3);
			if(divisor < 5) divisor = 5;
			Ne = (int) Math.pow(originalXList.size() / divisor, 1.0 / 3);			
		}else{
			Ne = (int) Math.pow(originalXList.size() / 5.0, 1.0 / 3);			
		}
		
		double[] xBounds = getBounds(sortedXList, Ne);
		double[] yBounds = getBounds(sortedYList, Ne);
		double[] zBounds = getBounds(sortedZList, Ne);
		double[][][] XYZ = new double[Ne][Ne][Ne];
		for(int i = 0; i < originalXList.size(); i++){
			int xIndex = getBoundedBy(originalXList.get(i), xBounds);
			int yIndex = getBoundedBy(originalYList.get(i), yBounds);
			int zIndex = getBoundedBy(originalZList.get(i), zBounds);
			XYZ[xIndex][yIndex][zIndex]++;
		}
		return XYZ;		
	}
		
	private static int getBoundedBy(double value, double[] bounds){
		/*
		 * Bounds must be sorted with increasing value
		 */
		for(int i = 0; i < bounds.length; i++){
			if(value <= bounds[i]){
				return i;
			}
		}
		return bounds.length;
	}
	
	private static double[] getBounds(List<Double> sortedList, int Ne){
		double[] bounds = new double[Ne - 1];
		int j = 0;
		int expected = sortedList.size() / Ne;
		int remainder = sortedList.size() % Ne;
		for(int i = 0; i < bounds.length; i++){
			int count = 0;
			for(; j < sortedList.size(); j++){
				count++;
				if((i >= remainder && count == expected) || count == expected + 1) break;				
			}
			bounds[i] = sortedList.get(j);
		}
		return bounds;
	}
	
	public static double pValueUsingCellucciMethod(List<Double> xList, List<Double> yList){
		double[][] XY = partitionUsingCellucciMethod(xList,yList,true);
		double[] X = DiscreteMI.sumOver(XY, 1);
		double[] Y = DiscreteMI.sumOver(XY, 0);
		double chiSquare = 0.0;
		for(int i = 0; i < XY.length; i++){
			for(int j = 0; j < XY[0].length; j++){
				double expected = X[i] * Y[j] / xList.size();
				if(expected != 0) chiSquare += Math.pow(XY[i][j] - expected, 2.0) / expected;
			}
		}
		double df = (XY.length - 1) * (XY[0].length - 1);	
		return 1 - Gamma.incompleteGamma(df/2.0, chiSquare/2.0);
	}

}
