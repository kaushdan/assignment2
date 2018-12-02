package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;

import bgu.spl.mics.Future;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	
	private static ResourcesHolder instance=null;
	private LinkedList<DeliveryVehicle> freeVehicles=new LinkedList<>();
	private LinkedList<DeliveryVehicle> occupiedVehicles=new LinkedList<>();
	 
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		if(instance==null) {
			return new ResourcesHolder();
		}
		return instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public synchronized Future<DeliveryVehicle> acquireVehicle() {
		while(freeVehicles.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
		DeliveryVehicle v=this.freeVehicles.remove();
		this.occupiedVehicles.add(v);
		Future<DeliveryVehicle> future=new Future<>();
		future.resolve(v);
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public synchronized void releaseVehicle(DeliveryVehicle vehicle) {
		this.occupiedVehicles.remove(vehicle);
		this.freeVehicles.add(vehicle);
		this.notifyAll();
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		if(vehicles!=null) {
			for(DeliveryVehicle v: vehicles) {
				this.freeVehicles.add(v);
			}
		}
	}

}
