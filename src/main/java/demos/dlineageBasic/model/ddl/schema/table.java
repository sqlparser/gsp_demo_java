
package demos.dlineageBasic.model.ddl.schema;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class table implements Comparable<table>
{

	@Attribute(required = true)
	private String name;

	@Attribute(required = false)
	private String isView;

	@Attribute(required = false)
	private String alias;

	@Attribute(required = false)
	private String description;
	
	@Attribute(required = false)
	private String parent;

	@ElementList(entry = "column", inline = true, required = false)
	private List<column> columns;

	@ElementList(entry = "foreign-key", inline = true, required = false)
	private List<foreignKey> foreignKeys;

	@ElementList(entry = "index", inline = true, required = false)
	private List<index> indices;

	@ElementList(entry = "unique", inline = true, required = false)
	private List<unique> uniques;

	public String getName( )
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getAlias( )
	{
		return alias;
	}

	public void setAlias( String alias )
	{
		this.alias = alias;
	}

	public String getDescription( )
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public List<column> getColumns( )
	{
		if ( columns == null )
			columns = new ArrayList<column>( );
		return columns;
	}

	public void setColumns( List<column> columns )
	{
		this.columns = columns;
	}

	public List<foreignKey> getForeignKeys( )
	{
		return foreignKeys;
	}

	public void setForeignKeys( List<foreignKey> foreignKeys )
	{
		this.foreignKeys = foreignKeys;
	}

	public List<index> getIndices( )
	{
		return indices;
	}

	public void setIndices( List<index> indices )
	{
		this.indices = indices;
	}

	public List<unique> getUniques( )
	{
		return uniques;
	}

	public void setUniques( List<unique> uniques )
	{
		this.uniques = uniques;
	}

	public String getIsView( )
	{
		return isView;
	}

	public void setIsView( String isView )
	{
		this.isView = isView;
	}
	
	public String getParent() 
	{
		return parent;
	}

	public void setParent(String parent) 
	{
		this.parent = parent;
	}

	@Override
	public int compareTo( table o )
	{
		if ( ( this.isView == null || Boolean.parseBoolean( this.isView ) == false )
				&& ( o.isView != null && Boolean.parseBoolean( o.isView ) == true ) )
			return -1;
		if ( ( this.isView != null && Boolean.parseBoolean( this.isView ) == true )
				&& ( o.isView == null || Boolean.parseBoolean( o.isView ) == false ) )
			return 1;
		return this.getName( ).compareTo( o.getName( ) );
	}
	
}
