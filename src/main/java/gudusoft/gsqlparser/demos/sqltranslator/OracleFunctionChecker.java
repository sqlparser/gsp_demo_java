
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OracleFunctionChecker extends FunctionChecker
{

	public static FunctionCheckResult checkFunction( TFunctionCall function,
			EDbVendor targetVendor )
	{
		if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "ADD_MONTHS" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = function.toString( )
							.replaceAll( "(?i)ADD_MONTHS\\s*\\(", "DATEADD(m, " );
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "BITAND" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "CEIL" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = function.toString( )
							.replaceAll( "(?i)CEIL\\s*\\(", "CEILING(" );
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "CHR" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					Pattern pattern = Pattern.compile( "USING\\s+NCHAR_CS",
							Pattern.CASE_INSENSITIVE );
					Matcher matcher = pattern.matcher( function.toString( ) );
					if ( matcher.find( ) )
					{
						return createFunctionCheckResult( function,
								targetVendor,
								false );
					}
					else
						return null;
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "CONCAT" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "(";
					TExpressionList args = function.getArgs( );
					for ( int i = 0; i < args.size( ); i++ )
					{
						mssqlTranslate += args.getExpression( i ).toString( );
						if ( i < args.size( ) - 1 )
						{
							mssqlTranslate += "+";
						}
					}
					mssqlTranslate += ")";
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "COSH" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "DECODE" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					TExpressionList args = function.getArgs( );
					String mssqlTranslate = "(CASE ";
					for ( int i = 0; i < args.size( ); i++ )
					{
						if ( i == 0 )
						{
							mssqlTranslate += args.getExpression( i )
									.toString( );
						}
						else if ( i < args.size( ) - 1 )
						{
							mssqlTranslate += ( " WHEN "
									+ args.getExpression( i ).toString( )
									+ " THEN " + args.getExpression( ++i )
									.toString( ) );
						}
						else
						{
							mssqlTranslate += ( " ELSE " + args.getExpression( i )
									.toString( ) );
						}
					}
					mssqlTranslate += " END)";
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "EXTRACT" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "FROM_TZ" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = function.toString( )
							.replaceAll( "(?i)FROM_TZ\\s*\\(",
									"TODATETIMEOFFSET(" );
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "GREATEST" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "INITCAP" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "INSTR" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "LAST_DAY" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "LEAST" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "LENGTH" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "FROM_TZ" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = function.toString( )
							.replaceAll( "(?i)LN\\s*\\(", "LOG(" );
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "LOG" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "LPAD" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "LTRIM" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					TExpressionList args = function.getArgs( );
					if ( args.size( ) > 1 )
					{
						return createFunctionCheckResult( function,
								targetVendor,
								false );
					}
					else
						return null;

			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "MOD" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "(";
					TExpressionList args = function.getArgs( );
					for ( int i = 0; i < args.size( ); i++ )
					{
						mssqlTranslate += args.getExpression( i ).toString( );
						if ( i < args.size( ) - 1 )
						{
							mssqlTranslate += "%";
						}
					}
					mssqlTranslate += ")";
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );

			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "MONTHS_BETWEEN" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate1 = "DATEDIFF(MONTH, CAST(p2 AS float), CAST(DATEADD(DAY, (-CAST(DATEPART(DAY, p2) AS float(53)) + 1), ";
					String mssqlTranslate2 = "p1) AS float))";
					TExpressionList args = function.getArgs( );
					if ( args.size( ) == 2 )
					{
						String mssqlTranslate = mssqlTranslate1.replace( "p2",
								args.getExpression( 0 ).toString( ) );
						mssqlTranslate += mssqlTranslate2.replace( "p1",
								args.getExpression( 1 ).toString( ) );
						return createFunctionCheckResult( function,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
						return createFunctionCheckResult( function,
								targetVendor,
								false );

			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "NEW_TIME" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "NLS_INITCAP" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "NVL" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = function.toString( )
							.replaceAll( "(?i)NVL\\s*\\(", "ISNULL(" );
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "RAWTOHEX" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "REPLACE" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					TExpressionList args = function.getArgs( );
					if ( args.size( ) == 2 )
					{
						String mssqlTranslate = "REPLACE("
								+ args.getExpression( 0 ).toString( )
								+ ","
								+ args.getExpression( 1 ).toString( )
								+ ",'')";
						return createFunctionCheckResult( function,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
						return null;
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "ROUND" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					TExpressionList args = function.getArgs( );
					if ( args.size( ) == 1 )
					{
						return createFunctionCheckResult( function,
								targetVendor,
								false );
					}
					if ( args.size( ) == 2 )
					{
						String arg1 = args.getExpression( 0 ).toString( );
						try
						{
							Double.parseDouble( arg1 );
							return null;
						}
						catch ( NumberFormatException e )
						{
						}
						return createFunctionCheckResult( function,
								targetVendor,
								false );
					}
					else
						return createFunctionCheckResult( function,
								targetVendor,
								false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "RPAD" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "RTRIM" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					TExpressionList args = function.getArgs( );
					if ( args.size( ) == 1 )
					{
						return null;
					}
					else
						return createFunctionCheckResult( function,
								targetVendor,
								false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "SINH" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "SUBSTR" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "SYS_GUID" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							true,
							"NEWID()" );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "TANH" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "TO_CHAR" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					TExpressionList args = function.getArgs( );
					if ( args.size( ) == 1 )
					{
						String mssqlTranslate = "CAST("
								+ args.getExpression( 0 ).toString( )
								+ " AS CHAR)";
						return createFunctionCheckResult( function,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
						return createFunctionCheckResult( function,
								targetVendor,
								false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "TO_DATE" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					TExpressionList args = function.getArgs( );
					if ( args.size( ) == 1 )
					{
						String mssqlTranslate = "CAST("
								+ args.getExpression( 0 ).toString( )
								+ " AS DATETIME)";
						return createFunctionCheckResult( function,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
						return createFunctionCheckResult( function,
								targetVendor,
								false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "TO_NUMBER" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "TRANSLATE" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "TRUNC" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "TRIM" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}
		else if ( function.getFunctionName( )
				.toString( )
				.equalsIgnoreCase( "WIDTH_BUCKET" ) )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createFunctionCheckResult( function,
							targetVendor,
							false );
			}
		}

		return null;
	}
}
