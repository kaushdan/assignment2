package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

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
//			System.out.println("received terminate broadcast "+getName());
			terminate();
		});
		
		subscribeEvent(DeliveryEvent.class, message->{
//			System.out.println("sending AcquireVehicle Event "+getName());
//			System.out.println("received future of future "+getName());
			Future<Future<DeliveryVehicle>> futureOfFuture=sendEvent(new AcquireVehicleEvent());
			if(futureOfFuture!=null) {
//				System.out.println("Trying to get future of future data at: "+getName());
				Future<DeliveryVehicle> future=futureOfFuture.get();
				if(future!=null) {
//					System.out.println("got future of future data at: "+getName());
//					System.out.println("Trying to get future data at: "+getName());
					DeliveryVehicle v=future.get();
					if(v!=null) {
//						System.out.println("got future data at: "+getName());
						v.deliver(message.getAddress(), message.getDistance());
//						System.out.println(v.toString()+" finished delivery "+getName());
//						System.out.println("sending ReleaseVehicle Event "+getName()); 
						sendEvent(new ReleaseVehicleEvent(v));
					}
//					else 
//						System.out.println("vehicle is null");
				}
//				else
//					System.out.println("future is null");
			}
//			else
//				System.out.println("future of future is null");
		});
		
		this.count.addAndGet(1);
	}

}
