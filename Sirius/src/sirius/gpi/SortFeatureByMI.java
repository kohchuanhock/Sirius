package sirius.gpi;

import java.util.Comparator;

import sirius.trainer.features.Feature;

public class SortFeatureByMI implements Comparator<Feature> {        
	public int compare(Feature a, Feature b) {		
		if (a.getMutualInformation() < b.getMutualInformation()) {
			return 1;
		} else if(a.getMutualInformation() > b.getMutualInformation()){
			return -1;
		} else {
			return 0;
		}		
	}    
}
