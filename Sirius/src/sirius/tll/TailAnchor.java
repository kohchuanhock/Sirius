package sirius.tll;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sirius.misc.sequencevisualizer.SequenceQuencher;
import sirius.misc.sequencevisualizer.SequenceResult;
import sirius.trainer.features.GenerateArff;
import sirius.utils.Graph;
import sirius.utils.R;
import sirius.utils.RDraw;
import sirius.utils.RPlotGraph;
import sirius.utils.Utils;

public class TailAnchor {
	public static void main(String[] args){
		try{
			String dir = Utils.selectDirectory("Please select Tail Anchor Directory");
			Set<String> peroMembraneSet = TailAnchor.getPeroxisomalMembraneProteins(dir);
			System.out.println("Size: " + peroMembraneSet.size());
			Map<String, String> id2Sequence = TailAnchor.getSequence(peroMembraneSet, dir);
			Set<String> notFound = new HashSet<String>();
			if(peroMembraneSet.size() != id2Sequence.size()){
				for(String s:peroMembraneSet){
					if(id2Sequence.containsKey(s) == false) notFound.add(s);
				}
				for(String s:notFound){
					System.out.println(s);
				}
				System.out.println(peroMembraneSet.size() + "\t" + id2Sequence.size());
				throw new Error("Not all peroxisomal membrane are found!");
			}
			outputSequence(id2Sequence, dir);
			plotGraphs(id2Sequence, dir);
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static void plotGraphs(Map<String, String> id2Sequence, String dir){
		double valueCutoff = 0;
		int lengthCutoff = 7;
		boolean showCalculation = false;
		int plusOneIndex = -1;
		int windowSize = 1;
		int stepSize = 1;
		boolean cumulative = true;
		boolean invertValues = false;
		R r = new R();
		for(String header:id2Sequence.keySet()){
			String sequence = id2Sequence.get(header);
			SequenceQuencher sq = new SequenceQuencher(sequence, GenerateArff.aminoAcidHydrophobicity, valueCutoff, 
					lengthCutoff, showCalculation, plusOneIndex, windowSize, stepSize, cumulative, invertValues, null);
			List<Double> yList = sq.getGlobalViewResults();
			List<Double> xList = new ArrayList<Double>();
			for(double i = 0.0; i < sequence.length(); i += 1.0) xList.add(i);
			Graph g = new Graph(header, "Sequence Position", "Hydrophobicity", xList, yList);
			StringBuffer sb = RPlotGraph.plotGraph(g, dir + File.separator + header + ".pdf", true);
			int shift = -1;
			for(SequenceResult sr:sq.getResults()){
				sb.append(RDraw.drawText((sr.getIndexFrom() + sr.getIndexTo()) / 2.0, yList.get(sr.getIndexTo()) + shift, 
						Utils.roundToDecimals(sr.getValue(), 2) + ""));
				sb.append(RDraw.drawRectangle(sr.getIndexFrom(), yList.get(sr.getIndexFrom()), sr.getIndexTo(), yList.get(sr.getIndexTo())));
			}
			r.runCode(sb);
		}
	}
	
	private static void outputSequence(Map<String, String> id2Sequence, String outputDir) throws Exception{
		BufferedWriter output = new BufferedWriter(new FileWriter(outputDir + File.separator + "PeroxisomalMembrane.fasta"));
		for(String header:id2Sequence.keySet()){
			output.write(">" + header);
			output.newLine();
			output.write(id2Sequence.get(header));
			output.newLine();
		}
		output.close();
	}
	
	private static Set<String> getPeroxisomalMembraneProteins(String dir) throws Exception{
		Set<String> set = new HashSet<String>();
		String inputFile = dir + File.separator + "PeroxisomalMembrane.txt";
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		String line; 
		input.readLine();//skip first line
		while((line = input.readLine()) != null){
			String[] s = line.split("\t");
			set.add(s[0]);
		}
		input.close();
		return set;
	}
	
	private static Map<String, String> getSequence(Set<String> set, String dir) throws Exception{
		Map<String, String> id2Sequence = new HashMap<String, String>();
		String inputFile = dir + File.separator + "POCProteome.fasta";
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		String header;
		String sequence;
		while((header = input.readLine()) != null){
			String[] s = header.split(" ");
			sequence = input.readLine();
			header = s[0].substring(1);
			if(set.contains(header)) id2Sequence.put(header, sequence);
		}
		input.close();
		return id2Sequence;
	}
}
