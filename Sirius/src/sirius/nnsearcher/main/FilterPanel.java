package sirius.nnsearcher.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import sirius.main.ApplicationData;
import sirius.predictor.main.SequenceNameTableModel;
import sirius.trainer.main.StatusPane;

public class FilterPanel extends JPanel implements MouseListener, ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;

	private JFrame parent;
	private JTable mustHaveTable;	
	private MustHaveTableModel mustHaveTableModel;	
	private ApplicationData applicationData;

	private JButton addMustHaveButton = new JButton("  Add  ");
	private JButton deleteMustHaveButton = new JButton(" Delete ");
	private JButton loadConstraintsButton = new JButton(" Load ");
	private JButton saveConstraintsButton = new JButton(" Save ");	

	private FastaFormatWithScoreTableModel fastaFormatWithScoreTableModel = null;
	private SequenceNameTableModel sequenceNameTableModel = null;
	private StatusPane statusPane;
	
	public FilterPanel(JFrame mainFrame, SequenceNameTableModel sequenceNameTableModel, ApplicationData applicationData, StatusPane statusPane){
		this.sequenceNameTableModel = sequenceNameTableModel;
		this.init(mainFrame, applicationData, statusPane);
	}
	
	public FilterPanel(JFrame mainFrame, FastaFormatWithScoreTableModel fastaFormatWithScoreTableModel, ApplicationData applicationData, StatusPane statusPane){
		this.fastaFormatWithScoreTableModel = fastaFormatWithScoreTableModel;
		this.init(mainFrame, applicationData, statusPane);
	}
	
	private void init(JFrame mainFrame, ApplicationData applicationData, StatusPane statusPane){
		this.statusPane = statusPane;
		this.applicationData = applicationData;
		this.parent = mainFrame;
		
		this.setLayout(new BorderLayout());	
		this.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Filter"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JPanel mustHaveTablePanel = new JPanel(new BorderLayout());
		this.mustHaveTableModel = new MustHaveTableModel();
		this.mustHaveTable = new JTable(this.mustHaveTableModel);
		this.mustHaveTableModel.setTable(this.mustHaveTable);
		this.mustHaveTableModel.setFrame(mainFrame);
		this.mustHaveTable.addMouseListener(this);
		JScrollPane mustHaveTableScrollPane = new JScrollPane(mustHaveTable);
		mustHaveTablePanel.add(mustHaveTableScrollPane, BorderLayout.CENTER);
		mustHaveTable.getColumnModel().getColumn(0).setMaxWidth(50);
		mustHaveTable.getColumnModel().getColumn(1).setMinWidth(300);
		this.loadConstraintsButton.addActionListener(this);
		this.saveConstraintsButton.addActionListener(this);
		this.addMustHaveButton.addActionListener(this);		
		this.deleteMustHaveButton.addActionListener(this);	
		JPanel mustHaveButtonPanel = new JPanel();
		mustHaveButtonPanel.add(this.addMustHaveButton);
		mustHaveButtonPanel.add(this.deleteMustHaveButton);		
		mustHaveButtonPanel.add(this.loadConstraintsButton);
		mustHaveButtonPanel.add(this.saveConstraintsButton);
		
		mustHaveTablePanel.add(mustHaveButtonPanel, BorderLayout.SOUTH);
		JPanel tablePanel = new JPanel(new GridLayout(1,1));
		tablePanel.add(mustHaveTablePanel);		
		this.add(tablePanel,BorderLayout.CENTER);
	}
	
	public MustHaveTableModel getMustHaveTableModel(){
		return this.mustHaveTableModel;
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		if(me.getClickCount() == 2 && me.getSource().equals(this.mustHaveTable)){
			if(this.mustHaveTable.getSelectedRow() != -1){
				this.mustHaveTableModel.showConstraint(this.mustHaveTable.getSelectedRow());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(this.addMustHaveButton)){
			addConstraint(this.mustHaveTableModel);
		}else if(ae.getSource().equals(this.deleteMustHaveButton)){
			deleteConstraint(this.mustHaveTableModel);
		}else if(ae.getSource().equals(this.loadConstraintsButton)){
			loadConstraints();
		}else if(ae.getSource().equals(this.saveConstraintsButton)){
			saveConstraints();
		}	
	}
	
	private void loadConstraints(){
		if(this.mustHaveTableModel.loadConstraints()){	
			if(this.fastaFormatWithScoreTableModel != null)
				this.fastaFormatWithScoreTableModel.loadConstraints(this.applicationData, this.statusPane.getStatusLabel());
			else{
				this.sequenceNameTableModel.loadConstraints(this.applicationData, this.statusPane.getStatusLabel());
			}
		}
	}
	
	private void saveConstraints(){
		this.mustHaveTableModel.saveConstraints();
	}
	
	private void addConstraint(MustHaveTableModel model){		
		AddConstraintsDialog dialog = new AddConstraintsDialog(parent,model,applicationData);
		dialog.setLocationRelativeTo(parent);    		
		dialog.setVisible(true);				
		if(this.fastaFormatWithScoreTableModel != null){
			this.fastaFormatWithScoreTableModel.addConstraints(this.applicationData, this.statusPane.getStatusLabel());
		}else{
			this.sequenceNameTableModel.addConstraints(this.applicationData, this.statusPane.getStatusLabel());
		}
	}
	
	private void deleteConstraint(MustHaveTableModel model){
		if(this.mustHaveTable.getSelectedRow() != -1){
			model.delete(this.mustHaveTable.getSelectedRow());
			if(this.mustHaveTableModel.getRowCount() > 0)
				this.mustHaveTable.setRowSelectionInterval(0, 0);	
			if(this.fastaFormatWithScoreTableModel != null){
				this.fastaFormatWithScoreTableModel.deleteConstraints(this.applicationData, this.statusPane.getStatusLabel());
			}else{
				this.sequenceNameTableModel.deleteConstraints(this.applicationData, this.statusPane.getStatusLabel());
			}
		}
	}
}
