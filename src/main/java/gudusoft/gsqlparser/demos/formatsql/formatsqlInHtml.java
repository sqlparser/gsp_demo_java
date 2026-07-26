
package demos.formatsql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.output.HighlightingElement;
import gudusoft.gsqlparser.pp.output.OutputConfig;
import gudusoft.gsqlparser.pp.output.OutputConfigFactory;
import gudusoft.gsqlparser.pp.output.html.HtmlHighlightingElementRender;
import gudusoft.gsqlparser.pp.output.html.HtmlOutputConfig;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.GOutputFmt;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class formatsqlInHtml
{

	public formatsqlInHtml( TGSqlParser sqlparser, boolean custom )
	{

		GFmtOpt option = GFmtOptFactory.newInstance( );
		option.outputFmt = GOutputFmt.ofhtml;
		if ( custom )
		{
			OutputConfig outputConfig = OutputConfigFactory.getOutputConfig( option,
					sqlparser.getDbVendor( ) );
			if ( outputConfig instanceof HtmlOutputConfig )
			{
				customOutputConfig( (HtmlOutputConfig) outputConfig );
			}
			FormatterFactory.setOutputConfig( outputConfig );
		}

		String result = FormatterFactory.pp( sqlparser, option );

		ByteArrayInputStream bis = new ByteArrayInputStream( result.getBytes( ) );
		String outputFile = sqlparser.sqlfilename + ".html";
		writeToFile( new File( outputFile ), bis, true );
		try
		{
			Process process = Runtime.getRuntime( ).exec( "Explorer \""
					+ outputFile
					+ "\"" );
			process.waitFor( );
		}
		catch ( Exception e )
		{
			Logger.getLogger( formatsqlInHtml.class.getName( ) )
					.log( Level.WARNING, "Start the explorer failed.", //$NON-NLS-1$
							e );
		}
	}

	private void customOutputConfig( HtmlOutputConfig outputConfig )
	{
		outputConfig.setGlobalFontSize( 12 );
		outputConfig.setGlobalFontName( "Courier New" );
		outputConfig.addHighlightingElementRender( HighlightingElement.sfkStandardkeyword,
				new HtmlHighlightingElementRender( HighlightingElement.sfkStandardkeyword,
						new Color( 127, 0, 85 ),
						new Font( "Courier New", Font.BOLD, 12 ) ) );
		outputConfig.addHighlightingElementRender( HighlightingElement.sfkIdentifer,
				new HtmlHighlightingElementRender( HighlightingElement.sfkIdentifer,
						Color.BLACK,
						new Font( "Courier New", Font.PLAIN, 12 ) ) );
		outputConfig.addHighlightingElementRender( HighlightingElement.sfkSQString,
				new HtmlHighlightingElementRender( HighlightingElement.sfkSQString,
						Color.BLUE,
						new Font( "Courier New", Font.PLAIN, 12 ) ) );
		outputConfig.addHighlightingElementRender( HighlightingElement.sfkSymbol,
				new HtmlHighlightingElementRender( HighlightingElement.sfkSymbol,
						Color.RED,
						new Font( "Courier New", Font.PLAIN, 12 ) ) );
	}

	public static void main( String args[] )
	{

		if ( args.length < 1 )
		{
			System.out.println( "Usage: java formatsqlInHtml [Sql File][/c]" );
			System.out.println( "Sql File: Specify the sql file will be formatted." );
			System.out.println( "/c: option, use the custom format color and font setting." );
			return;
		}
		File file = new File( args[0] );
		if ( !file.exists( ) )
		{
			System.out.println( "File not exists:" + args[0] );
			return;
		}

		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmssql );
		sqlparser.sqlfilename = args[0];

		int ret = sqlparser.parse( );
		if ( ret == 0 )
		{
			new formatsqlInHtml( sqlparser,
					args.length > 1 ? "/c".equalsIgnoreCase( args[1] ) : false );
		}
		else
		{
			System.out.println( sqlparser.getErrormessage( ) );
		}
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
			Logger.getLogger( formatsqlInHtml.class.getName( ) )
					.log( Level.WARNING, "Write file failed.", //$NON-NLS-1$
							e );
			try
			{
				if ( fouts != null )
					fouts.close( );
			}
			catch ( IOException f )
			{
				Logger.getLogger( formatsqlInHtml.class.getName( ) )
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
					Logger.getLogger( formatsqlInHtml.class.getName( ) )
							.log( Level.WARNING, "Close input stream failed.", //$NON-NLS-1$
									f );
				}
			}
		}
	}

}
