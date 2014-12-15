package Krankenhaus;

import java.util.concurrent.TimeUnit;
import desmoj.core.simulator.*;

public class Aufnahme extends SimProcess {
	
	/**
	 * Erste Station des Patienten in der Klinik, alle Patienten müssen diese Besuchen
	 */
	
	private Process myModel;
	
	/**
	    * Constructor der Aufnahme
	    *
	    * Neue Aufnahmekraft, die Patient verarbeitet
	    *
	    * @param owner das Modell zu dem die Aufnahme gehört
	    * @param name Der Name der Aufname
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public Aufnahme(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      // speichert den referenz, zu welchem Modell die Aufnhame gehört
	      myModel = (Process)owner;
	   }

	   /**
	    * Lebenszyklus der AUfnahme
	    *
	    * Ein Loop der folgendes macht
	    * Überprüfung ob Patienten warten:
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
	         // überprüfung, ob jemand wartet
	         if (myModel.aufnahmeQueue.isEmpty()) {
	        	 //keiner wartet
	        	 //in üntätigeAufnahme Wartetschlange einfügen
	        	 myModel.untaetigeAufnahmeQueue.insert(this);
	        	 passivate(); //warte bis vom Patient reaktiviert
	         }
	         else {
	        	 //ein Patient wartet
	        	 //ersten Patienten aus der Queue nehmen
	        	 Patient naechsterPatient =myModel.aufnahmeQueue.first();
	        	 myModel.aufnahmeQueue.remove(naechsterPatient);
	        	 //bearbeitung des Patienten
	        	 hold(new TimeSpan(myModel.getAufnahmeZeit(), TimeUnit.MINUTES));
	        	 
	        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
	        	 naechsterPatient.activate(new TimeSpan (0.0));
	        	 
	         } 
	         }
	      }
}
