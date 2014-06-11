package sirius.utils;

import java.util.List;

import sirius.utils.REnum.LEGENDLOCATION;

public class RPlotGraph {
	public static StringBuffer plotGraph(Graph graph){
		return plotGraph(graph, null, null);
	}	
	
	public static StringBuffer plotGraph(Graph graph, String outputFileLocation){
		return plotGraph(graph, outputFileLocation, true);
	}
	
	public static StringBuffer plotGraph(Graph graph, String outputFileLocation, boolean outputAsPDF){
		return plotGraph(graph, outputFileLocation, null, outputAsPDF, false, null, null, null, null);
	}
	
	public static StringBuffer plotGraph(Graph graph, String outputFileLocation, boolean outputAsPDF, boolean connectPoints){
		return plotGraph(graph, outputFileLocation, null, outputAsPDF, connectPoints, null, null, null, null);
	}

	public static StringBuffer plotGraph(Graph graph, String outputFileLocation, List<String> xAxisLabelList){
		return plotGraph(graph, outputFileLocation, xAxisLabelList, true, false, null, null, null, null);
	}	
	
	public static StringBuffer plotGraph(Graph graph, String outputFileLocation, List<String> xAxisLabelList, boolean outputAsPDF, 
			boolean connectPoints){
		return plotGraph(graph, outputFileLocation, xAxisLabelList, outputAsPDF, connectPoints, null, null, null, null);
	}

	public static StringBuffer plotGraph(Graph graph, String outputFileLocation, List<String> xAxisLabelList, boolean outputAsPDF, 
			boolean connectPoints, Double xMin, Double xMax, Double yMin, Double yMax){
		/*
		 * Output a single graph to PDF or PNG with the specified xAxis labels
		 *
		 */
		List<Double> xList = graph.getXList();
		List<Double> yList = graph.getYList();		
		String xLabel = graph.getXLabel();
		String yLabel = graph.getYLabel();	
		String title = graph.getLegendTitle();
		StringBuffer xString = new StringBuffer();
		StringBuffer yString = new StringBuffer();
		for(int x = 0; x < xList.size(); x++){
			if(x != 0){
				xString.append(",");
				yString.append(",");
			}
			xString.append(xList.get(x));
			yString.append(yList.get(x));
		}
		StringBuffer code=new StringBuffer();
		code.append("x<-c(" + xString + ");");
		code.append("y<-c(" + yString + ");");
		if(outputFileLocation != null){
			outputFileLocation = outputFileLocation.replace("\\", "/");
			if(outputAsPDF){
				code.append("pdf(file=\"" + outputFileLocation + "\",paper=\"a4r\",width=0,height=0);");       
			}else{
				//PNG
				code.append("png(\"" + outputFileLocation + "\");");
			}
		}   
		
		if(connectPoints){
			code.append("plot(x,y,xlab=\"" + xLabel + "\",ylab=\"" + yLabel + 
			"\", pch = 3, col = 2, type = \"b\", lty = 1");        	
		}else{
			code.append("plot(x,y,xlab=\"" + xLabel + "\",ylab=\"" + yLabel + 
			"\", pch = 3, col = 2, type = \"b\", lty = 0"); 
		}
		
		if(xMin != null){
			code.append(", xlim = c(" + xMin + "," + xMax + ")");
		}
		
		if(yMin != null){
			code.append(", ylim = c(" + yMin + "," + yMax + ")");
		}
		
		if(xAxisLabelList != null){//If X axis is to be provided
			code.append(", xaxt = \"n\");");
			xString = new StringBuffer();
			for(int x = 0; x < xAxisLabelList.size(); x++){
				if(x != 0){
					xString.append(",");        		        		
				}
				xString.append(xAxisLabelList.get(x));
			}
			code.append("axis(1, 1:" + xAxisLabelList.size() + ",c(" + xString + ") );");
		}else{
			code.append(");");
		}
		code.append("title(\"" + title + "\");");
		return code;
	}
	
	public static StringBuffer plotGraphs(List<Graph> graphList, String title){
		return plotGraphs(graphList, null, title, null);
	}
	
	public static StringBuffer plotGraphs(List<Graph> graphList, String title, String outputFileLocation){
		return plotGraphs(graphList, outputFileLocation, title, null);
	}

	public static StringBuffer plotGraphs(List<Graph> graphList, String title, List<String> xAxisList){
		return plotGraphs(graphList, null, title, xAxisList);
	}
	
