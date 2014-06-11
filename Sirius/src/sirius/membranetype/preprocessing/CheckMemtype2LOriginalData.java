package sirius.membranetype.preprocessing;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Utils;

public class CheckMemtype2LOriginalData {
	/*
	 * Check to see the integrity of Memtype Original Data
	 * Note: It mainly involves a NonMembrane with Membrane type because over the years, it could be that the nonMembrane is identified
	 * as a Membrane protein
	 */
	public static void main(String[] args){
		String[] memtypeString = new String[9];
		memtypeString[0] = "NonMembrane";
		memtypeString[1] = "TypeI";
		memtypeString[2] = "TypeII";
		memtypeString[3] = "TypeIII";
		memtypeString[4] = "TypeIV";
		memtypeString[5] = "MultiPass";
		memtypeString[6] = "LipidAnchor";
		memtypeString[7] = "GPIAnchor";
		memtypeString[8] = "Peripheral";	
		
		String dir1 = Utils.selectDirectory("Select Train Dir");
		String dir2 = Utils.selectDirectory("Select Test Dir");
		Hashtable<String, List<FastaFormat>> name2FastaList = new Hashtable<String, List<FastaFormat>>();
		for(String s:memtypeString){
			name2FastaList.put("Train_" + s, FastaFileReader.readFastaFile(dir1 + s + ".fasta"));
			name2FastaList.put("Test_" + s, FastaFileReader.readFastaFile(dir2 + s + ".fasta"));
		}
		HashSet<String> alreadyAppearedSet = new HashSet<String>();
		for(String n1:name2FastaList.keySet()){
			alreadyAppearedSet.add(n1);
			for(String n2:name2FastaList.keySet()){
				if(alreadyAppearedSet.contains(n2)) continue;//prevents repeating
				System.out.println("Comparing " + n1 + " (" + name2FastaList.get(n1).size() + ") against " + n2 + " (" + 
						name2FastaList.get(n2).size() + ")");
				int count = 0;
				for(FastaFormat fx:name2FastaList.get(n1)){
					for(FastaFormat fy:name2FastaList.get(n2)){
						count++;
						if(fx.getSequence().equals(fy.getSequence())){
							System.out.println("===============================================");
							System.out.println(fx.getHeader() + " OR " + fy.getHeader());
							System.out.println("===============================================");
						}
					}
				}
				if(name2FastaList.get(n1).size() * name2FastaList.get(n2).size() != count){
					throw new Error("ERROR!");
				}
				System.out.println("Comparison: " + count);
			}
		}
	}
}
