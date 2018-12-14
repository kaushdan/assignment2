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
//			System.out.println("received terminate broadcast "+getName());
			terminate();
		});
		
		subscribeBroadcast(TickBroadcast.class, message->{
			this.currentTick=message.getTick();
		});
		
		subscribeEvent(BookOrderEvent.class, message->{
//			System.out.println("Order at "+getName()+" of "+message.getBookTitle()+" of "+message.getCustomer().getName());
			message.setProcessTick(this.currentTick);
			Customer customer=message.getCustomer();
			Future<Integer> bookFuture=(Future<Integer>)sendEvent(new CheckAvailabiltyEvent(message.getBookTitle()));
			Integer price=bookFuture.get();
//			System.out.println("price of "+message.getBookTitle()+" :"+price);
			if(price!=null && price!=-1) {
//				System.out.println("maybe "+message.getCustomer().getName()+" can buy "+message.getBookTitle());
				synchronized (customer) { 
					if(customer.getAvailableCreditAmount()>=price) {
						Future<OrderResult> resultFuture=(Future<OrderResult>) sendEvent(new TakeBookEvent(message.getBookTitle()));
						OrderResult result=resultFuture.get();
						if(result!=null && result.equals(OrderResult.SUCCESSFULLY_TAKEN)) { 
							this.moneyRegister.chargeCreditCard(customer, price);
							OrderReceipt receipt=new OrderReceipt(0, this.getName(), customer.getId(), message.getBookTitle(), price, this.currentTick,
									message.getOrderTick(), message.getProcessTick());
							customer.addReceipt(receipt);
							this.moneyRegister.file(receipt);
							complete(message,receipt);
						} 
						else {
//							System.out.println(message.getBookTitle()+" isn't available anymore");
							complete(message,null);
						}
					}
					else {
//						System.out.println(customer.getName()+" doesnt have money to buy "+message.getBookTitle());
						complete(message,null);
					}
				}
			}
			else {
//				System.out.println(message.getBookTitle()+" isn't availible anymore or future resolved with null");
				complete(message,null);
			}
		});
		
		this.count.addAndGet(1);
	}

}
