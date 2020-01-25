
package demos.dlineage.dataflow.model.xml;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

//import demos.antiSQLInjection.expr.util.StringUtil;
import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;

public class table
{
	@Attribute(required = false)
	private String id;
	
	@Attribute(required = false)
	private String database;
	
	@Attribute(required = false)
	private String schema;
	
	@Attribute(required = false)
	private String name;
	
	@Attribute(required = false)
	private String alias;

	@Attribute(required = false)
	private String type;

	@Attribute(required = false)
	private String isTarget;

	@Attribute(required = false)
	private String coordinate;

	@ElementList(entry = "column", inline = true, required = false)
	private List<column> columns;

	@Attribute(required = false)
	private String parent;

	public String getAlias( )
	{
		return alias;
	}

	public void setAlias( String alias )
	{
		this.alias = alias;
	}

	public List<column> getColumns( )
	{
		return columns;
	}

	public void setColumns( List<column> columns )
	{
		this.columns = columns;
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

	public String getId( )
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getType( )
	{
		return type;
	}

	public void setType( String type )
	{
		this.type = type;
	}

	public boolean isView( )
	{
		return "view".equals( type );
	}

	public boolean isTable( )
	{
		return "table".equals( type );
	}

	public boolean isResultSet( )
	{
		return type != null && !isView( ) && !isTable( );
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

	public boolean isTarget( )
	{
		return "true".equals( isTarget );
	}

	public String getParent( )
	{
		return parent;
	}

	public void setParent( String parent )
	{
		this.parent = parent;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public String getFullName(){
		StringBuilder fullName = new StringBuilder();
		if(!SQLUtil.isEmpty(database)){
			fullName.append(database).append(".");
		}
		if(!SQLUtil.isEmpty(schema)){
			fullName.append(database).append(".");
		}
		fullName.append(name);
		return fullName.toString();
	}

}
