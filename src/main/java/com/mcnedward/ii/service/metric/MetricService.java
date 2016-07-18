package com.mcnedward.ii.service.metric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.MetricBuildException;
import com.mcnedward.ii.service.metric.element.DitMetric;
import com.mcnedward.ii.service.metric.element.NocMetric;
import com.mcnedward.ii.service.metric.element.WmcMetric;
import com.mcnedward.ii.utils.IILogger;

/**
 * This is a service tool for creating files readable by Excel for the Metrics of a {@link JavaSolution}.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class MetricService {

	private static final String METRIC_DIRECTORY_PATH = "C:/users/edward/dev/IIMetrics";
	private static final String FILE_EXTENSION = "txt";
	private static final String NEWLINE = "\n";
	private static final String DELIMITER = "\t";

	public boolean buildMetrics(JavaSolution solution) {
		try {
			buildDitMetrics(solution);
			buildNocMetrics(solution);
			buildWmcMetrics(solution);
			return true;
		} catch (MetricBuildException e) {
			IILogger.error(e);
			return false;
		}
	}

	private void buildDitMetrics(JavaSolution solution) throws MetricBuildException {
		List<DitMetric> ditMetrics = solution.getDitMetrics();
		MType metricType = MType.DIT;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType, "Number of Inherited Methods");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (DitMetric metric : ditMetrics) {
			builder.append(buildRow(metric.element, metric.metric, String.valueOf(metric.numberOfInheritedMethods)) + NEWLINE);
		}

		writeToFile(solution, metricType, builder.toString());
	}

	private void buildNocMetrics(JavaSolution solution) throws MetricBuildException {
		List<NocMetric> nocMetrics = solution.getNocMetrics();
		MType metricType = MType.NOC;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType, "Class Children");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (NocMetric metric : nocMetrics) {
			builder.append(buildRow(metric.element, metric.metric, metric.classChildren.toString()) + NEWLINE);
		}

		writeToFile(solution, metricType, builder.toString());
	}

	private void buildWmcMetrics(JavaSolution solution) throws MetricBuildException {
		List<WmcMetric> wmcMetrics = solution.getWmcMetrics();
		MType metricType = MType.WMC;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType);

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (WmcMetric metric : wmcMetrics) {
			builder.append(buildRow(metric.element, metric.metric) + NEWLINE);
		}

		writeToFile(solution, metricType, builder.toString());
	}

	private String buildRow(JavaElement element, int metric) {
		return buildRow(element, metric, null);
	}

	private String buildRow(JavaElement element, int metric, String extra) {
		String extraColumn = "";
		if (extra != null && !extra.equals("")) {
			extraColumn += DELIMITER + extra;
		}
		return String.format("%s%s %s%s", element.getFullyQualifiedName(), DELIMITER, String.valueOf(metric), extraColumn);
	}

	private String getRowTitles(MType metricType) {
		return getRowTitles(metricType, null);
	}

	private String getRowTitles(MType metricType, String extra) {
		String extraColumn = "";
		if (extra != null && !extra.equals("")) {
			extraColumn += DELIMITER + extra;
		}
		return String.format("Class or Interface%s %s%s", DELIMITER, metricType.metricName, extraColumn);
	}

	private String getDocTitle(JavaSolution solution, MType metricType) {
		return String.format("%s v. %s - %s", solution.getProjectName(), solution.getVersion(), metricType.metricName);
	}

	private String getFileName(JavaSolution solution, MType metricType) {
		return String.format("%s_%s_%s.%s", solution.getProjectName(), solution.getVersion(), metricType, FILE_EXTENSION);
	}

	private void writeToFile(JavaSolution solution, MType metricType, String output) throws MetricBuildException {
		String fileName = getFileName(solution, metricType);

		try {
			String basePath = getDirectoryPath(solution);
			String filePath = String.format("%s/%s", basePath, fileName);
			File file = new File(filePath);
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.write(output);
			writer.close();

			IILogger.info(String.format("Created file for project [%s] version [%s]! [%s]", solution.getProjectName(), solution.getVersion(), filePath));
		} catch (FileNotFoundException e) {
			throw new MetricBuildException(
					String.format("File %s for solution [%s] project [%s] was not found...", fileName, solution.getProjectName(), solution.getVersion()), e);
		} catch (UnsupportedEncodingException e) {
			throw new MetricBuildException(
					String.format("Error writing to %s for project [%s] version [%s]...", fileName, solution.getProjectName(), solution.getVersion()), e);
		}
	}

	/**
	 * Creates the directories for metrics for this solution, if those directories do not yet exists. This also returns
	 * the full base path for this solution's directory.
	 * 
	 * @param solution
	 *            The {@link JavaSolution}
	 * @return The base path for the solution directory
	 */
	private String getDirectoryPath(JavaSolution solution) {
		String filePath = String.format("%s/%s/%s", METRIC_DIRECTORY_PATH, solution.getSystemName(), solution.getProjectName());
		File metricDirectory = new File(filePath);
		metricDirectory.mkdirs();
		return filePath;
	}

}