	public static StringBuffer plotGraphs(LEGENDLOCATION ll, List<Graph> graphList, String title, String outputFileLocation){
		return plotGraphs(graphList, outputFileLocation, title, null, true, true, ll,
				null, null, null, null);
	}

	public static StringBuffer plotGraphs(List<Graph> graphList, String outputFileLocation, String graphTitle,
			List<String> xAxisLabelsList){
		return plotGraphs(graphList, outputFileLocation, graphTitle, xAxisLabelsList, true, true);
	}
	
	public static StringBuffer plotGraphs(List<Graph> graphList, String outputFileLocation, String graphTitle,
			List<String> xAxisLabelsList, boolean outputAsPDF, boolean connectPoints){
		return plotGraphs(graphList, outputFileLocation, graphTitle, xAxisLabelsList, outputAsPDF, connectPoints, LEGENDLOCATION.TOPRIGHT,
				null, null, null, null);
	}
	
	public static StringBuffer plotGraphs(List<Graph> graphList, String outputFileLocation, String graphTitle,
			List<String> xAxisLabelsList, boolean outputAsPDF, boolean connectPoints, LEGENDLOCATION ll){
		return plotGraphs(graphList, outputFileLocation, graphTitle, xAxisLabelsList, outputAsPDF, connectPoints, ll,
				null, null, null, null);
	}

