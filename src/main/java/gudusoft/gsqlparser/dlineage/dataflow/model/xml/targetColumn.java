
package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name = "target")
public class targetColumn
{

	@Attribute(required = false)
	private String id;

	@Attribute(required = false)
	private String column;

	@Attribute(required = false)
	private String function;

	@Attribute(required = false)
	private String target_id;

	@Attribute(required = false)
	private String target_name;
	
	@Attribute(required = false)
	private String parent_id;

	@Attribute(required = false)
	private String parent_name;

	@Attribute(required = false)
	private String coordinate;

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

	public String getFunction( )
	{
		return function;
	}

	public void setFunction( String function )
	{
		this.function = function;
	}

	public String getTarget_id() {
		return target_id;
	}

	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}

	public String getTarget_name() {
		return target_name;
	}

	public void setTarget_name(String target_name) {
		this.target_name = target_name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
