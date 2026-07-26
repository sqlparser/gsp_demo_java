
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class CONCAT extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 1 );
		StringBuilder sb = new StringBuilder( );
		for ( Expr a : args )
		{
			a = evalArg( context, a );
			if ( a instanceof ExprString )
			{
				sb.append( ( (ExprString) a ).str );
			}
			else if ( a instanceof ExprNumber )
			{
				sb.append( a.toString( ) );
			}
			else if ( a == null )
			{
				return new ExprVariable( "NULL" );
			}
		}
		return new ExprString( sb.toString( ) );
	}
}
