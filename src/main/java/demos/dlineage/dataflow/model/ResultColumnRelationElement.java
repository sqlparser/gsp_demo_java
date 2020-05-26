
package demos.dlineage.dataflow.model;

import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.nodes.TObjectName;

public class ResultColumnRelationElement implements
		RelationElement<ResultColumn>
{

	private ResultColumn column;
	
	private TObjectName columnName;

	private ESqlClause relationLocation;

	public ResultColumnRelationElement( ResultColumn column )
	{
		this.column = column;
	}
	
	public ResultColumnRelationElement( ResultColumn column, TObjectName columnName )
	{
		this.column = column;
		this.columnName = columnName;
	}

	public ResultColumnRelationElement( ResultColumn column,
			ESqlClause relationLocation )
	{
		this.column = column;
		this.relationLocation = relationLocation;
	}

	@Override
	public ResultColumn getElement( )
	{
		return column;
	}

	public ESqlClause getRelationLocation( )
	{
		return relationLocation;
	}


	public TObjectName getColumnName() {
		return columnName;
	}

	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ( ( column == null ) ? 0 : column.hashCode( ) );
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
		ResultColumnRelationElement other = (ResultColumnRelationElement) obj;
		if ( column == null )
		{
			if ( other.column != null )
				return false;
		}
		else if ( !column.equals( other.column ) )
			return false;
		if ( relationLocation != other.relationLocation )
			return false;
		return true;
	}

}
