
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class LEAST extends AbstractFunction
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

		Object minValue = null;

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
						if ( minValue == null )
						{
							minValue = number;
						}
						else
						{
							if ( ( (Double) minValue ) > number )
								minValue = number;
						}
					}
					catch ( NumberFormatException e )
					{
						continue;
					}
				}
				else
				{
					if ( minValue == null )
					{
						minValue = value;
					}
					else
					{
						if ( minValue.toString( ).compareTo( value ) > 0 )
							minValue = value;
					}
				}
			}
			else if ( a instanceof ExprNumber )
			{
				double number = ( (ExprNumber) a ).doubleValue( );
				if ( isNumber )
				{
					if ( minValue == null )
					{
						minValue = number;
					}
					else
					{
						if ( ( (Double) minValue ) > number )
							minValue = number;
					}
				}
				else
				{
					String value = "" + number;
					if ( minValue == null )
					{
						minValue = value;
					}
					else
					{
						if ( minValue.toString( ).compareTo( value ) > 0 )
							minValue = value;
					}
				}
			}
		}
		if ( isNumber )
			return new ExprDouble( (Double) minValue );
		else
			return new ExprString( minValue.toString( ) );
	}
}
