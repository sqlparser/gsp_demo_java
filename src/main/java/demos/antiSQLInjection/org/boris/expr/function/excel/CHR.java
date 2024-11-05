
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class CHR extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 1 );
		StringBuffer buffer = new StringBuffer( );
		for ( Expr arg : args )
		{
			int c;
			try
			{
				c = (int) Double.parseDouble( asString( context, arg, false ) );
			}
			catch ( NumberFormatException e )
			{
				return ExprError.VALUE;
			}
			if ( c < 1 || c > 255 )
				return ExprError.VALUE;
			else
				buffer.append( (char) c );
		}
		return new ExprString( buffer.toString( ) );
	}
}
