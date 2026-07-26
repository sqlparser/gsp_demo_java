
package org.boris.expr.util;

import org.boris.expr.ExprType;

public class ExprTypeUtil
{

	public static boolean isBinaryOperator( ExprType type )
	{
		switch ( type )
		{
			case Double :
				return false;
			case Integer :
				return false;
			case Boolean :
				return false;
			case String :
				return false;
			case Addition :
				return true;
			case Subtraction :
				return true;
			case Multiplication :
				return true;
			case Division :
				return true;
			case Function :
				return false;
			case Variable :
				return false;
			case Expression :
				return false;
			case StringConcat :
				return true;
			case Error :
				return false;
			case Array :
				return false;
			case Missing :
				return false;
			case LessThan :
				return true;
			case LessThanOrEqualTo :
				return true;
			case GreaterThan :
				return true;
			case GreaterThanOrEqualTo :
				return true;
			case NotEqual :
				return true;
			case Equal :
				return true;
			case Power :
				return true;
			case In :
				return true;
			case Unary :
				return false;
		}
		return false;
	}
}
