
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class REVERSE extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		String string = asString( context, args[0], false );
		return new ExprString( new StringBuffer( string ).reverse( ).toString( ) );
	}
}
