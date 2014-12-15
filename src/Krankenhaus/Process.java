package Krankenhaus;

import desmoj.core.simulator.*;
import desmoj.core.dist.*;
import java.util.concurrent.TimeUnit;

public class Process extends Model {
	
	/**
	    * Process constructor.
	    *
	    * Creates a new Process model via calling
	    * the constructor of the superclass.
	    *
	    * @param owner the model this model is part of (set to null when there is 
	    *              no such model)
	    * @param modelName this model's name
	    * @param showInReport flag to indicate if this model shall produce output 
	    *                     to the report file
	    * @param showInTrace flag to indicate if this model shall produce output 
	    *                    to the trace file
	    */
	   public Process(Model owner, String modelName, boolean showInReport, boolean showInTrace) 
	   {
	      super(owner, modelName, showInReport, showInTrace);
	   }
	   

	   /**
	    * Model parameter: Anzahl der Aufnahmekräfte
	    */
	   protected static int NUM_AUFN = 1;
	   
	   /**
	    * Zufallszahl für die Zeit der Anknuft der Patienten
	    */
	   
	   /**
	    * Model Parameter: Anzahl der routine Ärtze
	    */
	   protected static int NUM_DOC_R = 2 ;
	   
	   /**
	    * Model Parameter: Anzahl der Komplexer Ärtze
	    */
	   protected static int NUM_DOC_K = 2;
	   
	   /**
	    * Zeit bis ein neuer Patient ankommt
	    */
	   private desmoj.core.dist.ContDistExponential patientAnkunftszeit;
	
	   /**
	    * Behandlungszeit routine
	    */
	   private desmoj.core.dist.ContDistExponential behandlungszeitR; 
	   
	   /**
	    * Behandlungszeit komplex
	    */
	   private desmoj.core.dist.ContDistExponential behandlungszeitK;
	   
	   /**
	    * Zufallszeit wie lange die Aufnahme der Patienten dauert
	    */
	   private desmoj.core.dist.ContDistExponential AufnahmeZeit;
	   
	   /**
	    * Warteschlange, in die sich alle Patienten einriehen um von der 
	    * Aufnahme bedient zu werden
	    */
	   protected desmoj.core.simulator.ProcessQueue<Patient> aufnahmeQueue;
	   
	   /**
	    * Warteschlange, in die sich alle Patienten einriehen um von der 
	    * routine Behanldung bedient zu werden
	    */
	   protected desmoj.core.simulator.ProcessQueue<Patient> behandlungRQueue;
	  
	   /**
	    * Warteschlange, in die sich alle Patienten einriehen um von der 
	    * komplex Behanldung bedient zu werden
	    */
	   protected desmoj.core.simulator.ProcessQueue<Patient> behandlungKQueue;
	   
	   /**
	    * Warteschlange für untätige Aufnahmekräfte, sie warten auf die Ankunft neuer Patienten
	    */
	   protected desmoj.core.simulator.ProcessQueue<Aufnahme> untaetigeAufnahmeQueue;
	   
	   /**
	    * Warteschlange für untätige routine Ärtze, sie warten auf die Ankunft neuer Patienten
	    */
	   protected desmoj.core.simulator.ProcessQueue<BehandlungR> untaetigeBehandlungRQueue;
	   
	   /**
	    * Warteschlange für untätige komplexe Ärtze, sie warten auf die Ankunft neuer Patienten
	    */
	   protected desmoj.core.simulator.ProcessQueue<BehandlungK> untaetigeBehandlungKQueue;
	   
	   /**
	    * Gibt ein Beispiel der Aufnahmezeit eines Patienten zurück
	    * 
	    * @return double Aufnahmezeitbeispiel
	    */
	   public double getAufnahmeZeit() 
	   {
	      return AufnahmeZeit.sample();
	   }
	   
	   /**
	    * Gibt ein Beispiel für die Patientenankunftszeit zurück
	    * 
	    * @return double Patientenanknuftszeit Beispiel
	    */
	   public double getPatientAnkunftsZeit() 
	   {
	      return patientAnkunftszeit.sample();
	   }
	   
