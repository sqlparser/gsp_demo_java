
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class LPAD extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 2 );
		assertMaxArgCount( args, 3 );
		Expr a = evalArg( context, args[0] );
		String string = a.toString( );
		if ( a instanceof ExprString )
			string = ( (ExprString) a ).str;

		Expr b = evalArg( context, args[1] );
		int number = ( (ExprInteger) b ).intValue( );

		String replace = " ";
		if ( args.length == 3 )
		{
			Expr c = evalArg( context, args[2] );
			replace = c.toString( );
			if ( c instanceof ExprString )
				replace = ( (ExprString) c ).str;
		}

		String value = lpad( string, number, replace );
		return new ExprString( value );
	}

	private String lpad( String s, int n, String replace )
	{
		if ( s.length( ) > n )
			return s.substring( 0, n );
		StringBuffer buffer = new StringBuffer( );
		while ( s.length( ) + buffer.length( ) < n )
		{
			for ( int i = 0; i < replace.length( )
					&& buffer.length( ) + s.length( ) < n; i++ )
			{
				buffer.append( replace.charAt( i ) );
			}
		}
		s = buffer.append( s ).toString( );
		return s;
	}
}
