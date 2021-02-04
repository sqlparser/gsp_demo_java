package demos.dlineageBasic.model.view;

import java.awt.Point;

public class ReferenceModel {
	public static final int TYPE_ALIAS_TABLE = 1;
	public static final int TYPE_ALIAS_COLUMN = 2;
	public static final int TYPE_FIELD_TABLE = 3;
	public static final int TYPE_FIELD_COLUMN = 4;

	private Point location;
	private int referenceType;
	private TableModel table;
	private ColumnModel column;
	private AliasModel alias;
	private FieldModel field;
	private Clause clause;

	public Clause getClause() {
		return clause;
	}

	public void setClause(Clause clause) {
		this.clause = clause;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public int getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(int referenceType) {
		this.referenceType = referenceType;
	}

	public TableModel getTable() {
		return table;
	}

	public void setTable(TableModel table) {
		this.table = table;
	}

	public ColumnModel getColumn() {
		return column;
	}

	public void setColumn(ColumnModel column) {
		this.column = column;
	}

	public AliasModel getAlias() {
		return alias;
	}

	public void setAlias(AliasModel alias) {
		this.alias = alias;
	}

	public FieldModel getField() {
		return field;
	}

	public void setField(FieldModel field) {
		this.field = field;
	}

}
