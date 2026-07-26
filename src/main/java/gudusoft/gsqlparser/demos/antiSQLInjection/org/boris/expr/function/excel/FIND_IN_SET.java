
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class FIND_IN_SET extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 2 );
		Expr a = evalArg( context, args[0] );
		Expr b = evalArg( context, args[1] );
		if ( a == null || b == null )
			return new ExprVariable( "NULL" );
		String str1 = asString( context, args[0], false );
		String str2 = asString( context, args[1], false );
		String[] splits = str2.split( "," );
		int index = -1;
		for ( int i = 0; i < splits.length; i++ )
		{
			if ( splits[i].trim( ).equals( str1 ) )
			{
				index = i;
				break;
			}
		}
		index++;
		return new ExprInteger( index );
	}
}
