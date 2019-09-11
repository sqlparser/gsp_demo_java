
package demos.tracedatalineage;

import java.util.LinkedHashMap;
import java.util.List;

public class Procedure
{

	private LinkedHashMap<String, Cursor> cursors;

	private LinkedHashMap<Procedure, Execute> executes;

	private String fullName;

	private String name;

	private LinkedHashMap<String, Parameter> params;

	private List<TableRelation> tableRelations;

	private LinkedHashMap<String, Variable> variables;

	public LinkedHashMap<String, Cursor> getCursors( )
	{
		return cursors;
	}

	public LinkedHashMap<Procedure, Execute> getExecutes( )
	{
		return executes;
	}

	public String getFullName( )
	{
		return fullName;
	}

	public String getName( )
	{
		return name;
	}

	public LinkedHashMap<String, Parameter> getParams( )
	{
		return params;
	}

	public List<TableRelation> getTableRelations( )
	{
		return tableRelations;
	}

	public LinkedHashMap<String, Variable> getVariables( )
	{
		return variables;
	}

	public void setCursors( LinkedHashMap<String, Cursor> cursors )
	{
		this.cursors = cursors;
	}

	public void setExecutes( LinkedHashMap<Procedure, Execute> executes )
	{
		this.executes = executes;
	}

	public void setFullName( String fullName )
	{
		this.fullName = fullName;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public void setParams( LinkedHashMap<String, Parameter> params )
	{
		this.params = params;
	}

	public void setTableRelations( List<TableRelation> tableRelations )
	{
		this.tableRelations = tableRelations;
	}

	public void setVariables( LinkedHashMap<String, Variable> variables )
	{
		this.variables = variables;
	}
}
