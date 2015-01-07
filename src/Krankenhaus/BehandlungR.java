package Krankenhaus;
import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.*;

public class BehandlungR extends SimProcess {
	
	/**
	 * routine Behandlung der Patienten
	 */
	
	private Process myModel;
	
	/**
	    * Constructor der routine Behandlung
	    *
	    * die routine Ärzte, die Patienten behandeln
	    *
	    * @param owner das Modell zu dem die Aufnahme gehört
	    * @param name Der Name der Behandlung
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public BehandlungR(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      // speichert den referenz, zu welchem Modell die Aufnhame gehört
	      myModel = (Process)owner;
	   }
	   
	   /**
	    * Lebenszyklus der routine Behandlung
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

          
        
	      // Behandlung beginnt 30 min nach Modellstart, und 4 Stunden 30min nach Modellstart 1 Stunde Mittagspause
	      while (true) {
	    	  
	    	  //blocken für die Mittagspause
			   /**
			    * jeder Arzt hat eine Stunde Mittagspause, ab 4.25 Simulationszeit keine 
			    * neuen Patienten, Mittagspause unterbricht die Behandlung nicht, nach 4.30 abgefangende 
			    * Mittagspausen dauern trotzdem eine volle Stunde
			    */
	          if (myModel.mittagspauseQueueR.contains(this) && (myModel.presentTime().getTimeAsDouble() >= 265.0))
	          {   
	        	  BehandlungR mittagspauseR = this;
	        	  if(myModel.presentTime().getTimeAsDouble() < 270.0) // wenn es zwischen 11.55 und 12.00 ist
	        	  {
	        		  sendTraceNote("Puffer vor Mittagspause");
	        		  hold(new TimeInstant (270, TimeUnit.MINUTES )); //dann warten vom aktuellen zeitpunkt bis 12.00
	        	  }
	        	  sendTraceNote("Mittagspause");
	        	  hold(new TimeSpan (60.0));//eine Stunde warten
	        	  myModel.mittagspauseQueueR.remove(mittagspauseR);//entfernen aus der Mittagswarteschlange, da diese erledigt wurde
		          myModel.untaetigeBehandlungRQueue.insert(this);
	        	  mittagspauseR.passivate();
	          }
	        	  
	         // überprüfung, ob jemand in einer der beiden Queues wartet
	         if (myModel.behandlungRQueue.isEmpty() && myModel.prioBehandlungRQueue.isEmpty()) {
	        	 //keiner wartet
	        	 //in üntätigeAufnahme Wartetschlange einfügen
	        	 myModel.untaetigeBehandlungRQueue.insert(this);
	        	 passivate(); //warte bis vom Patient reaktiviert
	         }
	         else {
	        	 if (!myModel.prioBehandlungRQueue.isEmpty())
	        	 {
	        		//ein Patient wartet in der PrioQueue
		        	 //ersten Patienten aus der Queue nehmen
		        	 Patient naechsterPatientBehR = myModel.prioBehandlungRQueue.first();
		        	 myModel.prioBehandlungRQueue.remove(naechsterPatientBehR);
		        	 //bearbeitung des Patienten
		        	 hold(new TimeSpan(myModel.getBehandlungszeitR(), TimeUnit.MINUTES));
		        	 
		        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
		        	 naechsterPatientBehR.activate(new TimeSpan (0.0));
	        	 }else
	        	 {
	        	 //ein Patient wartet in der normalen Queue
	        	 //ersten Patienten aus der Queue nehmen
	        	 Patient naechsterPatientBehR = myModel.behandlungRQueue.first();
	        	 myModel.behandlungRQueue.remove(naechsterPatientBehR);
	        	 //bearbeitung des Patienten
	        	 hold(new TimeSpan(myModel.getBehandlungszeitR(), TimeUnit.MINUTES));
	        	 
	        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
	        	 naechsterPatientBehR.activate(new TimeSpan (0.0));
	        	 	        	 	        	 
	        	 }
	         } 
	         }
	      
	   }
	   

	   
}
