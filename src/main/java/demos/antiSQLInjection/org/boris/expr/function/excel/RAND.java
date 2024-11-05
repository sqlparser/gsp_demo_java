
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class RAND extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 0 );
		assertMaxArgCount( args, 1 );
		double seed = 1;
		if ( args.length == 1 )
		{
			seed = asDouble( context, args[0], true );
		}
		return new ExprDouble( Math.random( ) * seed );
	}
}
