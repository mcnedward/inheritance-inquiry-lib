package com.mcnedward.ii.service.metric;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.MetricBuildException;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.MetricExportListener;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.NocHierarchy;
import com.mcnedward.ii.service.metric.element.MetricInfo;
import com.mcnedward.ii.service.metric.element.MetricOptions;
import com.mcnedward.ii.service.metric.element.WmcMetric;
import com.mcnedward.ii.utils.IILogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * This is a service tool for creating files readable by Excel for the Metrics of a {@link JavaSolution}.
 *
 * @author Edward - Jul 14, 2016
 */
public final class MetricService implements IMetricService {

	private static final String METRIC_DIRECTORY_PATH = "C:/users/edward/dev/IIMetrics";
	private static final String CSV_FILE_EXTENSION = "csv";
	private static final String FILE_EXTENSION = "txt";
	private static final String NEWLINE = "\n";
	private static final String CSV_DELIMITER = ",";
	private static final String DELIMITER = "\t";

	public void exportMetrics(MetricOptions options, MetricExportListener listener) {
		try {
			if (options.exportDit()) {
				buildDitMetricsDetails(options);
			}
			if (options.exportNoc()) {
				buildNocMetricsDetails(options);
			}
			if (options.exportWmc()) {
				buildWmcMetricsDetails(options);
			}
			if (options.exportFull()) {
				buildSolutionDetails(options);
			}
		} catch (MetricBuildException | TaskBuildException e) {
			listener.onBuildError("There was a problem building the metric details.", e);
		}
		listener.onMetricsExported();
	}

	/**
	 * Builds the full details, including the Metric levels and any other analyzed information, for a single
	 * {@link JavaSolution}.
	 * 
	 * @param options
	 *            The {@link MetricOptions}
	 *
	 * @throws TaskBuildException
	 * @throws MetricBuildException
	 */
	public void buildSolutionDetails(MetricOptions options) throws TaskBuildException, MetricBuildException {
		JavaSolution solution = options.getSolution();
		String initialDetails = buildInitialDetails(solution);

		String ditLevels = buildMetricLevels(solution, MetricType.DIT);
		String ditInfo = buildMetricInfo(solution, MetricType.DIT);

		String nocLevels = buildMetricLevels(solution, MetricType.NOC);
		String nocInfo = buildMetricInfo(solution, MetricType.NOC);

		String wmcLevels = buildMetricLevels(solution, MetricType.WMC);
		String wmcInfo = buildMetricInfo(solution, MetricType.WMC);

		String oMethodInfo = buildMetricInfo(solution, MetricType.OM);
		String eMethodInfo = buildMetricInfo(solution, MetricType.EM);

		String fileName = String.format("%s_%s", solution.getProjectName(), "FullMetrics");
		writeExcel(options, fileName, initialDetails, ditLevels, ditInfo, nocLevels, nocInfo, wmcLevels, wmcInfo, oMethodInfo, eMethodInfo);
	}

	private String buildInitialDetails(JavaSolution solution) {
		List<String> columns = new ArrayList<>();
		columns.add("Classes");
		columns.add("Inheritance Use");
		columns.add("Max Width");
		columns.add("Average Width");
		columns.add("Max NDC");
		columns.add("Average NDC");
		columns.add("NOC & WMC Danger");
		columns.add("Overridden Methods");
		columns.add("Extended Methods");

		List<List<String>> rows = new ArrayList<>();
		List<String> row = new ArrayList<>();

		row.add(String.valueOf(solution.getClassCount()));
		row.add(String.valueOf(solution.getInheritanceCount()));
		row.add(String.valueOf(solution.getMaxWidth()));
		row.add(String.valueOf(solution.getAverageWidth()));
		row.add(String.valueOf(solution.getNdcMax()));
		row.add(String.valueOf(solution.getNdcAverage()));
		row.add(String.valueOf(solution.getInheritedMethodRisks().size()));
		row.add(String.valueOf(solution.getOMethods().size()));
		row.add(String.valueOf(solution.getEMethods().size()));
		rows.add(row);

		return buildExcel(columns, rows, null);
	}

