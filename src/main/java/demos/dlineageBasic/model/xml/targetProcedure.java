
package demos.dlineageBasic.model.xml;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class targetProcedure
{

	@Attribute(required = false)
	private String owner;

	@Attribute(required = false)
	private String name;

	@Attribute(required = false)
	private String highlightInfo;

	@Attribute(required = false)
	private String coordinate;

	@ElementList(entry = "sourceProcedure", inline = true, required = false)
	private List<sourceProcedure> sourceProcedures;

	public String getOwner( )
	{
		return owner;
	}

	public void setOwner( String owner )
	{
		this.owner = owner;
	}

	public String getName( )
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getHighlightInfo( )
	{
		return highlightInfo;
	}

	public void setHighlightInfo( String highlightInfo )
	{
		this.highlightInfo = highlightInfo;
	}

	public String getCoordinate( )
	{
		return coordinate;
	}

	public void setCoordinate( String coordinate )
	{
		this.coordinate = coordinate;
	}

	public List<sourceProcedure> getSourceProcedures( )
	{
		return sourceProcedures;
	}

	public void setSourceProcedures( List<sourceProcedure> sourceProcedures )
	{
		this.sourceProcedures = sourceProcedures;
	}

}
