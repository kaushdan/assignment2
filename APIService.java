package bgu.spl.mics.application.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Order;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private HashMap<Integer, LinkedList<String>> ordersByTick;
	private Customer customer;
	private AtomicInteger count;
	
	public APIService(String name,Customer customer, AtomicInteger count) {
		super(name);
		ordersByTick=new HashMap<>();
		this.customer=customer;
		Order[] orderSchedule=customer.getOrderSchedule();
		for(Order order: orderSchedule) {
			if(ordersByTick.containsKey(order.getTick())) {
				this.ordersByTick.get(order.getTick()).add(order.getBookTitle());
			}
			else {
				this.ordersByTick.put(order.getTick(), new LinkedList<String>());
				this.ordersByTick.get(order.getTick()).add(order.getBookTitle());
			}
		}
		this.count=count;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, message->{
			terminate();
		});
		
		subscribeBroadcast(TickBroadcast.class, message->{
			int tick=message.getTick();
			if(this.ordersByTick.containsKey(tick)) {
				LinkedList<String> orders=this.ordersByTick.get(tick);
				for(String bookTitle: orders) {
					sendEvent(new BookOrderEvent(this.customer,bookTitle,tick));
				}
			}
		});
		
		this.count.addAndGet(1);
	}

}