	@SuppressWarnings("unused")
	private String buildMetricLevels(List<JavaSolution> solutions, MetricType metricType) throws TaskBuildException {
		List<ExcelRow> rows = new ArrayList<>();
		List<String> columnHeaders = new ArrayList<>();
		for (JavaSolution solution : solutions) {
			MetricLevelSection section = new MetricLevelSection(solution, metricType);
			rows.addAll(section.excelRows);
			columnHeaders.addAll(section.columnHeaders);
		}
		return buildExcelSections(rows, columnHeaders, metricType.name());
	}

	private String buildMetricLevels(JavaSolution solution, MetricType metricType) throws TaskBuildException {
		MetricLevelSection section = new MetricLevelSection(solution, metricType);
		return buildExcelSections(section.excelRows, section.columnHeaders, metricType.name());
	}

	private String buildExcelSections(List<ExcelRow> excelRows, List<String> columnHeaders, String title) {
		// Add any missing column headers to the rows
		for (ExcelRow row : excelRows) {
			for (String columnHeader : columnHeaders) {
				boolean contains = false;
				for (ExcelColumn column : row.getColumns()) {
					if (column.columnName.equals(columnHeader)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					ExcelColumn c = new ExcelColumn(columnHeader);
					c.count = 0;
					row.columnMap.put(columnHeader, c);
				}
			}
		}

		// Sort all of the row's columns
		ExcelColumnComparator comp = new ExcelColumnComparator();
		for (ExcelRow row : excelRows) {
			List<ExcelColumn> sortedColumns = row.getColumns();
			sortedColumns.sort(comp);
			row.setSortedColumns(sortedColumns);
		}

		// Sort all of the column headers
		ColumnHeaderComparator comp2 = new ColumnHeaderComparator();
		columnHeaders.sort(comp2);
		// Add the first column
		columnHeaders.add(0, "System Name");

		List<List<String>> rows = new ArrayList<>();
		for (ExcelRow excelRow : excelRows) {
			List<String> row = new ArrayList<>();
			row.add(excelRow.rowName);
			for (ExcelColumn c : excelRow.getSortedColumns()) {
				row.add(String.valueOf(c.count));
			}
			rows.add(row);
		}
		return buildExcel(columnHeaders, rows, title);
	}

	private String buildMetricInfo(JavaSolution solution, MetricType metricType) throws TaskBuildException {
		MetricInfo metricInfo = solution.getMetricInfo(metricType);
		if (metricInfo == null)
			return "";

		List<String> columnHeaders = new ArrayList<>();
		columnHeaders.add("Min");
		columnHeaders.add("Average");
		columnHeaders.add("Max");
		columnHeaders.add("Max Class for " + metricType.metricName);

		List<List<String>> rows = new ArrayList<>();
		List<String> row = new ArrayList<>();
		rows.add(row);
		row.add(String.valueOf(metricInfo.getMin()));
		row.add(String.valueOf(metricInfo.getAverage()));
		row.add(String.valueOf(metricInfo.getMax()));
		row.add(metricInfo.getMaxClasses().toString());

		return buildExcel(columnHeaders, rows, null);
	}

	public void buildDitMetricsDetails(MetricOptions options) throws MetricBuildException {
		JavaSolution solution = options.getSolution();
		List<DitHierarchy> ditHierarchies = solution.getDitHierarchies();
		MetricType metricType = MetricType.DIT;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType, "Number of Inherited Methods" + getDelimiter(options) + "Total Number of Methods");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (DitHierarchy hierarchy : ditHierarchies) {
			if (hierarchy.getDit() > 1)
				builder.append(buildRow(options, hierarchy.getElementName(), hierarchy.getDit(), String.valueOf(hierarchy.getInheritedMethodCount()))
						+ getDelimiter(options) + String.valueOf(hierarchy.getElementMethodCount()) + NEWLINE);
		}

		String fileName = String.format("%s_%s", solution.getProjectName(), metricType.metricName);
		writeToFile(options, fileName, builder.toString());
	}

