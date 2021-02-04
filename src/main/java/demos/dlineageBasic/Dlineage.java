package demos.dlineageBasic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import demos.dlineageBasic.columnImpact.ColumnImpact;
import demos.dlineageBasic.metadata.DDLParser;
import demos.dlineageBasic.metadata.DDLSchema;
import demos.dlineageBasic.metadata.ProcedureRelationScanner;
import demos.dlineageBasic.metadata.ViewParser;
import demos.dlineageBasic.model.ddl.schema.database;
import demos.dlineageBasic.model.metadata.ColumnMetaData;
import demos.dlineageBasic.model.metadata.MetaScanner;
import demos.dlineageBasic.model.metadata.ProcedureMetaData;
import demos.dlineageBasic.model.metadata.TableMetaData;
import demos.dlineageBasic.model.xml.columnImpactResult;
import demos.dlineageBasic.model.xml.procedureImpactResult;
import demos.dlineageBasic.util.Pair;
import demos.dlineageBasic.util.SQLUtil;
import demos.dlineageBasic.util.XML2Model;
import demos.dlineageBasic.util.XMLUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class Dlineage
{

	public static final String TABLE_CONSTANT = "CONSTANT";

	private Map<TableMetaData, List<ColumnMetaData>> tableColumns = new HashMap<TableMetaData, List<ColumnMetaData>>( );
	private Pair<procedureImpactResult, List<ProcedureMetaData>> procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
			new ArrayList<ProcedureMetaData>( ) );

	private boolean strict = false;
	private boolean showUIInfo = false;
	private File sqlDir;
	private File[] sqlFiles;
	private String sqlContent;
	private EDbVendor vendor;

	public Dlineage( String sqlContent, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.vendor = vendor;
		this.sqlFiles = null;
		this.sqlContent = sqlContent;
		tableColumns.clear( );
		SQLUtil.resetVirtualTableNames();
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );

		String content = sqlContent;
		String database = null;

		TGSqlParser parser = new TGSqlParser( vendor );
		parser.sqltext = content;
		int ret = parser.parse( );
		if ( ret == 0 )
		{
			String returnDatabase = new DDLParser( tableColumns,
					procedures,
					vendor,
					parser,
					strict,
					database ).getDatabase( );

			if ( returnDatabase != null )
			{
				database = returnDatabase;
			}

			returnDatabase = new ViewParser( tableColumns,
					vendor,
					parser,
					strict,
					database ).getDatabase( );

			if ( returnDatabase != null )
			{
				database = returnDatabase;
			}

			returnDatabase = new ProcedureRelationScanner( procedures,
					vendor,
					parser,
					strict,
					database ).getDatabase( );
		}
		else
		{
			System.err.println( parser.getErrormessage( ) );
		}
	}

	public Dlineage( String[] sqlContents, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.vendor = vendor;
		this.sqlFiles = null;
		tableColumns.clear( );
		SQLUtil.resetVirtualTableNames();
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );

		String database = null;
		for ( int i = 0; i < sqlContents.length; i++ )
		{
			String content = sqlContents[i];

			TGSqlParser parser = new TGSqlParser( vendor );
			parser.sqltext = content;
			int ret = parser.parse( );
			if ( ret == 0 )
			{
				String returnDatabase = new DDLParser( tableColumns,
						procedures,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}

				returnDatabase = new ViewParser( tableColumns,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}

				database = new ProcedureRelationScanner( procedures,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}
			}
			else
			{
				System.err.println( parser.getErrormessage( ) );
			}
		}
	}

	public Dlineage( File[] sqlFiles, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.vendor = vendor;
		this.sqlFiles = sqlFiles;
		tableColumns.clear( );
		SQLUtil.resetVirtualTableNames();
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );
		File[] children = sqlFiles;

		String database = null;
		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );

			TGSqlParser parser = new TGSqlParser( vendor );
			parser.sqltext = content;
			int ret = parser.parse( );
			if ( ret == 0 )
			{
				String returnDatabase = new DDLParser( tableColumns,
						procedures,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}

				returnDatabase = new ViewParser( tableColumns,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}

				returnDatabase = new ProcedureRelationScanner( procedures,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}
			}
			else
			{
				System.err.println( parser.getErrormessage( ) );
			}
		}
	}

	public Dlineage( File sqlDir, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.sqlDir = sqlDir;
		this.vendor = vendor;
		tableColumns.clear( );
		SQLUtil.resetVirtualTableNames();
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );
		File[] children = listFiles( sqlDir );

		String database = null;
		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );
			TGSqlParser parser = new TGSqlParser( vendor );
			parser.sqltext = content;
			int ret = parser.parse( );
			if ( ret == 0 )
			{
				String returnDatabase = new DDLParser( tableColumns,
						procedures,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}

				returnDatabase = new ViewParser( tableColumns,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}

				returnDatabase = new ProcedureRelationScanner( procedures,
						vendor,
						parser,
						strict,
						database ).getDatabase( );

				if ( returnDatabase != null )
				{
					database = returnDatabase;
				}
			}
			else
			{
				System.err.println( parser.getErrormessage( ) );
			}
		}
	}

	public void columnImpact( )
	{
		String result = getColumnImpactResult( false );
		System.out.println( result );
	}

	public String getColumnImpactResult( )
	{
		return getColumnImpactResult( true );
	}

	public String getColumnImpactResult( boolean analyzeDlineage )
	{
		if ( sqlContent == null )
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
			Document doc = null;
			Element columnImpactResult = null;
			try
			{
				DocumentBuilder builder = factory.newDocumentBuilder( );
				doc = builder.newDocument( );
				doc.setXmlVersion( "1.0" );
				columnImpactResult = doc.createElement( "columnImpactResult" );
				doc.appendChild( columnImpactResult );
				if ( sqlDir != null && sqlDir.isDirectory( ) )
				{
					Element dirNode = doc.createElement( "dir" );
					dirNode.setAttribute( "name", sqlDir.getAbsolutePath( ) );
					columnImpactResult.appendChild( dirNode );
				}
			}
			catch ( ParserConfigurationException e )
			{
				e.printStackTrace( );
			}

			File[] children = sqlFiles == null ? listFiles( sqlDir ) : sqlFiles;
			for ( int i = 0; i < children.length; i++ )
			{
				File child = children[i];
				if ( child.isDirectory( ) )
					continue;
				if ( child != null )
				{
					Element fileNode = doc.createElement( "file" );
					fileNode.setAttribute( "name", child.getAbsolutePath( ) );
					ColumnImpact impact = new ColumnImpact( fileNode,
							vendor,
							tableColumns,
							strict );
					impact.setDebug( false );
					impact.setShowUIInfo( showUIInfo );
					impact.setTraceErrorMessage( false );
					impact.setAnalyzeDlineage( analyzeDlineage );
					impact.ignoreTopSelect( false );
					impact.impactSQL( );
					if ( impact.getErrorMessage( ) != null
							&& impact.getErrorMessage( ).trim( ).length( ) > 0 )
					{
						System.err.println( impact.getErrorMessage( ).trim( ) );
					}
					if ( fileNode.hasChildNodes( ) )
					{
						columnImpactResult.appendChild( fileNode );
					}
				}
			}
			if ( doc != null )
			{
				try
				{
					return XMLUtil.format( doc, 2 );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
			}
		}
		else
		{
			ColumnImpact impact = new ColumnImpact( sqlContent,
					vendor,
					tableColumns,
					strict );
			impact.setDebug( false );
			impact.setShowUIInfo( showUIInfo );
			impact.setTraceErrorMessage( false );
			impact.setAnalyzeDlineage( true );
			impact.impactSQL( );
			if ( impact.getErrorMessage( ) != null
					&& impact.getErrorMessage( ).trim( ).length( ) > 0 )
			{
				System.err.println( impact.getErrorMessage( ).trim( ) );
			}
			return impact.getImpactResult( );
		}
		return null;
	}

	public void forwfdrAnalyze( String tableColumn,
			List<ColumnMetaData[]> relations )
	{
		ColumnMetaData columnMetaData = new MetaScanner( this ).getColumnMetaData( tableColumn );
		List<ColumnMetaData> columns = new ArrayList<ColumnMetaData>( );
		Iterator<TableMetaData> iter = tableColumns.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			columns.addAll( tableColumns.get( iter.next( ) ) );
		}
		if ( columnMetaData != null )
		{
			outputForwfdrAnalyze( columnMetaData, columns, 0, relations );
		}
	}

	public void backwfdrAnalyze( String viewColumn,
			List<ColumnMetaData[]> relations )
	{
		ColumnMetaData columnMetaData = new MetaScanner( this ).getColumnMetaData( viewColumn );
		if ( columnMetaData != null )
		{
			outputBackwfdrAnalyze( columnMetaData, 0, relations );
		}
	}

	private void outputBackwfdrAnalyze( ColumnMetaData columnMetaData,
			int level, List<ColumnMetaData[]> relations )
	{
		if ( level > 0 )
		{
			for ( int i = 0; i < level; i++ )
			{
				System.out.print( "---" );
			}
			System.out.print( ">" );
		}
		System.out.println( columnMetaData.getDisplayFullName( ) );
		if ( columnMetaData.getReferColumns( ) != null
				&& columnMetaData.getReferColumns( ).length > 0 )
		{
			for ( int i = 0; i < columnMetaData.getReferColumns( ).length; i++ )
			{
				ColumnMetaData sourceColumn = columnMetaData.getReferColumns( )[i];
				if ( containsRelation( columnMetaData, sourceColumn, relations ) )
				{
					outputBackwfdrAnalyze( columnMetaData.getReferColumns( )[i],
							level + 1,
							relations );
				}
			}
		}
	}

	private boolean containsRelation( ColumnMetaData targetColumn,
			ColumnMetaData sourceColumn, List<ColumnMetaData[]> relations )
	{
		if ( relations == null )
			return false;
		for ( int i = 0; i < relations.size( ); i++ )
		{
			ColumnMetaData[] relation = relations.get( i );
			if ( relation[0].equals( targetColumn )
					&& relation[1].equals( sourceColumn ) )
				return true;
		}
		return false;
	}

	private void outputForwfdrAnalyze( ColumnMetaData columnMetaData,
			List<ColumnMetaData> columns, int level,
			List<ColumnMetaData[]> relations )
	{
		if ( level > 0 )
		{
			for ( int i = 0; i < level; i++ )
			{
				System.out.print( "---" );
			}
			System.out.print( ">" );
		}
		System.out.println( columnMetaData.getDisplayFullName( ) );
		for ( int i = 0; i < columns.size( ); i++ )
		{
			ColumnMetaData targetColumn = columns.get( i );
			if ( Arrays.asList( targetColumn.getReferColumns( ) )
					.contains( columnMetaData ) )
			{
				if ( containsRelation( targetColumn, columnMetaData, relations ) )
				{
					outputForwfdrAnalyze( targetColumn,
							columns,
							level + 1,
							relations );
				}
			}
		}
	}

	public void outputDDLSchema( )
	{
		System.out.println( new DDLSchema( tableColumns ).getSchemaXML( ) );
	}

	public database[] getDataMetaInfos( )
	{
		return new DDLSchema( tableColumns ).getDataMetaInfos( );
	}

	private File[] listFiles( File sqlFiles )
	{
		List<File> children = new ArrayList<File>( );
		if ( sqlFiles != null )
			listFiles( sqlFiles, children );
		return children.toArray( new File[0] );
	}

	private void listFiles( File rootFile, List<File> children )
	{
		if ( rootFile.isFile( ) )
			children.add( rootFile );
		else
		{
			File[] files = rootFile.listFiles( );
			for ( int i = 0; i < files.length; i++ )
			{
				listFiles( files[i], children );
			}
		}
	}

	public columnImpactResult generateColumnImpact( StringBuilder errorMessage )
	{
		PrintStream systemSteam = System.err;
		ByteArrayOutputStream sw = new ByteArrayOutputStream( );
		PrintStream pw = new PrintStream( sw );
		System.setErr( pw );

		String impactResult = this.getColumnImpactResult( );

		pw.close( );
		System.setErr( systemSteam );

		if ( errorMessage != null )
		{
			errorMessage.append( new String( sw.toByteArray( ) ).trim( ) );
		}

		return getColumnImpactResult( impactResult );
	}

	private columnImpactResult getColumnImpactResult( String result )
	{
		try
		{
			String[] results = result.split( "\n" );
			StringBuilder buffer = new StringBuilder( );
			for ( int i = 0; i < results.length; i++ )
			{
				String line = results[i];
				if ( line.indexOf( "columnImpactResult" ) != -1
						|| line.indexOf( "targetColumn" ) != -1
						|| line.indexOf( "sourceColumn" ) != -1
						|| line.indexOf( "linkTable" ) != -1 )
				{
					buffer.append( line ).append( "\n" );
				}
			}
			return XML2Model.loadXML( columnImpactResult.class,
					buffer.toString( ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	public static void main( String[] args )
	{
		if ( args.length < 1 )
		{
			System.out.println( "Usage: java Dlineage [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>] [/fo <table column>] [/b <view column>] [/ddl] [/s] [/log]" );
			System.out.println( "/f: Option, specify the sql file path to analyze dlineage." );
			System.out.println( "/d: Option, specify the sql directory path to analyze dlineage." );
			System.out.println( "/t: Option, set the database type. Support oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle" );
			System.out.println( "/fo: Option, forwfdr analyze the specified table column." );
			System.out.println( "/b: Option, backwfdr analyze the specified view column." );
			System.out.println( "/ddl: Option, output the database DDL schema." );
			System.out.println( "/s: Option, set the strict match mode. It will match the catalog name and schema name." );
			System.out.println( "/log: Option, generate a dlineage.log file to log information." );
			return;
		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList( args );

		if ( argList.indexOf( "/f" ) != -1
				&& argList.size( ) > argList.indexOf( "/f" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/f" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isFile( ) )
			{
				System.out.println( sqlFiles + " is not a valid file." );
				return;
			}
		}
		else if ( argList.indexOf( "/d" ) != -1
				&& argList.size( ) > argList.indexOf( "/d" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/d" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isDirectory( ) )
			{
				System.out.println( sqlFiles + " is not a valid directory." );
				return;
			}
		}
		else
		{
			System.out.println( "Please specify a sql file path or directory path to analyze dlineage." );
			return;
		}

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}

		boolean strict = argList.indexOf( "/s" ) != -1;
		boolean log = argList.indexOf( "/log" ) != -1;
		PrintStream pw = null;
		ByteArrayOutputStream sw = null;
		PrintStream systemSteam = System.err;

		try
		{
			sw = new ByteArrayOutputStream( );
			pw = new PrintStream( sw );
			System.setErr( pw );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		Dlineage dlineage = new Dlineage( sqlFiles, vendor, strict, false );

		boolean forwfdrAnalyze = argList.indexOf( "/fo" ) != -1;
		boolean backwfdrAnalyze = argList.indexOf( "/b" ) != -1;
		boolean outputDDL = argList.indexOf( "/ddl" ) != -1;

		if ( !forwfdrAnalyze && !backwfdrAnalyze && !outputDDL )
		{
			dlineage.columnImpact( );
		}
		else if ( outputDDL )
		{
			dlineage.outputDDLSchema( );
		}
		else
		{
			DlineageRelation relation = new DlineageRelation( );
			columnImpactResult impactResult = relation.generateColumnImpact( dlineage,
					null );

			List<ColumnMetaData[]> relations = relation.collectDlineageRelations( dlineage,
					impactResult );

			if ( forwfdrAnalyze
					&& argList.size( ) > argList.indexOf( "/fo" ) + 1 )
			{
				String tableColumn = argList.get( argList.indexOf( "/fo" ) + 1 );
				dlineage.forwfdrAnalyze( tableColumn, relations );
			}

			if ( backwfdrAnalyze
					&& argList.size( ) > argList.indexOf( "/b" ) + 1 )
			{
				String viewColumn = argList.get( argList.indexOf( "/b" ) + 1 );
				dlineage.backwfdrAnalyze( viewColumn, relations );
			}
		}

		if ( pw != null )
		{
			pw.close( );
		}

		if ( sw != null )
		{
			String errorMessage = sw.toString( ).trim( );
			if ( errorMessage.length( ) > 0 )
			{
				if ( log )
				{
					try
					{
						pw = new PrintStream( new File( ".", "dlineage.log" ) );
						pw.print( errorMessage );
					}
					catch ( FileNotFoundException e )
					{
						e.printStackTrace( );
					}
				}

				System.setErr( systemSteam );
				System.err.println( errorMessage );
			}
		}
	}

	public Map<TableMetaData, List<ColumnMetaData>> getMetaData( )
	{
		return tableColumns;
	}

	public Pair<procedureImpactResult, List<ProcedureMetaData>> getProcedures( )
	{
		return procedures;
	}

	public boolean isStrict( )
	{
		return strict;
	}

	public EDbVendor getVendor( )
	{
		return vendor;
	}

}
