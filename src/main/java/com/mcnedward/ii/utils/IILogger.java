package com.mcnedward.ii.utils;

/**
 * @author Edward - Jul 12, 2016
 *
 */
public final class IILogger {

	public static void info(String out) {
		System.out.println(out);
	}
	
	public static void info(String out, Object...args) {
		System.out.println(String.format(out, args));
	}
	
	public static void error(String out, Exception e) {
		info(out);
		e.printStackTrace();
	}
	
}
