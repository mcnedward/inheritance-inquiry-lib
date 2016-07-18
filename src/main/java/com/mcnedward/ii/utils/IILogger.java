package com.mcnedward.ii.utils;

/**
 * @author Edward - Jul 12, 2016
 *
 */
public final class IILogger {

	public static boolean DEBUG = false;
	public static boolean ANALYSIS = false;
	
	public static void info(String out) {
		System.out.println("INFO  " + out);
	}
	
	public static void info(String out, Object...args) {
		System.out.println(String.format("INFO  " + out, args));
	}
	
	public static void debug(String out) {
		if (DEBUG) System.out.println("DEBUG " + out);
	}
	
	public static void debug(String out, Object...args) {
		if (DEBUG) System.out.println("DEBUG " + String.format(out, args));
	}
	
	public static void analysis(String out) {
		if (ANALYSIS) System.out.println("ANALYSIS " + out);
	}
	
	public static void analysis(String out, Object...args) {
		if (ANALYSIS) System.out.println("ANALYSIS " + String.format(out, args));
	}
	
	public static void error(String out, Throwable e) {
		System.out.println("ERROR " + out);
		e.printStackTrace();
	}
	
	public static void error(Exception e) {
		error(e.getMessage(), e);
	}
	
}
