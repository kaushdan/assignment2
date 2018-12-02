package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicle;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
 
/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	
	private ResourcesHolder garage;
	public ResourceService(String name) {
		super(name);
		garage=ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, message->{
			if(message.Stop()) {
				terminate();
			}
		});
		
		subscribeEvent(AcquireVehicle.class, message->{
			Future<DeliveryVehicle> future=garage.acquireVehicle();
			if(future!=null) {
				DeliveryVehicle v=future.get();
				v.deliver(message.getAddress(),message.getDistance());
				garage.releaseVehicle(v);
				complete(message,v);
			}
		});
		
	}

}
