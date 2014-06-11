package sirius.utils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/*
 * Information Theory
 */
public class DiscreteMI {
	public static double[] sumOver(double[][] binProb, int sumOverIndex){
		double[] newBinProb = null;
		switch(sumOverIndex){
		case 0: //Sum over X
			newBinProb = new double[binProb[0].length];
			for(int y = 0; y < binProb[0].length; y++){
				BigDecimal sum = new BigDecimal(0.0);			
				for(int x = 0; x < binProb.length; x++){					
					sum = sum.add(new BigDecimal(binProb[x][y]));
				}				
				newBinProb[y] = sum.doubleValue();				
			}			
			break;
		case 1: //Sum over Y			
			newBinProb = new double[binProb.length];
			for(int x = 0; x < binProb.length; x++){			
				BigDecimal sum = new BigDecimal(0.0);			
				for(int y = 0; y < binProb[0].length; y++){
					sum = sum.add(new BigDecimal(binProb[x][y]));
				}
				newBinProb[x] = sum.doubleValue();
			}
			break;
		default: throw new Error("Unhandled sumOverIndex: " + sumOverIndex);
		}
		return newBinProb;
	}
	
	public static double[][] sumOver(double[][][] binProb, int sumOverIndex){
		double[][] newBinProb = null;
		switch(sumOverIndex){
		case 0: //Sum over X
			newBinProb = new double[binProb[0].length][binProb[0][0].length];
			for(int y = 0; y < binProb[0].length; y++){
				for(int z = 0; z < binProb[0][0].length; z++){
					BigDecimal sum = new BigDecimal(0.0);
					for(int x = 0; x < binProb.length; x++){
						sum = sum.add(new BigDecimal(binProb[x][y][z]));
					}
					newBinProb[y][z] = sum.doubleValue();
				}
			}
			break;
		case 1: //Sum over Y
			newBinProb = new double[binProb.length][binProb[0][0].length];
			for(int x = 0; x < binProb.length; x++){
				for(int z = 0; z < binProb[0][0].length; z++){
					BigDecimal sum = new BigDecimal(0.0);
					for(int y = 0; y < binProb[0].length; y++){					
						sum = sum.add(new BigDecimal(binProb[x][y][z]));
					}
					newBinProb[x][z] = sum.doubleValue();
				}
			}
			break;
		case 2: //Sum over Z
			newBinProb = new double[binProb.length][binProb[0].length];
			for(int x = 0; x < binProb.length; x++){
				for(int y = 0; y < binProb[0].length; y++){
					BigDecimal sum = new BigDecimal(0.0);					
					for(int z = 0; z < binProb[0][0].length; z++){
						sum = sum.add(new BigDecimal(binProb[x][y][z]));
					}
					newBinProb[x][y] = sum.doubleValue();
				}
			}
			break;
		default: throw new Error("Unhandled sumOverIndex: " + sumOverIndex);
		}		
		return newBinProb;
	}
	
	public static double entropy(double[] binProb){		
		double sum = 0.0;
		for(int x = 0; x < binProb.length; x++){
			sum += binProb[x];
		}		
		double entropy = 0.0;
		for(int x = 0; x < binProb.length; x++){
			double p = binProb[x] / sum;
			if(p != 0.0){
				entropy += p * Math.log(p);
			}
		}
		if(entropy != 0.0)
			entropy *= -1;
		return entropy / Math.log(2);
	}			
	
