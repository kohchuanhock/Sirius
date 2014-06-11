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
package sirius.trainer.features.gui.geneticalgorithm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.features.Feature;

public class ViewFeaturesDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton saveButton = new JButton("Save Features");
	private JButton closeButton = new JButton("Close");
	private JTextField outputLocationTextField;
	private FeatureTableModel featureTableModel;
	private JLabel totalScoreLabel = new JLabel();
	private JLabel featureLabel = new JLabel();

	public ViewFeaturesDialog(FeatureTableModel featureTableModel,JTextField outputLocationTextField, String scoreString){
		this.outputLocationTextField = outputLocationTextField;
		this.featureTableModel = featureTableModel;
		this.featureTableModel.setScoreString(scoreString);
		//north panel
		JPanel northPanel = new JPanel(new GridLayout(1,4,5,5));
		northPanel.setBorder(BorderFactory.createTitledBorder("Information"));
		if(featureTableModel != null)
			this.featureLabel.setText("# of features: " + featureTableModel.getRowCount());					
		else
			this.featureLabel.setText("# of features: 0");		
		this.totalScoreLabel.setText("Total Score: 0.0");
		northPanel.add(this.featureLabel);
		northPanel.add(this.totalScoreLabel);
		northPanel.add(this.saveButton);
		northPanel.add(this.closeButton);
		this.saveButton.addActionListener(this);
		this.closeButton.addActionListener(this);
		//center panel
		JTable table = new JTable(featureTableModel);
		table.setEnabled(false);
		table.getColumnModel().getColumn(0).setMaxWidth(45);
		table.getColumnModel().getColumn(2).setMinWidth(80);
		table.getColumnModel().getColumn(2).setMaxWidth(80);
		JScrollPane sp = new JScrollPane(table);
		JPanel centerPanel = new JPanel(new GridLayout(1,1,5,5));
		centerPanel.setBorder(BorderFactory.createTitledBorder("Features"));
		centerPanel.add(sp);
			
		setTitle("Current Features");
		setLayout(new BorderLayout(5,5));
		add(northPanel,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
	}
	
	public void updateCFSScore(String value){
		this.totalScoreLabel.setText("CFS Score: " + value);
	}
	
	public void updateFeature(String value){
		this.featureLabel.setText("# of features: " + value);
	}

	private void saveFeatures(){
		try{			
			String filename = chooseOutputLocation();
			if(filename == null)
				return;
			BufferedWriter output = new BufferedWriter(new FileWriter(filename));
			List<Feature> data = new ArrayList<Feature>(this.featureTableModel.getData());
			for(int x = 0; x < data.size(); x++){
				output.write("Step 2: " + data.get(x).saveString(null));
				output.newLine();
			}
			output.close();
			JOptionPane.showMessageDialog(this, "Features saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
		}catch(Exception e){e.printStackTrace();}
	}
	
	private String chooseOutputLocation(){
		JFileChooser fc;				    	
		if(!this.outputLocationTextField.getText().equals("Please choose output location"))					
			fc = new JFileChooser(this.outputLocationTextField.getText());
		else
			fc = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Features Files", "features");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();				        
			String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".features") == -1)
				savingFilename += ".features";
			return savingFilename;
		}
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(this.saveButton))
			saveFeatures();
		else if(ae.getSource().equals(this.closeButton))
			this.dispose();
	}
}
