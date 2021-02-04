package demos.dlineageBasic.model.ddl.schema;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "database")
public class database {
	@Attribute(required = false)
	private String name;

	@ElementList(entry = "table", inline = true, required = false)
	private List<table> tables;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<table> getTables() {
		if (tables == null)
			tables = new ArrayList<table>();
		return tables;
	}

	public void setTables(List<table> tables) {
		this.tables = tables;
	}
}
