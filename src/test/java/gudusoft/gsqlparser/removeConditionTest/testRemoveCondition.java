package gudusoft.gsqlparser.removeConditionTest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import demos.removeCondition.removeCondition;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.Assert;
import junit.framework.TestCase;
import gudusoft.gsqlparser.commonTest.SqlFileList;

public class testRemoveCondition extends TestCase
{

	private String testFilesDirectory;
	private String testFileSuffix = ".sql";
	private boolean containsSubFolder = false;
	private Map<String, String> conditionMap = new HashMap<String, String>( );

	public void setUp( )
	{
		testFilesDirectory = "Test\\TestCases\\removeCondition";
		// containsSubFolder = false;
		// testFileSuffix = ".sql";
		conditionMap.put( "Prof4", "Prof4" );
		conditionMap.put( "Prof3", "Prof3" );
		conditionMap.put( "Radius_Origin_ZIP", "Radius_Origin_ZIP" );
		conditionMap.put( "Radius_Distance_in_Miles",
				"Radius_Distance_in_Miles" );
		conditionMap.put( "RadiusOriginZIP", "RadiusOriginZIP" );
		conditionMap.put( "RadiusDistanceinMiles", "RadiusDistanceinMiles" );
		conditionMap.put( "GivingFromDate", "2011-01-01" );
		conditionMap.put( "GivingThruDate", "2013-01-01" );
		conditionMap.put( "end_date", "2000-10-10" );
	}

	public static void testRemoveSpecialConditions( )
	{
		String sql = "SELECT SUM (d.amt)\r\n"
				+ "FROM summit.cntrb_detail d\r\n"
				+ "WHERE d.id = summit.mstr.id\r\n"
				+ "AND d.system_gift_type IN ( 'OG', 'PLP', 'PGP' )\r\n"
				+ "AND d.fund_coll_attrb IN ( '$Institute$' )\r\n"
				+ "AND d.fund_acct IN ( '$Fund$' )\r\n"
				+ "AND d.cntrb_date >= '$From_Date$'\r\n"
				+ "AND d.cntrb_date <= '$Thru_Date$'\r\n"
				+ "GROUP BY d.id;";
		String result = "SELECT SUM (d.amt)\r\n"
				+ "FROM summit.cntrb_detail d\r\n"
				+ "WHERE d.id = summit.mstr.id\r\n"
				+ "AND d.system_gift_type IN ( 'OG', 'PLP', 'PGP' )\r\n"
				+ "AND d.fund_coll_attrb IN ( 'ShanXi University' )\r\n"
				+ "GROUP BY d.id;";
		Map<String, String> conditionMap = new HashMap<String, String>( );
		conditionMap.put( "Institute", "ShanXi University" );
		removeCondition remove = new removeCondition( sql,
				EDbVendor.dbvmssql,
				conditionMap );
		Assert.assertEquals( remove.getRemoveResult( ), result );
	}

	public static void testIssue239( )
	{
		String sql = "SELECT SUM (d.amt) \r\n "
				+ "FROM   summit.cntrb_detail d \r\n "
				+ "WHERE  d.id = summit.mstr.id \r\n "
				+ "       AND (d.cntrb_date || d.cntrb_time) >= ('$From_Date$'  || '$From_Time$')\r\n "
				+ "       AND (d.cntrb_date || d.cntrb_time) <= ('$Thru_Date$'  || '$Thru_Date$')\r\n "
				+ "GROUP  BY d.id ";
		String result = "SELECT SUM (d.amt) \r\n"
				+ " FROM   summit.cntrb_detail d \r\n"
				+ " WHERE  d.id = summit.mstr.id \r\n"
				+ "        AND (d.cntrb_date || d.cntrb_time) >= ('20130731')\r\n"
				+ " GROUP  BY d.id";
		Map<String, String> conditionMap = new HashMap<String, String>( );
		conditionMap.put( "From_Date", "20130731" );
		removeCondition remove = new removeCondition( sql,
				EDbVendor.dbvoracle,
				conditionMap );
		Assert.assertEquals( remove.getRemoveResult( ), result );
	}

	public static void testRemoveNonConditions( )
	{
		String sql = "SELECT SUM (d.amt)\r\n"
				+ "FROM summit.cntrb_detail d\r\n"
				+ "WHERE d.id = summit.mstr.id\r\n"
				+ "AND d.system_gift_type IN ( 'OG', 'PLP', 'PGP' )\r\n"
				+ "AND d.fund_coll_attrb IN ( '$Institute$' )\r\n"
				+ "AND d.fund_acct IN ( '$Fund$' )\r\n"
				+ "AND d.cntrb_date >= '$From_Date$'\r\n"
				+ "AND d.cntrb_date <= '$Thru_Date$'\r\n"
				+ "GROUP BY d.id;";
		String result = "SELECT SUM (d.amt)\r\n"
				+ "FROM summit.cntrb_detail d\r\n"
				+ "WHERE d.id = summit.mstr.id\r\n"
				+ "AND d.system_gift_type IN ( 'OG', 'PLP', 'PGP' )\r\n"
				+ "AND d.fund_coll_attrb IN ( 'ShanXi University' )\r\n"
				+ "AND d.fund_acct IN ( 'Eclipse' )\r\n"
				+ "AND d.cntrb_date >= '2012-01-01'\r\n"
				+ "AND d.cntrb_date <= '2013-01-01'\r\n"
				+ "GROUP BY d.id;";
		Map<String, String> conditionMap = new HashMap<String, String>( );
		conditionMap.put( "Institute", "ShanXi University" );
		conditionMap.put( "Fund", "Eclipse" );
		conditionMap.put( "From_Date", "2012-01-01" );
		conditionMap.put( "Thru_Date", "2013-01-01" );
		removeCondition remove = new removeCondition( sql,
				EDbVendor.dbvmssql,
				conditionMap );
		Assert.assertEquals( remove.getRemoveResult( ), result );
	}

	public static void testRemoveAllConditions( )
	{
		String sql = "SELECT SUM (d.amt)\r\n"
				+ "FROM summit.cntrb_detail d\r\n"
				+ "WHERE d.fund_coll_attrb IN ( '$Institute$' )\r\n"
				+ "AND d.fund_acct IN ( '$Fund$' )\r\n"
				+ "AND d.cntrb_date >= '$From_Date$'\r\n"
				+ "AND d.cntrb_date <= '$Thru_Date$'\r\n"
				+ "GROUP BY d.id;";
		String result = "SELECT SUM (d.amt)\r\n"
				+ "FROM summit.cntrb_detail d\r\n"
				+ "GROUP BY d.id;";
		removeCondition remove = new removeCondition( sql,
				EDbVendor.dbvmssql,
				null );
		Assert.assertEquals( remove.getRemoveResult( ), result );
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
						".txt" );
				System.out.println(filePath);
				File xmlFile = new File( xmlFilePath );
				File sqlFile = new File( filePath );
				if ( xmlFile.exists( ) && sqlFile.exists( ) )
				{
					removeCondition remove = new removeCondition( sqlFile,
							EDbVendor.dbvoracle,
							conditionMap );
					
					String result =  remove.getRemoveResult( ).replaceAll("\\s+", " ").trim().toLowerCase();
					String result1 = getContent( xmlFile ).replaceAll("\\s+", " ").trim().toLowerCase();
					
					Assert.assertTrue(result.equals(result1));
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
