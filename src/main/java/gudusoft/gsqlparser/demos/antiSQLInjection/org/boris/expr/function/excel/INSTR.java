
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class INSTR extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 2 );
		assertMaxArgCount( args, 4 );
		int position = 0;
		int occurrence = 1;
		Expr string = evalArg( context, args[0] );
		Expr substring = evalArg( context, args[1] );
		boolean ltr = true;
		if ( args.length > 2 )
		{
			position = ( (ExprInteger) evalArg( context, args[2] ) ).intValue( );
			if ( position > 0 )
				position--;
			else if ( position < 0 )
			{
				position = ( -position ) - 1;
				ltr = false;
			}
			else
			{
				return ExprError.ILLEGAL_ARGUMENT;
			}
		}
		if ( args.length > 3 )
		{
			occurrence = ( (ExprInteger) evalArg( context, args[3] ) ).intValue( );
		}

		if ( ltr )
		{
			String str = string.toString( );
			if ( string instanceof ExprString )
				str = ( (ExprString) string ).str;

			str = str.substring( position );

			String sub = substring.toString( );
			if ( substring instanceof ExprString )
				sub = ( (ExprString) substring ).str;

			int index = nth( str, sub, occurrence );
			if ( index > 0 )
				return new ExprDouble( index + position + 1 );
			else
				return new ExprDouble( 0 );
		}
		else
		{
			String str = string.toString( );
			if ( string instanceof ExprString )
				str = ( (ExprString) string ).str;
			str = new StringBuffer( str ).reverse( ).toString( );
			str = str.substring( position );

			String sub = substring.toString( );
			if ( substring instanceof ExprString )
				sub = ( (ExprString) substring ).str;
			sub = new StringBuffer( sub ).reverse( ).toString( );
			int index = nth( str, sub, occurrence );
			if ( index > 0 )
				return new ExprDouble( str.length( )
						- index
						- sub.length( )
						+ 1 );
			else
				return new ExprDouble( 0 );
		}

	}

	public static int nth( String source, String pattern, int n )
	{
		int i = 0, pos = 0, tpos = 0;
		while ( i < n )
		{
			pos = source.indexOf( pattern );
			if ( pos > -1 )
			{
				source = source.substring( pos + 1 );
				tpos += pos + 1;
				i++;
			}
			else
			{
				return -1;
			}
		}
		return tpos - 1;
	}
}
