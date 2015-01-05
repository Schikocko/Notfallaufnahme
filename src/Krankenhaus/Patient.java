package Krankenhaus;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.*;
//test
/**
 * 
 * @author Ulf
 *
 * Der Patient der alle Station durchlaufen kann, aberh�nig von seiner Behandlungsart
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
	    * Wird benutzt um einen neuen Patienten zu erstellen, der durch unser System l�uft
	    *
	    * @param owner das Modell zu dem der  Patient geh�rt
	    * @param name Der Name des Patienten
	    * @param showInTrace flag to indicate if this process shall produce output
	    *                    for the trace
	    */
	   public Patient(Model owner, String name, boolean showInTrace) {

	      super(owner, name, showInTrace);
	      //speichert den referenz, zu welchem Modell die Aufnhame geh�rt
	      myModel = (Process)owner;
	      //Zuordnung der Typen
	      //1-35 R�ntgen 36-55 GipsENtfernen 56-60 GipsNeu 61-100 Wunderverband
	      typ =  new ContDistUniform(myModel, "Behandlungstyp", 1, 100, true, true);
	      //1-4 Komplex 5-10 Routine
	      komplexitaet = new ContDistUniform(myModel, "Komplexit�t", 1, 10, true, true);
	   }
	   
	   //gibt den Behandlungstyp zur�ck, f�r sp�tere Fallentscheidnung, wo es als n�chestes hingeht
	   public Double getTyp(){
		   return typ.sample();
	   }
	   
	 //gibt die Komplexit�t zur�ck, f�r sp�tere Fallentscheidnung, wo es als n�chestes hingeht
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
		   //dauerhafte zuweisung eines zuf�lligen Typen
		   double myTyp = this.getTyp();
		   
		   //dauerhafte zuweisung einer zuf�lligen Komplexit�t
		   double myKomplexitaet = this.getKomplexitaet();
		   
		   //betreten des Systems, Aufnahmewarteschlange
		   myModel.aufnahmeQueue.insert(this);
		   sendTraceNote ("Typ:" + myTyp);
		   sendTraceNote ("Aufnahme Warteschlange: " + myModel.aufnahmeQueue.length());
		   
		   //�berpr�fung, ob Aufnahmekraft zur verf�gung steht
		   if (!myModel.untaetigeAufnahmeQueue.isEmpty()) {
			   //steht zur Verf�gung
			   //nimmt die erste AUfnahmekraft aus der unt�tigen Queue
			   Aufnahme aufnahme = myModel.untaetigeAufnahmeQueue.first();
			   myModel.untaetigeAufnahmeQueue.remove(aufnahme) ;
			   
			   //als N�chster beareitet werden
			   aufnahme.activateAfter(this);
			  }
 		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   
		   // hiernach wurde man von der Aufnahme wieder Aktiviert, und hat die Aufnahme hinter sich
		   
		   sendTraceNote("Patient wurde Aufgenommen.");
		   
		   
		   //entscheidung ob komplex oder Routine, abh�ngig von Variable Typ, random aus 0-5 1,2 Komplex 3 4 5 routine
		   if (myKomplexitaet >= 5){ // routine 5-10
			   //wenn routine, in routine warteschlange einf�gen
			   myModel.behandlungRQueue.insert(this);
			   sendTraceNote ("Routine Behalndlungs Warteschlange: " + myModel.behandlungRQueue.length());
			   if (!myModel.untaetigeBehandlungRQueue.isEmpty()) {
				   //steht zur Verf�gung
				   //nimmt die erste routine Behandlung aus der unt�tigen Queue
				   BehandlungR behandlungR = myModel.untaetigeBehandlungRQueue.first();
				   myModel.untaetigeBehandlungRQueue.remove(behandlungR) ;
				   
				   //als N�chster beareitet werden
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
				   //steht zur Verf�gung
				   //nimmt die erste komplexe behandlung aus der unt�tigen Queue
				   BehandlungK behandlungK = myModel.untaetigeBehandlungKQueue.first();
				   myModel.untaetigeBehandlungKQueue.remove(behandlungK) ;
				   
				   //als N�chster beareitet werden
				   behandlungK.activateAfter(this);
				  }
			   
			   //warte darauf, dass Aufnahme frei wird
			   passivate();
			   sendTraceNote("Patient wurde Komplex behandelt.");
		   }
		   
	   //weiter f�r typ gips entfernen 36-55
	   if (myTyp > 35 && myTyp < 56)
	   {
		   //wenn Typ zwischen 36-55 dann in die Gips Warteschlange
		   myModel.gipsQueue.insert(this);
		   sendTraceNote ("Gips Warteschlange: " + myModel.gipsQueue.length());
		   
		   //�berpr�fung, ob Gipspfleger zur verf�gung steht
		   if (!myModel.untaetigeGipsQueue.isEmpty()) {
			   //steht zur Verf�gung
			   //nimmt den ersten Gipspfleger aus der unt�tigen Queue
			   Gips gips = myModel.untaetigeGipsQueue.first();
			   myModel.untaetigeGipsQueue.remove(gips) ;
			   
			   //als N�chster beareitet werden
			   gips.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote ("Patient war beim Gips");
	   }
	   
	   //nach der Behnadlung R�ntgen f�r R�ntgen(1-35) und GipsNeu(56-60)
	   if (myTyp < 36 || (myTyp > 55 && myTyp <= 60))
	   {
		 //wenn Typ zwischen R�ntgen(1-35) und GipsNeu(56-60) dann in die R�ntgen Warteschlange
		   myModel.roentgenQueue.insert(this);
		   sendTraceNote ("R�ntgen Warteschlange: " + myModel.roentgenQueue.length());
		   
		   //�berpr�fung, ob Gipspfleger zur verf�gung steht
		   if (!myModel.untaetigeRoentgenQueue.isEmpty()) {
			   //steht zur Verf�gung
			   //nimmt den ersten Gipspfleger aus der unt�tigen Queue
			   Roentgen roentgen = myModel.untaetigeRoentgenQueue.first();
			   myModel.untaetigeRoentgenQueue.remove(roentgen) ;
			   
			   //als N�chster beareitet werden
			   roentgen.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote ("Patient wurde ger�ngt");
	   }
	   
	   //weiter f�r typ GipsNeu  56-60
	   if (myTyp > 56 && myTyp <= 60)
	   {
		   //wenn Typ zwischen 56-60 dann in die Gips Warteschlange
		   myModel.gipsQueue.insert(this);
		   sendTraceNote ("GipsNeu Warteschlange: " + myModel.gipsQueue.length());
		   
		   //�berpr�fung, ob Gipspfleger zur verf�gung steht
		   if (!myModel.untaetigeGipsQueue.isEmpty()) {
			   //steht zur Verf�gung
			   //nimmt den ersten Gipspfleger aus der unt�tigen Queue
			   Gips gips = myModel.untaetigeGipsQueue.first();
			   myModel.untaetigeGipsQueue.remove(gips) ;
			   
			   //als N�chster beareitet werden
			   gips.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote("Patient wurde f�r GipsNeu behandelt");
	   }
	   
	   //Nach GipsNeu erneutes R�ntgen
	   if (myTyp > 55 && myTyp <= 60)
	   {
		 //wenn Typ zwischen GipsNeu(56-60) dann in die R�ntgen Warteschlange
		   myModel.roentgenQueue.insert(this);
		   sendTraceNote ("R�ntgen nach Gips Warteschlange: " + myModel.roentgenQueue.length());
		   
		   //�berpr�fung, ob Gipspfleger zur verf�gung steht
		   if (!myModel.untaetigeRoentgenQueue.isEmpty()) {
			   //steht zur Verf�gung
			   //nimmt den ersten Gipspfleger aus der unt�tigen Queue
			   Roentgen roentgen = myModel.untaetigeRoentgenQueue.first();
			   myModel.untaetigeRoentgenQueue.remove(roentgen) ;
			   
			   //als N�chster beareitet werden
			   roentgen.activateAfter(this);
			  }
		   //warte darauf, dass Aufnahme frei wird
		   passivate();
		   sendTraceNote ("Patient wurde nach Gips ger�ngt");
	   }
	   
	 //2te Behandlung nach Gips Neu oder R�ntgen  
	 //entscheidung ob komplex oder Routine, abh�ngig von Variable Typ, random aus 0-5 1,2 Komplex 3 4 5 routine
	   if (myTyp < 61){
	   if (myKomplexitaet >= 5){ // routine 5-10
		   //wenn routine, in routine warteschlange einf�gen
		   //einf��gen in PrioQueue f�r 2te behandlung
		   myModel.prioBehandlungRQueue.insert(this);
		   sendTraceNote ("Priotit�ts Routine Behalndlungs Warteschlange: " + myModel.prioBehandlungRQueue.length());
		   if (!myModel.untaetigeBehandlungRQueue.isEmpty()) {
			   //steht zur Verf�gung
			   //nimmt die erste AUfnahmekraft aus der unt�tigen Queue
			   BehandlungR behandlungR = myModel.untaetigeBehandlungRQueue.first();
			   myModel.untaetigeBehandlungRQueue.remove(behandlungR) ;
			   
			   //als N�chster beareitet werden
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
			   sendTraceNote ("Ende des Krankenhausbesuchs R�ntgen");
		   }
	   }
	   else
	   {//Komplex 1-4
		   //wenn nicht routine -> komplex in Komplexe Warteschlange
		   //einf�gen in die PrioQueue f�r 2te behandlung
		   myModel.prioBehandlungKQueue.insert(this);
	       sendTraceNote ("Priorit�ts Komplexe Behalndlungs Warteschlange: " + myModel.prioBehandlungKQueue.length());
		   if (!myModel.untaetigeBehandlungKQueue.isEmpty()){
			   //steht zur Verf�gung
			   //nimmt die erste AUfnahmekraft aus der unt�tigen Queue
			   BehandlungK behandlungK = myModel.untaetigeBehandlungKQueue.first();
			   myModel.untaetigeBehandlungKQueue.remove(behandlungK) ;
			   
			   //als N�chster beareitet werden
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
			   sendTraceNote ("Ende des Krankenhausbesuchs R�ntgen");
		   }
	   }
	   }
	   }
	   
}
