
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
	
	@Attribute(required = false)
	private String qualifiedTable;
	
	@Attribute(required = false)
	private String isFunction;
	

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

	public String getQualifiedTable() {
		return qualifiedTable;
	}

	public void setQualifiedTable(String qualifiedTable) {
		this.qualifiedTable = qualifiedTable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((qualifiedTable == null) ? 0 : qualifiedTable.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		column other = (column) obj;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (qualifiedTable == null) {
			if (other.qualifiedTable != null)
				return false;
		} else if (!qualifiedTable.equals(other.qualifiedTable))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	public String getIsFunction() {
		return isFunction;
	}

	public void setIsFunction(String isFunction) {
		this.isFunction = isFunction;
	}
}
