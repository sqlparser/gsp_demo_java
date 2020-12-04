package formatsql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.SqlFormatter;

import java.io.ByteArrayInputStream;
import java.io.File;

import junit.framework.TestCase;
import common.FileUtil;
import common.SqlFileList;

public class formatsqlVerifyTest extends TestCase
{

	public static int formatFiles( EDbVendor db, String dir )
	{

		TGSqlParser sqlparser = new TGSqlParser( db );
		SqlFileList sqlfiles = new SqlFileList( dir, true );
		int i = 0, j = 0;
		for ( int k = 0; k < sqlfiles.sqlfiles.size( ); k++ )
		{
			sqlparser.sqlfilename = sqlfiles.sqlfiles.get( k ).toString( );
			File sqlFile = new File( sqlparser.sqlfilename );
			String tidyFileName = new File( sqlFile.getParentFile( ),
					sqlFile.getName( ).replaceAll( "(?i)\\.sql", ".java.tidy" ) ).getAbsolutePath( );
			boolean b = sqlparser.parse( ) == 0;
			if ( b )
			{
				GFmtOpt option = GFmtOptFactory.newInstance( );
				try
				{
					String result = new SqlFormatter( ).format( sqlparser,
							option );
					ByteArrayInputStream bis = new ByteArrayInputStream( result.getBytes( ) );
					FileUtil.writeToFile(new File(tidyFileName), bis, true);
				}
				catch ( Exception e )
				{
					System.out.println( sqlparser.sqlfilename
							+ " formatsqlVerifyTest format error: "
							+ e.getMessage( ) );
					j++;
				}

			}
			else
			{
				System.out.println( sqlparser.sqlfilename
						+ " formatsqlVerifyTest parse error: "
						+ sqlparser.getErrormessage( ) );
				i++;
			}
		}

		if ( j > 0 || i > 0 )
		{
			System.err.println( db.toString( )
					+ ", total files formatted;"
					+ sqlfiles.sqlfiles.size( )
					+ ", format exception:"
					+ j
					+ ", parse exception:"
					+ +i );
		}
		return j;
	}

	public static void testSQLServer( )
	{
		assertTrue( formatFiles( EDbVendor.dbvmssql, "./TestCases/mssql" ) == 0 );
	}

	public static void testTeradata( )
	{
		assertTrue( formatFiles( EDbVendor.dbvteradata, "./TestCases/teradata" ) == 0 );
	}

	public static void testPostGreSQL( )
	{
		assertTrue( formatFiles( EDbVendor.dbvpostgresql,
				"./TestCases/postgresql" ) == 0 );
	}

	public static void testMySQL( )
	{
		assertTrue( formatFiles( EDbVendor.dbvmysql, "./TestCases/mysql" ) == 0 );
	}

	public static void testDB2( )
	{
		assertTrue( formatFiles( EDbVendor.dbvdb2, "./TestCases/db2" ) == 0 );
	}

	public static void testOracle( )
	{
		assertTrue( formatFiles( EDbVendor.dbvoracle, "./TestCases/oracle" ) == 0 );
	}

	// public static void testAll( )
	// {
	// assertTrue( formatFiles( EDbVendor.dbvoracle, "./TestCases" ) == 0 );
	// }

}
