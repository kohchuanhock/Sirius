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
package sirius.trainer.main;

import java.io.*;

public class SiriusSettings {
	public static String getInformation(String title){		
		File file = new File("SiriusSettings.txt");
		if(file.exists()){
			try{
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;				
				while ((line = in.readLine()) != null) {
					int index = line.indexOf(title);
					if(index != -1){												
						in.close();
						return line.substring(index + title.length());						
					}
				}
				in.close();
				return null;
			}catch(Exception ex){ex.printStackTrace(); return null;}
		}else{			
			return null;
		}		
	}
	public static void updateInformation(String title, String data){		
		File file = new File("SiriusSettings.txt");
		try{
			if(file.exists()){
				//do the stupid way. use another file to temp store.
				File tempFile = new File("SiriusSettings.temp");
				BufferedWriter outputTemp = new BufferedWriter(new FileWriter(tempFile));
				BufferedReader input = new BufferedReader(new FileReader("SiriusSettings.txt"));
				String line;
				boolean found = false;
				while((line = input.readLine()) != null){
					if(line.indexOf(title) == -1)
						outputTemp.write(line);						
					else{
						outputTemp.write(title + data);
						found = true;
					}						
					outputTemp.newLine();
					outputTemp.flush();
				}
				if(found == false){
					outputTemp.write(title + data);
					outputTemp.newLine();
					outputTemp.flush();
				}
				outputTemp.close();
				input.close();
				BufferedWriter output = new BufferedWriter(new FileWriter("SiriusSettings.txt"));
				BufferedReader inputTemp = new BufferedReader(new FileReader("SiriusSettings.temp"));
				while((line = inputTemp.readLine()) != null){
					output.write(line);
					output.newLine();
					output.flush();
				}
				output.close();
				inputTemp.close();
				tempFile.delete();
			}else{			
				file.createNewFile();
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(title + data);
				output.flush();
				output.close();
			}
		}catch(Exception ex){ex.printStackTrace();}
	}
}
