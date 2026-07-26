
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.DoubleInOutFunction;

public class SIGN extends DoubleInOutFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		double value;
		try
		{
			value = asDouble( context, args[0], true );
		}
		catch ( ExprException e )
		{
			return new ExprDouble( 1 );
		}

		return new ExprDouble( evaluate( value ) );

	}

	protected double evaluate( double value ) throws ExprException
	{
		if ( value < 0 )
			return -1;
		else if ( value > 0 )
			return 1;
		else
			return 0;
	}
}