	   /**
	    * Gibt ein Beispiel der Behnalungszeit eines routine Patienten zurück
	    * 
	    * @return double Behandlungszeitbeispiel
	    */
	   public double getBehandlungszeitR() 
	   {
	      return behandlungszeitR.sample();
	   }	 
	   
	   /**
	    * Gibt ein Beispiel der Behnalungszeit eines komplexen Patienten zurück
	    * 
	    * @return double Behandlungszeitbeispiel
	    */
	   public double getBehandlungszeitK() 
	   {
	      return behandlungszeitK.sample();
	   }
	   
	   /**
	    * Returns a description of the model to be used in the report.
	    * @return model description as a string
	    */
	   public String description() {
		   return "Einfaches Modell einer Notfallaufnahme mit mehreren Behandlungsräumen, Verschieden Patiententypen und 2 Typen von Ärtzen";
	   }

	   /**
	    * Activates dynamic model components (simulation processes).
	    *
	    * This method is used to place all events or processes on the
	    * internal event list of the simulator which are necessary to start
	    * the simulation.
	    *
	    * Hier, Patientenersteller, Aufnahme, Behandlungen, Röntgen und Gips werden erstellt und aktiviert 
	    * 
	    */
	   public void doInitialSchedules()
	   {
		// erstellt und aktiviert die gegebene Anzahl an Aufnahmekräften
		   for (int i=0; i < NUM_AUFN; i++)
		   {
		      Aufnahme aufnahme = new Aufnahme(this, "Aufnahmekraft", true);
		      aufnahme.activate(new TimeSpan(0));
		         // Wird sofort aktiviert, da die Aufnahme sofort anfängt zu Arbeiten
		   }
		   
			// erstellt und aktiviert die gegebene Anzahl an routine Ärzten
		   for (int i=0; i < NUM_AUFN; i++)
		   {
			   BehandlungR behandlungR = new BehandlungR(this, "routine Arzt", true);
			   behandlungK.activate(new TimeSpan(30)); 
		         // Wird nach 30min aktiviert, da die Ärtze erst 30min nach Beginnt mit den behandlungen anfangen
		   }
		   
			// erstellt und aktiviert die gegebene Anzahl an komplexen Ärzten
		   for (int i=0; i < NUM_AUFN; i++)
		   {
		      BehandlungK behandlungK = new BehandlungK(this, "komplexer Arzt", true);
		      behandlungK.activate(new TimeSpan(30)); 
		         // Wird nach 30min aktiviert, da die Ärtze erst 30min nach Beginnt mit den behandlungen anfangen
		   }
		   
		   
		   //erstellt und aktiviert den Patientenersteller
		   PatientenErsteller ersteller = new PatientenErsteller(this,"PatientenAnknuft",false);
		   ersteller.activate(new TimeSpan(0));
		   	    // Wird sofort aktiviert, das Patienten mit start unserer Simulation ankommen
	   }

