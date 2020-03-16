
package demos.dlineage.dataflow.model.xml;

import org.simpleframework.xml.Attribute;

import demos.dlineage.util.Pair;

public class column
{
	@Attribute(required = false)
	private String id;
	
	@Attribute(required = false)
	private String name;

	@Attribute(required = false)
	private String coordinate;
	
	@Attribute(required = false)
	private String source;

	public String getCoordinate( )
	{
		return coordinate;
	}

	public int getOccurrencesNumber( )
	{
		return PositionUtil.getOccurrencesNumber( coordinate );
	}

	public Pair<Integer, Integer> getStartPos( int index )
	{
		return PositionUtil.getStartPos( coordinate, index );
	}

	public Pair<Integer, Integer> getEndPos( int index )
	{
		return PositionUtil.getEndPos( coordinate, index );
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

	public String getId( )
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	

}
