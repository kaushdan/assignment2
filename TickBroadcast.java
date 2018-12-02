package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

	private int tick;
	private boolean stop;
	
	public TickBroadcast(int tick,boolean stop) {
		this.tick=tick;
		this.stop=stop;
	}
	
	public int getTick() {
		return this.tick;
	}
	
	public boolean Stop() {
		return this.stop;
	}
	
}
