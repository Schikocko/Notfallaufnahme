package Krankenhaus;
import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.*;

public class BehandlungK extends SimProcess {
	
	/**
	 * routine Behandlung der Patienten
	 */
	
	private Process myModel;
	
	/**
	    * Constructor der komplex Behandlung
	    *
	    * die komplex �rzte, die Patienten behandeln
	    *
	    * @param owner das Modell zu dem die Aufnahme geh�rt
	    * @param name Der Name der Behandlung
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public BehandlungK(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      // speichert den referenz, zu welchem Modell die Aufnhame geh�rt
	      myModel = (Process)owner;
	   }
	   
	   /**
	    * Lebenszyklus der komplex Behandlung
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
	         if (myModel.behandlungKQueue.isEmpty()) {
	        	 //keiner wartet
	        	 //in �nt�tigeAufnahme Wartetschlange einf�gen
	        	 myModel.untaetigeBehandlungKQueue.insert(this);
	        	 passivate(); //warte bis vom Patient reaktiviert
	         }
	         else {
	        	 //ein Patient wartet
	        	 //ersten Patienten aus der Queue nehmen
	        	 Patient naechsterPatientBehK =myModel.behandlungKQueue.first();
	        	 myModel.behandlungKQueue.remove(naechsterPatientBehK);
	        	 //bearbeitung des Patienten
	        	 hold(new TimeSpan(myModel.getBehandlungszeitK(), TimeUnit.MINUTES));
	        	 
	        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
	        	 naechsterPatientBehK.activate(new TimeSpan (0.0));
	        	 
	         } 
	         }
	      }

	   
}
