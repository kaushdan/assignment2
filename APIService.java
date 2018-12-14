package bgu.spl.mics.application.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Order;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

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
//			System.out.println("received terminate broadcast "+getName());
			terminate();
		});
		
		subscribeBroadcast(TickBroadcast.class, message->{
			int tick=message.getTick();
			Future<OrderReceipt>[] futures;
			if(this.ordersByTick.containsKey(tick)) {
				LinkedList<String> orders=this.ordersByTick.get(tick);
				futures=new Future [orders.size()]; 
				for(int i=0;i<orders.size();i++) {
//					System.out.println("Sending order "+orders.get(i)+" in "+getName()+" of customer "+ this.customer.getName());
					futures[i]=(Future<OrderReceipt>)sendEvent(new BookOrderEvent(this.customer,orders.get(i),tick));
				}
				for(Future<OrderReceipt> future: futures) {
					if(future!=null) {
						OrderReceipt receipt=future.get();
						if(receipt!=null) {
//							System.out.println("Buying of "+receipt.getBookTitle()+" succedeed of "+this.customer.getName());
//							System.out.println("sending delivery in "+getName()+" of "+this.customer.getName());
							sendEvent(new DeliveryEvent(customer.getDistance(), customer.getAddress()));
						}
					}
				}
			}
		});
		
		this.count.addAndGet(1);
	}

}
