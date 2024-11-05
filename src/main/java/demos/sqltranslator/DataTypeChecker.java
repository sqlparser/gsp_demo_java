
package demos.sqltranslator;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TTypeName;

public class DataTypeChecker
{

	public static DataTypeCheckResult checkDataType( TTypeName type,
			EDbVendor targetVendor )
	{
		if ( type.dbvendor == EDbVendor.dbvoracle )
		{
			return OracleDataTypeChecker.checkDataType( type, targetVendor );
		}
		else if ( type.dbvendor == EDbVendor.dbvmssql )
		{
			return MssqlDataTypeChecker.checkDataType( type, targetVendor );
		}
		else if ( type.dbvendor == EDbVendor.dbvmysql )
		{
			return MysqlDataTypeChecker.checkDataType( type, targetVendor );
		}

		return null;
	}


	protected static DataTypeCheckResult createDataTypeResult( TTypeName type,
			EDbVendor targetVendor, boolean canTranslate, String translate )
	{
		DataTypeCheckResult result = new DataTypeCheckResult( type,
				targetVendor );
		result.setCanTranslate( canTranslate );
		result.setTranslateResult( translate );
		return result;
	}

	protected static DataTypeCheckResult createDataTypeResult( TTypeName type,
			EDbVendor targetVendor, boolean canTranslate )
	{
		DataTypeCheckResult result = new DataTypeCheckResult( type,
				targetVendor );
		result.setCanTranslate( canTranslate );
		return result;
	}
}
