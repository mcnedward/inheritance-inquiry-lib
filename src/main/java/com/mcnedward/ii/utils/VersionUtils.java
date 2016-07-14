package com.mcnedward.ii.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public final class VersionUtils {
	
	private static final Logger logger = Logger.getLogger(VersionUtils.class);
	private static final String VERSION_REGEX = "-(\\d[\\.\\d]+)";
	
	public static String findVersion(String path) {
		final Pattern pattern = Pattern.compile(VERSION_REGEX);
		final Matcher matcher = pattern.matcher(path);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			logger.error("Could not find a version for file at path: " + path);
			return null;
		}
	}
}
