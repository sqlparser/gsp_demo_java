
package demos.tracedatalineage;

import java.util.List;

public class Statement
{
	private List<TableRelation> tableRelations;

	public List<TableRelation> getTableRelations( )
	{
		return tableRelations;
	}

	public void setTableRelations( List<TableRelation> tableRelations )
	{
		this.tableRelations = tableRelations;
	}
}
