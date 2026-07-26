package demos.columnDetail;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class ColumnMetaData extends LinkedHashMap<String, Object> {
	private static final String PROP_AUTOINCREMENT = "autoIncrement";
	private static final String PROP_TYPENAME = "typeName";
	private static final String PROP_TYPECODE = "typeCode";
	private static final String PROP_TABLENAME = "tableName";
	private static final String PROP_COLUMNDISPLAYSIZE = "columnDisplaySize";
	private static final String PROP_ALIAS = "alias";
	private static final String PROP_CATALOGNAME = "catalogName";
	private static final String PROP_PRECISION = "precision";
	private static final String PROP_SCALE = "scale";
	private static final String PROP_SCHEMANAME = "schemaName";
	private static final String PROP_READONLY = "readOnly";
	private static final String PROP_WRITEABLE = "writeable";
	private static final String PROP_COMMENT = "comment";
	private static final String PROP_NULL = "isNull";
	private static final String PROP_DEFAULTVALUE = "defaultValue";
	private static final String PROP_PRIMARYKEY = "primaryKey";
	private static final String PROP_NOTNULL = "isNotNull";
	private static final String PROP_UNIQUE = "unique";
	private static final String PROP_FOREIGNKEY = "foreignKey";
	private static final String PROP_CHECK = "check";

	private String name;
	private String tableName;
	private String schemaName;
	private String catalogName;

	private boolean strict = false;

	public ColumnMetaData(boolean strict) {
		this.strict = strict;

		this.put(PROP_CATALOGNAME, "");
		this.put(PROP_SCHEMANAME, "");
		this.put(PROP_TABLENAME, "");
		this.put(PROP_ALIAS, "");
		this.put(PROP_TYPENAME, "");
		this.put(PROP_TYPECODE, "");
		this.put(PROP_COLUMNDISPLAYSIZE, "");
		this.put(PROP_PRECISION, "");
		this.put(PROP_SCALE, "");
		this.put(PROP_AUTOINCREMENT, "");
		this.put(PROP_READONLY, "");
		this.put(PROP_WRITEABLE, "");
		this.put(PROP_COMMENT, "");
		this.put(PROP_NULL, "");
		this.put(PROP_NOTNULL, "");
		this.put(PROP_DEFAULTVALUE, "");
		this.put(PROP_UNIQUE, "");
		this.put(PROP_CHECK, "");
		this.put(PROP_PRIMARYKEY, "");
		this.put(PROP_FOREIGNKEY, "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		if (strict) {
			result = prime * result
					+ ((catalogName == null) ? 0 : catalogName.hashCode());

			result = prime * result
					+ ((schemaName == null) ? 0 : schemaName.hashCode());
		}
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ColumnMetaData other = (ColumnMetaData) obj;
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
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

	public String getTableName() {
		return (String) this.get(PROP_TABLENAME);
	}

	public String getName() {
		return name;
	}

	public String getSchemaName() {
		return (String) this.get(PROP_SCHEMANAME);
	}

	public String getCatalogName() {
		return (String) this.get(PROP_CATALOGNAME);
	}

	public void setName(String name) {
		name = trim(name);
		this.name = name;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.put(PROP_AUTOINCREMENT, autoIncrement);
	}

	public void setTypeName(String typeName) {
		this.put(PROP_TYPENAME, typeName);
	}

	public void setTypeCode(int typeCode) {
		this.put(PROP_TYPECODE, typeCode);
	}

	public void setTableName(String tableName) {
		tableName = trim(tableName);
		this.tableName = tableName;
		if (tableName != null)
			this.put(PROP_TABLENAME, tableName);
	}

	public void setColumnDisplaySize(int columnDisplaySize) {
		this.put(PROP_COLUMNDISPLAYSIZE, columnDisplaySize);
	}

	public void setCatalogName(String catalogName) {
		catalogName = trim(catalogName);
		this.catalogName = catalogName;
		if (catalogName != null)
			this.put(PROP_CATALOGNAME, catalogName);
	}

	public void setPrecision(int precision) {
		this.put(PROP_PRECISION, precision);
	}

	public void setScale(int scale) {
		this.put(PROP_SCALE, scale);
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

	public void setWriteable(boolean writeable) {
		this.put(PROP_WRITEABLE, writeable);
	}

	public void setReadOnly(boolean readOnly) {
		this.put(PROP_READONLY, readOnly);
	}

	public void setComment(String comment) {
		this.put(PROP_COMMENT, comment);
	}

	public void setNull(boolean isNull) {
		this.put(PROP_NULL, isNull);
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.put(PROP_PRIMARYKEY, primaryKey);
	}

	public void setUnique(boolean unique) {
		this.put(PROP_UNIQUE, unique);
	}

	public void setNotNull(boolean notNull) {
		this.put(PROP_NOTNULL, notNull);
	}

	public void setDefaultVlaue(String defaultValue) {
		this.put(PROP_DEFAULTVALUE, defaultValue);
	}

	public void setCheck(boolean check) {
		this.put(PROP_CHECK, check);

	}

	public void setForeignKey(boolean foreignKey) {
		this.put(PROP_FOREIGNKEY, foreignKey);
	}
}