package demos.dlineageBasic.model.view;

import java.util.ArrayList;
import java.util.List;

public class ColumnImpactModel {
	private String sql;
	private List<TableModel> tables = new ArrayList<TableModel>();
	private List<AliasModel> aliases = new ArrayList<AliasModel>();
	private List<FieldModel> fields = new ArrayList<FieldModel>();
	private List<ReferenceModel> references = new ArrayList<ReferenceModel>();

	public ReferenceModel[] getReferences() {
		return references.toArray(new ReferenceModel[0]);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public TableModel[] getTables() {
		return tables.toArray(new TableModel[0]);
	}

	public AliasModel[] getAliases() {
		return aliases.toArray(new AliasModel[0]);
	}

	public FieldModel[] getFields() {
		return fields.toArray(new FieldModel[0]);
	}

	public void addAlias(AliasModel alias) {
		if (alias != null && !aliases.contains(alias))
			aliases.add(alias);
	}

	public void addField(FieldModel field) {
		if (field != null && !fields.contains(field))
			fields.add(field);
	}

	public void addTable(TableModel table) {
		if (table != null && !tables.contains(table))
			tables.add(table);

	}

	public void addReference(ReferenceModel reference) {
		if (reference != null && !references.contains(reference))
			references.add(reference);
	}

	public void reset() {
		sql = null;
		tables.clear();
		aliases.clear();
		fields.clear();
		references.clear();
	}

	public boolean containsTable(String tableOwner, String tableName) {
		return getTable(tableOwner, tableName) != null;
	}

	public TableModel getTable(String tableOwner, String tableName) {
		String fullName = (tableOwner == null ? tableName
				: (tableOwner + "." + tableName));
		for (int i = 0; i < tables.size(); i++) {
			TableModel table = tables.get(i);
			if (fullName != null && fullName.equals(table.getFullName())) {
				return table;
			}
		}
		return null;
	}
}
