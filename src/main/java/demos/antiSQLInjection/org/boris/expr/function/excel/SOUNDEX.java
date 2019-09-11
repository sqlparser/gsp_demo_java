
package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SOUNDEX extends AbstractFunction
{

	public Expr evaluate( IEvaluationContext context, Expr[] args )
			throws ExprException
	{
		assertArgCount( args, 1 );
		Expr a = evalArg( context, args[0] );
		String string = a.toString( );
		if ( a instanceof ExprString )
			string = ( (ExprString) a ).str;

		String value = soundex( string );
		return new ExprString( value );
	}

	private String soundex( String s )
	{
		char[] x = s.toUpperCase( ).toCharArray( );
		char firstLetter = x[0];

		// convert letters to numeric code
		for ( int i = 0; i < x.length; i++ )
		{
			switch ( x[i] )
			{
				case 'B' :
				case 'F' :
				case 'P' :
				case 'V' :
				{
					x[i] = '1';
					break;
				}

				case 'C' :
				case 'G' :
				case 'J' :
				case 'K' :
				case 'Q' :
				case 'S' :
				case 'X' :
				case 'Z' :
				{
					x[i] = '2';
					break;
				}

				case 'D' :
				case 'T' :
				{
					x[i] = '3';
					break;
				}

				case 'L' :
				{
					x[i] = '4';
					break;
				}

				case 'M' :
				case 'N' :
				{
					x[i] = '5';
					break;
				}

				case 'R' :
				{
					x[i] = '6';
					break;
				}

				default :
				{
					x[i] = '0';
					break;
				}
			}
		}

		// remove duplicates
		String output = "" + firstLetter;
		for ( int i = 1; i < x.length; i++ )
			if ( x[i] != x[i - 1] && x[i] != '0' )
				output += x[i];

		// pad with 0's or truncate
		output = output + "0000";
		return output.substring( 0, 4 );
	}
}
