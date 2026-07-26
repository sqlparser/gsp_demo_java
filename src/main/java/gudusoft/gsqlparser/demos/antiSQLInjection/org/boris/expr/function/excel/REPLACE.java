
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class REPLACE extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 2 );
		assertMaxArgCount( args, 3 );

		String string = asString( context, args[0], false );
		String search = asString( context, args[1], false );

		if ( search == null )
			return new ExprString( string );

		String replace = "";
		if ( args.length == 3 )
		{
			replace = asString( context, args[2], false );
		}
		String value = string.replace( search, replace );
		return new ExprString( value );
	}
}
