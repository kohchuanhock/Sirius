package sirius.weka;

import sirius.utils.Arff;
import sirius.utils.Utils;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class Tester {
	/*
	 * Places where I have made changes in Weka codes
	 * weka.classifiers.trees.J48.ClassifierTree.java
	 * 		distributionForInstanceAHFU
	 * 		toStringAHFU
	 * 		getProbsLaplaceAHFU
	 * 		getProbsAHFU
	 * 		dumpTreeAHFU
	 * weka.classifier.tress.J48.C45Split.java
	 * 		whichSubsetAHFU
	 * weka.classifier.trees.J48.java
	 * 		distributionForInstanceAHFU
	 * 		toStringAHFU	 
	 */
	public static void main(String[] args){
		try{
			String arffFileLocation = Utils.selectFile("Please select Arff file");		
			Instances trainInstances = Arff.getAsInstances(arffFileLocation);
			trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
			J48 j48 = new J48();
			j48.buildClassifier(trainInstances);
//			j48.toStringAHFU();
			j48.toString();
			for(int i = 0; i < trainInstances.numInstances(); i++){
//				StringBuffer ruleBuffer = new StringBuffer();
//				StringBuffer featureBuffer = new StringBuffer();
//				double[] d = j48.distributionForInstanceAHFU(trainInstances.instance(i), ruleBuffer, featureBuffer);
				double[] d = j48.distributionForInstance(trainInstances.instance(i));
//				j48.distributionForInstanceAHFU(trainInstances.instance(i), ruleBuffer, featureBuffer);
				j48.distributionForInstance(trainInstances.instance(i));
				System.out.println(d[0] + " - " + j48.classifyInstance(trainInstances.instance(i)));
				System.out.println();			
			}
		}catch(Exception e){e.printStackTrace();}
	}
}

