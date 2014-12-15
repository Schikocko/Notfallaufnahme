package Krankenhaus;
import desmoj.core.simulator.*;
//test
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
	private int typ = 1;
	
	private int komplexitaet = 1;
	
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
	      //Zuordnung der Typen

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
		   
		   //entscheidung ob komplex oder Routine, abhängig von Variable Typ, random aus 0-5 1,2 Komplex 3 4 5 routine
		   if (this.getKomplexitaet() < 1){ // routine
			   //wenn routine, in routine warteschlange einfügen
			   myModel.behandlungRQueue.insert(this);
			   if (!myModel.untaetigeBehandlungRQueue.isEmpty()) {
				   //steht zur Verfügung
				   //nimmt die erste AUfnahmekraft aus der untätigen Queue
				   BehandlungR behandlungR = myModel.untaetigeBehandlungRQueue.first();
				   myModel.untaetigeBehandlungRQueue.remove(behandlungR) ;
				   
				   //als Nächster beareitet werden
				   behandlungR.activateAfter(this);
				  }
			   //warte darauf, dass Aufnahme frei wird
			   passivate();
			   sendTraceNote("Patient wurde routine behandelt.");
		   }
		   else //Komplex
			   //wenn nicht routine -> komplex in Komplexe Warteschlange
			   myModel.behandlungKQueue.insert(this);
			   if (!myModel.untaetigeBehandlungKQueue.isEmpty()){
				   //steht zur Verfügung
				   //nimmt die erste AUfnahmekraft aus der untätigen Queue
				   BehandlungK behandlungK = myModel.untaetigeBehandlungKQueue.first();
				   myModel.untaetigeBehandlungKQueue.remove(behandlungK) ;
				   
				   //als Nächster beareitet werden
				   behandlungK.activateAfter(this);
				  }
			   //warte darauf, dass Aufnahme frei wird
			   passivate();
			   sendTraceNote("Patient wurde Komplex behandelt.");
	   }
	   //TODO Lifecycle weiterführen, mit gips, röntgen und weiterer behandlung
}
