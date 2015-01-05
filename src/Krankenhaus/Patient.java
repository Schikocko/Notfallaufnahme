package Krankenhaus;
import desmoj.core.dist.ContDistUniform;
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
	private desmoj.core.dist.ContDistUniform typ ;
	
	private desmoj.core.dist.ContDistUniform komplexitaet;
	
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
	      //1-35 Röntgen 36-55 GipsENtfernen 56-60 GipsNeu 61-100 Wunderverband
	      typ =  new ContDistUniform(myModel, "Behandlungstyp", 1, 100, true, true);
	      //1-4 Komplex 5-10 Routine
	      komplexitaet = new ContDistUniform(myModel, "Komplexität", 1, 10, true, true);
	   }
	   
	   //gibt den Behandlungstyp zurück, für spätere Fallentscheidnung, wo es als nächestes hingeht
	   public Double getTyp(){
		   return typ.sample();
	   }
	   
	 //gibt die Komplexität zurück, für spätere Fallentscheidnung, wo es als nächestes hingeht
	   public double getKomplexitaet(){
		   return komplexitaet.sample();
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
		   //dauerhafte zuweisung eines zufälligen Typen
		   double myTyp = this.getTyp();
		   
		   //dauerhafte zuweisung einer zufälligen Komplexität
		   double myKomplexitaet = this.getKomplexitaet();
		   
		   //betreten des Systems, Aufnahmewarteschlange
		   myModel.aufnahmeQueue.insert(this);
		   sendTraceNote ("Typ:" + myTyp);
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
		   if (myKomplexitaet >= 5){ // routine 5-10
			   //wenn routine, in routine warteschlange einfügen
			   myModel.behandlungRQueue.insert(this);
			   sendTraceNote ("Routine Behalndlungs Warteschlange: " + myModel.behandlungRQueue.length());
			   if (!myModel.untaetigeBehandlungRQueue.isEmpty()) {
				   //steht zur Verfügung
				   //nimmt die erste routine Behandlung aus der untätigen Queue
				   BehandlungR behandlungR = myModel.untaetigeBehandlungRQueue.first();
				   myModel.untaetigeBehandlungRQueue.remove(behandlungR) ;
				   
				   //als Nächster beareitet werden
				   behandlungR.activateAfter(this);
				  }
			   			   
			   //warte darauf, dass Aufnahme frei wird
			   passivate();
			   sendTraceNote("Patient wurde routine behandelt.");
			   }
		   
		   else
		   {//Komplex 1-4
			   //wenn nicht routine -> komplex in Komplexe Warteschlange
			   myModel.behandlungKQueue.insert(this);
		       sendTraceNote ("Komplexe Behalndlungs Warteschlange: " + myModel.behandlungKQueue.length());
			   if (!myModel.untaetigeBehandlungKQueue.isEmpty()){
				   //steht zur Verfügung
				   //nimmt die erste komplexe behandlung aus der untätigen Queue
				   BehandlungK behandlungK = myModel.untaetigeBehandlungKQueue.first();
				   myModel.untaetigeBehandlungKQueue.remove(behandlungK) ;
				   
				   //als Nächster beareitet werden
				   behandlungK.activateAfter(this);
				  }
			   
			   //warte darauf, dass Aufnahme frei wird
			   passivate();
			   sendTraceNote("Patient wurde Komplex behandelt.");
		   }
		   
	   //weiter für typ gips entfernen 36-55
	   if (myTyp > 35 && myTyp < 56)
	   {
		   //wenn Typ zwischen 36-55 dann in die Gips Warteschlange
		   myModel.gipsQueue.insert(this);
		   sendTraceNote ("Gips Warteschlange: " + myModel.gipsQueue.length());
		   
		   //überprüfung, ob Gipspfleger zur verfügung steht
		   if (!myModel.untaetigeGipsQueue.isEmpty()) {
			   //steht zur Verfügung
			   //nimmt den ersten Gipspfleger aus der untätigen Queue
			   Gips gips = myModel.untaetigeGipsQueue.first();
			   myModel.untaetigeGipsQueue.remove(gips) ;
			   
			   //als Nächster beareitet werden
			   gips.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote ("Patient war beim Gips");
	   }
	   
	   //nach der Behnadlung Röntgen für Röntgen(1-35) und GipsNeu(56-60)
	   if (myTyp < 36 || (myTyp > 55 && myTyp <= 60))
	   {
		 //wenn Typ zwischen Röntgen(1-35) und GipsNeu(56-60) dann in die Röntgen Warteschlange
		   myModel.roentgenQueue.insert(this);
		   sendTraceNote ("Röntgen Warteschlange: " + myModel.roentgenQueue.length());
		   
		   //überprüfung, ob Gipspfleger zur verfügung steht
		   if (!myModel.untaetigeRoentgenQueue.isEmpty()) {
			   //steht zur Verfügung
			   //nimmt den ersten Gipspfleger aus der untätigen Queue
			   Roentgen roentgen = myModel.untaetigeRoentgenQueue.first();
			   myModel.untaetigeRoentgenQueue.remove(roentgen) ;
			   
			   //als Nächster beareitet werden
			   roentgen.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote ("Patient wurde geröngt");
	   }
	   
	   //weiter für typ GipsNeu  56-60
	   if (myTyp > 56 && myTyp <= 60)
	   {
		   //wenn Typ zwischen 56-60 dann in die Gips Warteschlange
		   myModel.gipsQueue.insert(this);
		   sendTraceNote ("GipsNeu Warteschlange: " + myModel.gipsQueue.length());
		   
		   //überprüfung, ob Gipspfleger zur verfügung steht
		   if (!myModel.untaetigeGipsQueue.isEmpty()) {
			   //steht zur Verfügung
			   //nimmt den ersten Gipspfleger aus der untätigen Queue
			   Gips gips = myModel.untaetigeGipsQueue.first();
			   myModel.untaetigeGipsQueue.remove(gips) ;
			   
			   //als Nächster beareitet werden
			   gips.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote("Patient wurde für GipsNeu behandelt");
	   }
	   
	   //Nach GipsNeu erneutes Röntgen
	   if (myTyp > 55 && myTyp <= 60)
	   {
		 //wenn Typ zwischen GipsNeu(56-60) dann in die Röntgen Warteschlange
		   myModel.roentgenQueue.insert(this);
		   sendTraceNote ("Röntgen nach Gips Warteschlange: " + myModel.roentgenQueue.length());
		   
		   //überprüfung, ob Gipspfleger zur verfügung steht
		   if (!myModel.untaetigeRoentgenQueue.isEmpty()) {
			   //steht zur Verfügung
			   //nimmt den ersten Gipspfleger aus der untätigen Queue
			   Roentgen roentgen = myModel.untaetigeRoentgenQueue.first();
			   myModel.untaetigeRoentgenQueue.remove(roentgen) ;
			   
			   //als Nächster beareitet werden
			   roentgen.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote ("Patient wurde nach Gips geröngt");
	   }
	   
	 //2te Behandlung nach Gips Neu oder Röntgen  
	 //entscheidung ob komplex oder Routine, abhängig von Variable Typ, random aus 0-5 1,2 Komplex 3 4 5 routine
	   if (myTyp < 61){
	   if (myKomplexitaet >= 5){ // routine 5-10
		   //wenn routine, in routine warteschlange einfügen
		   //einfüügen in PrioQueue für 2te behandlung
		   myModel.prioBehandlungRQueue.insert(this);
		   sendTraceNote ("Priotitäts Routine Behalndlungs Warteschlange: " + myModel.prioBehandlungRQueue.length());
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
		   sendTraceNote("Patient wurde 2tes mal routine behandelt.");
		   
		   if (myTyp > 55 && myTyp <= 60)//aussage welche Behandlung abgeschlossen wurde
		   {
			   sendTraceNote ("Ende der des Krankenhausbesuchs GipsNeu");
		   }else
		   {
			   sendTraceNote ("Ende des Krankenhausbesuchs Röntgen");
		   }
	   }
	   else
	   {//Komplex 1-4
		   //wenn nicht routine -> komplex in Komplexe Warteschlange
		   //einfügen in die PrioQueue für 2te behandlung
		   myModel.prioBehandlungKQueue.insert(this);
	       sendTraceNote ("Prioritäts Komplexe Behalndlungs Warteschlange: " + myModel.prioBehandlungKQueue.length());
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
		   sendTraceNote("Patient wurde 2tes mal komplex behandelt.");
		   
		   if (myTyp > 55 && myTyp <= 60)//aussage welche Behandlung abgeschlossen wurde
		   {
			   sendTraceNote ("Ende der des Krankenhausbesuchs GipsNeu");
		   }else
		   {
			   sendTraceNote ("Ende des Krankenhausbesuchs Röntgen");
		   }
	   }
	   }
	   }
	   
}
