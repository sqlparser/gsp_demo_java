
package demos.tracedatalineage;

import java.util.LinkedHashMap;

public class Execute
{

	private Procedure container;
	private Procedure target;
	private LinkedHashMap<String, Variable> variables;

	public Procedure getContainer( )
	{
		return container;
	}

	public Procedure getTarget( )
	{
		return target;
	}

	public LinkedHashMap<String, Variable> getVariables( )
	{
		return variables;
	}

	public void setContainer( Procedure container )
	{
		this.container = container;
	}

	public void setTarget( Procedure target )
	{
		this.target = target;
	}

	public void setVariables( LinkedHashMap<String, Variable> variables )
	{
		this.variables = variables;
	}
}
