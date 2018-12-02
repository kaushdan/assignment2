package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeliveryEvent implements Event<String>  {

	private int distance;
	private String address;
	
	public DeliveryEvent(int distance,String address) {
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
