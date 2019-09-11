
package org.boris.expr.function.excel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class STR extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 1 );
		assertMaxArgCount( args, 3 );
		double number = asDouble( context, args[0], true );
		if ( args.length == 1 )
			return new ExprString( "" + (int) number );

		int decimal = 0;
		if ( args.length == 3 )
			decimal = asInteger( context, args[2], true );

		BigDecimal bd = new BigDecimal( number );
		String string = bd.setScale( decimal, RoundingMode.HALF_UP ).toString( );

		int length = asInteger( context, args[1], true );

		StringBuffer buffer = new StringBuffer( );
		if ( string.length( ) > length )
		{
			for ( int i = 0; i < length; i++ )
			{
				buffer.append( "*" );
			}
		}
		else
		{
			for ( int i = 0; i + string.length( ) < length; i++ )
			{
				buffer.append( " " );
			}
			buffer.append( string );
		}
		return new ExprString( buffer.toString( ) );
	}
}
