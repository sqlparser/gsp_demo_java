
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class FIELD extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 2 );

		Expr expr = evalArg( context, args[0] );
		if ( expr == null )
			return new ExprInteger( 0 );

		String string = asString( context, args[0], false );
		for ( int i = 1; i < args.length; i++ )
		{
			String temp = asString( context, args[i], false );
			if ( string.equals( temp ) )
				return new ExprInteger( i );
			else
			{
				try
				{
					double number1 = Double.parseDouble( string );
					double number2 = Double.parseDouble( temp );
					if ( number1 == number2 )
						return new ExprInteger( i );
				}
				catch ( NumberFormatException e )
				{

				}
			}
		}
		return new ExprInteger( 0 );
	}
}
