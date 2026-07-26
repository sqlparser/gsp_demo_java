
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TFunctionCall;

public class MssqlFunctionChecker extends FunctionChecker
{

	public static FunctionCheckResult checkFunction( TFunctionCall function,
			EDbVendor targetVendor )
	{
		return null;
	}
}
