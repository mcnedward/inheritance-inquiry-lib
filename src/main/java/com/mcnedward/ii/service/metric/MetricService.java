package com.mcnedward.ii.service.metric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.MetricBuildException;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.NocHierarchy;
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

	/**
	 * Builds the details on every Metric for every element in a {@link JavaSolution}.
	 * 
	 * @param solution
	 *            The solution.
	 * @return True if the files were created, false otherwise.
	 */
	public boolean buildMetricDetails(JavaSolution solution) {
		try {
			buildDitMetricsDetails(solution);
			buildNocMetricsDetails(solution);
			buildWmcMetricsDetails(solution);
			return true;
		} catch (MetricBuildException e) {
			IILogger.error(e);
			return false;
		}
	}

	/**
	 * Builds the full details, including the Metric levels and any other analyzed information, for a single {@link JavaSolution}.
	 * @param solution The solution.
	 * @return True if the file was created, false otherwise.
	 * @throws TaskBuildException
	 */
	public boolean buildSolutionDetails(JavaSolution solution) throws TaskBuildException {
		String initialDetails = buildInitialDetails(solution);
		String ditLevels = buildMetricLevels(solution, MType.DIT);
		String ditInfo = buildMetricInfo(solution, MType.DIT);

		String nocLevels = buildMetricLevels(solution, MType.NOC);
		String nocInfo = buildMetricInfo(solution, MType.NOC);

		String wmcLevels = buildMetricLevels(solution, MType.WMC);
		String wmcInfo = buildMetricInfo(solution, MType.NOC);

		return writeExcel(solution, "FullMetrics", initialDetails, ditLevels, ditInfo, nocLevels, nocInfo, wmcLevels, wmcInfo);
	}

	/**
	 * Builds the all the Metric levels for a collection of {@link JavaSolution}s. 
	 * @param solutions The solutions.
	 * @return True if the files were created, false otherwise.
	 * @throws TaskBuildException
	 */
	public boolean buildSolutionsMetricLevels(List<JavaSolution> solutions) throws TaskBuildException {
		String ditData = buildMetricLevels(solutions, MType.DIT);
		String nocData = buildMetricLevels(solutions, MType.NOC);
		String wmcData = buildMetricLevels(solutions, MType.WMC);
		return writeExcel("Metrics", ditData, nocData, wmcData);
	}

	private String buildInitialDetails(JavaSolution solution) {
		List<String> columns = new ArrayList<>();
		columns.add("Classes");
		columns.add("Inheritance Use");
		List<List<String>> rows = new ArrayList<>();
		List<String> row = new ArrayList<>();
		row.add(String.valueOf(solution.getClassCount()));
		row.add(String.valueOf(solution.getInheritanceCount()));
		rows.add(row);
		return buildExcel(columns, rows, null);
	}

	private String buildMetricLevels(List<JavaSolution> solutions, MType metricType) throws TaskBuildException {
		List<ExcelRow> rows = new ArrayList<>();
		List<String> columnHeaders = new ArrayList<>();
		for (JavaSolution solution : solutions) {
			MetricLevelSection section = new MetricLevelSection(solution, metricType);
			rows.addAll(section.excelRows);
			columnHeaders.addAll(section.columnHeaders);
		}
		return buildExcelSections(rows, columnHeaders, metricType.name());
	}

	private String buildMetricLevels(JavaSolution solution, MType metricType) throws TaskBuildException {
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
		columnHeaders.add(0, "System");

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

	private String buildMetricInfo(JavaSolution solution, MType metricType) throws TaskBuildException {
		MetricInfoSection section = new MetricInfoSection(solution, metricType);
		return buildExcel(section.columnHeaders, section.rows, null);
	}

	public void buildDitMetricsDetails(JavaSolution solution) throws MetricBuildException {
		List<DitHierarchy> ditHierarchies = solution.getDitHierarchies();
		MType metricType = MType.DIT;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType, "Number of Inherited Methods" + DELIMITER + "Total Number of Methods");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (DitHierarchy hierarchy : ditHierarchies) {
			builder.append(buildRow(hierarchy.element, hierarchy.dit, String.valueOf(hierarchy.inheritedMethodCount)) + DELIMITER
					+ String.valueOf(hierarchy.elementMethodCount) + NEWLINE);
		}

		writeToFile(solution, metricType, builder.toString());
	}

	public void buildNocMetricsDetails(JavaSolution solution) throws MetricBuildException {
		List<NocHierarchy> nocMetrics = solution.getNocHierarchies();
		MType metricType = MType.NOC;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType, "Class Children");

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (NocHierarchy metric : nocMetrics) {
			builder.append(buildRow(metric.element, metric.noc, metric.tree.toString()) + NEWLINE);
		}

		writeToFile(solution, metricType, builder.toString());
	}

	public void buildWmcMetricsDetails(JavaSolution solution) throws MetricBuildException {
		List<WmcMetric> wmcMetrics = solution.getWmcMetrics();
		MType metricType = MType.WMC;

		String docTitle = getDocTitle(solution, metricType);
		String rowTitles = getRowTitles(metricType);

		StringBuilder builder = new StringBuilder(docTitle + NEWLINE);
		builder.append(rowTitles + NEWLINE);
		for (WmcMetric metric : wmcMetrics) {
			builder.append(buildRow(metric.fullyQualifiedName, metric.value) + NEWLINE);
		}

		writeToFile(solution, metricType, builder.toString());
	}

	private String buildRow(String fullyQualifiedName, int metric) {
		return buildRow(fullyQualifiedName, metric, null);
	}

	private String buildRow(String fullyQualifiedName, int metric, String extra) {
		String extraColumn = "";
		if (extra != null && !extra.equals("")) {
			extraColumn += DELIMITER + extra;
		}
		return String.format("%s%s %s%s", fullyQualifiedName, DELIMITER, String.valueOf(metric), extraColumn);
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

	private String getFileName(JavaSolution solution, String fileName) {
		return String.format("%s_%s_%s.%s", solution.getProjectName(), solution.getVersion(), fileName, FILE_EXTENSION);
	}

	/**
	 * Creates an Excel-readable file in the Metric directory for the {@link JavaSolution}.
	 * 
	 * @param title
	 *            A title for the file.
	 * @param excelData
	 *            The data for the file.
	 * @return True if it was successfully created, false otherwise.
	 */
	private boolean writeExcel(JavaSolution solution, String title, String... excelData) {
		try {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < excelData.length; i++) {
				String data = excelData[i];
				builder.append(data);
				if (i != excelData.length)
					builder.append(NEWLINE);
			}
			writeToFile(solution, title, builder.toString());
			return true;
		} catch (MetricBuildException e) {
			IILogger.error(e);
			return false;
		}
	}

	/**
	 * Creates an Excel-readable file in the root Metrics directory
	 * 
	 * @param title
	 *            The title for the file.
	 * @param excelData
	 *            The data for the file.
	 * @return True if the file was created, false otherwise.
	 */
	private boolean writeExcel(String title, String... excelData) {
		try {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < excelData.length; i++) {
				String data = excelData[i];
				builder.append(data);
				if (i != excelData.length)
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

	private void writeToFile(JavaSolution solution, MType metricType, String output) throws MetricBuildException {
		writeToFile(solution, metricType.toString(), output);
	}

	private void writeToFile(JavaSolution solution, String title, String output) throws MetricBuildException {
		String fileName = getFileName(solution, title);
		String basePath = getDirectoryPath(solution);
		String filePath = String.format("%s/%s", basePath, fileName);
		File file = new File(filePath);
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.write(output);
			writer.close();

			IILogger.info(String.format("Created file for project [%s] version [%s]! [%s]", solution.getProjectName(), solution.getVersion(),
					file.getPath()));
		} catch (FileNotFoundException e) {
			throw new MetricBuildException(String.format("File %s for solution [%s] project [%s] was not found...", file.getName(),
					solution.getProjectName(), solution.getVersion()), e);
		} catch (UnsupportedEncodingException e) {
			throw new MetricBuildException(String.format("Error writing to %s for project [%s] version [%s]...", file.getName(),
					solution.getProjectName(), solution.getVersion()), e);
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