
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class OCT extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		Long number = new Double( asDouble( context, args[0], false ) ).longValue( );
		return new ExprString( oct( number ) );
	}

	public String oct( Long d )
	{
		String o = "";
		if ( d < 8 )
		{
			o = d.toString( );
		}
		else
		{
			Long c;
			int s = 0;
			Long n = d;
			while ( n >= 8 )
			{
				s++;
				n = n / 8;
			}
			Long[] m = new Long[s];
			int i = 0;
			do
			{
				c = d / 8;
				m[i++] = d % 8;
				d = c;
			} while ( c >= 8 );
			o = d.toString( );
			for ( int j = m.length - 1; j >= 0; j-- )
			{
				o += m[j];
			}
		}
		return o;
	}

}
