
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class REMAINDER extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 2 );

		Expr n = args[0];
		if ( n instanceof ExprEvaluatable )
		{
			n = ( (ExprEvaluatable) n ).evaluate( context );
		}
		if ( n instanceof ExprArray )
		{
			ExprArray a = (ExprArray) n;
			if ( a.rows( ) > 1 )
			{
				return ExprError.VALUE;
			}

			n = a.get( 0, 0 );
		}
		if ( !( n instanceof ExprNumber ) )
		{
			return ExprError.VALUE;
		}

		double num = ( (ExprNumber) n ).doubleValue( );

		Expr d = args[1];
		if ( d instanceof ExprEvaluatable )
		{
			d = ( (ExprEvaluatable) d ).evaluate( context );
		}
		if ( !( d instanceof ExprNumber ) )
		{
			return ExprError.VALUE;
		}

		double div = ( (ExprNumber) d ).doubleValue( );
		double N = Math.round( num / div );
		double mod = num - ( div * N );
		if ( ( mod > 0 && div < 0 ) || ( mod < 0 && div > 0 ) )
			mod *= -1;
		return new ExprDouble( mod );
	}
}
