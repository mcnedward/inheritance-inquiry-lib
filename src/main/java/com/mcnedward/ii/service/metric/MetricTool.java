package com.mcnedward.ii.service.metric;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.metric.element.Metric;

/**
 * @author Edward - Jul 24, 2016
 *
 */
public class MetricTool {

	private MetricService mService;

	public MetricTool() {
		mService = new MetricService();
	}

	public void inquire(List<JavaSolution> solutions) throws TaskBuildException {
		inquireMetric(solutions, MType.DIT);
		inquireMetric(solutions, MType.NOC);
		inquireMetric(solutions, MType.WMC);
	}

	private void inquireMetric(List<JavaSolution> solutions, MType metricType) throws TaskBuildException {
		String title = metricType.name();

		List<ExcelRow> excelRows = new ArrayList<>();
		List<String> columnHeaders = new ArrayList<>();
		for (JavaSolution solution : solutions) {
			List<? extends Metric> metrics;
			switch (metricType) {
			case DIT:
				metrics = solution.getDitMetrics();
				break;
			case NOC:
				metrics = solution.getNocMetrics();
				break;
			case WMC:
				metrics = solution.getWmcMetrics();
				break;
			default:
				throw new TaskBuildException("Metric type " + metricType.name() + " is not acceptable for inquiry...");
			}

			ExcelRow row = getRow(metrics, solution.getSystemName(), columnHeaders);
			excelRows.add(row);
		}

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
		mService.buildExcel(columnHeaders, rows, title);
	}

	private ExcelRow getRow(List<? extends Metric> metrics, String systemName, List<String> columnHeaders) {
		ExcelRow row = new ExcelRow(systemName);
		for (Metric metric : metrics) {
			// Find the correct column, or create if it doesn't exist
			String columnName = String.valueOf(metric.metric);
			ExcelColumn column = row.columnMap.get(columnName);
			if (column == null) {
				column = new ExcelColumn(columnName);
				row.columnMap.put(columnName, column);
				if (!columnHeaders.contains(columnName))
					columnHeaders.add(columnName);
			}
			column.count++;
		}
		return row;
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
