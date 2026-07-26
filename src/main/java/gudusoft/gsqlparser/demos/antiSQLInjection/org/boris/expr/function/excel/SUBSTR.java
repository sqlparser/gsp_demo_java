
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SUBSTR extends AbstractFunction
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
		int position = ( (ExprInteger) b ).intValue( );
		if ( position == 0 )
			position = 1;

		boolean ltr = true;
		if ( position > 0 )
		{
			position--;
		}
		else
		{
			ltr = false;
		}

		int length = -1;
		if ( args.length == 3 )
		{
			Expr c = evalArg( context, args[2] );
			length = ( (ExprInteger) c ).intValue( );
		}

		String value = string;
		if ( ltr )
		{
			if ( length > 0 )
				value = value.substring( position,
						position + length > value.length( ) ? value.length( )
								: position + length );
			else if ( args.length == 3 )
				value = null;
			else
				value = value.substring( position );
			return new ExprString( value );
		}
		else
		{
			position = value.length( ) + position;
			if ( length > 0 )
				value = value.substring( position,
						position + length > value.length( ) ? value.length( )
								: position + length );
			else if ( args.length == 3 )
				value = null;
			else
				value = value.substring( position );
			return new ExprString( value );
		}
	}
}
