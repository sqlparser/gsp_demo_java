
package demos.tracedatalineage;

public class Column
{

	private Table container;

	private String fullName;

	private String name;

	private Table referTable;

	private String type;

	public Table getContainer( )
	{
		return container;
	}

	public String getFullName( )
	{
		return fullName;
	}

	public String getName( )
	{
		return name;
	}

	public Table getReferTable( )
	{
		return referTable;
	}

	public String getType( )
	{
		return type;
	}

	public void setContainer( Table container )
	{
		this.container = container;
	}

	public void setFullName( String fullName )
	{
		this.fullName = fullName;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public void setReferTable( Table referTable )
	{
		this.referTable = referTable;
	}

	public void setType( String type )
	{
		this.type = type;
	}

}
