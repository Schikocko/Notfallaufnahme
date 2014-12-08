package Krankenhaus;

/**
 * GITHUIBS
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

		      // get a reference to the model
		      ProcessesExample model = (ProcessesExample)getModel();

		      // endless loop:
		      while (true) {

		         // create a new truck
		         // Parameters:
		         // model       = it's part of this model
		         // "Truck"     = name of the object
		         // true        = yes please, show the truck in trace file
		         Truck truck = new Truck(model, "Truck", true);

		         // now let the newly created truck roll on the parking-lot
		         // which means we will activate it after this truck generator
		         truck.activateAfter(this);

		         // wait until next truck arrival is due
		         hold(new TimeSpan(model.getTruckArrivalTime(), TimeUnit.MINUTES));
		         // from inside to outside...
		         // we draw a new inter-arrival time
		         // we make a TimeSpan object out of it and
		         // we wait for exactly this period of time
		      }

}
