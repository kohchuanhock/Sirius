/*==========================================================================
	  SiriusPSB - A Generic System for Analysis of Biological Sequences
	        http://compbio.ddns.comp.nus.edu.sg/~sirius/index.php
============================================================================
	  Copyright (C) 2007 by Chuan Hock Koh
	
	  This program is free software; you can redistribute it and/or
	  modify it under the terms of the GNU General Public
	  License as published by the Free Software Foundation; either
	  version 3 of the License, or (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	  General Public License for more details.
	  	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
==========================================================================*/
package sirius.trainer.features.gui.advancedphysiochemical;

import java.util.Hashtable;

public class AdvancedPhysioChemicalFeatureComboBoxItem {
	private String itemName;
	private Hashtable<Character, Double> hashtable;
	private double defaultValueCutoff;
	private int defaultLengthCutoff;
	private boolean invertValues;	
	private int windowSize;
	private int stepSize;
	
	
	public AdvancedPhysioChemicalFeatureComboBoxItem(String itemName, Hashtable<Character, Double> hashtable, double defaultValueCutoff, 
			int defaultLengthCutoff, int windowSize, int stepSize,boolean invertValues){
		this.itemName = itemName;
		this.hashtable = hashtable;
		this.defaultValueCutoff = defaultValueCutoff;
		this.defaultLengthCutoff = defaultLengthCutoff;
		this.invertValues = invertValues;
		this.windowSize = windowSize;
		this.stepSize = stepSize;
	}
	
	public AdvancedPhysioChemicalFeatureComboBoxItem(String itemName, Hashtable<Character, Double> hashtable, double defaultValueCutoff, 
			int defaultLengthCutoff, boolean invertValues){
		this(itemName, hashtable, defaultValueCutoff, defaultLengthCutoff, 1, 1, invertValues);
	}
		
	public int getWindowSize(){
		return this.windowSize;
	}
	
	public int getStepSize(){
		return this.stepSize;
	}
	
	public String getItemName(){
		return this.itemName;
	}
	
	public Hashtable<Character, Double> getHashtable(){
		return this.hashtable;
	}
	
	public double getDefaultValueCutoff(){
		return this.defaultValueCutoff;
	}
	
	public int getDefaultLengthCutoff(){
		return this.defaultLengthCutoff;
	}
	
	public boolean getInvertValues(){
		return this.invertValues;
	}	
}
