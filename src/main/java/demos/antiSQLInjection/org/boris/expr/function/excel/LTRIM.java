
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class LTRIM extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 1 );
		assertMaxArgCount( args, 2 );
		Expr a = evalArg( context, args[0] );
		String string = a.toString( );
		if ( a instanceof ExprString )
			string = ( (ExprString) a ).str;

		String replace = " ";
		if ( args.length == 2 )
		{
			Expr c = evalArg( context, args[1] );
			replace = c.toString( );
			if ( c instanceof ExprString )
				replace = ( (ExprString) c ).str;
		}

		String value = ltrim( string, replace );
		return new ExprString( value );
	}

	private String ltrim( String s, String replace )
	{
		int index = -1;
		for ( int i = 0; i < s.length( ); i++ )
		{
			char ch = s.charAt( i );
			if ( replace.indexOf( ch ) == -1 )
			{
				index = i;
				break;
			}
		}
		if ( index != -1 )
			return s.substring( index );
		return "";
	}
}
