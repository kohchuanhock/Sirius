package sirius.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConversionInformation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Set<Integer>> newIndex2OldIndexSet = null;
	private Map<Integer, Integer> newIndex2OldIndex = null;
	private List<String> membershipIDList = new ArrayList<String>();
	
	public List<String> getMembershipIDList(){
		return this.membershipIDList;
	}
	
	public Map<Integer, Set<Integer>> getNewIndex2OldIndexSet(){
		return this.newIndex2OldIndexSet;
	}
	
	public Map<Integer, Integer> getNewIndex2OldIndex(){
		return this.newIndex2OldIndex;
	}
	
	public ConversionInformation(Map<String, Set<String>> probeID2MembershipIDSet, boolean debug, List<String> rowNameList, boolean collapse){
		if(collapse){
			conversionWithCollapse(probeID2MembershipIDSet, debug, rowNameList);
		}else{
			conversionWithoutCollapse(probeID2MembershipIDSet, debug, rowNameList);
		}
	}
	
	private void conversionWithCollapse(Map<String, Set<String>> probeID2MembershipIDSet, boolean debug, List<String> rowNameList){
		this.newIndex2OldIndexSet = new HashMap<Integer, Set<Integer>>();
		Set<String> membershipIDListSet = new HashSet<String>();//Prevent Double Adding
		for(int i = 0; i < rowNameList.size(); i++){
			String probeID = rowNameList.get(i);
			probeID = probeID.replace("\"", "");
			
			Set<String> membershipIDSet = probeID2MembershipIDSet.get(probeID);
			if(membershipIDSet == null || membershipIDSet.size() == 0){
				//No mapping from probeID to membershipID is found
				this.newIndex2OldIndexSet.put(this.membershipIDList.size(), new HashSet<Integer>());
				this.newIndex2OldIndexSet.get(this.membershipIDList.size()).add(i);
				this.membershipIDList.add(probeID);
			}else{
				//One-to-One or One-to-many mapping from probeID to membershipIDs
				List<String> sortedMembershipIDList = new ArrayList<String>();
				for(String membershipID:membershipIDSet) sortedMembershipIDList.add(membershipID);
				Collections.sort(sortedMembershipIDList);//Natural Sort - required to ensure that the order is not changed
				
				for(String membershipID:sortedMembershipIDList){
					if(newIndex2OldIndexSet.containsKey(membershipIDList.size()) == false) 
						newIndex2OldIndexSet.put(membershipIDList.size(), new HashSet<Integer>());
					
					newIndex2OldIndexSet.get(membershipIDList.size()).add(i);
					if(membershipIDListSet.contains(membershipID) == false){//Prevent double adding
						membershipIDListSet.add(membershipID);
						membershipIDList.add(membershipID);
					}
				}
			}
		}
		if(debug){
			System.out.println("Convert ProbeIDs to MembershipIDs - Collapse: true");
			System.out.println("# of Probes: " + rowNameList.size());
			System.out.println("# of MembershipID: " + membershipIDList.size());
		}
	}
	
	private void conversionWithoutCollapse(Map<String, Set<String>> probeID2MembershipIDSet, boolean debug, List<String> rowNameList){
		this.newIndex2OldIndex = new HashMap<Integer, Integer>();
		for(int i = 0; i < rowNameList.size(); i++){
			String probeID = rowNameList.get(i);
			probeID = probeID.replace("\"", "");
			Set<String> membershipIDSet = probeID2MembershipIDSet.get(probeID);
			if(membershipIDSet == null || membershipIDSet.size() == 0){
				//No mapping
				this.newIndex2OldIndex.put(this.membershipIDList.size(), i);
				this.membershipIDList.add(probeID);
			}else{
				//One-to-many mapping from probeID to entrezIDs
				List<String> sortedEntrezList = new ArrayList<String>();
				for(String entrezID:membershipIDSet) sortedEntrezList.add(entrezID);
				Collections.sort(sortedEntrezList);//Natural Sort - required to ensure that the order is not changed
				
				for(String entrezID:sortedEntrezList){
					this.newIndex2OldIndex.put(this.membershipIDList.size(), i);
					this.membershipIDList.add(entrezID);
				}
			}
		}
		if(debug){
			System.out.println("Convert ProbeIDs to EntrezIDs - Collapse: false");
			System.out.println("# of Probes: " + rowNameList.size());
			System.out.println("# of EntrezID: " + this.membershipIDList.size());
		}
	}
}
