package sirius.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Sampling {
	public static Set<Integer> selectUndersampleRandomly(Random rand, Set<Integer> largerSetIndex, int shorterListSize, int maxFoldDiff){
		/*
		 * Returns the indexes of randomly undersampled set
		 * Note: Used by GeneticAlgorithm.java
		 */
		Set<Integer> selectedSet = new HashSet<Integer>();
		List<Integer> fullList = new ArrayList<Integer>();
		for(int i:largerSetIndex) fullList.add(i);		
		while(selectedSet.size() < shorterListSize * maxFoldDiff && fullList.size() > 0){
			int i = rand.nextInt(fullList.size());
			selectedSet.add(fullList.get(i));
			fullList.remove(i);
		}
		return selectedSet;
	}
	
	public static Set<Integer> selectSubsetRandomly(Random rand, int originalSize, int subsetSize){
		/*
		 * Return the indexes of a subset selected randomly
		 * Note: Used by GeneticAlgorithm.java
		 */
		Set<Integer> selectedSet = new HashSet<Integer>();
		List<Integer> fullList = new ArrayList<Integer>();
		for(int i = 0; i < originalSize; i++) fullList.add(i);
		while(selectedSet.size() < subsetSize){
			int i = rand.nextInt(fullList.size());
			selectedSet.add(fullList.get(i));
			fullList.remove(i);
		}
		return selectedSet;
	}
			
	public static void oversample(List<FastaFormat> posFastaList, List<FastaFormat> negFastaList,
			List<FastaFormat> pList, List<FastaFormat> nList, boolean resampleEvenly){
		/*
		 * if maxFoldDiff == 0, then resample until same
		 * if maxFoldDiff == 1, then resample evenly
		 * 
		 * Oversample and add directly into posFastaList and negFastaList
		 * Note: Used by SiriusTwoLayerMembraneTypeClassifier I, II and III
		 */
		if(pList.size() >= nList.size()){
			if(resampleEvenly == false){
				int maxNum = pList.size();
				for(FastaFormat f:pList) posFastaList.add(f);				
				while(negFastaList.size() < maxNum){
					for(int x = 0; x < nList.size() && negFastaList.size() < maxNum; x++){
						negFastaList.add(nList.get(x));
					}				
				}
			}else{//Resample evenly
				int maxNum = pList.size();
				for(FastaFormat f:pList) posFastaList.add(f);				
				while(negFastaList.size() + nList.size() < maxNum){
					for(int x = 0; x < nList.size(); x++){
						negFastaList.add(nList.get(x));
					}
				}
			}
		}else{
			if(resampleEvenly == false){
				int maxNum = nList.size();
				for(FastaFormat f:nList) negFastaList.add(f);
				
				while(posFastaList.size() < maxNum){
					for(int x = 0; x < pList.size() && posFastaList.size() < maxNum; x++){
						posFastaList.add(pList.get(x));
					}
				}
			}else{
				int maxNum = nList.size();
				for(FastaFormat f:nList) negFastaList.add(f);
				
				while(posFastaList.size() + pList.size() < maxNum){
					for(int x = 0; x < pList.size() && posFastaList.size() < maxNum; x++){
						posFastaList.add(pList.get(x));
					}
				}
			}
		}
	}
	
	public static void undersample(List<FastaFormat> posFastaList, List<FastaFormat> negFastaList, 
			List<FastaFormat> pList, List<FastaFormat> nList, int maxFoldDiff){
		/*
		 * Undersample randomly and directly add to posFastaList and negFastaList
		 * Note: Used by SiriusTwoLayerMembraneTypeClassifier I, II and III
		 */
		Random rand = new Random(0);
		if(pList.size() >= nList.size()){
			int maxNum = nList.size() * maxFoldDiff;
			for(FastaFormat f:nList) negFastaList.add(f);
			while(posFastaList.size() < maxNum){
				int index = rand.nextInt(pList.size());
				posFastaList.add(pList.get(index));
				pList.remove(index);
			}
		}else{
			int maxNum = pList.size() * maxFoldDiff;
			for(FastaFormat f:pList) posFastaList.add(f);
			while(negFastaList.size() < maxNum){
				int index = rand.nextInt(nList.size());
				negFastaList.add(nList.get(index));
				nList.remove(index);
			}
		}
	}
}