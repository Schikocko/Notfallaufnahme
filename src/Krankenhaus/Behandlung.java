package Krankenhaus;
import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.*;

public class Behandlung extends SimProcess {
	
	/**
	 * routine Behandlung der Patienten
	 */
	
	private Notfallaufnahme myModel;
	//Queues der Behandlung, abh�ngig vom typen
	public desmoj.core.simulator.ProcessQueue<Patient> behandlungsQueue;
	public desmoj.core.simulator.ProcessQueue<Patient> prioBehandlungQueue;
	public desmoj.core.simulator.ProcessQueue<Behandlung> untaetigeBehandlungsQueue;
	public desmoj.core.simulator.ProcessQueue<Behandlung> mittagsPausenQueue;
	private String typ;
	
	/**
	    * Constructor der routine Behandlung
	    *
	    * die routine �rzte, die Patienten behandeln
	    *
	    * @param owner das Modell zu dem die Aufnahme geh�rt
	    * @param name Der Name der Behandlung
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public Behandlung(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      // speichert den referenz, zu welchem Modell die Aufnhame geh�rt
	      myModel = (Notfallaufnahme)owner;
	   }
	   
	   /**
	    * Setzt die Queue auf eine spezell vorbvereite
	    * 
	    * 
	    * @param qu die Queue wird �bergeben
	    */
	   public void setBehandlungsQueue (desmoj.core.simulator.ProcessQueue<Patient> qu)
	   {
		  behandlungsQueue = qu; 
	   }
	   
	   public void setUntaetigerBehandlungsQueue (desmoj.core.simulator.ProcessQueue<Behandlung> qu)
	   {
		  untaetigeBehandlungsQueue = qu; 
	   }
	   
	   public void setPrioQueue  (desmoj.core.simulator.ProcessQueue<Patient> qu)
	   {
		   prioBehandlungQueue = qu;
	   }
	   
	   public void setMittagsPausenQueue  (desmoj.core.simulator.ProcessQueue <Behandlung> qu)
	   {
		   mittagsPausenQueue = qu;
	   }
	   //variable des Typen, f�r die Zeit
	   public void setTyp (String t)
	   {
		   typ = t;
	   }
	   //�berpr�ft den Typen und gibt die entsprechende Bearbeitungszeit zur�ck
	   public double setBehandlungsZeit()
	   {
		   if (typ == "BehandlungK")
		   {
			   return myModel.getBehandlungszeitK();
		   }else
			   return myModel.getBehandlungszeitR();
		
	   }
	   
	   /**
	    * Lebenszyklus der routine Behandlung
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

          
        
	      // Behandlung beginnt 30 min nach Modellstart, und 4 Stunden 30min nach Modellstart 1 Stunde Mittagspause
	      while (true) {
	    	  
	    	  //blocken f�r die Mittagspause
			   /**
			    * jeder Arzt hat eine Stunde Mittagspause, ab 4.25 Simulationszeit keine 
			    * neuen Patienten, Mittagspause unterbricht die Behandlung nicht, nach 4.30 abgefangende 
			    * Mittagspausen dauern trotzdem eine volle Stunde
			    */
	          if (mittagsPausenQueue.contains(this) && (myModel.presentTime().getTimeAsDouble() >= 265.0))
	          {   
	        	  Behandlung mittagspause = this;
	        	  if(myModel.presentTime().getTimeAsDouble() < 270.0) // wenn es zwischen 11.55 und 12.00 ist
	        	  {
	        		  sendTraceNote("Puffer vor Mittagspause");
	        		  hold(new TimeInstant (270, TimeUnit.MINUTES )); //dann warten vom aktuellen zeitpunkt bis 12.00
	        	  }
	        	  sendTraceNote("Mittagspause");
	        	  hold(new TimeSpan (60.0));//eine Stunde warten
	        	  mittagsPausenQueue.remove(mittagspause);//entfernen aus der Mittagswarteschlange, da diese erledigt wurde
	        	  untaetigeBehandlungsQueue.insert(this);
	        	  mittagspause.passivate();
	          }
	        	  
	         // �berpr�fung, ob jemand in einer der beiden Queues wartet
	         if (behandlungsQueue.isEmpty() && prioBehandlungQueue.isEmpty()) {
	        	 //keiner wartet
	        	 //in �nt�tigeAufnahme Wartetschlange einf�gen
	        	 untaetigeBehandlungsQueue.insert(this);
	        	 passivate(); //warte bis vom Patient reaktiviert
	         }
	         else {
	        	 if (!prioBehandlungQueue.isEmpty())
	        	 {
	        		//ein Patient wartet in der PrioQueue
		        	 //ersten Patienten aus der Queue nehmen
		        	 Patient naechsterPatientBeh = prioBehandlungQueue.first();
		        	 prioBehandlungQueue.remove(naechsterPatientBeh);
		        	 //bearbeitung des Patienten
		        	 hold(new TimeSpan(setBehandlungsZeit(), TimeUnit.MINUTES));
		        	 
		        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
		        	 naechsterPatientBeh.activate(new TimeSpan (0.0));
	        	 }else
	        	 {
	        	 //ein Patient wartet in der normalen Queue
	        	 //ersten Patienten aus der Queue nehmen
	        	 Patient naechsterPatientBeh = behandlungsQueue.first();
	        	 behandlungsQueue.remove(naechsterPatientBeh);
	        	 //bearbeitung des Patienten
	        	 hold(new TimeSpan(setBehandlungsZeit(), TimeUnit.MINUTES));
	        	 
	        	 //wird reaktiviert, nachdem die bearbeitungszeit abgeschlossen ist
	        	 naechsterPatientBeh.activate(new TimeSpan (0.0));
	        	 	        	 	        	 
	        	 }
	         } 
	         }
	      
	   }
	   

	   
}
