package bgu.spl.mics.application.services;

import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabiltyEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private int currentTick;
	private MoneyRegister moneyRegister;
	private AtomicInteger count;
	
	public SellingService(String name, AtomicInteger count) {
		super(name);
		this.currentTick=1;
		this.moneyRegister=MoneyRegister.getInstance();
		this.count=count;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, message->{
			terminate();
		});
		
		subscribeBroadcast(TickBroadcast.class, message->{
			this.currentTick=message.getTick();
		});
		
		subscribeEvent(BookOrderEvent.class, message->{
			message.setProcessTick(this.currentTick);
			Customer customer=message.getCustomer();
			Future<Integer> bookFuture=(Future<Integer>)sendEvent(new CheckAvailabiltyEvent(message.getBookTitle()));
			int price=bookFuture.get();
			if(price!=-1) {
				synchronized (customer) { 
					if(customer.getAvailableCreditAmount()>=price) {
						Future<OrderResult> resultFuture=(Future<OrderResult>) sendEvent(new TakeBookEvent(message.getBookTitle()));
						OrderResult result=resultFuture.get();
						if(result.equals(OrderResult.SUCCESSFULLY_TAKEN)) {
							this.moneyRegister.chargeCreditCard(customer, price);
							OrderReceipt receipt=new OrderReceipt(message.getOrderTick(), this.getName(), customer.getId(), message.getBookTitle(), price, this.currentTick,
									message.getOrderTick(), message.getProcessTick());
							customer.addReceipt(receipt);
							this.moneyRegister.file(receipt);
							sendEvent(new DeliveryEvent(customer.getDistance(), customer.getAddress()));
						} 
					}
				}
			}
		});
		
		this.count.addAndGet(1);
	}

}
