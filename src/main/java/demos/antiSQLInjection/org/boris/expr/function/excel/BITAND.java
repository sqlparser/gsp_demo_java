
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class BITAND extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 2 );
		int x = asInteger( context, args[0], true );
		int y = asInteger( context, args[1], true );
		return new ExprInteger( x & y );
	}
}
