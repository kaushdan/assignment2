package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleseVehicleEvent implements Event<String> {
	
	private DeliveryVehicle v;
	
	public ReleseVehicleEvent(DeliveryVehicle v) {
		this.v=v;
	}
	
	public DeliveryVehicle getDeliveryVehicle() {
		return this.v;
	}
}