	public void buildNocMetricsDetails(MetricOptions options) throws MetricBuildException {
		buildNocMetricsDetails(options, null);
	}

	public void buildNocMetricsDetails(MetricOptions options, Collection<String> elements) throws MetricBuildException {
		JavaSolution solution = options.getSolution();
		List<NocHierarchy> nocMetrics = solution.getNocHierarchies();
		MetricType metricType = MetricType.NOC;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType, "Class Children");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (NocHierarchy metric : nocMetrics) {
			if (elements != null) {
				if (!elements.contains(metric.getElementName()))
					continue;
			}
			builder.append(buildRow(options, metric.getFullElementName(), metric.getNoc(), metric.getTree().toString()) + NEWLINE);
		}

		String fileName = String.format("%s_%s", solution.getProjectName(), metricType.metricName);
		writeToFile(options, fileName, builder.toString());
	}

	public void buildNocHierarachies(JavaSolution solution, Collection<String> elements) throws MetricBuildException {
		List<NocHierarchy> nocMetrics = solution.getNocHierarchies();

		for (NocHierarchy metric : nocMetrics) {
			if (elements != null) {
				if (!elements.contains(metric.getElementName()))
					continue;
			}

			StringBuilder builder = new StringBuilder(metric.getElementName() + NEWLINE);
			builder.append("Children" + DELIMITER + "WMC" + DELIMITER + "Inherited Method Count" + NEWLINE);
			while (!metric.getTree().isEmpty()) {
				NocHierarchy child = metric.getTree().pop();
				builder.append(child.getElementName() + DELIMITER + child.getWmc() + DELIMITER + child.getInheritedMethodCount() + NEWLINE);
			}
			// writeToFile(solution, metric.getElementName(), builder.toString());
		}
	}

	public void buildWmcMetricsDetails(MetricOptions options) throws MetricBuildException {
		JavaSolution solution = options.getSolution();
		List<WmcMetric> wmcMetrics = solution.getWmcMetrics();
		MetricType metricType = MetricType.WMC;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType);

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (WmcMetric metric : wmcMetrics) {
			builder.append(buildRow(options, metric.getFullyQualifiedName(), metric.getMetric()) + NEWLINE);
		}

		String fileName = String.format("%s_%s", solution.getProjectName(), metricType.metricName);
		writeToFile(options, fileName, builder.toString());
	}

	private String buildRow(MetricOptions options, String fullyQualifiedName, int metric) {
		return buildRow(options, fullyQualifiedName, metric, null);
	}

	private String buildRow(MetricOptions options, String fullyQualifiedName, int metric, String extra) {
		String extraColumn = "";
		if (extra != null && !extra.equals("")) {
			extraColumn += getDelimiter(options) + extra;
		}
		return String.format("%s%s %s%s", fullyQualifiedName, getDelimiter(options), String.valueOf(metric), extraColumn);
	}

	private String getRowTitles(MetricType metricType) {
		return getRowTitles(metricType, null);
	}

	private String getRowTitles(MetricType metricType, String extra) {
		String extraColumn = "";
		if (extra != null && !extra.equals("")) {
			extraColumn += DELIMITER + extra;
		}
		return String.format("Class or Interface%s %s%s", DELIMITER, metricType.metricName, extraColumn);
	}

	private String getDocTitle(JavaSolution solution, MetricType metricType) {
		return String.format("%s - %s", solution.getSystemName(), metricType.metricName);
	}

	/**
	 * Creates an Excel-readable file in the root Metrics directory
	 *
	 * @param options
	 *            The {@link MetricOptions}
	 * @param title
	 *            The title for the file.
	 * @param excelData
	 *            The data for the file.
	 * @return True if the file was created, false otherwise.
	 */
	private boolean writeExcel(MetricOptions options, String title, String... excelData) {
		try {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < excelData.length; i++) {
				String data = excelData[i];
				builder.append(data);
				if (i != excelData.length - 1)
					builder.append(NEWLINE);
			}
			writeToFile(title, builder.toString());
			return true;
		} catch (MetricBuildException e) {
			IILogger.error(e);
			return false;
		}
	}

	/**
	 * Builds a section for an Excel-readable file.
	 *
	 * @param columns
	 *            The column headers.
	 * @param rows
	 *            The rows of data.
	 * @param title
	 *            A title for the data.
	 * @return The String for the Excel-readable data.
	 */
	private String buildExcel(List<String> columns, List<List<String>> rows, String title) {
		StringBuilder builder = new StringBuilder();
		if (title != null)
			builder.append(title + NEWLINE);
		for (String column : columns) {
			builder.append(column + DELIMITER);
		}

		for (List<String> row : rows) {
			builder.append(NEWLINE);
			for (String rowContent : row) {
				builder.append(rowContent + DELIMITER);
			}
		}
		return builder.toString();
	}

	private void writeToFile(MetricOptions options, String title, String output) throws MetricBuildException {
		File file = buildMetricFile(options, title);
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.write(output);
			writer.close();

			IILogger.info(String.format("Created file %s", file.getPath()));
		} catch (FileNotFoundException e) {
			throw new MetricBuildException(String.format("File %s was not found...", file.getName()), e);
		} catch (UnsupportedEncodingException e) {
			throw new MetricBuildException(String.format("Error writing to %s...", file.getName()), e);
		}
	}

	private void writeToFile(String fileName, String output) throws MetricBuildException {
		String basePath = getDirectoryPath();
		String filePath = String.format("%s/%s.%s", basePath, fileName, FILE_EXTENSION);
		File file = new File(filePath);
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.write(output);
			writer.close();

			IILogger.info(String.format("Created file [%s]! [%s]", file.getName(), file.getPath()));
		} catch (FileNotFoundException e) {
			throw new MetricBuildException(String.format("File %s was not found...", file.getName()), e);
		} catch (UnsupportedEncodingException e) {
			throw new MetricBuildException(String.format("Error writing to %s ...", file.getName()), e);
		}
	}

	private File buildMetricFile(MetricOptions options, String fileName) throws MetricBuildException {
		String directoryPath = options.getDirectory().getAbsolutePath();

		File metricDirectory = new File(directoryPath);
		if (!metricDirectory.exists()) {
			if (!metricDirectory.mkdirs())
				throw new MetricBuildException("Could not build the directory at: " + directoryPath);
		}
		String theFileName = String.format("%s.%s", fileName, options.useCsvFormt() ? CSV_FILE_EXTENSION : FILE_EXTENSION);
		return new File(directoryPath + "/" + theFileName);
	}

	private String getDelimiter(MetricOptions options) {
		return options.useCsvFormt() ? CSV_DELIMITER : DELIMITER;
	}

	/**
	 * Creates the directories for metrics for this solution, if those directories do not yet exists. This also returns
	 * the full base path for this solution's directory.
	 * @return 
	 */
	private String getDirectoryPath() {
		String filePath = String.format("%s", METRIC_DIRECTORY_PATH);
		File metricDirectory = new File(filePath);
		metricDirectory.mkdirs();
		return filePath;
	}

	private final class ExcelColumnComparator implements Comparator<ExcelColumn> {
		@Override
		public int compare(ExcelColumn o1, ExcelColumn o2) {
			int value1 = Integer.valueOf(o1.columnName);
			int value2 = Integer.valueOf(o2.columnName);
			return value1 - value2;
		}
	}

	private final class ColumnHeaderComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			return Integer.valueOf(o1) - Integer.valueOf(o2);
		}
	}

}