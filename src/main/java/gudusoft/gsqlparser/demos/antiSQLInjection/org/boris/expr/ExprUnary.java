
package org.boris.expr;

public abstract class ExprUnary extends Expr
{

	protected Expr lhs;

	ExprUnary( Expr lhs )
	{
		super( ExprType.Unary, false );
		this.lhs = lhs;
	}

	public Expr getLHS( )
	{
		return lhs;
	}
}
