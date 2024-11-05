
package demos.sqltranslator;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TTypeName;

public class OracleDataTypeChecker extends DataTypeChecker
{

	public static DataTypeCheckResult checkDataType( TTypeName type,
			EDbVendor targetVendor )
	{
		if ( type.getDataType( ) == EDataType.varchar2_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = type.toString( )
							.replaceAll( "(?i)VARCHAR2", "VARCHAR" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = type.toString( )
							.replaceAll( "(?i)VARCHAR2", "VARCHAR" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.nvarchar2_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = type.toString( )
							.replaceAll( "(?i)NVARCHAR2", "NVARCHAR" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = type.toString( )
							.replaceAll( "(?i)NVARCHAR2", "NATIONAL VARCHAR" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.number_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = type.toString( )
							.replaceAll( "(?i)NUMBER", "NUMERIC" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = type.toString( )
							.replaceAll( "(?i)NUMBER", "NUMERIC" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.float_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.long_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = type.toString( )
							.replaceAll( "(?i)LONG", "TEXT" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = type.toString( )
							.replaceAll( "(?i)LONG", "LONGTEXT" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.date_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = type.toString( )
							.replaceAll( "(?i)DATE", "DATETIME" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = type.toString( )
							.replaceAll( "(?i)DATE", "DATETIME" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.binary_float_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.binary_double_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.timestamp_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.timestamp_with_time_zone_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = "TIMESTAMP";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.timestamp_with_local_time_zone_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = "TIMESTAMP";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.interval_year_to_month_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.interval_year_to_month_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.interval_day_to_second_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.raw_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = type.toString( )
							.replaceAll( "(?i)RAW", "VARBINARY" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = "LONGBLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.long_raw_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "IMAGE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = "LONGBLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.rowid_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "UNIQUEIDENTIFIER";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.urowid_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.char_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.nchar_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					return null;
				case dbvmysql :
					String mysqlTranslate = type.toString( )
							.replaceAll( "(?i)NCHAR", "NATIONAL CHAR" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.clob_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "TEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = "LONGTEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.nclob_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "NTEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = "LONGTEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.blob_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "IMAGE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.bfile_t )
		{
			switch ( targetVendor )
			{
				case dbvmssql :
					String mssqlTranslate = "TEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmysql :
					String mysqlTranslate = "LONGBLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		return null;
	}

}
