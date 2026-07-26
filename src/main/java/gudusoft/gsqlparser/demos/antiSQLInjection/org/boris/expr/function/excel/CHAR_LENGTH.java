
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class CHAR_LENGTH extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		String string = asString( context, args[0], false );
		return new ExprInteger( string.getBytes( ).length );
	}

}
