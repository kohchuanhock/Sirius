package sirius.nnsearcher.main;

import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import sirius.main.ApplicationData;
import sirius.trainer.features.gui.PhysioChemicalDialog;
import sirius.trainer.features.gui.kgram.KgramDialog;
import sirius.trainer.features.gui.multiplekgram.MultipleKgramDialog;
import sirius.trainer.features.gui.positionspecificfeature.PositionRelatedDialog;
import sirius.trainer.features.gui.ratio.RatioDialog;

public class AddConstraintsDialog extends JDialog implements ActionListener{	
	private static final long serialVersionUID = sirius.Sirius.version;
	
	private JButton kGramButton;
	private JButton multipleKGramButton;
	private JButton ratioButton;
	private JButton positionRelatedButton;
	private JButton physioChemicalButton;
	private JFrame parent;
	private ApplicationData applicationData;
	private MustHaveTableModel model;
	
	public AddConstraintsDialog(JFrame parent,MustHaveTableModel model,ApplicationData applicationData){
		this.parent = parent;
		this.applicationData = applicationData;
		this.model = model;
		
		
		setTitle("Select Feature Type");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);		
		
		this.positionRelatedButton = new JButton("Position-Related");
		this.positionRelatedButton.addActionListener(this);
		this.kGramButton = new JButton("K-grams");
		this.kGramButton.addActionListener(this);
		this.multipleKGramButton = new JButton("Multiple K-grams");
		this.multipleKGramButton.addActionListener(this);
		this.ratioButton = new JButton("Ratio of (#X:#Y)");
		this.ratioButton.addActionListener(this);
		this.physioChemicalButton = new JButton("Physiochemical");
		this.physioChemicalButton.addActionListener(this);
		
		if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){
			//protein sequences
			setLayout(new GridLayout(3,2));
			add(this.positionRelatedButton);
			add(this.kGramButton);
			add(this.multipleKGramButton);
			add(this.ratioButton);
			add(this.physioChemicalButton);
		}else{			
			//dna sequences			
			setLayout(new GridLayout(2,2));
			add(this.positionRelatedButton);
			add(this.kGramButton);
			add(this.multipleKGramButton);
			add(this.ratioButton);
		}
		//setSize(440,190);
		this.pack();
	}

	
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.positionRelatedButton)){
			PositionRelatedDialog dialog = new PositionRelatedDialog(null,null,applicationData,model);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true);   
		}else if(ae.getSource().equals(this.kGramButton)){
			KgramDialog dialog = new KgramDialog(null,null,applicationData,model);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true);    
		}else if(ae.getSource().equals(this.multipleKGramButton)){
			MultipleKgramDialog dialog = new MultipleKgramDialog(null,null,applicationData,model);
    		dialog.setLocationRelativeTo(parent);
    		dialog.setVisible(true);
		}else if(ae.getSource().equals(this.ratioButton)){
			RatioDialog dialog = new RatioDialog(null,null,applicationData,model);
    		dialog.setLocationRelativeTo(parent);
    		dialog.setVisible(true); 
		}else if(ae.getSource().equals(this.physioChemicalButton)){
			PhysioChemicalDialog dialog = new PhysioChemicalDialog(null,null,applicationData,model);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true); 
		}
	}
		
}