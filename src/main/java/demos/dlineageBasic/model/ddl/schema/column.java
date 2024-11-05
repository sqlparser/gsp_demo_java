package demos.dlineageBasic.model.ddl.schema;

import org.simpleframework.xml.Attribute;

public class column {
	@Attribute(required = true)
	private String name;
	
	@Attribute(required = false)
	private String primaryKey;
	
	@Attribute(required = false)
	private String required;
	
	@Attribute(required = false)
	private String type;
	
	@Attribute(required = false)
	private String size;
	
	@Attribute(name="default", required = false)
	private String defaultValue;
	
	@Attribute(required = false)
	private String autoIncrement;
	
	@Attribute(required = false)
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(String autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
