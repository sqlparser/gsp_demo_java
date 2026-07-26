
package org.boris.expr;

public class ExprPlus extends ExprUnary
{

	public ExprPlus( Expr lhs )
	{
		super( lhs );
	}

	public String toString( )
	{
		return "+";
	}

	public int hashCode( )
	{
		return "+".hashCode( );
	}

	public boolean equals( Object obj )
	{
		return obj instanceof ExprPlus;
	}
}
