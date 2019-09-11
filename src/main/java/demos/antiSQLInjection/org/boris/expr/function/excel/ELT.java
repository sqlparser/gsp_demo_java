
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class ELT extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 2 );

		int index = (int) asDouble( context, args[0], true );
		if ( index <= 0 || index >= args.length )
		{
			return new ExprVariable( "NULL" );
		}
		return new ExprString( asString( context, args[index], false ) );
	}
}
