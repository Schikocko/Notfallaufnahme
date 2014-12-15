package Krankenhaus;
import desmoj.core.simulator.*;

/**
 * 
 * @author Ulf
 *
 * Der Patient der alle Station durchlaufen kann, aberhänig von seiner Behandlungsart
 *
 */

public class Patient extends SimProcess {
	
	//zuweisung der Patientenattribute und Behandlungstypen
	//TODO Müssen noch randomwerte für die Typen bekommen
	private int typ;
	
	private int komplexitaet;
	
	private Process myModel;
	   /**
	    * Constructor der Patienten 
	    *
	    * Wird benutzt um einen neuen Patienten zu erstellen, der durch unser System läuft
	    *
	    * @param owner das Modell zu dem der  Patient gehört
	    * @param name Der Name des Patienten
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public Patient(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      //speichert den referenz, zu welchem Modell die Aufnhame gehört
	      myModel = (Process)owner;
	   }
	   
	   //gibt den Behandlungstyp zurück, für spätere Fallentscheidnung, wo es als nächestes hingeht
	   public int getTyp(){
		   return typ;
	   }
	   
	 //gibt die Komplexität zurück, für spätere Fallentscheidnung, wo es als nächestes hingeht
	   public int getKomplexitaet(){
		   return komplexitaet;
	   }
	   
	   /**
	    * Lebenszyklus des Patienten
	    * 
	    * Zuerst zur Aufnahme, dort wird Typ bestimmt und zugewiesen, danach anhand der Typen durch das System laufen
	    * 
	    * Aufnahme: 
	    * 
	    */

	   public void lifeCycle() {
		   //betreten des Systems, Aufnahmewarteschlange
		   myModel.aufnahmeQueue.insert(this);
		   sendTraceNote ("Aufnahme Warteschlange: " + myModel.aufnahmeQueue.length());
		   
		   //überprüfung, ob Aufnahmekraft zur verfügung steht
		   if (!myModel.untaetigeAufnahmeQueue.isEmpty()) {
			   //steht zur Verfügung
			   //nimmt die erste AUfnahmekraft aus der untätigen Queue
			   Aufnahme aufnahme = myModel.untaetigeAufnahmeQueue.first();
			   myModel.untaetigeAufnahmeQueue.remove(aufnahme) ;
			   
			   //als Nächster beareitet werden
			   aufnahme.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   
		   // hiernach wurde man von der Aufnahme wieder Aktiviert, und hat die Aufnahme hinter sich
		   
		   sendTraceNote("Patient wurde Aufgenommen.");
		   
		   
	   }
}
