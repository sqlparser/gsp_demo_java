package demos.dlineage.dataflow.model.xml;

import java.util.LinkedList;
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
		if(this.relations == null){
			this.relations = new LinkedList<>();
		}
		return this.relations;
	}

	public void setRelations(List<relation> relations) {
		this.relations = relations;
	}

	public List<table> getTables() {
		if(this.tables == null){
			this.tables = new LinkedList<>();
		}
		return this.tables;
	}

	public void setTables(List<table> tables) {
		this.tables = tables;
	}

	public List<table> getViews() {
		if(this.views == null){
			this.views = new LinkedList<>();
		}
		return this.views;
	}

	public void setViews(List<table> views) {
		this.views = views;
	}

	public List<table> getResultsets() {
		if(this.resultsets == null){
			this.resultsets = new LinkedList<>();
		}
		return this.resultsets;
	}

	public void setResultsets(List<table> resultsets) {
		this.resultsets = resultsets;
	}

	public List<procedure> getProcedures() {
		if(this.procedures == null){
			this.procedures = new LinkedList<>();
		}
		return this.procedures;
	}

	public void setProcedures(List<procedure> procedures) {
		this.procedures = procedures;
	}
}
