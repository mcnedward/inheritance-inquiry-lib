package com.mcnedward.ii.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;

import com.mcnedward.ii.analyzer.Analyzer;
import com.mcnedward.ii.analyzer.DitMetric;
import com.mcnedward.ii.analyzer.MType;
import com.mcnedward.ii.analyzer.NocMetric;
import com.mcnedward.ii.analyzer.WmcMetric;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.exception.MetricBuildException;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public final class MetricBuilder {
	private static final Logger logger = Logger.getLogger(MetricBuilder.class);
	private static final String FILE_EXTENSION = "txt";
	private static final String NEWLINE = "\n";
	private static final String DELIMITER = "\t";

	private String mPath;
	private Analyzer mAnalyzer;

	public MetricBuilder(String metricDirectoryPath) {
		mPath = metricDirectoryPath;
		mAnalyzer = new Analyzer();
	}

	public boolean buildMetrics(JavaProject project) {
		try {
			buildDitMetrics(project);
			buildNocMetrics(project);
			buildWmcMetrics(project);
			return true;
		} catch (MetricBuildException e) {
			logger.error(e);
			return false;
		}
	}

	private void buildDitMetrics(JavaProject project) throws MetricBuildException {
		List<DitMetric> ditMetrics = mAnalyzer.calculateDepthOfInheritanceTree(project);
		MType metricType = MType.DIT;

		String docTitle = getDocTitle(project, metricType);
		String rowTitles = getRowTitles(metricType, "Number of Inherited Methods");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (DitMetric metric : ditMetrics) {
			builder.append(buildRow(metric.element, metric.metric, String.valueOf(metric.numberOfInheritedMethods)) + NEWLINE);
		}

		writeToFile(project, metricType, builder.toString());
	}

	private void buildNocMetrics(JavaProject project) throws MetricBuildException {
		List<NocMetric> nocMetrics = mAnalyzer.calculateNumberOfChildren(project);
		MType metricType = MType.NOC;

		String docTitle = getDocTitle(project, metricType);
		String rowTitles = getRowTitles(metricType, "Class Children");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (NocMetric metric : nocMetrics) {
			builder.append(buildRow(metric.element, metric.metric, metric.classChildren.toString()) + NEWLINE);
		}

		writeToFile(project, metricType, builder.toString());
	}

	private void buildWmcMetrics(JavaProject project) throws MetricBuildException {
		List<WmcMetric> wmcMetrics = mAnalyzer.calculateWeightedMethodsPerClass(project);
		MType metricType = MType.WMC;

		String docTitle = getDocTitle(project, metricType);
		String rowTitles = getRowTitles(metricType);

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (WmcMetric metric : wmcMetrics) {
			builder.append(buildRow(metric.element, metric.metric) + NEWLINE);
		}

		writeToFile(project, metricType, builder.toString());
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

	private String getDocTitle(JavaProject project, MType metricType) {
		return String.format("%s v. %s - %s", project.getName(), project.getVersion(), metricType.metricName);
	}

	private String getFileName(JavaProject project, MType metricType) {
		return String.format("%s_%s_%s.%s", project.getName(), project.getVersion(), metricType, FILE_EXTENSION);
	}

	private void writeToFile(JavaProject project, MType metricType, String output) throws MetricBuildException {
		String fileName = getFileName(project, metricType);

		try {
			String basePath = getDirectoryPath(project);

			String filePath = String.format("%s/%s", basePath, fileName);
			File file = new File(filePath);
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.write(output);
			writer.close();

			logger.info(String.format("Created file %s for project [%s] version [%s]!", fileName, project.getName(), project.getVersion()));
		} catch (FileNotFoundException e) {
			throw new MetricBuildException(
					String.format("File %s for project [%s] version [%s] was not found...", fileName, project.getName(), project.getVersion()), e);
		} catch (UnsupportedEncodingException e) {
			throw new MetricBuildException(
					String.format("Error writing to %s for project [%s] version [%s]...", fileName, project.getName(), project.getVersion()), e);
		}
	}

	/**
	 * Creates the directories for graphs for this project, if those directories do not yet exists. This also returns
	 * the full base path for this project's directory.
	 * 
	 * @param project
	 *            The {@link JavaProject}
	 * @return The base path for the project directory
	 */
	private String getDirectoryPath(JavaProject project) {
		String filePath = String.format("%s/%s/%s", mPath, project.getSystemName(), project.getName());
		File graphDirectory = new File(filePath);
		graphDirectory.mkdirs();
		return filePath;
	}

}
