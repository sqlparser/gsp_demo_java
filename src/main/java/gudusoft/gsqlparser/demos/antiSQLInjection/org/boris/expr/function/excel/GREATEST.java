
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class GREATEST extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertMinArgCount( args, 1 );
		boolean isNumber = false;
		if ( args[0] instanceof ExprNumber )
		{
			isNumber = true;
		}

		Object maxValue = null;

		for ( Expr a : args )
		{
			a = evalArg( context, a );
			if ( a instanceof ExprString )
			{
				String value = ( (ExprString) a ).str;
				if ( isNumber )
				{
					try
					{
						double number = Double.parseDouble( value );
						if ( maxValue == null )
						{
							maxValue = number;
						}
						else
						{
							if ( ( (Double) maxValue ) < number )
								maxValue = number;
						}
					}
					catch ( NumberFormatException e )
					{
						continue;
					}
				}
				else
				{
					if ( maxValue == null )
					{
						maxValue = value;
					}
					else
					{
						if ( maxValue.toString( ).compareTo( value ) < 0 )
							maxValue = value;
					}
				}
			}
			else if ( a instanceof ExprNumber )
			{
				double number = ( (ExprNumber) a ).doubleValue( );
				if ( isNumber )
				{
					if ( maxValue == null )
					{
						maxValue = number;
					}
					else
					{
						if ( ( (Double) maxValue ) < number )
							maxValue = number;
					}
				}
				else
				{
					String value = "" + number;
					if ( maxValue == null )
					{
						maxValue = value;
					}
					else
					{
						if ( maxValue.toString( ).compareTo( value ) < 0 )
							maxValue = value;
					}
				}
			}
		}
		if ( isNumber )
			return new ExprDouble( (Double) maxValue );
		else
			return new ExprString( maxValue.toString( ) );
	}
}
