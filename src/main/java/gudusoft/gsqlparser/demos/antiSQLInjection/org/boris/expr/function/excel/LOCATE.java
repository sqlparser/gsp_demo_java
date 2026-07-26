
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class LOCATE extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 2 );
		assertMaxArgCount( args, 3 );

		Expr a = evalArg( context, args[0] );
		Expr b = evalArg( context, args[1] );

		if ( a == null || b == null )
		{
			return new ExprDouble( 0 );
		}

		String subString = asString( context, args[0], false ).toLowerCase( );
		String string = asString( context, args[1], false ).toLowerCase( );
		int pos = 0;
		if ( args.length == 3 )
		{
			pos = asInteger( context, args[2], false ) - 1;
		}
		if ( pos < 0 )
			pos = 0;

		if ( pos >= string.length( ) )
			return new ExprDouble( 0 );

		int result = string.substring( pos ).indexOf( subString ) + 1 + pos;
		return new ExprDouble( result );
	}
}
