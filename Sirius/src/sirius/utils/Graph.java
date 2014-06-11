package sirius.utils;

import java.util.ArrayList;
import java.util.List;

public class Graph {
	/*
	 * Designed for plotting standard graphs using R
	 */
	private String legendTitle;//Title
	private String xLabel;//String to be shown on x-label
	private String yLabel;//String to be shown on y-label
	private List<Double> xList;//x-points
	private List<Double> yList;//y-points
	
	public Graph(String legendTitle, double[] xList, double[] yList){
		this(legendTitle, "", "", xList, yList);
	}
	
	public Graph(String legendTitle, String xLabel, String yLabel, double[] xList, double[] yList){
		this.legendTitle = legendTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.xList = new ArrayList<Double>();
		this.yList = new ArrayList<Double>();
		for(double d:xList) this.xList.add(d);
		for(double d:yList) this.yList.add(d);
	}
	
	public Graph(String legendTitle, double[] xList, int[] yList){
		this(legendTitle, "", "", xList, yList);
	}
	
	public Graph(String legendTitle, String xLabel, String yLabel, double[] xList, int[] yList){
		this.legendTitle = legendTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.xList = new ArrayList<Double>();
		this.yList = new ArrayList<Double>();
		for(double d:xList) this.xList.add(d);
		for(int i:yList) this.yList.add(i + 0.0);
	}
	
	public Graph(String legendTitle, List<Double> xList, List<Double> yList){
		this.legendTitle = legendTitle;
		this.xLabel = "";
		this.yLabel = "";
		this.xList = xList;
		this.yList = yList;
	}
	
	public Graph(String legendTitle, String xLabel, String yLabel, List<Double> xList, List<Double> yList){
		if(xList.size() != yList.size()) throw new Error("xList.size != yList.size: " + xList.size() + ", " + yList.size());
		this.legendTitle = legendTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.xList = xList;
		this.yList = yList;
	}
	
	public Graph(String legendTitle, String xLabel, String yLabel, float[] xList, float[] yList){
		this.legendTitle = legendTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.xList = new ArrayList<Double>();
		this.yList = new ArrayList<Double>();
		for(double d:xList) this.xList.add(d);
		for(double d:yList) this.yList.add(d);		
	}
	
	public String getLegendTitle(){return this.legendTitle;}	
	public String getXLabel(){return this.xLabel;}
	public String getYLabel(){return this.yLabel;}
	public List<Double> getXList(){return this.xList;}
	public List<Double> getYList(){return this.yList;}
}