
package demos.dlineage.dataflow.model;

import gudusoft.gsqlparser.ESqlClause;

public class TableColumnRelationElement implements RelationElement<TableColumn>
{

	private TableColumn column;

	private ESqlClause relationLocation;

	public TableColumnRelationElement( TableColumn column )
	{
		this.column = column;
	}

	public TableColumnRelationElement( TableColumn column,
			ESqlClause relationLocation )
	{
		this.column = column;
		this.relationLocation = relationLocation;
	}

	@Override
	public TableColumn getElement( )
	{
		return column;
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
		TableColumnRelationElement other = (TableColumnRelationElement) obj;
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
