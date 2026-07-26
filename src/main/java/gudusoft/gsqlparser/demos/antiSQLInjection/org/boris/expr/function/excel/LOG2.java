
package org.boris.expr.function.excel;

import org.boris.expr.ExprException;
import org.boris.expr.function.DoubleInOutFunction;

public class LOG2 extends DoubleInOutFunction
{

	protected double evaluate( double value ) throws ExprException
	{
		return Math.log( value ) / Math.log( 2 );
	}
}
