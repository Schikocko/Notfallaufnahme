package Krankenhaus;

import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.*;

public class Angestellter extends SimProcess {
	/**
	 * routine Roentgen der Patienten
	 */
	private Notfallaufnahme myModel;
	//Die blankoQueue, werden unten mit den Queues des entsprechnenden Typens gef�llt
	private desmoj.core.simulator.ProcessQueue<Patient> angestellterQueue;
	private desmoj.core.simulator.ProcessQueue<Angestellter> untaetigerAngestellterQueue;
	//Typ des Angestellten, um die richtige Bearbeitungsdauer zu bestimmen
	private String typ; 
	
	/**
	    * Constructor der Roentgenbehandlung
	    *
	    *
	    * @param owner das Modell zu dem die Aufnahme geh�rt
	    * @param name Der Name der Behandlung
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public Angestellter(Model owner, String name, boolean showInTrace, desmoj.core.simulator.ProcessQueue<Patient> anQueue, //Queue f�r wartende Patienten
			   desmoj.core.simulator.ProcessQueue<Angestellter> untaetQueue, String t  ) { // untaetQueue = Wartschlange f�r untaetige Angestellte, t= typ f�r die zuordnung der Behandlungzeit

	      super(owner, name, showInTrace);
	      myModel = (Notfallaufnahme)owner;
	      angestellterQueue = anQueue;
	      untaetigerAngestellterQueue = untaetQueue;
	      typ = t;
	   	   }

	   //�berpr�ft den Typen und gibt die entsprechende Bearbeitungszeit zur�ck
	   private double setTime()
	   {
		   if (typ == "Aufnahme")
		   {
			   return myModel.getAufnahmeZeit();
		   }
		   if (typ == "Gips")
		   {
			   return myModel.getGipsZeit();
		   }
		   if (typ == "Roentgen")
		   {
			   return myModel.getRoentgenZeit();
		   }
		   else
			   return 0.0;
		  
		   
	   }
	   
	   /**
	    * Lebenszyklus der Roentgenbehandlung
	    *
	    * Ein Loop der folgendes macht
	    * �berpr�fung ob Patienten warten:
	    * WEnn jemand wartet:
	    *   a) entferne Patient aus Warteschlange
	    *   b) bearbeite Patient
	    *   c) wieder von vorne
	    * keine Patient wartet
	    *   a) warte (passivate) bis ein Patient ankommt (dieser reaktiviert die AUfnahme)
	    *   b) wieder von vorne
	    */
	   public void lifeCycle() {

	      // Aufnahme beginnt arbeit mit modellstart
	      while (true) {
	         // �berpr�fung, ob jemand wartet
	         if (angestellterQueue.isEmpty()) {
	        	 //keiner wartet
	        	 //in �nt�tigeAufnahme Wartetschlange einf�gen
	        	 untaetigerAngestellterQueue.insert(this);
	        	 passivate(); //warte bis vom Patient reaktiviert
	         }
	         else {
	        	 //ein Patient wartet
	        	 //ersten Patienten aus der Queue nehmen
	        	 Patient naechsterPatient = angestellterQueue.first();
	        	 angestellterQueue.remove(naechsterPatient);
	        	 //bearbeitung des Patienten
	        	 hold(new TimeSpan(setTime(), TimeUnit.MINUTES));
	        	 
	        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
	        	 naechsterPatient.activate(new TimeSpan (0.0));
	        	 
	         } 
	         }
	      }

	   
}
