package demos.dlineageBasic.model.view;


public class ColumnModel {
	private String name;
	private TableModel table;
	private String highlightInfos;
	private String coordinate;

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getHighlightInfos() {
		return highlightInfos;
	}

	public void setHighlightInfos(String highlightInfos) {
		this.highlightInfos = highlightInfos;
	}

	public TableModel getTable() {
		return table;
	}

	public void setTable(TableModel table) {
		this.table = table;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ColumnModel(String name, TableModel table) {
		this.name = name;
		this.table = table;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnModel other = (ColumnModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	public String getFullName() {
		return table.getFullName() + "." + name;
	}

}
