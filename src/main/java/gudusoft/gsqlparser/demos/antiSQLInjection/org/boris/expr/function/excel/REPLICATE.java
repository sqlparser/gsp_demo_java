
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class REPLICATE extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 2 );
		String string = asString( context, args[0], false );
		int number = asInteger( context, args[1], true );
		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < number; i++ )
		{
			buffer.append( string );
		}
		return new ExprString( buffer.toString( ) );
	}
}
