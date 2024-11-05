package demos.dlineageBasic.model.xml;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "columnImpactResult")
public class columnImpactResult {

	@ElementList(entry = "targetColumn", inline = true, required = false)
	private List<targetColumn> columns;

	@ElementList(entry = "table", inline = true, required = false)
	private List<table> tables;

	public List<targetColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<targetColumn> columns) {
		this.columns = columns;
	}

	public List<table> getTables() {
		return tables;
	}

	public void setTables(List<table> tables) {
		this.tables = tables;
	}
}
