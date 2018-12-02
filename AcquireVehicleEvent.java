package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class AcquireVehicleEvent implements Event<DeliveryVehicle> {
	
	private int distance;
	private String address;
	
	public AcquireVehicleEvent(int distance, String address) {
		this.distance=distance;
		this.address=address;
	}
	
	public int getDistance() {
		return this.distance;
	}
	
	public String getAddress() {
		return this.address;
	}
 
}