	   /**
	    * Initialises static model components like distributions and queues.
	    */
	   public void init() 
	   {
		// erstellt  die Aufnahmezeit
		   // Parameters:
		   // this                     = belongs to this model
		   // "AufnahmezeitStream"	   = the name of the stream
		   // 0.6                      = Durchschnittszeit der Patientenaufnahme
		   // true                     = show in report?
		   // false                    = show in trace?
		   AufnahmeZeit= new ContDistExponential(this, "AufnahmezeitStream", 0.6 , true, false);
		   //AUfnahmezeiten dürfen nicht negativ sein
		   AufnahmeZeit.setNonNegative (true);
		   
		// erstellt  die Patientenankunftszeit
		   // Parameters:
		   // this                          = belongs to this model
		   // "patientAnkunftszeitStream"   = the name of the stream
		   // 0.6                           = Durchschnittszeit in der neue Patienten das System betreten
		   // true                          = show in report?
		   // false                         = show in trace?
		   patientAnkunftszeit= new ContDistExponential(this, "patientAnkunftszeitStream", 1.2 , true, false);
		   //Anknuftszeiten dürfen nicht negativ sein
		   patientAnkunftszeit.setNonNegative (true);
		   
			// erstellt  die Komplexbehandlungszeit
		   // Parameters:
		   // this                          = belongs to this model
		   // "behandlungszeitKStream"   = the name of the stream
		   // 4.6                           = Durchschnittszeit in der neue Patienten das System betreten
		   // true                          = show in report?
		   // false                         = show in trace?
		   behandlungszeitK= new ContDistExponential(this, "behandlungszeitKStream", 4.6 , true, false);
		   //Behnadlungszeiten dürfen nicht negativ sein
		   behandlungszeitK.setNonNegative (true);
		   
			// erstellt  die routinebehandlungszeit
		   // Parameters:
		   // this                          = belongs to this model
		   // "behandlungszeitRStream"   = the name of the stream
		   // 4.6                           = Durchschnittszeit in der neue Patienten das System betreten
		   // true                          = show in report?
		   // false                         = show in trace?
		   behandlungszeitR= new ContDistExponential(this, "behandlungszeitRStream", 3.2 , true, false);
		   //Behnadlungszeiten dürfen nicht negativ sein
		   behandlungszeitR.setNonNegative (true);
		   
		   // erstellt eine neue AufnahmeQueue
		   // Parameters:
		   // this          = belongs to this model
		   // "aufnahmeQueue" = the name of the Queue
		   // true          = show in report?
		   // true          = show in trace?
		   aufnahmeQueue = new ProcessQueue<Patient>(this, "aufnahmeQueue", true, true);
		   
		   // erstellt eine neue Rountine Behandlung
		   // Parameters:
		   // this          = belongs to this model
		   // "behandlungRQueue" = the name of the Queue
		   // true          = show in report?
		   // true          = show in trace?
		   behandlungRQueue = new ProcessQueue<Patient>(this, "behandlungRQueue", true, true);
		   
		   // erstellt eine neue komplex Behandlung
		   // Parameters:
		   // this          = belongs to this model
		   // "behandlungKQueue" = the name of the Queue
		   // true          = show in report?
		   // true          = show in trace?
		   behandlungKQueue = new ProcessQueue<Patient>(this, "behandlungKQueue", true, true);
		   
		   // erstellt eine neue untaetigeAufnahmeQueue
		   // Parameters:
		   // this          = belongs to this model
		   // "untaetigeAufnahmeQueue" = the name of the Queue
		   // true          = show in report?
		   // true          = show in trace?
		   untaetigeAufnahmeQueue = new ProcessQueue<Aufnahme>(this, "untaetigeAufnahmeQueue", true, true);
		   
		   // erstellt eine neue untaetigeBehandlungRQueue
		   // Parameters:
		   // this          = belongs to this model
		   // "untaetigeBehandlungRQueue" = the name of the Queue
		   // true          = show in report?
		   // true          = show in trace?
		   untaetigeBehandlungRQueue = new ProcessQueue<BehandlungR>(this, "untaetigeBehandlungRQueue", true, true);
		   
		   // erstellt eine neue untaetigeBehandlungKQueue
		   // Parameters:
		   // this          = belongs to this model
		   // "untaetigeBehandlungKQueue" = the name of the Queue
		   // true          = show in report?
		   // true          = show in trace?
		   untaetigeBehandlungKQueue = new ProcessQueue<BehandlungR>(this, "untaetigeBehandlungKQueue", true, true);
	   }
	   
	   public static void main(java.lang.String[] args) 
	   {
		// erstellt Modell and Experiment
		   Process model = new Process(null, "Modell einer Notfallaufnahme", true, true);
		  
		   //Erstellt das Experiment
		   Experiment exp = new Experiment("Notfallaufnahme", TimeUnit.SECONDS, TimeUnit.MINUTES, null);
		   
		   //verbindet das Modell und das Experiment
		   model.connectToExperiment(exp);
		   
		// Setzt die Experiment Paramenter
		   exp.setShowProgressBar(true);  
		   exp.stop(new TimeInstant(540, TimeUnit.MINUTES));   // Setzt die Simulationdauer auf 540 min (9 Stunden)
		   exp.tracePeriod(new TimeInstant(0), new TimeInstant(100, TimeUnit.MINUTES));
		                                              
		   exp.debugPeriod(new TimeInstant(0), new TimeInstant(60, TimeUnit.MINUTES));  
		   
		   //startet das Experiment bei 0.0
		   exp.start();
		   exp.report(); //erstellt den Report
		   exp.finish();//beenedet das Experiment und alle noch laufenden Teile
	   }




}
