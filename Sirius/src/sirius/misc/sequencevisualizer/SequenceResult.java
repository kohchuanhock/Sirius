package sirius.misc.sequencevisualizer;

import java.text.DecimalFormat;

public class SequenceResult{
	int indexFrom;//inclusive
	int indexTo;//inclusive
	double value;
	
	public SequenceResult(double value, int indexFrom, int indexTo){		
		this.value = value;
		this.indexFrom = indexFrom;
		this.indexTo = indexTo;
	}
	
	public String toString(){
		DecimalFormat df = new DecimalFormat("0.####");
		return "Value: " + df.format(this.value) + " Index From: " + this.indexFrom + " Index To: " + this.indexTo;
	}
	
	public int getIndexFrom(){
		return this.indexFrom;
	}
	
	public int getIndexTo(){
		return this.indexTo;
	}
	
	public double getValue(){
		return this.value;
	}	
	
	public int length(){
		return this.indexTo - this.indexFrom + 1;
	}
	
	public int getMidPoint(){
		return (this.indexTo + this.indexFrom) / 2;
	}
}