
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class INSERT extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 4 );
		Expr a = evalArg( context, args[0] );
		Expr b = evalArg( context, args[1] );
		Expr c = evalArg( context, args[2] );
		Expr d = evalArg( context, args[3] );

		if ( a == null || b == null || c == null || d == null )
		{
			return new ExprVariable( "NULL" );
		}

		String string = asString( context, args[0], false );
		int pos = asInteger( context, args[1], true );
		int length = asInteger( context, args[2], true );
		String replace = asString( context, args[3], false );

		if ( pos < 1 || pos > string.length( ) )
			return new ExprVariable( string );

		int leftLength = string.length( ) - pos;

		if ( length < 1 || length > leftLength )
		{
			return new ExprString( string.substring( 0, pos - 1 ) + replace );
		}

		return new ExprString( string.substring( 0, pos - 1 )
				+ replace
				+ string.substring( pos + length - 1 ) );
	}
}
