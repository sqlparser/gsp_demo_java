
package demos.tracedatalineage;

import java.util.Map;

public class TableRelation
{

	private Map relations;

	private Table table;

	public Map getRelations( )
	{
		return relations;
	}

	public Table getTable( )
	{
		return table;
	}

	public void setRelations( Map relations )
	{
		this.relations = relations;
	}

	public void setTable( Table table )
	{
		this.table = table;
	}

}
