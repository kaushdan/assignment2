package bgu.spl.mics.application.passiveObjects;

public class Input {

	private BookInventoryInfo[] initialInventory;
	private VehiclesHolder[] initialResources;
	private ServicesData services;
	
	public Input(BookInventoryInfo[] initialInventory,VehiclesHolder[] initialResources,ServicesData services) {
		this.initialInventory=initialInventory;
		this.initialResources=initialResources;
		this.services=services;
	}
	private class VehiclesHolder{
		private DeliveryVehicle[] vehicles;
	}
	
	private class ServicesData{
		private TimeData time;
		private int selling;
		private int inventoryService;
		private int logistics;
		private int resourcesService;
		private Customer[] customers;
	}
	private class TimeData{
		private int speed;
		private int duration;
	}
	
	public BookInventoryInfo[] getInventory() {
		return this.initialInventory;
	}
	
	public DeliveryVehicle[] getVehicles() {
		return this.initialResources[0].vehicles;
	}
	
	public int getSpeed() {
		return this.services.time.speed;
	}
	
	public int getDuration() {
		return this.services.time.duration;
	}
	
	public int getSelling() {
		return this.services.selling;
	}
	
	public int getLogistics() {
		return this.services.logistics;
	}
	
	public int getInventoryService() {
		return this.services.inventoryService;
	}
	
	public int getResourceService() {
		return this.services.resourcesService;
	}
	
	public Customer[] getCustomers() {
		return this.services.customers;
	}
	
	
}
