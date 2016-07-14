package com.mcnedward.ii.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public final class Stopwatch {

	private long startTime, elapsedTime;
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public long stop() {
		elapsedTime = getCurrentElapsedTime();
		startTime = 0l;	// Reset the time
		return elapsedTime;
	}
	
	public String stopAndGetTime() {
		return getTime(stop());
	}

	public String getTime(long time) {
		try {
			long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
			long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
			long millis = time - TimeUnit.SECONDS.toMillis(seconds);
			return String.format("%02d:%02d:%02d", minutes, seconds, millis);
		} catch (Exception e) {
			return "Problem when getting time...";
		}
	}
	
	public String getElapsedTime() {
		return getTime(getCurrentElapsedTime());
	}
	
	private long getCurrentElapsedTime() {
		long currentTime = System.currentTimeMillis();
		return currentTime - startTime;
	}
	
}
