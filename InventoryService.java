package bgu.spl.mics.application.services;

import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabiltyEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;
	private AtomicInteger count;
	
	public InventoryService(String name, AtomicInteger count) {
		super(name);
		this.inventory=Inventory.getInstance();
		this.count=count;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, message->{
			terminate();
		});
		
		subscribeEvent(CheckAvailabiltyEvent.class, message->{
			int price=inventory.checkAvailabiltyAndGetPrice(message.getBookTitle());
			complete(message, price);
		});
		
		subscribeEvent(TakeBookEvent.class, message->{
			OrderResult result=inventory.take(message.getBookTitle());
			complete(message,result);
		});
		
		this.count.addAndGet(1);
	}

}
