
package org.boris.expr.function.excel;

import java.util.HashMap;
import java.util.Map;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class TRANSLATE extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 3 );
		Expr a = evalArg( context, args[0] );
		String string = a.toString( );
		if ( a instanceof ExprString )
			string = ( (ExprString) a ).str;

		Expr b = evalArg( context, args[1] );
		String from = b.toString( );
		if ( b instanceof ExprString )
			from = ( (ExprString) b ).str;

		Expr c = evalArg( context, args[2] );
		String to = c.toString( );
		if ( c instanceof ExprString )
			to = ( (ExprString) c ).str;

		Map repalceMap = new HashMap( );
		for ( int i = 0; i < from.length( ); i++ )
		{
			Character fch = new Character( from.charAt( i ) );
			Character tch = null;
			if ( to.length( ) > i )
				tch = new Character( to.charAt( i ) );
			if ( !repalceMap.containsKey( fch ) )
				repalceMap.put( fch, tch );
		}

		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < string.length( ); i++ )
		{
			Character ch = new Character( string.charAt( i ) );
			if ( repalceMap.containsKey( ch ) )
			{
				Character value = (Character) repalceMap.get( ch );
				if ( value != null )
					buffer.append( value );
			}
			else
			{
				buffer.append( ch );
			}
		}

		return new ExprString( buffer.toString( ) );
	}
}
