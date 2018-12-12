package bgu.spl.mics.application.services;

import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	
	private AtomicInteger count;
	
	public LogisticsService(String name, AtomicInteger count) {
		super(name);
		this.count=count;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, message->{
			terminate();
		});
		
		subscribeEvent(DeliveryEvent.class, message->{
			Future<DeliveryVehicle> future=sendEvent(new AcquireVehicleEvent());
			if(future!=null) {
				DeliveryVehicle v=future.get();
				v.deliver(message.getAddress(), message.getDistance());
				sendEvent(new ReleseVehicleEvent(v));
			}
		});
		
		this.count.addAndGet(1);
	}

}
