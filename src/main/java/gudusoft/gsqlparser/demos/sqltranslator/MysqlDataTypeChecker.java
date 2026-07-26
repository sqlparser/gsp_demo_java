
package demos.sqltranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TTypeName;

public class MysqlDataTypeChecker extends DataTypeChecker
{

	public static DataTypeCheckResult checkDataType( TTypeName type,
			EDbVendor targetVendor )
	{
		String content = type.toString( )
				.replaceAll( "(?i)UNSIGNED", "" )
				.replaceAll( "(?i)ZEROFILL", "" )
				.trim( );

		Pattern pattern = Pattern.compile( "CHARACTER\\s+SET",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( content );
		if ( matcher.find( ) )
		{
			content = content.substring( 0, matcher.start( ) ).trim( );
		}

		pattern = Pattern.compile( "COLLATE", Pattern.CASE_INSENSITIVE );
		matcher = pattern.matcher( content );
		if ( matcher.find( ) )
		{
			content = content.substring( 0, matcher.start( ) ).trim( );
		}

		if ( type.getDataType( ) == EDataType.tinyint_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)TINYINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)TINYINT",
								"NUMBER" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
					else
					{
						String oracleTranslate = "SMALLINT";
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmssql :
					if ( content.matches( "(?i)TINYINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)TINYINT",
								"NUMERIC" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
					{
						String mssqlTranslate = "TINYINT";
						if ( type.toString( ).equalsIgnoreCase( mssqlTranslate ) )
							return null;
						else
							return createDataTypeResult( type,
									targetVendor,
									true,
									mssqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.bool_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "NUMBER (1)";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "BIT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.smallint_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)SMALLINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)TINYINT",
								"NUMBER" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
					else
					{
						String oracleTranslate = "SMALLINT";
						if ( type.toString( )
								.equalsIgnoreCase( oracleTranslate ) )
							return null;
						else
							return createDataTypeResult( type,
									targetVendor,
									true,
									oracleTranslate );
					}
				case dbvmssql :
					if ( content.matches( "(?i)SMALLINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)TINYINT",
								"NUMERIC" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
					{
						String mssqlTranslate = "SMALLINT";
						if ( type.toString( ).equalsIgnoreCase( mssqlTranslate ) )
							return null;
						else
							return createDataTypeResult( type,
									targetVendor,
									true,
									mssqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.mediumint_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)MEDIUMINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)MEDIUMINT",
								"NUMBER" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
					else
					{
						String oracleTranslate = "INTEGER";
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmssql :
					if ( content.matches( "(?i)MEDIUMINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)MEDIUMINT",
								"NUMERIC" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
					{
						String mssqlTranslate = "INT";
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.int_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)INT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)INT",
								"NUMBER" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
					else if ( content.matches( "(?i)INTEGER\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)INTEGER",
								"NUMBER" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
					else
					{
						String oracleTranslate = "INT";
						if ( content.toUpperCase( ).startsWith( "INTEGER" ) )
							oracleTranslate = "INTEGER";
						if ( type.toString( )
								.equalsIgnoreCase( oracleTranslate ) )
							return null;
						else
							return createDataTypeResult( type,
									targetVendor,
									true,
									oracleTranslate );
					}
				case dbvmssql :
					if ( content.matches( "(?i)INT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)INT",
								"NUMERIC" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else if ( content.matches( "(?i)INTEGER\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)INTEGER",
								"NUMERIC" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
					{
						String mssqlTranslate = "INT";
						if ( content.toUpperCase( ).startsWith( "INTEGER" ) )
							mssqlTranslate = "BIGINT";
						if ( type.toString( ).equalsIgnoreCase( mssqlTranslate ) )
							return null;
						else
							return createDataTypeResult( type,
									targetVendor,
									true,
									mssqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.bigint_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.matches( "(?i)BIGINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)BIGINT",
								"NUMBER" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
					else
					{
						String oracleTranslate = "INTEGER";
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmssql :
					if ( content.matches( "(?i)BIGINT\\s*\\(\\s*\\S+\\s*\\)" ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)BIGINT",
								"NUMERIC" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
					{
						String mssqlTranslate = "BIGINT";
						if ( type.toString( ).equalsIgnoreCase( mssqlTranslate ) )
							return null;
						else
							return createDataTypeResult( type,
									targetVendor,
									true,
									mssqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.serial_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "INTEGER";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "BIGINT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.dec_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.toUpperCase( ).startsWith( "DECIMAL" ) )
					{
						if ( content.equals( type.toString( ).trim( ) ) )
							return null;
						return createDataTypeResult( type,
								targetVendor,
								true,
								content );
					}
					if ( content.toUpperCase( ).startsWith( "DEC" ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)DEC",
								"DECIMAL" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
				case dbvmssql :
					if ( content.toUpperCase( ).startsWith( "DECIMAL" ) )
					{
						if ( content.equals( type.toString( ).trim( ) ) )
							return null;
						return createDataTypeResult( type,
								targetVendor,
								true,
								content );
					}
					if ( content.toUpperCase( ).startsWith( "DEC" ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)DEC",
								"DECIMAL" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.numeric_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.equals( type.toString( ).trim( ) ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
				case dbvmssql :
					if ( content.equals( type.toString( ).trim( ) ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
			}
		}
		else if ( type.getDataType( ) == EDataType.float_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.contains( "," ) )
					{
						String oracleTranslate = content.replaceAll( "(?i)FLOAT",
								"DECIMAL" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								oracleTranslate );
					}
					else
					{
						if ( content.equals( type.toString( ).trim( ) ) )
							return null;
						return createDataTypeResult( type,
								targetVendor,
								true,
								content );
					}
				case dbvmssql :
					if ( content.contains( "," ) )
					{
						String mssqlTranslate = content.replaceAll( "(?i)FLOAT",
								"DECIMAL" );
						return createDataTypeResult( type,
								targetVendor,
								true,
								mssqlTranslate );
					}
					else
					{
						if ( content.equals( type.toString( ).trim( ) ) )
							return null;
						return createDataTypeResult( type,
								targetVendor,
								true,
								content );
					}
			}
		}
		else if ( type.getDataType( ) == EDataType.double_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "DOUBLE PRECISION";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "FLOAT (53)";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.real_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( content.equals( type.toString( ).trim( ) ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
				case dbvmssql :
					if ( content.equals( type.toString( ).trim( ) ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
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
				case dbvmssql :
					String mssqlTranslate = "BIT";
					if ( content.equalsIgnoreCase( mssqlTranslate ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.date_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmssql :
					String mssqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
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
				case dbvmssql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.timestamp_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return null;
				case dbvmssql :
					String mssqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.time_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "TIMESTAMP";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "DATETIME";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.year_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.char_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( type.toString( ).equalsIgnoreCase( content ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
				case dbvmssql :
					if ( type.toString( ).equalsIgnoreCase( content ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
			}
		}
		else if ( type.getDataType( ) == EDataType.nchar_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					if ( type.toString( ).equalsIgnoreCase( content ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
				case dbvmssql :
					String mssqlTranslate = content.replaceAll( "(?i)NATIONAL\\s+CHAR",
							"NCHAR" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.varchar_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = content.replaceAll( "(?i)VARCHAR",
							"VARCHAR2" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					if ( type.toString( ).equalsIgnoreCase( content ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							content );
			}
		}
		else if ( type.getDataType( ) == EDataType.nvarchar_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = content.replaceAll( "(?i)NATIONAL\\s+VARCHAR",
							"NVARCHAR2" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = content.replaceAll( "(?i)NATIONAL\\s+VARCHAR",
							"NVARCHAR" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.binary_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String mssqlTranslate = content.replaceAll( "(?i)BINARY",
							"RAW" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmssql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.varbinary_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String mssqlTranslate = content.replaceAll( "(?i)VARBINARY",
							"RAW" );
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
				case dbvmssql :
					return null;
			}
		}
		else if ( type.getDataType( ) == EDataType.tinyblob_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "CHAR";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "IMAGE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.tinytext_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "CHAR";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "CHAR";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.blob_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "BLOB";
					if ( content.equalsIgnoreCase( oracleTranslate ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "IMAGE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
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
				case dbvmssql :
					String mssqlTranslate = "TEXT";
					if ( content.equalsIgnoreCase( mssqlTranslate ) )
						return null;
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.mediumblob_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "LONG";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "IMAGE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.mediumtext_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "LONG";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "TEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.longblob_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "BLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "IMAGE";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.longtext_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					String oracleTranslate = "BLOB";
					return createDataTypeResult( type,
							targetVendor,
							true,
							oracleTranslate );
				case dbvmssql :
					String mssqlTranslate = "TEXT";
					return createDataTypeResult( type,
							targetVendor,
							true,
							mssqlTranslate );
			}
		}
		else if ( type.getDataType( ) == EDataType.enum_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		else if ( type.getDataType( ) == EDataType.set_t )
		{
			switch ( targetVendor )
			{
				case dbvoracle :
					return createDataTypeResult( type, targetVendor, false );
				case dbvmssql :
					return createDataTypeResult( type, targetVendor, false );
			}
		}
		return null;
	}
}
