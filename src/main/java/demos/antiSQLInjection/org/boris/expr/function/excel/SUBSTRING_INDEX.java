
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SUBSTRING_INDEX extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 3 );
		String string = asString( context, args[0], false );
		String delim = asString( context, args[1], false );
		int count = asInteger( context, args[2], true );

		boolean ltr = true;
		if ( count < 0 )
		{
			ltr = false;
			string = new StringBuffer( string ).reverse( ).toString( );
			delim =  new StringBuffer( delim ).reverse( ).toString( );
			count = -count;
		}

		int pos = -1;
		for ( int i = 0; i < count; i++ )
		{
			int index = string.substring( pos + 1 ).indexOf( delim );
			if ( index > -1 )
			{
				pos += ( index + delim.length( ) );
			}
			else
			{
				return new ExprVariable( "NULL" );
			}
		}

		string = string.substring( 0, pos + 1 - delim.length( ) );
		if ( !ltr )
			string = new StringBuffer( string ).reverse( ).toString( );
		return new ExprString( string );
	}
}
