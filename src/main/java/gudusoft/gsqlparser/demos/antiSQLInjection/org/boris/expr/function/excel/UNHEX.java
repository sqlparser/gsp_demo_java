
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class UNHEX extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		String string = asString( context, args[0], true );
		if ( string.length( ) % 2 != 0 )
		{
			return new ExprVariable( "NULL" );
		}
		StringBuffer buffer = new StringBuffer( );
		try
		{
			for ( int i = 0; i < string.length( ); i++ )
			{
				String numStr = "" + string.charAt( i ) + string.charAt( ++i );
				int number = Integer.parseInt( numStr, 16 );
				buffer.append( (char) number );
			}
		}
		catch ( Exception e )
		{
			return new ExprVariable( "NULL" );
		}
		return new ExprString( buffer.toString( ) );
	}

}
