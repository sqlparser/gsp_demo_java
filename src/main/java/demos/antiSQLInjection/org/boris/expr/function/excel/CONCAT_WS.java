
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class CONCAT_WS extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 3 );
		StringBuilder sb = new StringBuilder( );
		String sep = asString( context, args[0], false );
		if ( args[0] instanceof ExprVariable
				&& ( (ExprVariable) args[0] ).toString( )
						.toLowerCase( )
						.equals( "null" ) )
		{
			return new ExprVariable( "NULL" );
		}
		for ( int i = 1; i < args.length; i++ )
		{
			String a = asString( context, args[i], false );
			if ( args[i] instanceof ExprVariable
					&& ( (ExprVariable) args[i] ).toString( )
							.toLowerCase( )
							.equals( "null" ) )
			{
				if ( i > 1 && i == args.length - 1 )
				{
					sb.delete( sb.length( ) - sep.length( ),
							sb.length( ) );
				}
				continue;
			}
			sb.append( a );
			if ( i < args.length - 1 )
				sb.append( sep );

		}

		return new ExprString( sb.toString( ) );
	}
}
