package bgu.spl.mics.application.services;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int duration;
	private int speed;
	private int currentTime;
	private AtomicInteger count;
	private int startCount;
	
	public TimeService(String name,int speed,int duration, AtomicInteger count, int startCount) {
		super(name);
		this.duration=duration;
		this.speed=speed;
		this.currentTime=1;
		this.count=count;
		this.startCount=startCount;
	}

	@Override
	protected void initialize() {
		while(count.get()<startCount) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		while(this.currentTime-1!=this.duration ) {
			sendBroadcast(new TickBroadcast(this.currentTime));
			this.currentTime++;
			try {
				TimeUnit.MILLISECONDS.sleep(speed);
			} catch (InterruptedException e) {}
		}
		sendBroadcast(new TerminateBroadcast());
		terminate();
	}

}
