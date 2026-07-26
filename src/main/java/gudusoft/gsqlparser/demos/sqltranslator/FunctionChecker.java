
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TFunctionCall;

public class FunctionChecker
{

	public static FunctionCheckResult checkFunction( TFunctionCall function,
			EDbVendor targetVendor )
	{
		if ( function.getGsqlparser( ).getDbVendor( ) == EDbVendor.dbvoracle )
		{
			return OracleFunctionChecker.checkFunction( function, targetVendor );
		}
		else if ( function.getGsqlparser( ).getDbVendor( ) == EDbVendor.dbvmssql )
		{
			return MssqlFunctionChecker.checkFunction( function, targetVendor );
		}
		return null;
	}

	protected static FunctionCheckResult createFunctionCheckResult(
			TFunctionCall function, EDbVendor targetVendor, boolean canTranslate,
			String translate )
	{
		FunctionCheckResult result = new FunctionCheckResult( function,
				targetVendor );
		result.setCanTranslate( canTranslate );
		result.setTranslateResult( translate );
		return result;
	}

	protected static FunctionCheckResult createFunctionCheckResult(
			TFunctionCall function, EDbVendor targetVendor, boolean canTranslate )
	{
		FunctionCheckResult result = new FunctionCheckResult( function,
				targetVendor );
		result.setCanTranslate( canTranslate );
		return result;
	}
}
