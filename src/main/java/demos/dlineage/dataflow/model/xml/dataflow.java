package demos.dlineage.dataflow.model.xml;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "dlineage")
public class dataflow {
	@ElementList(entry = "procedure", inline = true, required = false)
	private List<procedure> procedures;
	@ElementList(entry = "table", inline = true, required = false)
	private List<table> tables;
	@ElementList(entry = "view", inline = true, required = false)
	private List<table> views;
	@ElementList(entry = "resultset", inline = true, required = false)
	private List<table> resultsets;
	@ElementList(entry = "relation", inline = true, required = false)
	private List<relation> relations;

	public dataflow() {
	}

	public List<relation> getRelations() {
		return this.relations;
	}

	public void setRelations(List<relation> relations) {
		this.relations = relations;
	}

	public List<table> getTables() {
		return this.tables;
	}

	public void setTables(List<table> tables) {
		this.tables = tables;
	}

	public List<table> getViews() {
		return this.views;
	}

	public void setViews(List<table> views) {
		this.views = views;
	}

	public List<table> getResultsets() {
		return this.resultsets;
	}

	public void setResultsets(List<table> resultsets) {
		this.resultsets = resultsets;
	}

	public List<procedure> getProcedures() {
		return this.procedures;
	}

	public void setProcedures(List<procedure> procedures) {
		this.procedures = procedures;
	}
}
