package sirius.trainer.features.gui.geneticalgorithm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import sirius.main.ApplicationData;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateFeatures;
import sirius.trainer.main.SiriusSettings;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step2.DefineFeaturePane;
import sirius.trainer.step2.FeatureTableModel;
import sirius.utils.FastaFormat;

public class GeneticAlgorithmDialog extends JFrame implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JTextField outputLocationTextField = new JTextField("Please choose output location",35);
	private JButton eliteButton = new JButton("Elite");
	private JButton settingsButton = new JButton("Settings");
	private JButton runButton;
	private JLabel runtimeLabel = new JLabel(" (D:H:M:S) 00:00:00:00 ");
	private StatusPane statusPane = new StatusPane("Ready.");
	private XYSeries dataPoints = new XYSeries("(Score,Generation)");
	private JButton chooseButton = new JButton("   Choose   ");
	public RunGA run = new RunGA(false); 
	private SettingsDialog settingsDialog;
	private ApplicationData appData;
	private FeatureTableModel featureTableModel;
	private Thread gaThread;
	private ViewFeaturesDialog dialog;
	private boolean calledFromStep4;
	
	public void setRandomNumber(int rand){this.settingsDialog.setRandomNumber(rand);}
	public int getRandomNumber(){return this.settingsDialog.getRandomNumber();}
	public SettingsDialog getSettingsDialog(){return this.settingsDialog;}
	public void setOutputLocation(String string){this.outputLocationTextField.setText(string);}
	public JTextField getOutputLocation(){return this.outputLocationTextField;}
	public boolean isGAThreadAlive(){return this.gaThread.isAlive();}
	public void setApplicationData(ApplicationData appData){this.appData = appData;}
	public void clearPreviousGAResults(){
		this.featureTableModel.markAll();
		this.featureTableModel.removeMarked();
		this.dataPoints.clear();
	}
	
	public GeneticAlgorithmDialog(ApplicationData appData, FeatureTableModel featureTableModel, boolean calledFromStep4){
		this.calledFromStep4 = calledFromStep4;
		if(calledFromStep4){
			this.runButton = new JButton("Apply");
			this.appData = appData;
		}else{
			this.runButton = new JButton("Run");
			this.appData = new ApplicationData(appData);
		}
		setTitle("Genetic Algorithm");
		setLayout(new BorderLayout(5,5));		
		if(this.appData != null)
			this.appData.setStatusPane(this.statusPane);
		this.featureTableModel = new FeatureTableModel(featureTableModel,true,true);		
		this.settingsDialog = new SettingsDialog(this.outputLocationTextField.getText());
		this.dialog = new ViewFeaturesDialog(this.featureTableModel,this.outputLocationTextField, "Score");
		//Output Location Panel
		JPanel outputLocationPanel = new JPanel(new BorderLayout(5,5));
		outputLocationPanel.setBorder(BorderFactory.createTitledBorder("Output Location"));
		outputLocationPanel.add(this.outputLocationTextField,BorderLayout.CENTER);		
		outputLocationPanel.add(this.chooseButton,BorderLayout.EAST);
		this.outputLocationTextField.setEditable(false);
		this.chooseButton.addActionListener(this);
		//Controls Panel
		JPanel settingsPanel = new JPanel(new GridLayout(1,3,5,5));
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
		settingsPanel.add(this.eliteButton);
		settingsPanel.add(this.settingsButton);
		settingsPanel.add(this.runButton);
		this.eliteButton.addActionListener(this);
		this.settingsButton.addActionListener(this);
		this.runButton.addActionListener(this);
		//Runtime Panel
		JPanel runtimePanel = new JPanel(new GridLayout(1,1,5,5));
		runtimePanel.setBorder(BorderFactory.createTitledBorder("Total Runtime"));
		runtimePanel.add(this.runtimeLabel);		
		this.runtimeLabel.setHorizontalAlignment(JTextField.RIGHT);
		//Results Panel
		JPanel resultsPanel = new JPanel(new GridLayout(1,1,5,5));
		JFreeChart chart = createChart(createDataset());
		ChartPanel panel = new ChartPanel(chart);
		resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		resultsPanel.add(panel);
			
		JPanel northPanel = new JPanel(new BorderLayout(5,5));
		northPanel.add(runtimePanel,BorderLayout.WEST);
		northPanel.add(outputLocationPanel,BorderLayout.CENTER);
		northPanel.add(settingsPanel,BorderLayout.EAST);
		
		add(northPanel,BorderLayout.NORTH);
		add(resultsPanel,BorderLayout.CENTER);
		add(this.statusPane,BorderLayout.SOUTH);
		
		// Add window listener to close properly
        this.addWindowListener
        (
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    GeneticAlgorithmDialog.this.windowClosed();
                }
            }
        );  
	}
	
	public GeneticAlgorithmDialog(ApplicationData appData, FeatureTableModel featureTableModel){
		this(appData, featureTableModel, false);
	}
	
	public void updateCFS(String value){
		this.dialog.updateCFSScore(value);
	}
	
	public void updateFeature(String value){
		this.dialog.updateFeature(value);
	}
	
	/**
     * Shutdown procedure when run as an application.
     */
    protected void windowClosed() {
    	//Have the thread running in the background
    	Thread waitThread = new Thread(){
    		public void run(){
    			GeneticAlgorithmDialog.this.setVisible(false);
    			GeneticAlgorithmDialog.this.run.setValue(false);
    	    	//wait till thread ends
    	    	while(GeneticAlgorithmDialog.this.gaThread != null && GeneticAlgorithmDialog.this.gaThread.isAlive())
    	    		try{
    	    			Thread.sleep(1000);
    	    		}catch(Exception e){}
    	    	// Exit application.
    	    		GeneticAlgorithmDialog.this.dispose();
    		}
    	};    	
    	waitThread.setPriority(Thread.MIN_PRIORITY);
    	waitThread.start();
    }
	
	private XYDataset createDataset() {			  
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(this.dataPoints);
        return dataset;
	}
	
	private JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            "",  // title
            "Generation",             // x-axis label
            "Elite Score",   // y-axis label
            dataset,            // data
            PlotOrientation.VERTICAL,//PlotOrientation
            false,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }
        return chart;
    }

	@Override
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.runButton)){
			if(this.runButton.getText().equalsIgnoreCase("Run")){
				if(this.outputLocationTextField.getText().equals("Please choose output location")){
					JOptionPane.showMessageDialog(this, "Please choose output location first!", "Invalid Output Location", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				this.runButton.setText("Stop");
				this.run.setValue(true);
				this.settingsDialog.setEnabled(false);
				this.chooseButton.setEnabled(false);
				this.outputLocationTextField.setEnabled(false);
				run();
			}else if(this.runButton.getText().equalsIgnoreCase("Stop")){
				int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to stop GA?");
				if(answer == JOptionPane.YES_OPTION){
					this.run.setValue(false);
					this.statusPane.setSuffix(" - Will terminate @ the end of this Generation!");
				}
			}else{
				if(this.outputLocationTextField.getText().equals("Please choose output location")){
					JOptionPane.showMessageDialog(this, "Please choose output location first!", "Invalid Output Location", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				this.dispose();
			}							
		}else if(ae.getSource().equals(this.settingsButton)){
			showSettings();
		}else if(ae.getSource().equals(this.chooseButton)){
			chooseOutputLocation();
		}else if(ae.getSource().equals(this.eliteButton))
			showEliteFeatures();
	}
	
	private void showEliteFeatures(){		
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	public void run(boolean needUI, final List<FastaFormat> posFastaList, final List<FastaFormat> negFastaList, final String outputFilename,final int fold, final int randomNumber){
		//I will start two additional thread here
		//one is for time
		//another is for genetic algorithm
		
		//GA
		this.gaThread = new Thread(){
			public void run(){			
				try{
				if(GeneticAlgorithmDialog.this.calledFromStep4){
					new GeneticAlgorithm(GeneticAlgorithmDialog.this.settingsDialog,
						GeneticAlgorithmDialog.this.outputLocationTextField.getText(), GeneticAlgorithmDialog.this.appData,
						GeneticAlgorithmDialog.this, GeneticAlgorithmDialog.this.featureTableModel,GeneticAlgorithmDialog.this.dataPoints,
						GeneticAlgorithmDialog.this.run, GeneticAlgorithmDialog.this.statusPane, 
						posFastaList, negFastaList, fold, randomNumber);
				}else{
					new GeneticAlgorithm(GeneticAlgorithmDialog.this.settingsDialog,
						GeneticAlgorithmDialog.this.outputLocationTextField.getText(), GeneticAlgorithmDialog.this.appData,
						GeneticAlgorithmDialog.this, GeneticAlgorithmDialog.this.featureTableModel,GeneticAlgorithmDialog.this.dataPoints,
						GeneticAlgorithmDialog.this.run, -1, -1);
				}
				}catch(Exception e){e.printStackTrace();}
				if(GeneticAlgorithmDialog.this.calledFromStep4){
					//Genetic Algorithm completed either finished or stopped
					GeneticAlgorithmDialog.this.runButton.setText("Apply");
					//Load MaxMCC features
					List<Feature> featureList = DefineFeaturePane.loadFeatureFile(
							GeneticAlgorithmDialog.this.outputLocationTextField.getText() + File.separator + "maxScoreFeature.features", 
							GeneticAlgorithmDialog.this.appData);				
					//generate features
					new GenerateFeatures(GeneticAlgorithmDialog.this.appData,featureList,posFastaList,negFastaList,outputFilename);					
				}else
					GeneticAlgorithmDialog.this.runButton.setText("Run");
				GeneticAlgorithmDialog.this.settingsDialog.setEnabled(true);
				GeneticAlgorithmDialog.this.chooseButton.setEnabled(true);
				GeneticAlgorithmDialog.this.outputLocationTextField.setEnabled(true);				
			}
		};
		this.gaThread.setPriority(Thread.MIN_PRIORITY);
		this.gaThread.start();
		if(needUI){
			//Time
			Thread runtimeThread = new Thread(){
				public void run(){
					long trainTimeStart = System.currentTimeMillis();
					DecimalFormat df = new DecimalFormat("00");
					while(GeneticAlgorithmDialog.this.gaThread.isAlive()){
						long trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
						int sec = (int) (trainTimeElapsed / 1000.0);
						int min = sec / 60;
						int hour = min / 60;
						int day = hour / 24;
						String secString = df.format(sec % 60);
						String minString = df.format(min % 60);
						String hourString = df.format((hour % 60) % 24);
						String dayString = df.format(day % 24);
						String runtimeString = " (D:H:M:S) " + dayString + ":" + hourString + ":" + minString + ":" + 
							secString + " ";
						GeneticAlgorithmDialog.this.runtimeLabel.setText(runtimeString);
						try{
							Thread.sleep(500);
						}catch(Exception e){}
					}				
				}
			};
			runtimeThread.setPriority(Thread.MIN_PRIORITY);
			runtimeThread.start();
		}
	}
	
	public void run(boolean needUI){
		this.run(needUI, null, null, null, -1, -1);
	}
	
	private void run(){
		this.run(true);
	}
	
	private void showSettings(){		    		   
		settingsDialog.setModal(true);
		settingsDialog.pack();
		settingsDialog.setLocationRelativeTo(this);
		settingsDialog.setVisible(true);  
	}
	
	private void chooseOutputLocation(){
		JFileChooser chooser;		
    	String lastLocation = SiriusSettings.getInformation("LastGAOutputDirectoryLocation: ");
		if(lastLocation == null)
			lastLocation = SiriusSettings.getInformation("LastWorkingDirectoryLocation: ");							
		if(lastLocation == null)
			chooser = new JFileChooser();	
		else
			chooser = new JFileChooser(lastLocation);    			
		chooser.setDialogTitle("Set Genetic Algorithm Output Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			this.outputLocationTextField.setText(chooser.getSelectedFile().toString());  
			SiriusSettings.updateInformation("LastGAOutputDirectoryLocation: ", chooser.getSelectedFile().getAbsolutePath());
	   	}
	    else{//no selection
	    }
	}
}