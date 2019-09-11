
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class ASCII extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		Expr a = evalArg( context, args[0] );
		if ( a instanceof ExprArray )
			return ExprError.VALUE;
		if ( a instanceof ExprString )
		{
			if ( ( (ExprString) a ).str.length( ) == 0 )
				return ExprError.EMPTY;
			return new ExprDouble( (int) ( (ExprString) a ).str.charAt( 0 ) );
		}
		if ( a.toString( ).length( ) == 0 )
			return ExprError.EMPTY;
		return new ExprDouble( (int) a.toString( ).charAt( 0 ) );
	}

}
