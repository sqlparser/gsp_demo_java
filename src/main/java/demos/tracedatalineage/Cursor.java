
package demos.tracedatalineage;

import java.util.LinkedHashMap;

public class Cursor
{

	private LinkedHashMap<String, Column> columns;

	private String name;

	private LinkedHashMap<String, Variable> params;

	private Procedure procedure;

	public LinkedHashMap<String, Column> getColumns( )
	{
		return columns;
	}

	public String getName( )
	{
		return name;
	}

	public LinkedHashMap<String, Variable> getParams( )
	{
		return params;
	}

	public Procedure getProcedure( )
	{
		return procedure;
	}

	public void setColumns( LinkedHashMap<String, Column> columns )
	{
		this.columns = columns;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public void setParams( LinkedHashMap<String, Variable> params )
	{
		this.params = params;
	}

	public void setProcedure( Procedure procedure )
	{
		this.procedure = procedure;
	}
}
