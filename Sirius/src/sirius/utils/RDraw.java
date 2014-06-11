package sirius.utils;

import sirius.utils.REnum.COLOR;

public class RDraw {

	public static StringBuffer drawText(double x, double y, String text){
		/*
		 * Draw text in graph
		 * cex => size
		 * pos => 1=below, 2=left, 3=above, 4=right
		 * col => colour
		 */
		return new StringBuffer("text(" + x + "," + y + "," + text + "," + "cex=0.6, pos=3, col=\"red\");");
	}

	public static StringBuffer drawRectangle(double x1, double y1, double x2, double y2){
		return new StringBuffer("rect(" + x1 + "," + y1 + "," + x2 + "," + y2 + ", border=\"red\");");
	}

	public static StringBuffer drawReferenceLine(boolean isHorizontal, double location){
		return RDraw.drawReferenceLine(isHorizontal, location, 2, null, COLOR.BLACK);
	}

	public static StringBuffer drawReferenceLine(boolean isHorizontal, double location, String text){
		return RDraw.drawReferenceLine(isHorizontal, location, 2, text, COLOR.BLACK);
	}

	public static StringBuffer drawReferenceLine(boolean isHorizontal, double location, String text, boolean typicalLocationForText){
		return RDraw.drawReferenceLine(isHorizontal, location, 2, text, COLOR.BLACK, typicalLocationForText);
	}

	public static StringBuffer drawReferenceLine(boolean isHorizontal, double location, String text, COLOR color){
		return RDraw.drawReferenceLine(isHorizontal, location, 2, text, color);
	}

	public static StringBuffer drawReferenceLine(boolean isHorizontal, double location, int lty, String text, COLOR color){
		return RDraw.drawReferenceLine(isHorizontal, location, lty, text, color, true);
	}

	public static StringBuffer drawReferenceLine(boolean isHorizontal, double location, int lty, String text, COLOR color, boolean typicalLocationForText){
		/*
		 * Draw a reference line
		 */
		//double labelMagnification = 2.0;
		StringBuffer s = new StringBuffer("abline(");
		if(isHorizontal){
			s.append("h");
		}else{
			s.append("v");
		}
		s.append(" = " + location + ", lty = " + lty + ", col = \"");
		switch(color){
		case BLACK: s.append("black"); break;
		case BLUE: s.append("blue"); break;
		case RED: s.append("red"); break;
		case YELLOW: s.append("yellow"); break;
		default: throw new Error("Unhandled color: " + color);
		}
		s.append("\");");
		if(text != null){
			if(isHorizontal){
				if(typicalLocationForText) s.append(RDraw.drawAxis(2, location, text));
				else s.append(RDraw.drawAxis(4, location, text));
			}else{
				if(typicalLocationForText) s.append(RDraw.drawAxis(1, location, text));
				else s.append(RDraw.drawAxis(3, location, text));
			}
		}
		return s;
	}

	public static StringBuffer drawAxis(int side, double location, String text){
		/*
		 * Label the axis
		 */
		double labelMagnification = 2.0;
		if(text.contains("expression(") == false) text = "'" + text + "'";
		return new StringBuffer("axis(" + side + ", at = " + location + ", " + text + ", cex.axis = " + labelMagnification + ", las = 2);");
	}

}