/*
 	//ClassifierTree.java
 	private double getProbsLaplaceAHFU(int classIndex, Instance instance, double weight, boolean show) 
	throws Exception {

		double prob = 0;

		if (m_isLeaf) {
			if(show == false && classIndex == 0) {
				System.out.println(m_localModel.dumpLabel(0,m_train));				
			}
			return weight * localModel().classProbLaplace(classIndex, instance, -1);
		} else {					
			int treeIndex = ((C45Split)localModel()).whichSubsetAHFU(instance, show);
			if(classIndex == 0 && show == false) {
				System.out.println(m_localModel.leftSide(m_train) +
						m_localModel.rightSide(treeIndex, m_train) + " ");
			}		
			if (treeIndex == -1) {
				double[] weights = localModel().weights(instance);
				for (int i = 0; i < m_sons.length; i++) {
					if (!son(i).m_isEmpty) {
						prob += son(i).getProbsLaplaceAHFU(classIndex, instance, 
								weights[i] * weight, show);
					}
				}
				return prob;
			} else {
				if (son(treeIndex).m_isEmpty) {
					return weight * localModel().classProbLaplace(classIndex, instance, 
							treeIndex);
				} else {
					return son(treeIndex).getProbsLaplaceAHFU(classIndex, instance, weight, show);
				}
			}
		}
	}
	
 	private double getProbsAHFU(int classIndex, Instance instance, double weight, boolean show) 
	throws Exception {

		double prob = 0;		
		if (m_isLeaf) {
			if(show == false && classIndex == 0) {
				System.out.println("HENCE PREDICT: " + m_localModel.dumpLabel(0,m_train));				
			}
			return weight * localModel().classProb(classIndex, instance, -1);
		} else {			
			int treeIndex = ((C45Split)localModel()).whichSubsetAHFU(instance, show);
			if(show == false && classIndex == 0) {
				if(treeIndex != -1){
					System.out.print(m_localModel.leftSide(m_train) + 			
							m_localModel.rightSide(treeIndex, m_train) + ", ");
				}else{
					System.out.println(m_localModel.leftSide(m_train) + " = ?");
				}
			}
			if (treeIndex == -1) {
				double[] weights = localModel().weights(instance);
				for (int i = 0; i < m_sons.length; i++) {
					if (!son(i).m_isEmpty) {
						prob += son(i).getProbsAHFU(classIndex, instance, 
								weights[i] * weight, show);
					}
				}
				return prob;
			} else {
				if (son(treeIndex).m_isEmpty) {
					return weight * localModel().classProb(classIndex, instance, 
							treeIndex);
				} else {
					return son(treeIndex).getProbsAHFU(classIndex, instance, weight, show);
				}
			}
		}
	}
	
	 public final double [] distributionForInstanceAHFU(Instance instance,
			boolean useLaplace, boolean show) 
	throws Exception {

		double [] doubles = new double[instance.numClasses()];

		for (int i = 0; (i < doubles.length && show==false) || i == 0 ; i++) {
			if (!useLaplace) {
				//System.out.println("getProbsAHFU");
				doubles[i] = getProbsAHFU(i, instance, 1, show);
			} else {
				//System.out.println("Use Labplace");
				doubles[i] = getProbsLaplaceAHFU(i, instance, 1, show);
			}
		}

		return doubles;
	}
	
	public List<String> toStringAHFU() {		
		try {
			//StringBuffer text = new StringBuffer();
			List<String> sList = new ArrayList<String>();
			if (m_isLeaf) {
				//Do this if leaf node
				//text.append(": ");
				//Display the label
				//text.append(m_localModel.dumpLabel(0,m_train));
				sList.add(m_localModel.dumpLabel(0,m_train));
			}else{
				//Do this if internal node
				dumpTreeAHFU(0,"",sList);
			}
			//text.append("\n\nNumber of Rules  : \t"+numLeaves()+"\n");			
			//return text.toString();
			return sList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void dumpTreeAHFU(int depth, String text, List<String> sList) 
	throws Exception {		
		for(int i=0;i<m_sons.length;i++) {
			String newText;
			if(depth == 0){
				newText = "IF " + text + m_localModel.leftSide(m_train) + 
					m_localModel.rightSide(i, m_train);
			}else{
				newText = text + " AND " + m_localModel.leftSide(m_train) + 
					m_localModel.rightSide(i, m_train);
			}						
			if (m_sons[i].m_isLeaf) {
				//If is leaf, print the label
				//text.append(": ");
				//text.append(m_localModel.dumpLabel(i,m_train));
				sList.add(newText + 
						" THEN " + m_localModel.dumpLabel(i,m_train));
			}else{
				//if is internal node
				m_sons[i].dumpTreeAHFU(depth+1,newText, 
						sList);
			}
			//text.append();
			//text.append();			
		}
	}
 	
*/

/*
 	C45Split.java
 	
 	public final int whichSubsetAHFU(Instance instance, boolean show) 
  throws Exception {	  
	  if (instance.isMissing(m_attIndex)){
		  return -1;
	  }else{
		  if(show){
			  System.out.println(instance.attribute(m_attIndex).name() + " IS " + 
					instance.stringValue(m_attIndex));
		  }
		  if (instance.attribute(m_attIndex).isNominal()){
			  return (int)instance.value(m_attIndex);
		  }else{
			  if (Utils.smOrEq(instance.value(m_attIndex),m_splitPoint)){		
				  return 0;
			  }else{
				  return 1;
			  }
		  }
	  }
  }
*/

/*
   J48.java
   
   public final double [] distributionForInstanceAHFU(Instance instance, boolean show) 
  throws Exception {

	  return m_root.distributionForInstanceAHFU(instance, m_useLaplace, show);
}

public void toStringAHFU(){
	  if (m_root == null) {
	      System.out.println("No classifier built");
	    }
	    if (m_unpruned)
	      System.out.println("J48 unpruned tree as Rules");
	    else
	      System.out.println("J48 pruned tree as Rules");
	    System.out.println("-------------------------");
	    List<String> sList = m_root.toStringAHFU();
	    for(String s:sList) System.out.println(s);
	    System.out.println();
  }
*/