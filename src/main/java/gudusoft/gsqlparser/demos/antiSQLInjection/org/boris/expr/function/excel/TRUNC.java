
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class TRUNC extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 1 );
		assertMaxArgCount( args, 2 );
		double num = asDouble( context, args[0], true );
		int dig = 0;
		if ( args.length == 2 )
			dig = asInteger( context, args[1], true );
		return new ExprDouble( truncateDouble( num, dig ) );
	}

	private double truncateDouble( double number, int numDigits )
	{
		double result = number;
		String arg = "" + number;
		int idx = arg.indexOf( '.' );
		if ( idx != -1 )
		{
			if ( arg.length( ) > idx + numDigits )
			{
				if ( numDigits > 0 )
					result = Double.parseDouble( arg.substring( 0, idx
							+ numDigits
							+ 1 ) );
				else
					result = Double.parseDouble( arg.substring( 0, idx
							+ numDigits ) )
							* Math.pow( 10, -numDigits );
			}
		}
		return result;
	}
}
