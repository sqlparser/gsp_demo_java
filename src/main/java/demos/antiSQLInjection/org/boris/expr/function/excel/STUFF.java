
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class STUFF extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 4 );
		String string = asString( context, args[0], false );
		int start = asInteger( context, args[1], true );
		int length = asInteger( context, args[2], true );
		String replace = asString( context, args[3], false );

		if ( start <= 0 || length <= 0 || start > string.length( ) )
			return new ExprString( "" );

		if ( start + length > string.length( ) )
			length = string.length( ) - start + 1;

		return new ExprString( string.replaceFirst( string.substring( start - 1,
				start + length - 1 ),
				replace ) );
	}
}
