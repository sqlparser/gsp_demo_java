
package demos.dlineageBasic.model.xml;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class targetColumn
{

	@Attribute(required = false)
	private String alias;

	@Attribute(required = false)
	private String coordinate;

	@Attribute(required = false)
	private String name;

	@Attribute(required = false)
	private String aliasCoordinate;

	@Attribute(required = false)
	private String columnHighlightInfo;

	@Attribute(required = false)
	private String aliasHighlightInfo;

	@ElementList(entry = "sourceColumn", inline = true, required = false)
	private List<sourceColumn> columns;

	@ElementList(entry = "linkTable", inline = true, required = false)
	private List<linkTable> linkTables;

	public List<linkTable> getLinkTable( )
	{
		return linkTables;
	}

	public void setLinkTable( List<linkTable> linkTables )
	{
		this.linkTables = linkTables;
	}

	public List<sourceColumn> getColumns( )
	{
		return columns;
	}

	public void setColumns( List<sourceColumn> columns )
	{
		this.columns = columns;
	}

	public String getAlias( )
	{
		return alias;
	}

	public void setAlias( String alias )
	{
		this.alias = alias;
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

	public String getColumnHighlightInfo( )
	{
		return columnHighlightInfo;
	}

	public void setColumnHighlightInfo( String columnHighlightInfo )
	{
		this.columnHighlightInfo = columnHighlightInfo;
	}

	public String getAliasHighlightInfo( )
	{
		return aliasHighlightInfo;
	}

	public void setAliasHighlightInfo( String aliasHighlightInfo )
	{
		this.aliasHighlightInfo = aliasHighlightInfo;
	}

	public String getAliasCoordinate( )
	{
		return aliasCoordinate;
	}

	public void setAliasCoordinate( String aliasCoordinate )
	{
		this.aliasCoordinate = aliasCoordinate;
	}

}
