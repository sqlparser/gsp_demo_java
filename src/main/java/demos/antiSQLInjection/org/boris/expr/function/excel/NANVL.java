
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class NANVL extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 2 );
		double n2;
		try
		{
			n2 = asDouble( context, args[0], true );
		}
		catch ( ExprException e )
		{
			double n1 = asDouble( context, args[1], true );
			return new ExprDouble( n1 );
		}
		return new ExprDouble( n2 );
	}
}
