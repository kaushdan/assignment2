package bgu.spl.mics.application.services;

import java.util.concurrent.TimeUnit;

import bgu.spl.mics.MicroService;
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
	
	public TimeService(String name,int speed,int duration) {
		super(name);
		this.duration=duration;
		this.speed=speed;
		this.currentTime=1;
	}

	@Override
	protected void initialize() {
		sendBroadcast(new TickBroadcast(this.currentTime,false));
		
		subscribeBroadcast(TickBroadcast.class, message->{
			try {
				TimeUnit.MILLISECONDS.sleep(speed);
			} catch (InterruptedException e) {}
			currentTime++;
			if(currentTime-1==duration) {
				sendBroadcast(new TickBroadcast(this.currentTime,true));
				terminate();
			}
			else {
				sendBroadcast(new TickBroadcast(this.currentTime,false));
			}
		});
		
	}

}
