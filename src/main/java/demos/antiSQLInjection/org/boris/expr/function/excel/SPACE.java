
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SPACE extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		int number = asInteger( context, args[0], true );
		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < number; i++ )
		{
			buffer.append( " " );
		}
		return new ExprString( buffer.toString( ) );
	}
}
