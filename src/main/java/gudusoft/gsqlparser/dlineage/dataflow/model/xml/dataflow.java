package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "dlineage")
@XmlType(propOrder = { "procedures", "tables", "views", "resultsets", "relations" })
public class dataflow {

	private List<procedure> procedures;

	private List<table> tables;

	private List<table> views;

	private List<table> resultsets;

	private List<relation> relations;

	public dataflow() {
	}

	@XmlElement(name = "procedure", required = false)
	public List<procedure> getProcedures() {
		if (this.procedures == null) {
			this.procedures = new LinkedList<procedure>();
		}
		return this.procedures;
	}

	@XmlElement(name = "table", required = false)
	public List<table> getTables() {
		if (this.tables == null) {
			this.tables = new LinkedList<table>();
		}
		return this.tables;
	}

	@XmlElement(name = "view", required = false)
	public List<table> getViews() {
		if (this.views == null) {
			this.views = new LinkedList<table>();
		}
		return this.views;
	}

	@XmlElement(name = "resultset", required = false)
	public List<table> getResultsets() {
		if (this.resultsets == null) {
			this.resultsets = new LinkedList<table>();
		}
		return this.resultsets;
	}

	@XmlElement(name = "relation", required = false)
	public List<relation> getRelations() {
		if (this.relations == null) {
			this.relations = new LinkedList<relation>();
		}
		return this.relations;
	}

	public void setRelations(List<relation> relations) {
		this.relations = relations;
	}

	public void setTables(List<table> tables) {
		this.tables = tables;
	}

	public void setViews(List<table> views) {
		this.views = views;
	}

	public void setResultsets(List<table> resultsets) {
		this.resultsets = resultsets;
	}

	public void setProcedures(List<procedure> procedures) {
		this.procedures = procedures;
	}
}
