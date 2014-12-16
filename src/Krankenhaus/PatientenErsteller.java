package Krankenhaus;
import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ulf
 *
 *Diese Klasse erstellt einen neuen Patienten, aktiviert ihn und setzt das erstellen
 *eines weiteren neuen Patienten fest
 */
	
public class PatientenErsteller extends SimProcess 
{
	/**
	    * Patientenersteller Constructor
	    * @param owner das Modell zu dem der Patientenersteller gehört
	    * @param name Name des Patientenerstellers
	    * @param showInTrace aktivieren, wenn es soll getraced werden
	    */
	   public PatientenErsteller(Model owner, String name, boolean showInTrace) 
	   {
	      super(owner, name, showInTrace);
	   }

	   /**
	    * Erstellt immer wieder neue Patienten
	    */
	   public void lifeCycle() {

		      // wird dem modell zugewiesen, in festgelegem Abstand
		      Process model = (Process)getModel();
		      // endless loop:
		      while (true) {

		         // erstelle einen neuen Patienten
		         // Parameters:
		         // model       = Ist ein teil dieses Modell
		         // "Patient"   = Name der Entität
		         // true        = der Patient wird getraced
		         Patient patient = new Patient(model, "Patient", true);
		         		         
		         // aktiviert den Patient
		         patient.activateAfter(this);

		         // warte auf die ankunft eines weiteren Patienten
		         hold(new TimeSpan(model.getPatientAnkunftsZeit(), TimeUnit.MINUTES));
		         // wartet bis die Patienten anknuftszeit verstrichen ist, 
		         // um dann den loop erneut zu durchlaufen
		      }
	   }

}
