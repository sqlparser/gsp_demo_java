
package demos.dlineage.dataflow.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name = "source")
public class sourceColumn
{

	@Attribute(required = false)
	private String id;

	@Attribute(required = false)
	private String column;

	@Attribute(required = false)
	private String value;

	@Attribute(required = false)
	private String source_id;

	@Attribute(required = false)
	private String source_name;

	@Attribute(required = false)
	private String column_type;

	@Attribute(required = false)
	private String parent_id;

	@Attribute(required = false)
	private String parent_name;

	@Attribute(required = false)
	private String coordinate;

	@Attribute(required = false)
	private String clauseType;
	
	@Attribute(required = false)
	private String source;

	public String getCoordinate( )
	{
		return coordinate;
	}

	public void setCoordinate( String coordinate )
	{
		this.coordinate = coordinate;
	}

	public String getColumn( )
	{
		return column;
	}

	public void setColumn( String column )
	{
		this.column = column;
	}

	public String getId( )
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getParent_id( )
	{
		return parent_id;
	}

	public void setParent_id( String parent_id )
	{
		this.parent_id = parent_id;
	}

	public String getParent_name( )
	{
		return parent_name;
	}

	public void setParent_name( String parent_name )
	{
		this.parent_name = parent_name;
	}

	public String getValue( )
	{
		return value;
	}

	public void setValue( String value )
	{
		this.value = value;
	}

	public String getSource_name( )
	{
		return source_name;
	}

	public void setSource_name( String source_name )
	{
		this.source_name = source_name;
	}

	public String getSource_id( )
	{
		return source_id;
	}

	public void setSource_id( String source_id )
	{
		this.source_id = source_id;
	}

	public String getClauseType( )
	{
		return clauseType;
	}

	public void setClauseType( String clauseType )
	{
		this.clauseType = clauseType;
	}

	public String getColumn_type( )
	{
		return column_type;
	}

	public void setColumn_type( String column_type )
	{
		this.column_type = column_type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + ((parent_id == null) ? 0 : parent_id.hashCode());
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
		sourceColumn other = (sourceColumn) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (parent_id == null) {
			if (other.parent_id != null)
				return false;
		} else if (!parent_id.equals(other.parent_id))
			return false;
		return true;
	}

	

	
}
