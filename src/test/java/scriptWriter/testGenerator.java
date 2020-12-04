
package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;
import common.SqlFileList;

public class testGenerator extends TestCase
{

	boolean supportedSqlType( ESqlStatementType sqlStatementType )
	{
		return ( ( sqlStatementType == ESqlStatementType.sstselect )
				|| ( sqlStatementType == ESqlStatementType.sstdelete )
				|| ( sqlStatementType == ESqlStatementType.sstupdate )
				|| ( sqlStatementType == ESqlStatementType.sstinsert )
				|| ( sqlStatementType == ESqlStatementType.sstmerge )
				|| ( sqlStatementType == ESqlStatementType.sstcreatetable )
				|| ( sqlStatementType == ESqlStatementType.sstcreateview )
				|| ( sqlStatementType == ESqlStatementType.sstdropindex )
				|| ( sqlStatementType == ESqlStatementType.sstUseDatabase )
				|| ( sqlStatementType == ESqlStatementType.sstmssqlcreatefunction ) || ( sqlStatementType == ESqlStatementType.sstmssqlif )

		);
	}

	void processfiles( EDbVendor db, String dir )
	{

		TGSqlParser sqlparser = new TGSqlParser( db );
		SqlFileList sqlfiles = new SqlFileList( dir, true );
		for ( int k = 0; k < sqlfiles.sqlfiles.size( ); k++ )
		{
			sqlparser.sqlfilename = sqlfiles.sqlfiles.get( k ).toString( );

			try
			{
				boolean b = sqlparser.parse( ) == 0;
				assertTrue( sqlparser.sqlfilename
						+ "\n"
						+ sqlparser.getErrormessage( ),
						b );
				if ( b )
				{
					for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
					{
						if ( supportedSqlType( sqlparser.sqlstatements.get( i ).sqlstatementtype ) )
						{
							// assertTrue(sqlparser.sqlfilename,rewriteQuery(sqlparser.sqlstatements.get(i),db));
							if ( !rewriteQuery( sqlparser.sqlstatements.get( i ),
									db ) )
							{
								System.out.println( "\n"
										+ sqlparser.sqlfilename
										+ "\n" );
							}
						}
					}
				}

			}
			catch ( Exception e )
			{
				System.out.println( "testGenerator error:"
						+ e.getMessage( )
						+ " "
						+ sqlparser.sqlfilename );
			}
		}

	}

	boolean rewriteQuery( TCustomSqlStatement sqlStatement, EDbVendor dbVendor )
	{
		String sourceSql = sqlStatement.toString( );
		String targetSql = sqlStatement.toScript( );
		return testScriptGenerator.verifyScript( dbVendor, sourceSql, targetSql, sqlStatement.getStartToken().lineNo );
	}

	public void testOracle( )
	{
		//processfiles(EDbVendor.dbvoracle,"c:/prg/gsqlparser/Test/TestCases/oracle");
		// processfiles(EDbVendor.dbvoracle,
		// "c:/prg/gsqlparser/Test/TestCases/java/oracle/");
		//processfiles(EDbVendor.dbvoracle,"c:/prg/gsqlparser/Test/TestCases/commonsql/");

	}

	public void testSQLServer( )
	{
	//	processfiles(EDbVendor.dbvmssql,"c:/prg/gsqlparser/Test/TestCases/mssql");

	}
}
