
package demos.utils;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLUtil
{

	public static boolean isEmpty( String value )
	{
		return value == null || value.trim( ).length( ) == 0;
	}

	public static String getFileContent( File file )
	{
		String charset = null;
		String sqlfilename = file.getAbsolutePath( );
		String fileContent = "";

		try
		{
			FileInputStream fr = new FileInputStream( sqlfilename );
			byte[] bom = new byte[4];
			fr.read( bom, 0, bom.length );
			if ( ( ( bom[0] == (byte) 0xFF ) && ( bom[1] == (byte) 0xFE ) )
					|| ( ( bom[0] == (byte) 0xFE ) && ( bom[1] == (byte) 0xFF ) ) )
			{
				charset = "UTF-16";
				if ( ( ( bom[2] == (byte) 0xFF ) && ( bom[3] == (byte) 0xFE ) )
						|| ( ( bom[2] == (byte) 0xFE ) && ( bom[3] == (byte) 0xFF ) ) )
				{
					charset = "UTF-32";
				}
			}
			else if ( ( bom[0] == (byte) 0xEF )
					&& ( bom[1] == (byte) 0xBB )
					&& ( bom[2] == (byte) 0xBF ) )
			{
				charset = "UTF-8"; // UTF-8,EF BB BF
			}
			fr.close( );
		}
		catch ( FileNotFoundException e )
		{
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		}
		catch ( IOException e )
		{
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		}

		try
		{
			BufferedReader br = new BufferedReader( new InputStreamReader( getInputStreamWithoutBom( sqlfilename ),
					charset == null ? Charset.defaultCharset( ).name( )
							: charset ) );
			try
			{
				StringBuilder sb = new StringBuilder( );
				String line = br.readLine( );

				while ( line != null )
				{
					sb.append( line );
					sb.append( System.getProperty( "line.separator" ) );
					line = br.readLine( );
				}
				fileContent = sb.toString( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
			finally
			{
				try
				{
					br.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}
		catch ( UnsupportedEncodingException e )
		{
			e.printStackTrace( );
		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

		return fileContent.trim( );
	}

	public static String getInputStreamContent( InputStream is, boolean close )
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			while ( true )
			{
				int r = is.read( tmp );
				if ( r == -1 )
					break;
				out.write( tmp, 0, r );
			}
			byte[] bytes = out.toByteArray( );
			if ( close )
			{
				is.close( );
			}
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

	public static String getFileContent( String filePath )
	{
		if ( filePath == null )
			return "";
		File file = new File( filePath );
		if ( !file.exists( ) || file.isDirectory( ) )
			return "";
		return getFileContent( file );
	}

	public static String trimObjectName( String string )
	{
		if ( string == null )
			return string;

		if ( string.indexOf( '.' ) != -1 && string.length( ) < 128 )
		{
			List<String> splits = parseNames( string );
			StringBuilder buffer = new StringBuilder( );
			for ( int i = 0; i < splits.size( ); i++ )
			{
				buffer.append( trimObjectName( splits.get( i ) ) );
				if ( i < splits.size( ) - 1 )
				{
					buffer.append( "." );
				}
			}
			string = buffer.toString( );
		}
		else
		{
			if ( string.startsWith( "\"" ) && string.endsWith( "\"" ) )
				return string.substring( 1, string.length( ) - 1 );

			if ( string.startsWith( "[" ) && string.endsWith( "]" ) )
				return string.substring( 1, string.length( ) - 1 );
		}
		return string;
	}

	public static List<String> parseNames( String nameString )
	{
		String name = nameString.trim( );
		List<String> names = new ArrayList<String>( );
		String[] splits = nameString.toUpperCase( ).split( "\\." );
		if ( ( name.startsWith( "\"" ) && name.endsWith( "\"" ) )
				|| ( name.startsWith( "[" ) && name.endsWith( "]" ) ) )
		{
			for ( int i = 0; i < splits.length; i++ )
			{
				String split = splits[i].trim( );
				if ( split.startsWith( "[" ) && !split.endsWith( "]" ) )
				{
					StringBuilder buffer = new StringBuilder( );
					buffer.append( splits[i] );
					while ( !( split = splits[++i].trim( ) ).endsWith( "]" ) )
					{
						buffer.append( "." );
						buffer.append( splits[i] );
					}

					buffer.append( "." );
					buffer.append( splits[i] );

					names.add( buffer.toString( ) );
					continue;
				}
				if ( split.startsWith( "\"" ) && !split.endsWith( "\"" ) )
				{
					StringBuilder buffer = new StringBuilder( );
					buffer.append( splits[i] );
					while ( !( split = splits[++i].trim( ) ).endsWith( "\"" ) )
					{
						buffer.append( "." );
						buffer.append( splits[i] );
					}

					buffer.append( "." );
					buffer.append( splits[i] );

					names.add( buffer.toString( ) );
					continue;
				}
				names.add( splits[i] );
			}
		}
		else
		{
			names.addAll( Arrays.asList( splits ) );
		}
		return names;
	}

	public static void writeToFile( File file, InputStream source, boolean close )
	{
		BufferedInputStream bis = null;
		BufferedOutputStream fouts = null;
		try
		{
			bis = new BufferedInputStream( source );
			if ( !file.exists( ) )
			{
				if ( !file.getParentFile( ).exists( ) )
				{
					file.getParentFile( ).mkdirs( );
				}
				file.createNewFile( );
			}
			fouts = new BufferedOutputStream( new FileOutputStream( file ) );
			byte b[] = new byte[1024];
			int i = 0;
			while ( ( i = bis.read( b ) ) != -1 )
			{
				fouts.write( b, 0, i );
			}
			fouts.flush( );
			fouts.close( );
			if ( close )
				bis.close( );
		}
		catch ( IOException e )
		{
			Logger.getLogger( SQLUtil.class.getName( ) ).log( Level.WARNING,
					"Write file failed.", //$NON-NLS-1$
					e );
			try
			{
				if ( fouts != null )
					fouts.close( );
			}
			catch ( IOException f )
			{
				Logger.getLogger( SQLUtil.class.getName( ) )
						.log( Level.WARNING, "Close output stream failed.", f ); //$NON-NLS-1$
			}
			if ( close )
			{
				try
				{
					if ( bis != null )
						bis.close( );
				}
				catch ( IOException f )
				{
					Logger.getLogger( SQLUtil.class.getName( ) )
							.log( Level.WARNING, "Close input stream failed.", //$NON-NLS-1$
									f );
				}
			}
		}
	}

	public static void writeToFile( File file, String string )
			throws IOException
	{

		if ( !file.exists( ) )
		{
			if ( !file.getParentFile( ).exists( ) )
			{
				file.getParentFile( ).mkdirs( );
			}
			file.createNewFile( );
		}
		PrintWriter out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( file ) ) );
		if ( string != null )
			out.print( string );
		out.close( );
	}

	public static void appendToFile( File file, String string )
			throws IOException
	{

		if ( !file.exists( ) )
		{
			if ( !file.getParentFile( ).exists( ) )
			{
				file.getParentFile( ).mkdirs( );
			}
			file.createNewFile( );
		}
		PrintWriter out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( file,
				true ) ) );
		if ( string != null )
			out.println( string );
		out.close( );
	}

	public static void deltree( File root )
	{
		if ( root == null || !root.exists( ) )
		{
			return;
		}

		if ( root.isFile( ) )
		{
			root.delete( );
			return;
		}

		File[] children = root.listFiles( );
		if ( children != null )
		{
			for ( int i = 0; i < children.length; i++ )
			{
				deltree( children[i] );
			}
		}

		root.delete( );
	}

	private static int virtualTableIndex = -1;
	private static Map<String, String> virtualTableNames = new HashMap<String, String>( );

	public synchronized static String generateVirtualTableName(
			TCustomSqlStatement stmt )
	{
		if ( virtualTableNames.containsKey( stmt.toString( ) ) )
			return virtualTableNames.get( stmt.toString( ) );
		else
		{
			String tableName = null;
			virtualTableIndex++;
			if ( virtualTableIndex == 0 )
			{
				tableName = "RESULT SET COLUMNS";
			}
			else
			{
				tableName = "RESULT SET COLUMNS " + virtualTableIndex;
			}
			virtualTableNames.put( stmt.toString( ), tableName );
			return tableName;
		}
	}

	public synchronized static void resetVirtualTableNames( )
	{
		virtualTableIndex = -1;
		virtualTableNames.clear( );
	}

	/**
	 * 
	 * @param file
	 *            the filePath
	 * @param enc
	 *            the default encoding
	 * @return the UTFFileHandler.UnicodeInputStream
	 * @throws Exception
	 */
	public static InputStream getInputStreamWithoutBom( String file )
			throws IOException
	{
		UnicodeInputStream stream = null;
		FileInputStream fis = new FileInputStream( file );
		stream = new UnicodeInputStream( fis, null );
		return stream;
	}

	/**
	 * This inputstream will recognize unicode BOM marks and will skip bytes if
	 * getEncoding() method is called before any of the read(...) methods.
	 * 
	 * Usage pattern: String enc = "ISO-8859-1"; // or NULL to use systemdefault
	 * FileInputStream fis = new FileInputStream(file); UnicodeInputStream uin =
	 * new UnicodeInputStream(fis, enc); enc = uin.getEncoding(); // check and
	 * skip possible BOM bytes InputStreamReader in; if (enc == null) in = new
	 * InputStreamReader(uin); else in = new InputStreamReader(uin, enc);
	 */
	public static class UnicodeInputStream extends InputStream
	{

		PushbackInputStream internalIn;
		boolean isInited = false;
		String defaultEnc;
		String encoding;

		private static final int BOM_SIZE = 4;

		public UnicodeInputStream( InputStream in, String defaultEnc )
		{
			internalIn = new PushbackInputStream( in, BOM_SIZE );
			this.defaultEnc = defaultEnc;
		}

		public String getDefaultEncoding( )
		{
			return defaultEnc;
		}

		public String getEncoding( )
		{
			if ( !isInited )
			{
				try
				{
					init( );
				}
				catch ( IOException ex )
				{
					IllegalStateException ise = new IllegalStateException( "Init method failed." );
					ise.initCause( ise );
					throw ise;
				}
			}
			return encoding;
		}

		/**
		 * Read-ahead four bytes and check for BOM marks. Extra bytes are unread
		 * back to the stream, only BOM bytes are skipped.
		 */
		protected void init( ) throws IOException
		{
			if ( isInited )
				return;

			byte bom[] = new byte[BOM_SIZE];
			int n, unread;
			n = internalIn.read( bom, 0, bom.length );

			if ( ( bom[0] == (byte) 0x00 )
					&& ( bom[1] == (byte) 0x00 )
					&& ( bom[2] == (byte) 0xFE )
					&& ( bom[3] == (byte) 0xFF ) )
			{
				encoding = "UTF-32BE";
				unread = n - 4;
			}
			else if ( ( bom[0] == (byte) 0xFF )
					&& ( bom[1] == (byte) 0xFE )
					&& ( bom[2] == (byte) 0x00 )
					&& ( bom[3] == (byte) 0x00 ) )
			{
				encoding = "UTF-32LE";
				unread = n - 4;
			}
			else if ( ( bom[0] == (byte) 0xEF )
					&& ( bom[1] == (byte) 0xBB )
					&& ( bom[2] == (byte) 0xBF ) )
			{
				encoding = "UTF-8";
				unread = n - 3;
			}
			else if ( ( bom[0] == (byte) 0xFE ) && ( bom[1] == (byte) 0xFF ) )
			{
				encoding = "UTF-16BE";
				unread = n - 2;
			}
			else if ( ( bom[0] == (byte) 0xFF ) && ( bom[1] == (byte) 0xFE ) )
			{
				encoding = "UTF-16LE";
				unread = n - 2;
			}
			else
			{
				encoding = defaultEnc;
				unread = n;
			}

			if ( unread > 0 )
				internalIn.unread( bom, ( n - unread ), unread );

			isInited = true;
		}

		public void close( ) throws IOException
		{
			internalIn.close( );
		}

		public int read( ) throws IOException
		{
			return internalIn.read( );
		}
	}

	public static TCustomSqlStatement getParentStmt( TExpression expr )
	{
		return expr.getStartToken( ).stmt;
	}

	public static TCustomSqlStatement getTopStmt( TCustomSqlStatement stmt )
	{
		if ( stmt != null && stmt.getParentStmt( ) != null )
		{
			return getTopStmt( stmt.getParentStmt( ) );
		}
		else
			return stmt;
	}

	public static String getVendorName( EDbVendor vendor )
	{
		return vendor.name( ).replace( "dbv", "" );
	}

	public static String getContent( TGSqlParser sqlparser )
	{
		if ( !isEmpty( sqlparser.getSqltext( ) ) )
		{
			return sqlparser.getSqltext( );
		}
		return getFileContent( new File( sqlparser.sqlfilename ) );
	}

	public static EDbVendor getVendor( String venderValue )
	{
		EDbVendor vendor = null;
		if ( venderValue.equalsIgnoreCase( "ansi" ) )
		{
			vendor = EDbVendor.dbvansi;
		}
		else if ( venderValue.equalsIgnoreCase( "mssql" ) )
		{
			vendor = EDbVendor.dbvmssql;
		}
		else if ( venderValue.equalsIgnoreCase( "db2" ) )
		{
			vendor = EDbVendor.dbvdb2;
		}
		else if ( venderValue.equalsIgnoreCase( "mysql" ) )
		{
			vendor = EDbVendor.dbvmysql;
		}
		else if ( venderValue.equalsIgnoreCase( "netezza" ) )
		{
			vendor = EDbVendor.dbvnetezza;
		}
		else if ( venderValue.equalsIgnoreCase( "teradata" ) )
		{
			vendor = EDbVendor.dbvteradata;
		}
		else if ( venderValue.equalsIgnoreCase( "oracle" ) )
		{
			vendor = EDbVendor.dbvoracle;
		}
		else if ( venderValue.equalsIgnoreCase( "informix" ) )
		{
			vendor = EDbVendor.dbvinformix;
		}
		else if ( venderValue.equalsIgnoreCase( "sybase" ) )
		{
			vendor = EDbVendor.dbvsybase;
		}
		else if ( venderValue.equalsIgnoreCase( "postgresql" ) )
		{
			vendor = EDbVendor.dbvpostgresql;
		}
		else if ( venderValue.equalsIgnoreCase( "hive" ) )
		{
			vendor = EDbVendor.dbvhive;
		}
		else if ( venderValue.equalsIgnoreCase( "greenplum" ) )
		{
			vendor = EDbVendor.dbvgreenplum;
		}
		else if ( venderValue.equalsIgnoreCase( "redshift" ) )
		{
			vendor = EDbVendor.dbvredshift;
		}
		else if ( venderValue.equalsIgnoreCase( "snowflake" ) )
		{
			vendor = EDbVendor.dbvsnowflake;
		}
		return vendor;
	}
}
