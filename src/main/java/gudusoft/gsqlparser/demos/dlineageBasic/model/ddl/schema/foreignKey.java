package demos.dlineageBasic.model.ddl.schema;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class foreignKey {
	@Attribute(required = true)
	private String name;
	
	@Attribute(required = true)
	private String foreignTable;
	
	@Attribute(required = false)
	private String onDelete;
	
	@Attribute(required = false)
	private String onUpdate;
	
	@ElementList(entry = "reference", inline = true, required = false)
	private List<reference> references;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getForeignTable() {
		return foreignTable;
	}

	public void setForeignTable(String foreignTable) {
		this.foreignTable = foreignTable;
	}

	public String getOnDelete() {
		return onDelete;
	}

	public void setOnDelete(String onDelete) {
		this.onDelete = onDelete;
	}

	public String getOnUpdate() {
		return onUpdate;
	}

	public void setOnUpdate(String onUpdate) {
		this.onUpdate = onUpdate;
	}

	public List<reference> getReferences() {
		if(references == null)
			references = new ArrayList<reference>();
		return references;
	}

	public void setReferences(List<reference> references) {
		this.references = references;
	}
}