	public static StringBuffer plotGraphs(List<Graph> graphList, String outputFileLocation, String graphTitle,
			List<String> xAxisLabelsList, boolean outputAsPDF, boolean connectPoints, LEGENDLOCATION ll, 
			Double xMin, Double xMax, Double yMin, Double yMax){
		/*
		 * Output multiple graphs to a single PDF or PNG with Title and specified xAxis Labels
		 */
		int pchValue = 3;
		int colValue = 8;
		double labelManification = 2.5;
		double axisManification = 2.0;
		double titleManification = 2.0;
		//bottom, left, top and right
		double bottomSpace = 4;
		double leftSpace = 5.0;
		double topSpace = 4.5;
		double rightSpace = 0.5;
		
		//Preprocess xAxisLabelsList
		if(xAxisLabelsList != null){
			for(int i = 0; i < xAxisLabelsList.size(); i++){
				if(xAxisLabelsList.get(i).charAt(0) != '\''){
					xAxisLabelsList.set(i, "\'" + xAxisLabelsList.get(i) + "\'");
				}
			}
		}
		
		StringBuffer code=new StringBuffer();
		if(outputFileLocation != null){
			outputFileLocation = outputFileLocation.replace("\\", "/");
			if(outputAsPDF){
				code.append("pdf(file=\"" + outputFileLocation + "\",paper=\"USr\",width=0,height=0);");
			}else{
				//PNG
				code.append("png(file=\"" + outputFileLocation + "\");");
			}
		}
		code.append("par(mar=c(" + bottomSpace + ", " + leftSpace + ", " + topSpace + ", " + rightSpace + "));");
		
		if(xMin == null){
			xMin = Double.POSITIVE_INFINITY;
			xMax = Double.NEGATIVE_INFINITY;
			for(Graph g:graphList){
				for(double v:g.getXList()){
					if(v < xMin) xMin = v;
					if(v > xMax) xMax = v;
				}
			}
		}
		
		if(yMin == null){
			yMin = Double.POSITIVE_INFINITY;
			yMax = Double.NEGATIVE_INFINITY;
			for(Graph g:graphList){
				for(double v:g.getYList()){
					if(v < yMin) yMin = v;
					if(v > yMax) yMax = v;
				}
			}
		}
		
		StringBuffer legendString = new StringBuffer();
		if(graphList.size() == 0) throw new Error("Graph List Size is 0!!");
		for(int a = 0; a < graphList.size(); a++){
			if(a != 0){
				legendString.append(",");
			}
			legendString.append("\"" + graphList.get(a).getLegendTitle() + "\"");
			StringBuffer xString = new StringBuffer();
			StringBuffer yString = new StringBuffer();
			List<Double> xList = graphList.get(a).getXList();
			List<Double> yList = graphList.get(a).getYList();			
			for(int x = 0; x < xList.size(); x++){
				if(x != 0){
					xString.append(",");
					yString.append(",");
				}
				xString.append(xList.get(x));
				yString.append(yList.get(x));
			}
			code.append("x" + a + "<-c(" + xString + ");");
			code.append("y" + a + "<-c(" + yString + ");");	        	             
		}
		
		for(int a = 0; a < graphList.size(); a++){
			
			String xLabel = graphList.get(a).getXLabel();
			String yLabel = graphList.get(a).getYLabel();			
			if(xLabel.contains("expression") == false) xLabel = "\"" + xLabel +  "\"";
			if(yLabel.contains("expression") == false) yLabel = "\"" + yLabel +  "\"";
			
			if(a == 0){	        	
				if(xAxisLabelsList != null){
					//Specific the x-axis labeling
					if(connectPoints){
						//lty = 1 if should connect points and the type of line used to connect points
						code.append("plot(x" + a + ",y" + a + ",ylim = c(" + yMin + "," + yMax + "), " + 
								"xlim = c(" + xMin + "," + xMax + "), " +  
								"xlab=" + xLabel + ",ylab=" + yLabel + 
								", pch = " + (a + pchValue) + ", col = " + (a + colValue) + ",type = \"b\", lty = 1, xaxt = \"n\"," + 
								"cex.lab = " + labelManification + ",cex.axis = " + axisManification +",mar = c(0,0,0,0));");
					}else{
						//lty = 0 if no need to connect points
						code.append("plot(x" + a + ",y" + a + ",ylim = c(" + yMin + "," + yMax + "), " +
								"xlim = c(" + xMin + "," + xMax + "), " 
								+ "xlab=" + xLabel + ",ylab=" + yLabel + 
								", pch = " + (a + pchValue) + ", col = " + (a + colValue) + ",type = \"b\", lty = 0, xaxt = \"n\"," + 
								"cex.lab = " + labelManification + ",cex.axis = " + axisManification +",mar = c(0,0,0,0));");
					}
					/*
					 * Specified X-axis Labeling
					 */
					StringBuffer xString = new StringBuffer();
					for(int x = 0; x < xAxisLabelsList.size(); x++){
						if(x != 0){
							xString.append(",");        		        		
						}
						xString.append(xAxisLabelsList.get(x));
					}
					code.append("axis(1, 1:" + (xAxisLabelsList.size()) + ",c(" + xString + ") );");
				}else{
					//Not X-Axis Labeling
					if(connectPoints){
						code.append("plot(x" + a + ",y" + a + ",ylim = c(" + yMin + "," + yMax + "), " + 
							"xlim = c(" + xMin + "," + xMax + "), " +
							"xlab=" + xLabel + ",ylab=" + yLabel + 
							", pch = " + (a + pchValue) + ", col = " + (a + colValue) + ",type = \"b\", lty = 1," + 
								"cex.lab = " + labelManification + ",cex.axis = " + axisManification + ",mar = c(0,0,0,0));");
					}else{
						code.append("plot(x" + a + ",y" + a + ",ylim = c(" + yMin + "," + yMax + "), " + 
							"xlim = c(" + xMin + "," + xMax + "), " +
							"xlab=" + xLabel + ",ylab=" + yLabel + 
							", pch = " + (a + pchValue) + ", col = " + (a + colValue) + ",type = \"b\", lty = 0," + 
								"cex.lab = " + labelManification + ",cex.axis = " + axisManification +",mar = c(0,0,0,0));");
					}
				}
				/*
				 * Title and Legend
				 */
				String legendLocationString;
				switch(ll){
				case TOPLEFT: legendLocationString = "topleft"; break;
				case TOPRIGHT: legendLocationString = "topright"; break;
				case TOPCENTER: legendLocationString = "top"; break;
				default: throw new Error("Unhandled Case");
				}
				code.append("title(\"" + graphTitle + "\", cex.main = " + titleManification + ");");
				code.append("legend(\"" + legendLocationString + "\", c(" + legendString + 
						"), col = seq(" + (colValue) + "," + (graphList.size() + colValue)+ ",1),pch = seq(" + pchValue + "," + 
						(graphList.size() + pchValue) + "), bty=\"n\", cex = " + labelManification + ");");
			}else{
				//Unspecified x-axis labels
				if(connectPoints){
					//lty = 1 if should connect points
					code.append("points(x" + a +  ",y" + a + ",pch = " + (a + pchValue) + ", col = " + (a + colValue) + 
					",type = \"b\", lty = 1);");
				}else{
					//lty = 0 if no need to connect points
					code.append("points(x" + a +  ",y" + a + ",pch = " + (a + pchValue) + ", col = " + (a + colValue) + 
					",type = \"b\", lty = 0);");
				}
			}			
		}
		return code;
	}
}
