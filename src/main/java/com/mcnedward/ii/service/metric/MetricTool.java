package com.mcnedward.ii.service.metric;

import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
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

	public void inquire(List<JavaSolution> solutions) {
		inquireDit(solutions);
		inquireNoc(solutions);
		inquireWmc(solutions);
	}

	private void inquireDit(List<JavaSolution> solutions) {
		String title = "System DITs";
		String[] columns = new String[] { "System Name", "1", "2", "3", "4", "5", "6", ">=7" };

		List<String[]> rows = new ArrayList<>();
		for (JavaSolution solution : solutions) {
			rows.add(getRow(solution.getSystemName(), solution.getDitMetrics()));
		}

		mService.buildExcel(columns, rows, title);
	}

	private void inquireNoc(List<JavaSolution> solutions) {
		String title = "System NOCs";
		String[] columns = new String[] { "System Name", "1", "2", "3", "4", "5", "6", ">=7" };

		List<String[]> rows = new ArrayList<>();
		for (JavaSolution solution : solutions) {
			rows.add(getRow(solution.getSystemName(), solution.getNocMetrics()));
		}

		mService.buildExcel(columns, rows, title);
	}

	private void inquireWmc(List<JavaSolution> solutions) {
		String title = "System WMCs";
		String[] columns = new String[] { "System Name", "1", "2", "3", "4", "5", "6", ">=7" };

		List<String[]> rows = new ArrayList<>();
		for (JavaSolution solution : solutions) {
			rows.add(getRow(solution.getSystemName(), solution.getWmcMetrics()));
		}

		mService.buildExcel(columns, rows, title);
	}

	private String[] getRow(String systemName, List<? extends Metric> metrics) {
		int one, two, three, four, five, six, sevenOrMore;
		one = two = three = four = five = six = sevenOrMore = 0;
		for (Metric metric : metrics) {
			if (metric.metric == 1) {
				one++;
			} else if (metric.metric == 2) {
				two++;
			} else if (metric.metric == 3) {
				three++;
			} else if (metric.metric == 4) {
				four++;
			} else if (metric.metric == 5) {
				five++;
			} else if (metric.metric == 6) {
				six++;
			} else if (metric.metric >= 7) {
				sevenOrMore++;
			}
		}
		return new String[] { systemName, String.valueOf(one), String.valueOf(two), String.valueOf(three), String.valueOf(four), String.valueOf(five),
				String.valueOf(six), String.valueOf(sevenOrMore) };
	}

}
