
package org.boris.expr.function.excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class INITCAP extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		Expr a = evalArg( context, args[0] );
		String string = a.toString( );
		if ( a instanceof ExprString )
			string = ( (ExprString) a ).str;

		Pattern pattern = Pattern.compile( "\\S+", Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( string );
		StringBuffer buffer = new StringBuffer( );
		while ( matcher.find( ) )
		{
			String splits = matcher.group( );
			StringBuffer temp = new StringBuffer( );
			for ( int i = 0; i < splits.length( ); i++ )
			{
				if ( i == 0 )
					temp.append( Character.toUpperCase( splits.charAt( i ) ) );
				else
					temp.append( Character.toLowerCase( splits.charAt( i ) ) );
			}
			matcher.appendReplacement( buffer, temp.toString( ) );
		}
		matcher.appendTail( buffer );
		return new ExprString( buffer.toString( ) );
	}
}
