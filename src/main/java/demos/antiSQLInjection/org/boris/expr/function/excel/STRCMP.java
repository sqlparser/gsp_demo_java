
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class STRCMP extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 2 );
		Expr a = evalArg( context, args[0] );
		Expr b = evalArg( context, args[1] );
		if ( a == null && b != null )
			return new ExprInteger( -1 );
		else if ( a != null && b == null )
			return new ExprInteger( 1 );
		else if ( a == null && b == null )
			return new ExprInteger( 0 );
		String str1 = asString( context, args[0], false );
		String str2 = asString( context, args[1], false );
		return new ExprInteger( str1.compareTo( str2 ) );
	}
}
