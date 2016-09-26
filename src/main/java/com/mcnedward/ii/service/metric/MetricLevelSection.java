package com.mcnedward.ii.service.metric;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.metric.element.Metric;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: This should be placed in the AnalyzerService. I think anything that has to do with calculations should be done there
 * @author Edward - Aug 13, 2016
 *
 */
public final class MetricLevelSection extends MetricSection {

	List<String> columnHeaders;
	List<ExcelRow> excelRows;

	protected MetricLevelSection(JavaSolution solution, MType metricType) throws TaskBuildException {
		super(solution, metricType);
	}
	
	@Override
	protected void init() {
		columnHeaders = new ArrayList<>();
		excelRows = new ArrayList<>();
		
	}
	
	@Override
	protected void handleMetrics(List<? extends Metric> metrics) {
		ExcelRow row = new ExcelRow(mSystemName);
		for (Metric metric : metrics) {
			if (metric.isInterface()) {
				continue;
			}
			// Find the correct column, or create if it doesn't exist
			String columnName = String.valueOf(metric.getMetric());
			ExcelColumn column = row.columnMap.get(columnName);
			if (column == null) {
				column = new ExcelColumn(columnName);
				row.columnMap.put(columnName, column);
				if (!columnHeaders.contains(columnName))
					columnHeaders.add(columnName);
			}
			column.count++;
		}
		excelRows.add(row);
	}
}
