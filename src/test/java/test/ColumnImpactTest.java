
package test;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;
import gudusoft.gsqlparser.TBaseType;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;
import common.SqlFileList;
import demos.antiSQLInjection.columnImpact.ColumnImpact;

public class ColumnImpactTest extends TestCase
{

	private String testFilesDirectory;
	private String testFileSuffix = ".sql";
	private boolean containsSubFolder = false;

	public void setUp( )
	{
		testFilesDirectory = "/sqlscripts/columnImpact";
		// containsSubFolder = false;
		// testFileSuffix = ".sql";
	}

	public void testCrashSQL( )
	{
		String sqltext = "select job,\r\n"
				+ "max( decode( deptno, 10, cnt, null ) ) dept_10,\r\n"
				+ "max( decode( deptno, 20, cnt, null ) ) dept_20,\r\n"
				+ "max( decode( deptno, 30, cnt, null ) ) dept_30,\r\n"
				+ "max( decode( deptno, 40, cnt, null ) ) dept_40\r\n"
				+ "from ( select job, deptno, count(*) cnt\r\n"
				+ "from emp\r\n"
				+ "group by job, deptno )\r\n"
				+ "group by job;";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "job depends on: emp.job, emp.deptno\r\n"
						+ "dept_10 depends on: emp.deptno, emp.job, emp\r\n"
						+ "dept_20 depends on: emp.deptno, emp.job, emp\r\n"
						+ "dept_30 depends on: emp.deptno, emp.job, emp\r\n"
						+ "dept_40 depends on: emp.deptno, emp.job, emp\r\n" ) ));

	}

	public void testWithCTESQL( )
	{
		String sqltext = "WITH dept_count AS (\r\n"
				+ "SELECT deptno, COUNT(*) AS dept_count\r\n"
				+ "FROM   emp\r\n"
				+ "GROUP BY deptno)\r\n"
				+ "SELECT e.ename AS employee_name,\r\n"
				+ "dc.dept_count AS emp_dept_count\r\n"
				+ "FROM   emp e,\r\n"
				+ "dept_count dc\r\n"
				+ "WHERE  e.deptno = dc.deptno;";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "employee_name depends on: emp.ename, emp.deptno\r\nemp_dept_count depends on: emp, emp.deptno\r\n" ) ));

	}

	public void testUnionSQL( )
	{
		String sqltext = "SELECT SAL FROM\r\n"
				+ "(SELECT SAL FROM Store_Information\r\n"
				+ "Union\r\n"
				+ "SELECT SAL FROM Internet_Sales);";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "SAL depends on: store_information.sal, internet_sales.sal\r\n" ) ));

	}

	public void testCTEAndWhereSQL( )
	{
		String sqltext = "WITH dept_costs\r\n"
				+ "AS (SELECT SUM(sal) dept_total, dname,\r\n"
				+ "FROM   emp e,\r\n"
				+ "dept d\r\n"
				+ "WHERE  e.deptno = d.deptno\r\n"
				+ "GROUP  BY dname),\r\n"
				+ "avg_cost\r\n"
				+ "AS (SELECT SUM(dept_total) / COUNT(*) AVG\r\n"
				+ "FROM   dept_costs)\r\n"
				+ "SELECT *\r\n"
				+ "FROM   dept_costs\r\n"
				+ "WHERE  dept_total > (SELECT AVG\r\n"
				+ "FROM   avg_cost)\r\n"
				+ "ORDER  BY dname; \r\n";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "* depends on: emp.sal, dept.sal, emp.deptno, dept.deptno, emp.dname, dept.dname\r\n" ) ));

	}

	public void testConnectbyAndStartwithSQL( )
	{
		String sqltext = "select * from persons.dept connect by prior paredeptid=deptid start with deptid=76\r\n";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvoracle,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "* depends on: persons.dept, persons.dept.paredeptid, persons.dept.deptid\r\n" ) ));
	}

	public void testWhereSQL( )
	{
		String sqltext = "select a.ca1, a.ca2, b.cb1\r\n"
				+ "from\r\n"
				+ "(select ca1,ca2, ca_id from ta where ca3 = 5) a,\r\n"
				+ "(select cb1, cb_id from tb where cb3 = 5) b\r\n"
				+ "where b.cb_id = a.ca_id; \r\n";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "a.ca1 depends on: ta.ca1, ta.ca3, tb.cb_id, tb.cb3, ta.ca_id\r\na.ca2 depends on: ta.ca2, ta.ca3, tb.cb_id, tb.cb3, ta.ca_id\r\nb.cb1 depends on: tb.cb1, tb.cb3, tb.cb_id, ta.ca_id, ta.ca3\r\n" ) ));

	}

	public void testCaseWhenSQL( )
	{
		String sqltext = "select product_id,product_type_id,\r\n"
				+ "case product_type_id\r\n"
				+ "when 1 then 'Book'\r\n"
				+ "when 2 then 'Video'\r\n"
				+ "when 3 then 'DVD'\r\n"
				+ "when 4 then 'CD'\r\n"
				+ "else 'Magazine'\r\n"
				+ "end as testcasewhen\r\n"
				+ "from products; \r\n";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "product_id depends on: products.product_id\r\nproduct_type_id depends on: products.product_type_id\r\ntestcasewhen depends on: products.product_type_id\r\n" ) ));
	}

	public void testOrderBySQL( )
	{
		String sqltext = "select * from mytb order by mycol nulls first; \r\n";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "* depends on: mytb, mytb.mycol\r\n" ) ));
	}

	public void testUpdateSQL( )
	{
		String sqltext = "UPDATE sds_tst_prdct \r\n"
				+ "SET base_cow_id = b.bsecowid \r\n"
				+ "FROM sds_tst_prdct a, \r\n"
				+ "bog_pr_jil b \r\n"
				+ "WHERE a.std_tst_id = b.tstid \r\n"
				+ "AND b.bsecowid > 1 \r\n";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "base_cow_id depends on: sds_tst_prdct.std_tst_id, bog_pr_jil.tstid, bog_pr_jil.bsecowid\r\n" ) ));

	}

	public void testColumnLevelResult( )
	{
		String sqltext = "select T1.PRODUCT, T2.AMOUNT from\r\n"
				+ "PRODUCTS t1,\r\n"
				+ "(select PRODUCT_ID, (AMOUNT + QTY) AMOUNT  from SALES WHERE PRODUCT_TYPE = 'xyz') T2\r\n"
				+ "where T1.PRODUCT_ID=T2.PRODUCT_ID";
		ColumnImpact impact = new ColumnImpact( sqltext,
				EDbVendor.dbvmssql,
				true,
				false,
				true,
				null );
		impact.impactSQL();
		//System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));
		Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
				,( "T1.PRODUCT depends on: products.product\r\nT2.AMOUNT depends on: sales.amount, sales.qty\r\n" ) ));

	}

	static class MetaDatabaseFilter implements IMetaDatabase {

		 String columns[][] = {
			        {"server","db","dbo","other_table","c1"},
			        {"server","db","dbo","some_table","c2"}
			    };


		public boolean checkColumn( String server, String database,
				String schema, String table, String column )
		{
			boolean bServer, bDatabase, bSchema, bTable, bColumn, bRet = false;
			for ( int i = 0; i < columns.length; i++ )
			{
				if ( server == null )
				{
					bServer = true;
				}
				else
				{
					bServer = columns[i][0].equalsIgnoreCase( server );
				}
				if ( !bServer )
					continue;

				if ( database == null )
				{
					bDatabase = true;
				}
				else
				{
					bDatabase = columns[i][1].equalsIgnoreCase( database );
				}
				if ( !bDatabase )
					continue;

				if ( schema == null )
				{
					bSchema = true;
				}
				else
				{
					bSchema = columns[i][2].equalsIgnoreCase( schema );
				}

				if ( !bSchema )
					continue;

				bTable = columns[i][3].equalsIgnoreCase( table );
				if ( !bTable )
					continue;

				bColumn = columns[i][4].equalsIgnoreCase( column );
				if ( !bColumn )
					continue;

				bRet = true;
				break;

			}
			return bRet;
		}   
	}

	public void testSQLFromFile( )
	{
		SqlFileList fileList = new SqlFileList( testFilesDirectory,
				containsSubFolder,
				testFileSuffix );
		if ( fileList.sqlfiles != null )
		{
			for ( int i = 0; i < fileList.sqlfiles.size( ); i++ )
			{
				String filePath = fileList.sqlfiles.get( i );
				String xmlFilePath = filePath.replaceAll( "(?i)"
						+ Pattern.quote( testFileSuffix ),
						".xml" );
				File xmlFile = new File( xmlFilePath );
				if ( xmlFile.exists( ) )
				{
					EDbVendor vendor = EDbVendor.dbvoracle;
					if ( filePath.indexOf( "#182" ) > -1 )
					{
						vendor = EDbVendor.dbvmssql;
					}
					ColumnImpact impact = new ColumnImpact( new File( filePath ),
							vendor,
							true,
							true,
							new MetaDatabaseFilter( ) );
					impact.impactSQL();
				//	System.out.println( TBaseType.compareStringsLineByLine(impact.getImpactResult( ) ));

					Assert.assertTrue( TBaseType.compareStringsLineByLine(impact.getImpactResult( )
							.trim( )
							,( getContent( xmlFile ) ) ));
				}
			}
		}
	}

	private String getContent( File file )
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			InputStream is = new BufferedInputStream( new FileInputStream( file ) );
			while ( true )
			{
				int r = is.read( tmp );
				if ( r == -1 )
					break;
				out.write( tmp, 0, r );
			}
			byte[] bytes = out.toByteArray( );
			is.close( );
			out.close( );
			String content = new String( bytes );
			return content.trim( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}
}
