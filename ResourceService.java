package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
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
	private AtomicInteger count;
	
	public ResourceService(String name, AtomicInteger count) {
		super(name);
		garage=ResourcesHolder.getInstance();
		this.count=count;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, message->{
//			System.out.println("received terminate broadcast "+getName());
			this.garage.releaseVehicle(null);
			terminate();
		}); 
		
		subscribeEvent(AcquireVehicleEvent.class, message->{
//			System.out.println("Aquiring Vehicle "+getName());
			Future<DeliveryVehicle> future=garage.acquireVehicle();
			if(future!=null) {
//				System.out.println("future of future complete :"+getName());
				complete(message,future);
			}
		});
		
		subscribeEvent(ReleaseVehicleEvent.class, message->{
//			System.out.println("Relesing Vehicle "+message.getDeliveryVehicle().toString()+" "+getName());
			garage.releaseVehicle(message.getDeliveryVehicle());
		});
		
		this.count.addAndGet(1);
	}

}
