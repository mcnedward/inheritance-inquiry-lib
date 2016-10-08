package com.mcnedward.ii.utils;

import com.mcnedward.ii.listener.BuildListener;

/**
 * @author Edward - Jul 12, 2016
 *
 */
public final class IILogger {

	public static boolean DEBUG = false;
	public static boolean ANALYSIS = false;
    public static boolean INFO = true;
    public static boolean ERROR = true;

    public static void setLogLevels(boolean debug, boolean analysis, boolean info, boolean error) {
        DEBUG = debug;
        ANALYSIS = analysis;
        INFO = info;
        ERROR = error;
    }
	
	public static void info(String out) {
		if (INFO) System.out.println("INFO  " + out);
	}
	
	public static void info(String out, Object...args) {
		if (INFO) System.out.println(String.format("INFO  " + out, args));
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

    public static void error(String out, Object...args) {
        if (ERROR) System.out.println(String.format("ERROR  " + out, args));
    }
	
	public static void error(String out, Throwable e) {
        if (ERROR) System.out.println("ERROR " + out);
		e.printStackTrace();
	}
	
	public static void error(Exception e) {
		if (ERROR) error(e.getMessage(), e);
	}

    public static void notify(BuildListener listener, String message, int progress) {
        if (listener != null)
            listener.onProgressChange(message, progress);
        else
            info(message + " " + progress);
    }
	
}
