package Krankenhaus;
import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.*;

public class Roentgen extends SimProcess {
	
	/**
	 * routine Roentgen der Patienten
	 */
	
	private Process myModel;
	
	/**
	    * Constructor der Roentgenbehandlung
	    *
	    *
	    * @param owner das Modell zu dem die Aufnahme geh�rt
	    * @param name Der Name der Behandlung
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public Roentgen(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      // speichert den referenz, zu welchem Modell die Aufnhame geh�rt
	      myModel = (Process)owner;
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
	         if (myModel.roentgenQueue.isEmpty()) {
	        	 //keiner wartet
	        	 //in �nt�tigeAufnahme Wartetschlange einf�gen
	        	 myModel.untaetigeRoentgenQueue.insert(this);
	        	 passivate(); //warte bis vom Patient reaktiviert
	         }
	         else {
	        	 //ein Patient wartet
	        	 //ersten Patienten aus der Queue nehmen
	        	 Patient naechsterPatient =myModel.roentgenQueue.first();
	        	 myModel.roentgenQueue.remove(naechsterPatient);
	        	 //bearbeitung des Patienten
	        	 hold(new TimeSpan(myModel.getRoentgenZeit(), TimeUnit.MINUTES));
	        	 
	        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
	        	 naechsterPatient.activate(new TimeSpan (0.0));
	        	 
	         } 
	         }
	      }

	   
}
