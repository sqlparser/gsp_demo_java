
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprDouble;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class WIDTH_BUCKET extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 4 );
		double num = asDouble( context, args[0], true );
		double min = asDouble( context, args[1], true );
		double max = asDouble( context, args[2], true );
		int bucketNum = asInteger( context, args[3], true );
		if ( num < min )
			return new ExprDouble( 0 );
		else if ( num >= max )
			return new ExprDouble( bucketNum + 1 );
		else
		{
			return new ExprDouble( (int) ( (num-min) / ( ( max - min ) / bucketNum ) ) + 1 );
		}

	}
}
