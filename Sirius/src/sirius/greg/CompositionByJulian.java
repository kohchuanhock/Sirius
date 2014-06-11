package sirius.greg;

import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Utils;

public class CompositionByJulian {
	public static void main(String[] args){
		//W C F I Y V L H M A T R G Q S N P D E K
		int wCount = 0, cCount = 0, fCount = 0, iCount = 0, yCount = 0, vCount = 0, lCount = 0, hCount = 0, mCount = 0, aCount = 0,
			tCount = 0, rCount = 0, gCount = 0, qCount = 0, sCount = 0, nCount = 0, pCount = 0, dCount = 0, eCount = 0, kCount = 0,
			xCount = 0; 
		String fileLocation = Utils.selectFile("Please select file to compute its Composition (FASTA file)");
		double totalSequenceLength = 0.0;
		FastaFileReader fastaReader = new FastaFileReader(fileLocation);
		for(FastaFormat ff:fastaReader.getData()){
			for(char c:ff.getSequence().toCharArray()){
				totalSequenceLength++;
				switch(c){
				case 'W': wCount++; break;
				case 'C': cCount++; break;
				case 'F': fCount++; break;
				case 'I': iCount++; break;
				case 'Y': yCount++; break;
				case 'V': vCount++; break;
				case 'L': lCount++; break;
				case 'H': hCount++; break;
				case 'M': mCount++; break;
				case 'A': aCount++; break;
				case 'T': tCount++; break;
				case 'R': rCount++; break;
				case 'G': gCount++; break;
				case 'Q': qCount++; break;
				case 'S': sCount++; break;
				case 'N': nCount++; break;
				case 'P': pCount++; break;
				case 'D': dCount++; break;
				case 'E': eCount++; break;
				case 'K': kCount++; break;
				case 'X': xCount++; break;
				default: throw new Error("Unhandled Character: " + c);
				}
			}
		}
		System.out.println("TotalSequenceLength: " + totalSequenceLength);
		System.out.println("W: " + wCount + ", " + wCount/totalSequenceLength);
		System.out.println("C: " + cCount + ", " + cCount/totalSequenceLength);
		System.out.println("F: " + fCount + ", " + fCount/totalSequenceLength);
		System.out.println("I: " + iCount + ", " + iCount/totalSequenceLength);
		System.out.println("Y: " + yCount + ", " + yCount/totalSequenceLength);
		System.out.println("V: " + vCount + ", " + vCount/totalSequenceLength);
		System.out.println("L: " + lCount + ", " + lCount/totalSequenceLength);
		System.out.println("H: " + hCount + ", " + hCount/totalSequenceLength);
		System.out.println("M: " + mCount + ", " + mCount/totalSequenceLength);
		System.out.println("A: " + aCount + ", " + aCount/totalSequenceLength);
		System.out.println("T: " + tCount + ", " + tCount/totalSequenceLength);
		System.out.println("R: " + rCount + ", " + rCount/totalSequenceLength);
		System.out.println("G: " + gCount + ", " + gCount/totalSequenceLength);
		System.out.println("Q: " + qCount + ", " + qCount/totalSequenceLength);
		System.out.println("S: " + sCount + ", " + sCount/totalSequenceLength);
		System.out.println("N: " + nCount + ", " + nCount/totalSequenceLength);
		System.out.println("P: " + pCount + ", " + pCount/totalSequenceLength);
		System.out.println("D: " + dCount + ", " + dCount/totalSequenceLength);
		System.out.println("E: " + eCount + ", " + eCount/totalSequenceLength);
		System.out.println("K: " + kCount + ", " + kCount/totalSequenceLength);		
		System.out.println("X: " + xCount + ", " + xCount/totalSequenceLength);		
	}
}
