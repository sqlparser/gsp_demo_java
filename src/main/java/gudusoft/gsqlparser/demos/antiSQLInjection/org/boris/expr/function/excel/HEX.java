
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class HEX extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		if ( args[0] instanceof ExprNumber )
		{
			long number = (long) asDouble( context, args[0], true );
			return new ExprString( Long.toHexString( number ).toUpperCase( ) );
		}
		else
		{
			String string = asString( context, args[0], true );
			StringBuffer buffer = new StringBuffer( );
			for ( int i = 0; i < string.length( ); i++ )
			{
				buffer.append( Integer.toHexString( string.charAt( i ) ) );
			}
			return new ExprString( buffer.toString( ) );
		}
	}

}
