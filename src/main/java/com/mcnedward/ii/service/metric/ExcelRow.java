package com.mcnedward.ii.service.metric;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Edward - Jul 25, 2016
 *
 */
public class ExcelRow {

	public String rowName;
	public Map<String, ExcelColumn> columnMap;
	private List<ExcelColumn> sortedColumns;
	
	public ExcelRow(String rowName) {
		this.rowName = rowName;
		columnMap = new LinkedHashMap<>();
	}
	
	public List<ExcelColumn> getColumns() {
		List<ExcelColumn> columns = new ArrayList<>();
		for (Map.Entry<String, ExcelColumn> entry : columnMap.entrySet()) {
			columns.add(entry.getValue());
		}
		return columns;
	}

	public List<ExcelColumn> getSortedColumns() {
		return sortedColumns;
	}

	public void setSortedColumns(List<ExcelColumn> sortedColumns) {
		this.sortedColumns = sortedColumns;
	}
	
}
