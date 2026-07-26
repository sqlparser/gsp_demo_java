
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Maths;

public class ROUND extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 1 );
		assertMaxArgCount( args, 2 );

		double num = asDouble( context, args[0], true );

		int dps = 0;
		if ( args.length == 2 )
			dps = asInteger( context, args[1], true );
		
		return new ExprDouble( Maths.round( num, dps ) );
	}
}
