
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class BIN extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		double number = asDouble( context, args[0], false );
		return new ExprString( bin( (long) number ) );
	}

	public String bin( long number )
	{
		if ( number < 2 )
			return "" + number;
		else
			return ( bin( (long) Math.floor( number / 2 ) ) + "" + ( number % 2 ) );
	}
}
