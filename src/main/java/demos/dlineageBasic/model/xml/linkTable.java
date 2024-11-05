
package demos.dlineageBasic.model.xml;

import org.simpleframework.xml.Attribute;

public class linkTable
{

	@Attribute(required = false)
	private String coordinate;

	@Attribute(required = false)
	private String name;

	@Attribute(required = false)
	private String tableName;

	@Attribute(required = false)
	private String tableOwner;

	@Attribute(required = false)
	private String highlightInfos;

	@Attribute(required = false)
	private String type;

	public String getType( )
	{
		return type;
	}

	public void setType( String type )
	{
		this.type = type;
	}

	public String getTableOwner( )
	{
		return tableOwner;
	}

	public void setTableOwner( String tableOwner )
	{
		this.tableOwner = tableOwner;
	}

	public String getHighlightInfos( )
	{
		return highlightInfos;
	}

	public void setHighlightInfos( String highlightInfos )
	{
		this.highlightInfos = highlightInfos;
	}

	public String getCoordinate( )
	{
		return coordinate;
	}

	public void setCoordinate( String coordinate )
	{
		this.coordinate = coordinate;
	}

	public String getName( )
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getTableName( )
	{
		return tableName;
	}

	public void setTableName( String tableName )
	{
		this.tableName = tableName;
	}
}
