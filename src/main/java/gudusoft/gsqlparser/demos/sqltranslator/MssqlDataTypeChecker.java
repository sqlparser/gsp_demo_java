
package demos.sqltranslator;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TTypeName;

public class MssqlDataTypeChecker extends DataTypeChecker
{

	public static DataTypeCheckResult checkDataType( TTypeName type,
			EDbVendor targetVendor )
	{
		if ( type.getDataType( ) == EDataType.bigint_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = type.toString( )
							.replaceAll( "(?i)BIGINT", "INTEGER" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.int_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.smallint_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.tinyint_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = type.toString( )
							.replaceAll( "(?i)TINYINT", "SMALLINT" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.dec_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.numeric_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.bit_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "NUMBER (1)";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					String mysqlTranslate = "TINYINT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.money_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "DECIMAL (19, 4)";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					String mysqlTranslate = "DECIMAL (19, 4)";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.smallmoney_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "DECIMAL (10, 4)";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					String mysqlTranslate = "DECIMAL (10, 4)";
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
				case dbvoracle :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.real_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.date_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmysql :
					String mysqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.datetimeoffset_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = type.toString( )
							.replaceAll( "(?i)DATETIMEOFFSET", "TIMESTAMP" )
							+ " WITH TIME ZONE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.datetime2_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = type.toString( )
							.replaceAll( "(?i)DATETIME2", "TIMESTAMP" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.smalldatetime_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "DATE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					String mysqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.datetime_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "DATE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.time_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = type.toString( )
							.replaceAll( "(?i)TIME", "TIMESTAMP" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					String mysqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.char_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.varchar_t )
		{
			String content = type.toString( ).trim( );
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)VARCHAR\\s*\\(\\s*MAX\\s*\\)" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"CLOB" );
					}
					else if ( content.matches( "(?i)VARCHAR\\s*\\(\\s*\\)" )
							|| content.matches( "(?i)VARCHAR" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"VARCHAR2 (1)" );
					}
					else
					{
						String oracleTranslate = type.toString( )
								.replaceAll( "(?i)VARCHAR", "VARCHAR2" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmysql :
					if ( content.matches( "(?i)VARCHAR\\s*\\(\\s*MAX\\s*\\)" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"TEXT" );
					}
					else if ( content.matches( "(?i)VARCHAR\\s*\\(\\s*\\)" )
							|| content.matches( "(?i)VARCHAR" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"VARCHAR2 (1)" );
					}
					else
					{
						String mysqlTranslate = type.toString( )
								.replaceAll( "(?i)VARCHAR", "VARCHAR2" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mysqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.text_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "CLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.nchar_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
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
		else if ( type.getDataType( ) == EDataType.nvarchar_t )
		{
			String content = type.toString( ).trim( );
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)NVARCHAR\\s*\\(\\s*MAX\\s*\\)" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"NCLOB" );
					}
					else if ( content.matches( "(?i)NVARCHAR\\s*\\(\\s*\\)" )
							|| content.matches( "(?i)NVARCHAR" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"NVARCHAR2 (1)" );
					}
					else
					{
						String oracleTranslate = type.toString( )
								.replaceAll( "(?i)NVARCHAR", "NVARCHAR2" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmysql :
					if ( content.matches( "(?i)NVARCHAR\\s*\\(\\s*MAX\\s*\\)" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"LONGTEXT" );
					}
					else if ( content.matches( "(?i)NVARCHAR\\s*\\(\\s*\\)" )
							|| content.matches( "(?i)NVARCHAR" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"NATIONAL VARCHAR (1)" );
					}
					else
					{
						String mysqlTranslate = type.toString( )
								.replaceAll( "(?i)NVARCHAR", "NATIONAL VARCHAR" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mysqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.ntext_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "NCLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					String mysqlTranslate = "LONGTEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.binary_t )
		{
			String content = type.toString( ).trim( );
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)BINARY\\s*\\(\\s*\\)" )
							|| content.matches( "(?i)BINARY" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"RAW (1)" );
					}
					else
					{
						String oracleTranslate = type.toString( )
								.replaceAll( "(?i)BINARY", "RAW" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmysql :
					String mysqlTranslate = "LONGBLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.varbinary_t )
		{
			String content = type.toString( ).trim( );
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)VARBINARY\\s*\\(\\s*MAX\\s*\\)" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"BLOB" );
					}
					else if ( content.matches( "(?i)VARBINARY\\s*\\(\\s*\\)" )
							|| content.matches( "(?i)VARBINARY" ) )
					{
						return createDataTypeResult( type,
								targetVendor,
								true,
								"RAW (1)" );
					}
					else
					{
						String oracleTranslate = type.toString( )
								.replaceAll( "(?i)VARBINARY", "RAW" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmysql :
					String mysqlTranslate = "LONGBLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.image_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "BLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					String mysqlTranslate = "LONGBLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mysqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.cursor_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.rowversion_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.timestamp_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.hierarchyid_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.uniqueidentifier_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "CHAR (36)";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.sql_variant_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "SYS.ANYDATA";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.xml_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "CLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.table_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmysql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		return null;
	}
}
