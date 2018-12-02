package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	/**
	 * saves for an Event the micro-services that processes it
	 */
	private HashMap<Class<? extends Event<?>>,Queue<MicroService>> eventsToServices;
	/**
	 * saves for a Broadcast the micro-services subscribed to it
	 */
	private HashMap<Class<? extends Broadcast>,LinkedList<MicroService>> brodcastToServices;
	/**
	 * saves for a micro-service an array of size 3
	 * slot 0: queue of messages for it to do
	 * slot 1: list of events it subscribed to
	 * slot 2: list of broadcasts it subscribed to
	 */
	private HashMap<MicroService,Object[]> microServicesQueues;
	/**
	 * saves for an Event it's Future result
	 * means that in the future another micro-servce 
	 * to determine it's value
	 */
	private HashMap<Event<?>,Future<?>> eventsFuture;
//	private Object lockE;
//	private Object lockB;
//	private Object lockR;
//	private Object lockUR;
	
	private static MessageBus instance=null;
	
	public static MessageBus getInstance() {
		if(instance == null) {
            instance = new MessageBusImpl();
         }
         return instance;
	}
	
	private MessageBusImpl() {
		this.brodcastToServices=new HashMap<>();
		this.eventsToServices=new HashMap<>();
		this.microServicesQueues=new HashMap<>();
		this.eventsFuture=new HashMap<>();
//		this.lockB=new Object();
//		this.lockE=new Object();
//		this.lockR=new Object();
//		this.lockUR=new Object();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//synchronized (this.lockE) {
			if(this.eventsToServices.containsKey(type)) {
				this.eventsToServices.get(type).add(m);
				((LinkedList<Class<? extends Event<T>>>)this.microServicesQueues.get(m)[1]).add(type);
			}
			else {
				this.eventsToServices.put(type,new LinkedList<MicroService>());
				this.eventsToServices.get(type).add(m);
				((LinkedList<Class<? extends Event<T>>>)this.microServicesQueues.get(m)[1]).add(type);
			}
		//}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//synchronized (this.lockB) {
			if(this.brodcastToServices.containsKey(type)) {
				this.brodcastToServices.get(type).add(m);
				((LinkedList<Class<? extends Broadcast>>)this.microServicesQueues.get(m)[2]).add(type);
			}
			else {
				this.brodcastToServices.put(type, new LinkedList<MicroService>());
				this.brodcastToServices.get(type).add(m);
				((LinkedList<Class<? extends Broadcast>>)this.microServicesQueues.get(m)[2]).add(type);
			}
		//}
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * determines the result of the Future var for e
	 */
	public <T> void complete(Event<T> e, T result) {
		((Future<T>)this.eventsFuture.get(e)).resolve(result); 
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * gives the Broadcast to
	 * all micro-services
	 * subscribed to it
	 */
	public synchronized void sendBroadcast(Broadcast b) {
		Class<? extends Broadcast> type=b.getClass();
		LinkedList<MicroService> list=this.brodcastToServices.get(type);
		for(MicroService m: list) {
			((Queue<Message>)this.microServicesQueues.get(m)[0]).add(b);
		}
		this.notifyAll();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * gives the Event to
	 * one micro-service using
	 * round robin pattern
	 * returns a Future var for the event
	 * which will be determinate in the future
	 */
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		Class<? extends Event<T>> type=(Class<? extends Event<T>>) e.getClass();
		Queue<MicroService> queue=this.eventsToServices.get(type);
		if(queue==null || queue.peek()==null) 
			return null;
		MicroService m=queue.remove();
		/**
		 * round robin
		 */
		((Queue<Message>)this.microServicesQueues.get(m)[0]).add(e);
		Future<T> future=new Future<>();
		this.eventsFuture.put(e, future);
		queue.add(m);
		this.notifyAll(); 
		return future;
	}

	@Override
	public void register(MicroService m) {
		//synchronized (this.lockR) {
			Object[] array=new Object[3];
			array[0]=new LinkedList<Message>();
			array[1]=new LinkedList<Class<? extends Event<?>>>();
			array[2]=new LinkedList<Class<? extends Broadcast>>();
			this.microServicesQueues.put(m, array);
		//}
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * removes all references
	 * for the micro-service
	 */
	public void unregister(MicroService m) {
		//synchronized (lockUR) {
			Object[] array=this.microServicesQueues.get(m);
			LinkedList<Class<? extends Event<?>>> eventsTypeList=(LinkedList<Class<? extends Event<?>>>)array[1]; 
			LinkedList<Class<? extends Broadcast>> broadcastsTypeList=(LinkedList<Class<? extends Broadcast>>)array[2];
			for(Class<? extends Event<?>> type: eventsTypeList) {
				removeEvent(type,m);
			}
			for(Class<? extends Broadcast> type: broadcastsTypeList) {
				removeBroadcast(type,m);
			}
			this.microServicesQueues.remove(m);
		//}
	}

	private void removeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		LinkedList<MicroService> list=this.brodcastToServices.get(type);
		list.remove(m);
	}

	private void removeEvent(Class<? extends Event<?>> type, MicroService m) {
		Queue<MicroService> queue=this.eventsToServices.get(type);
		MicroService head=queue.peek();
		if(head!=null) {
			MicroService check=queue.remove();
			while(!check.equals(m)) {
				queue.add(check);
				check=queue.remove();
			}
			if(!head.equals(m)) {
				check=queue.peek();
				while(!check.equals(head)) {
					check=queue.remove();
					queue.add(check);
					check=queue.peek();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException, IllegalStateException{
		if(!this.microServicesQueues.containsKey(m))
			throw new IllegalStateException();
		Queue<Message> queue=(Queue<Message>) this.microServicesQueues.get(m)[0];
		while(queue.isEmpty()) {
			this.wait();
		}
		return queue.remove();
	}

	

}
