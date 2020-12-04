
package antiSQLInjection;

import gudusoft.gsqlparser.EDbVendor;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import demos.antiSQLInjection.TAntiSQLInjection;

public class antiSQLInjectionBatchTest extends TestCase
{

	private final static Logger logger = Logger.getLogger( antiSQLInjectionBatchTest.class.getSimpleName( ) );

	private TAntiSQLInjection anti = new TAntiSQLInjection( EDbVendor.dbvmysql );
	{
		anti.check_not_in_allowed_statement( false );
	}

	private int identify;
	private int totalIdentify;

	private int misjudgment;
	private int totalMisjudgment;

	private long usedTime;

//	 public static void main( String[] args )
	public void testMain()
	{
//		String testDir = "c:\\prg\\gsqlparser\\Test\\TestCases\\sqlinjection";
//
//		File testFile = new File( testDir );
//		if ( testFile.isDirectory( ) )
//		{
//			File[] children = testFile.listFiles( );
//			for ( int i = 0; i < children.length; i++ )
//			{
//				new antiSQLInjectionBatchTest( children[i] );
//			}
//		}
//		else if ( testFile.isFile( ) )
//		{
//			new antiSQLInjectionBatchTest( testFile );
//		}

	}

//	public antiSQLInjectionBatchTest( File testArchiveFile )
//	{
//		unRarFile( testArchiveFile.getAbsolutePath( ) );
//		System.out.println( "archive file: " + testArchiveFile.getName( ) );
//		System.out.println( "identify: "
//				+ roundFloat( identify * 100f / totalIdentify, 2 )
//				+ "%, sql count = "
//				+ totalIdentify );
//		System.out.println( "misjudgment: "
//				+ roundFloat( misjudgment * 100f / totalMisjudgment, 2 )
//				+ "%, sql count = "
//				+ totalMisjudgment );
//		System.out.println( "used time: "
//				+ roundFloat( usedTime / 1000f, 2 )
//				+ "s" );
//		System.out.println( );
//	}

	public static Float roundFloat( Float value, int round )
	{
		if ( null != value )
		{
			BigDecimal data = new BigDecimal( value );
			value = data.setScale( round, BigDecimal.ROUND_HALF_UP )
					.floatValue( );
			return value;
		}
		else
			return value;
	}

	public void unRarFile( String srcRarPath )
	{
		if ( !srcRarPath.toLowerCase( ).endsWith( ".rar" ) )
		{
			return;
		}
		Archive a = null;
		try
		{
			a = new Archive( new File( srcRarPath ) );
			if ( a != null )
			{
				FileHeader fh = a.nextFileHeader( );
				while ( fh != null )
				{
					if ( !fh.isDirectory( )
							&& fh.getFileNameString( ).endsWith( ".sql" ) )
					{
						try
						{
							ByteArrayOutputStream os = new ByteArrayOutputStream( );
							a.extractFile( fh, os );
							os.close( );

							antiSQLInjection( os.toString( ),
									new File( fh.getFileNameString( ) ).getName( ) );
						}
						catch ( Exception ex )
						{
							logger.log( Level.SEVERE, ex.getMessage( ), ex );
						}
					}
					fh = a.nextFileHeader( );
				}
			}

		}
		catch ( Exception e )
		{
			logger.log( Level.SEVERE, e.getMessage( ), e );
		}
		finally
		{
			if ( a != null )
			{
				try
				{
					a.close( );
				}
				catch ( IOException e )
				{
					logger.log( Level.SEVERE, e.getMessage( ), e );
				}
			}
		}
	}

	private void antiSQLInjection( String sql, String fileName )
	{
		if ( fileName.startsWith( "a" ) )
		{
			statisticInjectionSql( sql );
		}
		else if ( fileName.startsWith( "r" ) )
		{
			statisticVaildSql( sql );
		}
	}

	private void statisticInjectionSql( String sql )
	{
		long startTime = System.currentTimeMillis( );
		totalIdentify++;

		if ( anti.isInjected( sql ) )
			identify++;

		usedTime += ( System.currentTimeMillis( ) - startTime );

	}

	private void statisticVaildSql( String sql )
	{
		long startTime = System.currentTimeMillis( );
		totalMisjudgment++;

		if ( anti.isInjected( sql ) )
			misjudgment++;

		usedTime += ( System.currentTimeMillis( ) - startTime );
	}
}