	public static double entropy(double[][] binProb){
		double sum = 0.0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				sum += binProb[x][y];
			}			
		}
		double entropy = 0.0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				double p = binProb[x][y] / sum;
				if(p != 0){
					entropy += p * Math.log(p);
				}
			}
		}		
		if(entropy != 0.0)
			entropy *= -1;
		return entropy / Math.log(2);
	}
	
	public static double entropy(double[][][] binProb){
		double sum = 0.0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				for(int z = 0; z < binProb[x][y].length; z++){
					sum += binProb[x][y][z];
				}
			}			
		}
		double entropy = 0.0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				for(int z = 0; z < binProb[x][y].length; z++){
					double p = binProb[x][y][z] / sum;
					if(p != 0)
						entropy += p * Math.log(p);					
				}
			}
		}		
		if(entropy != 0.0)
			entropy *= -1;
		return entropy / Math.log(2);
	}
	
	public static double conditionalEntropy(double[][] binProb, int givenIndex){
		double sum = 0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				sum += binProb[x][y];
			}			
		}
		double entropy = 0.0;
		switch(givenIndex){
		case 0: //Given X
			double[] xProb = sumOver(binProb, 1);
			for(int x = 0; x < binProb.length; x++){
				double px = xProb[x];
				if(px == 0.0) continue;
				for(int y = 0; y < binProb[0].length; y++){				
					double pxy = binProb[x][y];
					if(pxy != 0.0) entropy += pxy * Math.log(px / pxy);
				}
			}			
			break;
		case 1: //Given Y 
			double[] yProb = sumOver(binProb, 0);
			for(int y = 0; y < binProb[0].length; y++){			
				double py = yProb[y];
				if(py == 0.0) continue;
				for(int x = 0; x < binProb.length; x++){
					double pxy = binProb[x][y];
					if(pxy != 0.0) entropy += pxy * Math.log(py / pxy);
				}
			}			
			break;			
		default: throw new Error("Unhandled givenIndex: " + givenIndex);
		}
		return entropy / (Math.log(2) * sum);
	}		
	
	public static double mutualInformation(double[][] binProb){
		/*
		 * MI(X;Y)
		 */
		if(binProb == null) return -1;
		double sum = 0.0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				sum += binProb[x][y];
			}			
		}
		double mutualInformation = 0.0;			
		double[] xProb = sumOver(binProb, 1);
		double[] yProb = sumOver(binProb, 0);
		for(int x = 0; x < binProb.length; x++){
			double px = xProb[x];			
			if(px == 0.0) continue;
			for(int y = 0; y < binProb[0].length; y++){
				double py = yProb[y];				
				double pxy = binProb[x][y];
				if(pxy != 0.0 && py != 0.0) mutualInformation += pxy * Math.log((pxy * sum) / (px * py));
			}
		}		
		return mutualInformation / (Math.log(2) * sum);
	}
	
	public static double mutualInformation(double[][][] binProb){
		/*
		 * MI(X,Y;Z)
		 */	
		double sum = 0.0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				for(int z = 0; z < binProb[x][y].length; z++){
					sum += binProb[x][y][z];
				}
			}			
		}
		double mutualInformation = 0.0;			
		double[][] xyProb = sumOver(binProb, 2);
		double[] zProb = sumOver(sumOver(binProb, 0),0);
		for(int x = 0; x < binProb.length; x++){			
			for(int y = 0; y < binProb[0].length; y++){
				double pxy = xyProb[x][y];
				if(pxy == 0.0) continue;
				for(int z = 0; z < binProb[x][y].length; z++){								
					double pxyz = binProb[x][y][z];
					double pz = zProb[z];
					if(pxyz != 0.0 && pz != 0.0) mutualInformation += pxyz * Math.log((pxyz * sum) / (pxy * pz));
				}
			}
		}
		return mutualInformation / (Math.log(2) * sum);
	}
	
	public static double conditionalMutualInformation(double[][][] binProb, int givenIndex){
		/*
		 * MI(X,Y | Z)
		 * Based on formula given in Wikipedia
		 */
		double sum = 0.0;
		for(int x = 0; x < binProb.length; x++){
			for(int y = 0; y < binProb[x].length; y++){
				for(int z = 0; z < binProb[x][y].length; z++){
					sum += binProb[x][y][z];
				}
			}			
		}
		double cmi = 0.0;
		switch(givenIndex){
		case 0://Given X
			//named yxProb simple to prevent name clash with next case
			double[][] yxProb = sumOver(binProb, 2);
			//named zxProb simple to prevent name clash with next case
			double[][] zxProb = sumOver(binProb, 1);
			double[] xProb = sumOver(yxProb,1);
			for(int x = 0; x < binProb.length; x++){
				if(xProb[x] == 0.0) continue;
				double px = xProb[x];
				for(int y = 0; y < binProb[0].length; y++){			
					if(yxProb[x][y] == 0.0) continue;
					double pyx = yxProb[x][y];
					for(int z = 0; z < binProb[0][0].length; z++){																		
						if(zxProb[x][z] == 0.0 || binProb[x][y][z] == 0.0) continue;						
						double pzx = zxProb[x][z];
						double pxyz = binProb[x][y][z];
						cmi += pxyz * Math.log((px * pxyz) / (pyx * pzx));
					}
				}			
			}		
			break;
		case 1://Given Y
			double[][] xyProb = sumOver(binProb, 2);
			//named zyProb simply to prevent name clash with next case
			double[][] zyProb = sumOver(binProb, 0);
			double[] yProb = sumOver(xyProb,0);			
			for(int y = 0; y < binProb[0].length; y++){
				if(yProb[y] == 0.0) continue;
				double py = yProb[y];
			for(int z = 0; z < binProb[0][0].length; z++){
				if(zyProb[y][z] == 0.0) continue;
				double pzy = zyProb[y][z];
				for(int x = 0; x < binProb.length; x++){										
						if(xyProb[x][y] == 0.0 || binProb[x][y][z] == 0.0) continue;						
						double pxy = xyProb[x][y];
						double pxyz = binProb[x][y][z];
						cmi += pxyz * Math.log((py * pxyz) / (pxy * pzy));
					}
				}			
			}	
			break;
		case 2://Given Z
			double[][] xzProb = sumOver(binProb, 1);
			double[][] yzProb = sumOver(binProb, 0);
			double[] zProb = sumOver(yzProb,0);			
			for(int z = 0; z < binProb[0][0].length; z++){
				if(zProb[z] == 0.0) continue;
				double pz = zProb[z];
				for(int x = 0; x < binProb.length; x++){
					if(xzProb[x][z] == 0.0) continue;
					double pxz = xzProb[x][z];
					for(int y = 0; y < binProb[0].length; y++){
						if(yzProb[y][z] == 0.0 || binProb[x][y][z] == 0.0) continue;						
						double pyz = yzProb[y][z];
						double pxyz = binProb[x][y][z];
						cmi += pxyz * Math.log((pz * pxyz) / (pxz * pyz));
					}
				}			
			}
			break;
		default: throw new Error("Unhandled givenIndex: " + givenIndex);
		}
		//Change it to base 2
		return cmi / (Math.log(2) * sum);
	}
	
	public static double informationGain(double[][] binProb, int givenIndex){
		/*
		 * IG(X|Y) = H(X) - H(X|Y)
		 */
		double[] xProb = sumOver(binProb, givenIndex);
		double hx = entropy(xProb);
		double hxy = conditionalEntropy(binProb, givenIndex);
		return hx - hxy;
	}
	
	public static double SU(double[][] binProb){
		/*
		 * SU(X,Y) = 2(IG(X|Y) / (H(X) + H(Y)))
		 */		
		double MIxy = mutualInformation(binProb);
		double[] xProb = sumOver(binProb, 0);
		double[] yProb = sumOver(binProb, 1);
		double hx = entropy(xProb);
		double hy = entropy(yProb);
		return 2*(MIxy / (hx + hy));
	}
	
	public static double normalizedMutualInformation(double[][][] binProb){
		/*
		 * Normalized Mutual Information
		 * NMI(X, Y) = MI(X,Y,Z) / min(H(X), H(Y), H(Z))
		 */
		double MIxyz = mutualInformation(binProb);
		double[][] xyProb = sumOver(binProb, 2);
		double[][] xzProb = sumOver(binProb, 1);
		double[][] yzProb = sumOver(binProb, 0);
		double hxy = entropy(xyProb);
		double hxz = entropy(xzProb);		
		double hyz = entropy(yzProb);
		return MIxyz / Math.min(hxy, Math.min(hxz,hyz));
	}
	
	public static double normalizedMutualInformation(double[][] binProb){
		/*
		 * Normalized Mutual Information
		 * NMI(X, Y) = MI(X,Y) / min(H(X), H(Y))
		 */
		double MIxy = mutualInformation(binProb);
		double[] xProb = sumOver(binProb, 1);
		double[] yProb = sumOver(binProb, 0);		
		double hx = entropy(xProb);
		double hy = entropy(yProb);
		return MIxy / Math.min(hx, hy);
	}
	
	public static double normalizedConditionalMutualInformation(double[][][] binProb, int givenIndex){
		/*
		 * Normalized Conditional Mutual Information
		 * NCMI(X, Y | Z) = CMI(X, Y | Z) / min(H(X|Z), H(Y|Z))		 
		 */
		double cmi = conditionalMutualInformation(binProb, givenIndex);
		if(cmi == 0.0){
			return 0.0;
		}
		double[][] xyProb;
		double[][] xzProb;
		double[][] yzProb;
		switch(givenIndex){
		//Given X
		case 0:
			xyProb = sumOver(binProb, 2);
			xzProb = sumOver(binProb, 1);
			double hyx = conditionalEntropy(xyProb, 0);
			double hzx = conditionalEntropy(xzProb, 0);
			return cmi / Math.min(hyx, hzx);
		//Given Y
		case 1:
			xyProb = sumOver(binProb, 2);
			yzProb = sumOver(binProb, 0);
			double hxy = conditionalEntropy(xyProb, 1);
			double hzy = conditionalEntropy(yzProb, 0);
			return cmi / Math.min(hxy, hzy);
		//Given Z
		case 2:
			xzProb = sumOver(binProb, 1);
			yzProb = sumOver(binProb, 0);
			double hxz = conditionalEntropy(xzProb, 1);
			double hyz = conditionalEntropy(yzProb, 1);
			return cmi / Math.min(hxz, hyz);
		default: throw new Error("Unhandled Case - givenIndex: " + givenIndex);
		}		
	}

	public static double[][] convert2BinProb(List<?> xList, List<?> yList, 
			boolean distributeMissing){				
		Set<Object> xSet = new HashSet<Object>();
		Set<Object> ySet = new HashSet<Object>();
		xSet.addAll(xList);
		ySet.addAll(yList);
		Hashtable<Object, Integer> xChar2Index = new Hashtable<Object, Integer>();
		Hashtable<Object, Integer> yChar2Index = new Hashtable<Object, Integer>();
		int index = 0;		
		for(Object d:xSet) xChar2Index.put(d, index++);
		index = 0;
		for(Object d:ySet) yChar2Index.put(d, index++);
		double[][] XY = new double[xSet.size()][ySet.size()];
		for(int x = 0; x < xList.size(); x++){						
			XY[xChar2Index.get(xList.get(x))][yChar2Index.get(yList.get(x))]++;
		}
		if(distributeMissing){	
			if(yChar2Index.containsKey('?')){
				for(int x = 0; x < XY.length; x++){
					double missingTotal = 0.0;
					double total = 0.0;
					for(int y = 0; y < XY[x].length; y++){
						if(yChar2Index.get('?') == y){
							missingTotal += XY[x][y];
							XY[x][y] = 0;
						}else total += XY[x][y];						
					}
					if(total == 0.0) total = XY[x].length;
					for(int y = 0; y < XY[x].length; y++) XY[x][y] += XY[x][y] / total * missingTotal;					
				}
			}
			
			if(xChar2Index.containsKey('?')){
				for(int y = 0; y < XY[0].length; y++){
					double missingTotal = 0.0;
					double total = 0.0;
					for(int x = 0; x < XY.length; x++){
						if(xChar2Index.get('?') == x){
							missingTotal += XY[x][y];
							XY[x][y] = 0;
						}else total += XY[x][y];						
					}
					if(total == 0.0) total = XY.length;
					for(int x = 0; x < XY.length; x++) XY[x][y] += XY[x][y] / total * missingTotal;					
				}
			}
		}		
		return XY;
	}

	public static void main(String[] args){		
		//testDiscreteMI();
		XOR();
		NOT();
		AND();
		ADDITIVE();
		CAUSATIVE();
		IMPLY();
		OR();
	}
	
	public static void testDiscreteMI(){
		double[][][] abc = {{{0.2,0.1,0.15},{0.0,0.3,0.25}}, {{0.1,0.35,0.32},{0.0,0.32,0.33}}};
		double[][] ab = sumOver(abc, 2);
		double[][] ac = sumOver(abc, 1);
		double[][] bc = sumOver(abc, 0);		
		double[] a = sumOver(ab, 1);		
		double[] b = sumOver(bc, 1);		
		double[] c = sumOver(bc, 0);
		System.out.println("a: ");
		for(double d:a) System.out.print(d + ",");
		System.out.println();
		System.out.println("b: ");
		for(double d:b) System.out.print(d + ",");
		System.out.println();
		System.out.println("c: ");
		for(double d:c) System.out.print(d + ",");
		System.out.println();
		System.out.println("ab: ");
		for(double[] d1:ab){
			for(double d2:d1){
				System.out.print(d2 + ",");
			}
			System.out.println();
		}
		System.out.println("ac: ");
		for(double[] d1:ac){
			for(double d2:d1){
				System.out.print(d2 + ",");
			}
			System.out.println();
		}
		System.out.println("bc: ");
		for(double[] d1:bc){
			for(double d2:d1){
				System.out.print(d2 + ",");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("H(a): " + DiscreteMI.entropy(a));
		System.out.println("H(b): " + DiscreteMI.entropy(b));
		System.out.println("H(c): " + DiscreteMI.entropy(c));
		System.out.println();
		System.out.println("H(a,b): " + DiscreteMI.entropy(ab));
		System.out.println("H(a|b): " + DiscreteMI.conditionalEntropy(ab,1));
		System.out.println("H(b|a): " + DiscreteMI.conditionalEntropy(ab,0));
		System.out.println();
		System.out.println("H(a,c): " + DiscreteMI.entropy(ac));
		System.out.println("H(a|c): " + DiscreteMI.conditionalEntropy(ac,1));
		System.out.println("H(c|a): " + DiscreteMI.conditionalEntropy(ac,0));
		System.out.println();
		System.out.println("H(b,c): " + DiscreteMI.entropy(bc));
		System.out.println("H(b|c): " + DiscreteMI.conditionalEntropy(bc,1));
		System.out.println("H(c|b): " + DiscreteMI.conditionalEntropy(bc,0));
		System.out.println();
		System.out.println("H(a,b,c): " + DiscreteMI.entropy(abc));
		System.out.println();		
		System.out.println("MI(a,b): " + DiscreteMI.mutualInformation(ab));
		System.out.println("MI(a,c): " + DiscreteMI.mutualInformation(ac));
		System.out.println("MI(b,c): " + DiscreteMI.mutualInformation(bc));
		System.out.println();
		System.out.println("MI(a,b,c): " + DiscreteMI.mutualInformation(abc));
		System.out.println("MI(a,b|c): " + DiscreteMI.conditionalMutualInformation(abc, 2));
		System.out.println("MI(a,b|c): " + 
				(-DiscreteMI.entropy(abc) + DiscreteMI.entropy(ac) + DiscreteMI.entropy(bc) - 
				DiscreteMI.entropy(c)));
		System.out.println("MI(a,c|b): " + DiscreteMI.conditionalMutualInformation(abc, 1));
		System.out.println("MI(a,c|b): " + 
				(-DiscreteMI.entropy(abc) + DiscreteMI.entropy(bc) + DiscreteMI.entropy(ab) - 
				DiscreteMI.entropy(b)));
		System.out.println("MI(b,c|a): " + DiscreteMI.conditionalMutualInformation(abc, 0));
		System.out.println("MI(b,c|a): " + 
				(- DiscreteMI.entropy(abc) + DiscreteMI.entropy(ab) + DiscreteMI.entropy(ac) - 
				DiscreteMI.entropy(a)));		
		System.out.println("IG(X|Y): " + DiscreteMI.informationGain(ab, 1));
		System.out.println("IG(Y|X): " + DiscreteMI.informationGain(ab, 0));
		System.out.println("SU(X,Y): " + DiscreteMI.SU(ab));
	}
	
	public static void XOR(){
		/*
		 * Mutual Information of XOR boolean table
		 */			
		double[][][] XYO = new double[2][2][2];
		//
		XYO[0][0][0] = 1;
		XYO[0][1][0] = 0;
		XYO[1][0][0] = 0;
		XYO[1][1][0] = 1;
		//		
		XYO[0][0][1] = 1 - XYO[0][0][0];
		XYO[0][1][1] = 1 - XYO[0][1][0];
		XYO[1][0][1] = 1 - XYO[1][0][0];
		XYO[1][1][1] = 1 - XYO[1][1][0];
		double[][] XY = DiscreteMI.sumOver(XYO, 2);
		double[][] XO = DiscreteMI.sumOver(XYO, 1);
		double[][] YO = DiscreteMI.sumOver(XYO, 0);
		System.out.println("XOR");
		System.out.println("===============================================================");
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				for(int z = 0; z < 2; z++){
					if(XYO[x][y][z] == 1){
						System.out.println("X = " + x + ", Y = " + y + ", O = " + z);
					}
				}
			}
		}
		System.out.println();
		System.out.println("MI(X,Y): " + DiscreteMI.mutualInformation(XY));		
		System.out.println("MI(X,O): " + DiscreteMI.mutualInformation(XO));
		System.out.println("MI(Y,O): " + DiscreteMI.mutualInformation(YO));
		System.out.println();
		System.out.println("NMI(X,Y): " + DiscreteMI.normalizedMutualInformation(XY));
		System.out.println("NMI(X,O): " + DiscreteMI.normalizedMutualInformation(XO));
		System.out.println("NMI(Y,O): " + DiscreteMI.normalizedMutualInformation(YO));
		System.out.println();
		System.out.println("MI(X,Y,O): " + DiscreteMI.mutualInformation(XYO));
		System.out.println("MI(X,Y|O): " + DiscreteMI.conditionalMutualInformation(XYO, 2));
		System.out.println("MI(Y,O|X): " + DiscreteMI.conditionalMutualInformation(XYO, 0));
		System.out.println("MI(X,O|Y): " + DiscreteMI.conditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("NMI(X,Y,O): " + DiscreteMI.normalizedMutualInformation(XYO));
		System.out.println("NMI(X,Y|O): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 2));
		System.out.println("NMI(Y,O|X): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 0));
		System.out.println("NMI(X,O|Y): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("===============================================================");
		System.out.println();
	}
	
	public static void NOT(){
		/*
		 * Mutual Information of NOT boolean table
		 */			
		double[][][] XYO = new double[2][2][2];
		//
		XYO[0][0][0] = 0;		
		XYO[0][1][0] = 0;
		XYO[1][0][0] = 1;				
		XYO[1][1][0] = 1;
		//		
		XYO[0][0][1] = 1 - XYO[0][0][0];
		XYO[0][1][1] = 1 - XYO[0][1][0];
		XYO[1][0][1] = 1 - XYO[1][0][0];
		XYO[1][1][1] = 1 - XYO[1][1][0];
		double[][] XY = DiscreteMI.sumOver(XYO, 2);
		double[][] XO = DiscreteMI.sumOver(XYO, 1);
		double[][] YO = DiscreteMI.sumOver(XYO, 0);		
		System.out.println("NOT");
		System.out.println("===============================================================");
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				for(int z = 0; z < 2; z++){
					if(XYO[x][y][z] == 1){
						System.out.println("X = " + x + ", Y = " + y + ", O = " + z);
					}
				}
			}
		}
		System.out.println();
		System.out.println("MI(X,Y): " + DiscreteMI.mutualInformation(XY));
		System.out.println("MI(X,O): " + DiscreteMI.mutualInformation(XO));
		System.out.println("MI(Y,O): " + DiscreteMI.mutualInformation(YO));
		System.out.println();
		System.out.println("NMI(X,Y): " + DiscreteMI.normalizedMutualInformation(XY));
		System.out.println("NMI(X,O): " + DiscreteMI.normalizedMutualInformation(XO));
		System.out.println("NMI(Y,O): " + DiscreteMI.normalizedMutualInformation(YO));
		System.out.println();
		System.out.println("MI(X,Y,O): " + DiscreteMI.mutualInformation(XYO));
		System.out.println("MI(X,Y|O): " + DiscreteMI.conditionalMutualInformation(XYO, 2));
		System.out.println("MI(Y,O|X): " + DiscreteMI.conditionalMutualInformation(XYO, 0));
		System.out.println("MI(X,O|Y): " + DiscreteMI.conditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("NMI(X,Y,O): " + DiscreteMI.normalizedMutualInformation(XYO));
		System.out.println("NMI(X,Y|O): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 2));
		System.out.println("NMI(Y,O|X): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 0));
		System.out.println("NMI(X,O|Y): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("===============================================================");
		System.out.println();
	}
	
	public static void AND(){
		/*
		 * Mutual Information of AND boolean table
		 */			
		double[][][] XYO = new double[2][2][2];
		//
		XYO[0][0][0] = 1;
		XYO[0][1][0] = 1;
		XYO[1][0][0] = 1;
		XYO[1][1][0] = 0;
		//		
		XYO[0][0][1] = 1 - XYO[0][0][0];
		XYO[0][1][1] = 1 - XYO[0][1][0];
		XYO[1][0][1] = 1 - XYO[1][0][0];
		XYO[1][1][1] = 1 - XYO[1][1][0];
		double[][] XY = DiscreteMI.sumOver(XYO, 2);
		double[][] XO = DiscreteMI.sumOver(XYO, 1);
		double[][] YO = DiscreteMI.sumOver(XYO, 0);		
		System.out.println("AND");		
		System.out.println("===============================================================");
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				for(int z = 0; z < 2; z++){
					if(XYO[x][y][z] == 1){
						System.out.println("X = " + x + ", Y = " + y + ", O = " + z);
					}
				}
			}
		}
		System.out.println();
		System.out.println("MI(X,Y): " + DiscreteMI.mutualInformation(XY));
		System.out.println("MI(X,O): " + DiscreteMI.mutualInformation(XO));
		System.out.println("MI(Y,O): " + DiscreteMI.mutualInformation(YO));
		System.out.println();
		System.out.println("NMI(X,Y): " + DiscreteMI.normalizedMutualInformation(XY));
		System.out.println("NMI(X,O): " + DiscreteMI.normalizedMutualInformation(XO));
		System.out.println("NMI(Y,O): " + DiscreteMI.normalizedMutualInformation(YO));
		System.out.println();
		System.out.println("MI(X,Y,O): " + DiscreteMI.mutualInformation(XYO));
		System.out.println("MI(X,Y|O): " + DiscreteMI.conditionalMutualInformation(XYO, 2));
		System.out.println("MI(Y,O|X): " + DiscreteMI.conditionalMutualInformation(XYO, 0));
		System.out.println("MI(X,O|Y): " + DiscreteMI.conditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("NMI(X,Y,O): " + DiscreteMI.normalizedMutualInformation(XYO));
		System.out.println("NMI(X,Y|O): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 2));
		System.out.println("NMI(Y,O|X): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 0));
		System.out.println("NMI(X,O|Y): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("===============================================================");
		System.out.println();
	}
	
	public static void IMPLY(){
		/*
		 * Mutual Information of IMPLY boolean table
		 */			
		double[][][] XYO = new double[2][2][2];
		//
		XYO[0][0][0] = 0;
		XYO[0][1][0] = 0;
		XYO[1][0][0] = 1;
		XYO[1][1][0] = 0;
		//		
		XYO[0][0][1] = 1 - XYO[0][0][0];
		XYO[0][1][1] = 1 - XYO[0][1][0];
		XYO[1][0][1] = 1 - XYO[1][0][0];
		XYO[1][1][1] = 1 - XYO[1][1][0];
		double[][] XY = DiscreteMI.sumOver(XYO, 2);
		double[][] XO = DiscreteMI.sumOver(XYO, 1);
		double[][] YO = DiscreteMI.sumOver(XYO, 0);		
		System.out.println("IMPLY");
		System.out.println("===============================================================");
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				for(int z = 0; z < 2; z++){
					if(XYO[x][y][z] == 1){
						System.out.println("X = " + x + ", Y = " + y + ", O = " + z);
					}
				}
			}
		}
		System.out.println();
		System.out.println("MI(X,Y): " + DiscreteMI.mutualInformation(XY));
		System.out.println("MI(X,O): " + DiscreteMI.mutualInformation(XO));
		System.out.println("MI(Y,O): " + DiscreteMI.mutualInformation(YO));
		System.out.println();
		System.out.println("NMI(X,Y): " + DiscreteMI.normalizedMutualInformation(XY));
		System.out.println("NMI(X,O): " + DiscreteMI.normalizedMutualInformation(XO));
		System.out.println("NMI(Y,O): " + DiscreteMI.normalizedMutualInformation(YO));
		System.out.println();
		System.out.println("MI(X,Y,O): " + DiscreteMI.mutualInformation(XYO));
		System.out.println("MI(X,Y|O): " + DiscreteMI.conditionalMutualInformation(XYO, 2));
		System.out.println("MI(Y,O|X): " + DiscreteMI.conditionalMutualInformation(XYO, 0));
		System.out.println("MI(X,O|Y): " + DiscreteMI.conditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("NMI(X,Y,O): " + DiscreteMI.normalizedMutualInformation(XYO));
		System.out.println("NMI(X,Y|O): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 2));
		System.out.println("NMI(Y,O|X): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 0));
		System.out.println("NMI(X,O|Y): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("===============================================================");
		System.out.println();
	}

	public static void OR(){
		/*
		 * Mutual Information of OR boolean table
		 */			
		double[][][] XYO = new double[2][2][2];
		//
		XYO[0][0][0] = 1;
		XYO[0][1][0] = 0;
		XYO[1][0][0] = 0;
		XYO[1][1][0] = 0;
		//		
		XYO[0][0][1] = 1 - XYO[0][0][0];
		XYO[0][1][1] = 1 - XYO[0][1][0];
		XYO[1][0][1] = 1 - XYO[1][0][0];
		XYO[1][1][1] = 1 - XYO[1][1][0];
		double[][] XY = DiscreteMI.sumOver(XYO, 2);
		double[][] XO = DiscreteMI.sumOver(XYO, 1);
		double[][] YO = DiscreteMI.sumOver(XYO, 0);		
		System.out.println("OR");
		System.out.println("===============================================================");
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				for(int z = 0; z < 2; z++){
					if(XYO[x][y][z] == 1){
						System.out.println("X = " + x + ", Y = " + y + ", O = " + z);
					}
				}
			}
		}
		System.out.println();
		System.out.println("MI(X,Y): " + DiscreteMI.mutualInformation(XY));
		System.out.println("MI(X,O): " + DiscreteMI.mutualInformation(XO));
		System.out.println("MI(Y,O): " + DiscreteMI.mutualInformation(YO));
		System.out.println();
		System.out.println("NMI(X,Y): " + DiscreteMI.normalizedMutualInformation(XY));
		System.out.println("NMI(X,O): " + DiscreteMI.normalizedMutualInformation(XO));
		System.out.println("NMI(Y,O): " + DiscreteMI.normalizedMutualInformation(YO));
		System.out.println();
		System.out.println("MI(X,Y,O): " + DiscreteMI.mutualInformation(XYO));
		System.out.println("MI(X,Y|O): " + DiscreteMI.conditionalMutualInformation(XYO, 2));
		System.out.println("MI(Y,O|X): " + DiscreteMI.conditionalMutualInformation(XYO, 0));
		System.out.println("MI(X,O|Y): " + DiscreteMI.conditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("NMI(X,Y,O): " + DiscreteMI.normalizedMutualInformation(XYO));
		System.out.println("NMI(X,Y|O): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 2));
		System.out.println("NMI(Y,O|X): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 0));
		System.out.println("NMI(X,O|Y): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("===============================================================");
		System.out.println();
	}
	
	public static void CAUSATIVE(){
		/*
		 * Mutual Information of O cause X and O cause Y
		 */			
		double[][][] XYO = new double[2][2][2];
		//
		XYO[0][0][0] = 1;
		XYO[0][0][1] = 0;		
		//
		XYO[0][1][0] = 0;
		XYO[0][1][1] = 0;		
		//
		XYO[1][0][0] = 0;
		XYO[1][0][1] = 0;		
		//	
		XYO[1][1][0] = 0;
		XYO[1][1][1] = 1;		
		//
		double[][] XY = DiscreteMI.sumOver(XYO, 2);
		double[][] XO = DiscreteMI.sumOver(XYO, 1);
		double[][] YO = DiscreteMI.sumOver(XYO, 0);		
		System.out.println("CAUSATIVE: O cause X and O cause Y");
		System.out.println("===============================================================");
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				for(int z = 0; z < 2; z++){
					if(XYO[x][y][z] == 1){
						System.out.println("X = " + x + ", Y = " + y + ", O = " + z);
					}
				}
			}
		}
		System.out.println();
		System.out.println("MI(X,Y): " + DiscreteMI.mutualInformation(XY));
		System.out.println("MI(X,O): " + DiscreteMI.mutualInformation(XO));
		System.out.println("MI(Y,O): " + DiscreteMI.mutualInformation(YO));
		System.out.println();
		System.out.println("NMI(X,Y): " + DiscreteMI.normalizedMutualInformation(XY));
		System.out.println("NMI(X,O): " + DiscreteMI.normalizedMutualInformation(XO));
		System.out.println("NMI(Y,O): " + DiscreteMI.normalizedMutualInformation(YO));
		System.out.println();
		System.out.println("MI(X,Y,O): " + DiscreteMI.mutualInformation(XYO));
		System.out.println("MI(X,Y|O): " + DiscreteMI.conditionalMutualInformation(XYO, 2));
		System.out.println("MI(Y,O|X): " + DiscreteMI.conditionalMutualInformation(XYO, 0));
		System.out.println("MI(X,O|Y): " + DiscreteMI.conditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("NMI(X,Y,O): " + DiscreteMI.normalizedMutualInformation(XYO));
		System.out.println("NMI(X,Y|O): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 2));
		System.out.println("NMI(Y,O|X): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 0));
		System.out.println("NMI(X,O|Y): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("===============================================================");
		System.out.println();
	}

	public static void ADDITIVE(){
		/*
		 * Mutual Information of ADDITIVE boolean table
		 */			
		double[][][] XYO = new double[2][2][3];
		//
		XYO[0][0][0] = 1;
		XYO[0][0][1] = 0;
		XYO[0][0][2] = 0;
		//
		XYO[0][1][0] = 0;
		XYO[0][1][1] = 1;
		XYO[0][1][2] = 0;
		//
		XYO[1][0][0] = 0;
		XYO[1][0][1] = 1;
		XYO[1][0][2] = 0;
		//	
		XYO[1][1][0] = 0;
		XYO[1][1][1] = 0;
		XYO[1][1][2] = 1;
		//
		double[][] XY = DiscreteMI.sumOver(XYO, 2);
		double[][] XO = DiscreteMI.sumOver(XYO, 1);
		double[][] YO = DiscreteMI.sumOver(XYO, 0);		
		System.out.println("ADDITIVE: O = X + Y");
		System.out.println("===============================================================");
		for(int x = 0; x < 2; x++){
			for(int y = 0; y < 2; y++){
				for(int z = 0; z < 2; z++){
					if(XYO[x][y][z] == 1){
						System.out.println("X = " + x + ", Y = " + y + ", O = " + z);
					}
				}
			}
		}
		System.out.println();
		System.out.println("MI(X,Y): " + DiscreteMI.mutualInformation(XY));
		System.out.println("MI(X,O): " + DiscreteMI.mutualInformation(XO));
		System.out.println("MI(Y,O): " + DiscreteMI.mutualInformation(YO));
		System.out.println();
		System.out.println("NMI(X,Y): " + DiscreteMI.normalizedMutualInformation(XY));
		System.out.println("NMI(X,O): " + DiscreteMI.normalizedMutualInformation(XO));
		System.out.println("NMI(Y,O): " + DiscreteMI.normalizedMutualInformation(YO));
		System.out.println();
		System.out.println("MI(X,Y,O): " + DiscreteMI.mutualInformation(XYO));
		System.out.println("MI(X,Y|O): " + DiscreteMI.conditionalMutualInformation(XYO, 2));
		System.out.println("MI(Y,O|X): " + DiscreteMI.conditionalMutualInformation(XYO, 0));
		System.out.println("MI(X,O|Y): " + DiscreteMI.conditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("NMI(X,Y,O): " + DiscreteMI.normalizedMutualInformation(XYO));
		System.out.println("NMI(X,Y|O): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 2));
		System.out.println("NMI(Y,O|X): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 0));
		System.out.println("NMI(X,O|Y): " + DiscreteMI.normalizedConditionalMutualInformation(XYO, 1));
		System.out.println();
		System.out.println("===============================================================");
		System.out.println();
	}
}
