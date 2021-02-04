package demos.dlineageBasic.model.ddl.schema;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class unique {
	@Attribute(required = false)
	private String name;
	
	@ElementList(entry = "unique-column", inline = true, required = true)
	private List<uniqueColumn> uniqueColumns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<uniqueColumn> getUniqueColumns() {
		if(uniqueColumns==null){
			uniqueColumns = new ArrayList<uniqueColumn>();
		}
		return uniqueColumns;
	}

	public void setUniqueColumns(List<uniqueColumn> uniqueColumns) {
		this.uniqueColumns = uniqueColumns;
	}
}
