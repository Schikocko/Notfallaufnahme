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
	    * die komplex Ärzte, die Patienten behandeln
	    *
	    * @param owner das Modell zu dem die Aufnahme gehört
	    * @param name Der Name der Behandlung
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public BehandlungK(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      // speichert den referenz, zu welchem Modell die Aufnhame gehört
	      myModel = (Process)owner;
	   }
	   
	   /**
	    * Lebenszyklus der komplex Behandlung
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
		          if (myModel.mittagspauseQueueK.contains(this) && (myModel.presentTime().getTimeAsDouble() >= 265.0))
		          {   
		        	  BehandlungK mittagspauseK = this;
		        	  if(myModel.presentTime().getTimeAsDouble() < 270.0) // wenn es zwischen 11.55 und 12.00 ist
		        	  {
		        		  sendTraceNote("Puffer vor Mittagspasue");
		                  hold(new TimeSpan (270.0 - myModel.presentTime().getTimeAsDouble(), TimeUnit.MINUTES )); //dann warten vom aktuellen zeitpunkt bis 12.00
		        	  }
		        	  sendTraceNote("Mittagspause");
		        	  hold(new TimeSpan (60.0));//eine Stunde warten
		        	  myModel.mittagspauseQueueK.remove(mittagspauseK);//entfernen aus der Mittagswarteschlange, da diese erledigt wurde
			          myModel.untaetigeBehandlungKQueue.insert(this);
		        	  mittagspauseK.passivate();
		          }
	        
	    	 // überprüfung, ob jemand wartet in der Prio oder normalen Warteschlange
	         if (myModel.behandlungKQueue.isEmpty() && myModel.prioBehandlungKQueue.isEmpty()) {
	        	 //keiner wartet
	        	 //in üntätigeAufnahme Wartetschlange einfügen
	        	 myModel.untaetigeBehandlungKQueue.insert(this);
	        	 passivate(); //warte bis vom Patient reaktiviert
	         }
	         else {
	        	 //ein Patient wartet
	        	 //Einarbeitung der Priorität 
	        	 if (!myModel.prioBehandlungKQueue.isEmpty()){
	        		//ersten Patienten aus der PrioQueue nehmen
		        	 Patient naechsterPatientBehK = myModel.prioBehandlungKQueue.first();
		        	 myModel.prioBehandlungKQueue.remove(naechsterPatientBehK);
		        	 //bearbeitung des Patienten
		        	 hold(new TimeSpan(myModel.getBehandlungszeitK(), TimeUnit.MINUTES));
		        	 
		        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
		        	 naechsterPatientBehK.activate(new TimeSpan (0.0));
		        	 	 
	        	 }else {
	        	 //ersten Patienten aus der normalen Queue nehmen
	        	 Patient naechsterPatientBehK = myModel.behandlungKQueue.first();
	        	 myModel.behandlungKQueue.remove(naechsterPatientBehK);
	        	 //bearbeitung des Patienten
	        	 hold(new TimeSpan(myModel.getBehandlungszeitK(), TimeUnit.MINUTES));
	        	 
	        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
	        	 naechsterPatientBehK.activate(new TimeSpan (0.0));
	        	 }
	         } 
	         }
	      }

	   
}
