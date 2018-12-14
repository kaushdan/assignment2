package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	private DeliveryVehicle[] vehicles;
	private Boolean[] acqired;
	private Queue<Future<DeliveryVehicle>> futures;
	 
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		  return SingletonHolder.instance;
	}
	
	private static class SingletonHolder {
        private static ResourcesHolder instance = new ResourcesHolder();
    }
	
	private ResourcesHolder() {
		this.vehicles=null;
		 this.futures=new LinkedList<>();
		 this.acqired=null;
	}
	
	/** 
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future=new Future<>(); 
		return findMin(future);
	}
	
	private Future<DeliveryVehicle> findMin(Future<DeliveryVehicle> future) {
		for(int i=0;i<this.acqired.length;i++) {
			synchronized (this.vehicles[i]) {
				if(this.acqired[i]) {
//					System.out.println("Acquired Vehicle "+vehicles[i].toString()+" in ResorceHolder(findMin)");
					this.acqired[i]=false;
					future.resolve(this.vehicles[i]);
					return future;
				}
			} 
		}
//		System.out.println("couldnt find free Vehicle in ResourceHolder(findMin)");
//		System.out.println("adding to futures queue(findMin)");
		this.futures.add(future);
		return future;
	}

	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(vehicle==null) {
//			System.out.println("resolving all ramamining futures with null in ResourceHolder");
			for(Future<DeliveryVehicle> future: this.futures) {
				future.resolve(null);
			}
		}
		else {
//		System.out.println("Relesing Vehicle "+vehicle.toString()+" in ResourceHolder");
			int index=find(vehicle);
			synchronized (vehicle) {
				this.acqired[index]=true;
			}
			Future<DeliveryVehicle> future; 
			synchronized (futures) {
				future=this.futures.poll();
			}
			if(future!=null) {
				setMin(future);
			}
		}
	}
	
	private int find(DeliveryVehicle vehicle) {
		int i=0;
		for(;i<this.vehicles.length;i++) {
			if(this.vehicles[i].equals(vehicle))
				return i;
		}
		return i;
	}

	private void setMin(Future<DeliveryVehicle> future) {
		boolean resolved=false;
		for(int i=0;i<this.acqired.length;i++) {
			synchronized (this.vehicles[i]) {
				if(this.acqired[i]) {
//					System.out.println("Acquired Vehicle in ResorceHolder(setMin)");
					this.acqired[i]=false;
					future.resolve(this.vehicles[i]);
					resolved=true;
				}
			}
		}
		if(!resolved) {
//			System.out.println("couldnt find free Vehicle in ResourceHolder(setMin)");
			this.futures.add(future);
		}
	}

	private void sort(DeliveryVehicle[] vehicles) {
		for(int i=0;i<this.vehicles.length;i++) {
			int min=findMin(i);
			swap(i,min);
		} 
	}

	private void swap(int i, int j) {
		if(i!=j) {
			DeliveryVehicle tmp=this.vehicles[j];
			this.vehicles[j]=this.vehicles[i];
			this.vehicles[i]=tmp;
		}
	}

	private int findMin(int i) {
		int min=i;
		for(int j=i+1;j<this.vehicles.length;j++) {
			if(this.vehicles[j].getSpeed()<this.vehicles[min].getSpeed())
					min=j;
		}
		return min;
	}

	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		this.vehicles=vehicles;
		sort(this.vehicles);
		this.acqired=new Boolean[this.vehicles.length];
		for(int i=0;i<this.vehicles.length;i++)
			this.acqired[i]=true;
	}

}
