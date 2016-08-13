package com.mcnedward.ii.service.metric;

import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.metric.element.Metric;

/**
 * @author Edward - Aug 13, 2016
 *
 */
public final class MetricInfoSection extends MetricSection {

	List<String> columnHeaders;
	List<List<String>> rows;

	protected MetricInfoSection(JavaSolution solution, MType metricType) throws TaskBuildException {
		super(solution, metricType);
	}

	@Override
	protected void init() {
		columnHeaders = new ArrayList<>();
		columnHeaders.add("Min");
		columnHeaders.add("Max");
		rows = new ArrayList<>();
	}

	@Override
	protected void handleMetrics(List<? extends Metric> metrics) {
		int min = 0, max = 0;
		for (int i = 0; i < metrics.size(); i++) {
			int value = metrics.get(i).value;
			if (i == 0) {
				min = value;
				max = value;
			}
			if (value > max)
				max = value;
			if (value < min)
				min = value;
		}
		List<String> row = new ArrayList<>();
		row.add(String.valueOf(min));
		row.add(String.valueOf(max));
		rows.add(row);
	}

}
