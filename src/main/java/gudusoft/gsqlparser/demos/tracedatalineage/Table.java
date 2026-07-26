
package demos.tracedatalineage;

import java.util.LinkedHashMap;

public class Table
{

	private LinkedHashMap<String, Column> columns;

	private String database;

	private String fullName;

	private String name;

	public LinkedHashMap<String, Column> getColumns( )
	{
		return columns;
	}

	public String getDatabase( )
	{
		return database;
	}

	public String getFullName( )
	{
		return fullName;
	}

	public String getName( )
	{
		return name;
	}

	public void setColumns( LinkedHashMap<String, Column> columns )
	{
		this.columns = columns;
	}

	public void setDatabase( String database )
	{
		this.database = database;
	}

	public void setFullName( String fullName )
	{
		this.fullName = fullName;
	}

	public void setName( String name )
	{
		this.name = name;
	}

}
