
package gudusoft.gsqlparser.dlineage.dataflow.model;

import gudusoft.gsqlparser.ESqlClause;

public class ConstantRelationElement implements RelationElement<Constant>
{

	private Constant constant;

	private ESqlClause relationLocation;

	public ConstantRelationElement( Constant constant )
	{
		this.constant = constant;
	}

	public ConstantRelationElement( Constant constant,
			ESqlClause relationLocation )
	{
		this.constant = constant;
		this.relationLocation = relationLocation;
	}

	@Override
	public Constant getElement( )
	{
		return constant;
	}

	public ESqlClause getRelationLocation( )
	{
		return relationLocation;
	}

	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ( ( constant == null ) ? 0 : constant.hashCode( ) );
		result = prime
				* result
				+ ( ( relationLocation == null ) ? 0
						: relationLocation.hashCode( ) );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass( ) != obj.getClass( ) )
			return false;
		ConstantRelationElement other = (ConstantRelationElement) obj;
		if ( constant == null )
		{
			if ( other.constant != null )
				return false;
		}
		else if ( !constant.equals( other.constant ) )
			return false;
		if ( relationLocation != other.relationLocation )
			return false;
		return true;
	}

}
