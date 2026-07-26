
package org.boris.expr;

public class ExprMinus extends ExprUnary
{

	public ExprMinus( Expr lhs )
	{
		super( lhs );
	}

	public String toString( )
	{
		return "-";
	}

	public int hashCode( )
	{
		return "-".hashCode( );
	}

	public boolean equals( Object obj )
	{
		return obj instanceof ExprMinus;
	}
}
