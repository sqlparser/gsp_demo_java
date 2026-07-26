package demos.columnDetail;

import java.util.HashMap;

@SuppressWarnings("serial")
public class TableMetaData extends HashMap<String, Object> {
	private static final String PROP_NAME = "name";
	private static final String PROP_CATALOGNAME = "catalogName";
	private static final String PROP_SCHEMANAME = "schemaName";
	private static final String PROP_COMMENT = "comment";

	private String name;
	private String schemaName;
	private String catalogName;

	private boolean strict = false;

	public TableMetaData(boolean strict) {
		this.strict = strict;
		this.put(PROP_CATALOGNAME, "");
		this.put(PROP_SCHEMANAME, "");
		this.put(PROP_COMMENT, "");
	}

	public void setName(String name) {
		name = trim(name);
		this.name = name;
		if (name != null)
			this.put(PROP_NAME, name);
	}

	public void setCatalogName(String catalogName) {
		catalogName = trim(catalogName);
		this.catalogName = catalogName;
		if (catalogName != null)
			this.put(PROP_CATALOGNAME, catalogName);
	}

	public void setSchemaName(String schemaName) {
		schemaName = trim(schemaName);
		this.schemaName = schemaName;
		if (schemaName != null)
			this.put(PROP_SCHEMANAME, schemaName);
	}

	private String trim(String string) {
		if (string == null)
			return string;

		if (string.startsWith("\"") && string.endsWith("\""))
			return string.substring(1, string.length() - 1);

		return string;
	}

	public void setComment(String comment) {
		if (schemaName != null)
			this.put(PROP_COMMENT, comment);
	}

	public String getName() {
		return (String) this.get(PROP_NAME);
	}

	public String getCatalogName() {
		return (String) this.get(PROP_CATALOGNAME);
	}

	public String getSchemaName() {
		return (String) this.get(PROP_SCHEMANAME);
	}

	public String getComment() {
		return (String) this.get(PROP_COMMENT);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		if (strict) {
			result = prime * result
					+ ((catalogName == null) ? 0 : catalogName.hashCode());
			result = prime * result
					+ ((schemaName == null) ? 0 : schemaName.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		TableMetaData other = (TableMetaData) obj;

		if (strict) {
			if (catalogName == null) {
				if (other.catalogName != null)
					return false;
			} else if (!catalogName.equals(other.catalogName))
				return false;

			if (schemaName == null) {
				if (other.schemaName != null)
					return false;
			} else if (!schemaName.equals(other.schemaName))
				return false;
		}

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		return true;
	}

}