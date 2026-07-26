package demos.dlineageBasic.model.ddl.schema;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class index {
	
	@Attribute(required = false)
	private String name;
	
	@ElementList(entry = "index-column", inline = true, required = true)
	private List<indexColumn> indexColumns;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<indexColumn> getIndexColumns() {
		if(indexColumns == null)
			indexColumns = new ArrayList<indexColumn>();
		return indexColumns;
	}

	public void setIndexColumns(List<indexColumn> indexColumns) {
		this.indexColumns = indexColumns;
	}
	
}
