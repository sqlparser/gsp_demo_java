
package demos.plannerAnalyze;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import demos.antiSQLInjection.columnImpact.ColumnImpact;
import demos.antiSQLInjection.columnImpact.ColumnImpact.TColumn;

class PlanFileList
{

	public final List<String> planFiles = new ArrayList<String>( );
	private String suffix = ".pln";

	public PlanFileList( String dir )
	{
		File parent = new File( dir );
		if ( parent.exists( ) && parent.isDirectory( ) )
			traversePlanFiles( parent );
	}

	private void traversePlanFiles( File parent )
	{
		File[] files = parent.listFiles( new FileFilter( ) {

			public boolean accept( File file )
			{
				if ( file.getName( ).toLowerCase( ).endsWith( suffix ) )
				{
					planFiles.add( file.getAbsolutePath( ) );
				}

				if ( file.isDirectory( ) )
					return true;

				return false;
			}
		} );

		if ( files != null && files.length > 0 )
		{
			for ( File file : files )
			{
				traversePlanFiles( file );
			}
		}
	}
}

public class PlannerAnalyze
{

	private StringBuffer parseBuffer = new StringBuffer( );
	private String errorMessage;
	private Map<String, Map<String, List<ESqlClause>>> tableColumns = new LinkedHashMap<String, Map<String, List<ESqlClause>>>( );
	private String[] plannerInfos;
	private Map<String, String[]> sourceBuffer = new LinkedHashMap<String, String[]>( );
	private static TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvnetezza );

	public PlannerAnalyze( String plannerFilePath )
	{
		plannerInfos = analyzePlannerFile( plannerFilePath );
		if ( plannerInfos != null && plannerInfos.length == 7 )
		{
			sqlparser.sqltext = plannerInfos[0];
			String planId = new File( plannerFilePath ).getName( )
					.replaceAll( "\\..*", "" );
			plannerInfos[6] = planId;
			impactSQL( planId, sqlparser );
			if ( errorMessage != null )
			{
				errorMessage = "Parse "
						+ plannerFilePath
						+ " failed.\n"
						+ errorMessage;
			}
		}
		else
		{
			errorMessage = "Parse " + plannerFilePath + " failed.";
		}
	}

	private String[] analyzePlannerFile( String plannerFilePath )
	{
		File file = new File( plannerFilePath );
		String content = getContent( file );
		if ( content == null )
			return null;

		String[] info = new String[7];

		Pattern pattern = Pattern.compile( "SQL\\:.*?\\d+\\[\\d+\\]",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher matcher = pattern.matcher( content );
		if ( matcher.find( ) )
		{
			String sql = matcher.group( );
			sql = replaceLast( sql.replaceFirst( "(?i)SQL\\:", "" ),
					"\\d+\\[\\d+\\]",
					"" ).trim( );
			info[0] = sql;
			String leftContent = content.substring( matcher.end( ) );
			info[1] = getStartTime( leftContent );
			info[2] = getEndTime( leftContent );
			String[] totalAndRunTime = getTotalAndRunTime( leftContent );
			if ( totalAndRunTime != null )
			{
				info[3] = totalAndRunTime[1];
				info[4] = totalAndRunTime[0];
			}
			info[5] = plannerFilePath;
			return info;
		}
		return null;
	}

	private String[] getTotalAndRunTime( String content )
	{
		Pattern pattern = Pattern.compile( "Execute\\s+.*",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( content );
		if ( matcher.find( ) )
		{
			String time = matcher.group( ).trim( );
			String temp = replaceLast( time, "\\d+\\.\\d+", "" );
			String secondTime = time.replace( temp, "" ).trim( );
			String temp1 = replaceLast( temp.trim( ), "\\d+\\.\\d+", "" );
			String firstTime = temp.replace( temp1, "" ).trim( );
			return new String[]{
					firstTime, secondTime
			};
		}
		return null;
	}

	private String getEndTime( String content )
	{
		Pattern pattern = Pattern.compile( "Finish\\s+.*",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( content );
		if ( matcher.find( ) )
		{
			String sql = matcher.group( );
			return sql.replaceFirst( "(?i)Finish\\s+", "" ).trim( );
		}
		return null;
	}

	private String getStartTime( String content )
	{
		Pattern pattern = Pattern.compile( "GK\\s+Queue\\s+.*",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( content );
		if ( matcher.find( ) )
		{
			String time = matcher.group( ).replaceFirst( "(?i)GK\\s+Queue\\s+",
					"" );
			return replaceLast( replaceLast( time, "\\d+\\.\\d+", "" ),
					"\\d+\\.\\d+",
					"" ).trim( );
		}
		return null;
	}

	private String replaceLast( String input, String regex, String replacement )
	{
		Pattern pattern = Pattern.compile( regex );
		Matcher matcher = pattern.matcher( input );
		if ( !matcher.find( ) )
		{
			return input;
		}
		int lastMatchStart = 0;
		do
		{
			lastMatchStart = matcher.start( );
		} while ( matcher.find( ) );
		matcher.find( lastMatchStart );
		StringBuffer sb = new StringBuffer( input.length( ) );
		matcher.appendReplacement( sb, replacement );
		matcher.appendTail( sb );
		return sb.toString( );
	}

	private String getContent( File file )
	{
		InputStream is = null;
		ByteArrayOutputStream out = null;
		try
		{
			out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			is = new BufferedInputStream( new FileInputStream( file ) );
			while ( true )
			{
				int r = is.read( tmp );
				if ( r == -1 )
					break;
				out.write( tmp, 0, r );
			}
			byte[] bytes = out.toByteArray( );
			out.close( );
			String content = new String( bytes );
			return content.trim( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
			if ( out != null )
			{
				try
				{
					out.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}
		return null;
	}

	private void impactSQL( String planId, TGSqlParser sqlparser )
	{
		parseBuffer = new StringBuffer( );
		tableColumns.clear( );
		errorMessage = null;
		int ret = sqlparser.parse( );

		if ( ret != 0 )
		{
			errorMessage = sqlparser.getErrormessage( );
		}
		else
		{
			TStatementList stmts = sqlparser.sqlstatements;
			for ( int i = 0; i < stmts.size( ); i++ )
			{
				TCustomSqlStatement stmt = stmts.get( i );
				impactStatement( stmt );
			}

			ColumnImpact impact = null;

			if ( sqlparser.sqlfilename != null
					&& sqlparser.sqlfilename.trim( ).length( ) > 0 )
				impact = new ColumnImpact( new File( sqlparser.sqlfilename ),
						sqlparser.getDbVendor( ),
						true,
						false );
			else
				impact = new ColumnImpact( sqlparser.sqltext,
						sqlparser.getDbVendor( ),
						true,
						false );
			impact.impactSQL();
			TColumn[] columnInfos = impact.getColumnInfos( );
			for ( int i = 0; i < columnInfos.length; i++ )
			{
				TColumn column = columnInfos[i];
				if ( column == null || column.tableFullNames == null )
					continue;
				for ( int j = 0; j < column.tableFullNames.size( ); j++ )
				{
					String tableName = column.tableFullNames.get( j );
					if ( tableName == null )
						continue;
					if ( tableColumns.containsKey( tableName ) )
					{
						updateClauseType( column, tableName );
					}
					else
					{
						Iterator<String> iter = tableColumns.keySet( )
								.iterator( );
						while ( iter.hasNext( ) )
						{
							updateClauseType( column, iter.next( ) );
						}
					}
				}
			}

			if ( !tableColumns.isEmpty( ) )
			{
				Iterator<String> tableIter = tableColumns.keySet( ).iterator( );
				while ( tableIter.hasNext( ) )
				{
					String tableName = tableIter.next( );
					Map<String, List<ESqlClause>> columns = tableColumns.get( tableName );
					Iterator<String> columnIter = columns.keySet( ).iterator( );
					Set<String> columnSet = new HashSet<String>( );
					StringBuffer columnBuffer = new StringBuffer( );
					while ( columnIter.hasNext( ) )
					{
						String columnName = columnIter.next( );
						if ( !columnSet.contains( columnName ) )
						{
							columnSet.add( columnName );
							if ( columnBuffer.length( ) != 0 )
								columnBuffer.append( " " );
							columnBuffer.append( columnName );
						}
						parseBuffer.append( planId ).append( "," );
						parseBuffer.append( tableName )
								.append( "," )
								.append( columnName )
								.append( "," );
						List<ESqlClause> locations = columns.get( columnName );

						if ( locations.contains( ESqlClause.resultColumn ) )
						{
							parseBuffer.append( 1 ).append( "," );
						}
						else
						{
							parseBuffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.where ) )
						{
							parseBuffer.append( 1 ).append( "," );
						}
						else
						{
							parseBuffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.joinCondition )
								|| locations.contains( ESqlClause.join ) )
						{
							parseBuffer.append( 1 ).append( "," );
						}
						else
						{
							parseBuffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.groupby ) )
						{
							parseBuffer.append( 1 ).append( "," );
						}
						else
						{
							parseBuffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.orderby ) )
						{
							parseBuffer.append( 1 );
						}
						else
						{
							parseBuffer.append( 0 );
						}

						parseBuffer.append( "\n" );
					}
					sourceBuffer.put( tableName, new String[]{
							planId, columnBuffer.toString( )
					} );
				}
			}
		}
	}

	private void updateClauseType( TColumn column, String tableName )
	{
		Map<String, List<ESqlClause>> columns = tableColumns.get( tableName );
		List<ESqlClause> clauses = columns.get( column.columnName );
		if ( clauses != null )
		{
			switch ( column.clauseType )
			{
				case select :
					if ( !clauses.contains( ESqlClause.resultColumn ) )
						clauses.add( ESqlClause.resultColumn );
					break;
				case join :
					if ( !clauses.contains( ESqlClause.joinCondition ) )
						clauses.add( ESqlClause.joinCondition );
					break;
				case orderby :
					if ( !clauses.contains( ESqlClause.orderby ) )
						clauses.add( ESqlClause.orderby );
					break;
				case groupby :
					if ( !clauses.contains( ESqlClause.groupby ) )
						clauses.add( ESqlClause.groupby );
					break;
				case where :
					if ( !clauses.contains( ESqlClause.where ) )
						clauses.add( ESqlClause.where );
					break;
			}
		}
	}

	public String getParseResult( )
	{
		return parseBuffer.toString( );
	}

	public Map<String, String[]> getSourceResult( )
	{
		return sourceBuffer;
	}

	public String getErrorMessage( )
	{
		return errorMessage;
	}

	public String[] getPlannerInfos( )
	{
		return plannerInfos;
	}

	private void impactStatement( TCustomSqlStatement stmt )
	{
		TTableList tables = stmt.tables;
		for ( int i = 0; i < tables.size( ); i++ )
		{
			TTable table = tables.getTable( i );
			if ( table.isBaseTable( ) )
			{
				String tableName = table.getFullName( );

				Map<String, List<ESqlClause>> columnMaps = null;
				if ( !tableColumns.containsKey( tableName ) )
				{
					tableColumns.put( tableName,
							new LinkedHashMap<String, List<ESqlClause>>( ) );
					columnMaps = tableColumns.get( tableName );
				}
				else
				{
					columnMaps = tableColumns.get( tableName );
				}

				TObjectNameList columnNames = table.getLinkedColumns( );
				for ( int j = 0; j < columnNames.size( ); j++ )
				{
					TObjectName columnName = columnNames.getObjectName( j );
					String column = columnName.getColumnNameOnly( );
					if ( column.equals( "*" ) )
						continue;
					List<ESqlClause> columnLocations = null;
					if ( !columnMaps.containsKey( column ) )
					{
						columnMaps.put( column, new ArrayList<ESqlClause>( ) );
						columnLocations = columnMaps.get( column );
					}
					else
					{
						columnLocations = columnMaps.get( column );
					}
					if ( !columnLocations.contains( columnName.getLocation( ) ) )
						columnLocations.add( columnName.getLocation( ) );
				}
			}
		}

		TStatementList stmts = stmt.getStatements( );
		for ( int i = 0; i < stmts.size( ); i++ )
		{
			impactStatement( stmts.get( i ) );
		}
	}

	public static void main( String[] args )
	{
		if ( args.length < 1 )
		{
			System.out.println( "Usage: java -jar PlannerAnalyze.jar <input files directory> [-log] [-debug]" );
			System.out.println( "-log: Option, generate a pa.log file to record the analysis log." );
			System.out.println( "-debug: Option, generate a debug.log file to record the files that doesn't generate any output." );
			return;
		}

		File inputDir = new File( args[0] );

		if ( !inputDir.isDirectory( ) )
		{
			System.out.println( inputDir + " is not a valid directory." );
			return;
		}

		File dir = new File( ".",
				"peak_techniques_output/puredata_optimisation" );
		if ( !dir.exists( ) )
		{
			dir.mkdirs( );
		}

		long time = System.currentTimeMillis( );
		File parsedFile = new File( dir, "parsed_" + time + ".dat" );
		File sourceFile = new File( dir, "mv_source_" + time + ".dat" );

		Map<String, String> errorMap = new LinkedHashMap<String, String>( );
		Map<String, List<String[]>> sourceMap = new TreeMap<String, List<String[]>>( );
		Map<String, String> debugMap = new LinkedHashMap<String, String>( );
		String[] files = new PlanFileList( inputDir.getAbsolutePath( ) ).planFiles.toArray( new String[0] );
		BufferedWriter out = null;

		try
		{
			out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( parsedFile,
					true ) ) );
			out.write( "PLAN_ID,TABLE_NAME,COLUMN_NAME,PROJECTION_FLAG,RESTRICTION_FLAG,JOIN_FLAG,GROUP_BY_FLAG,ORDER_BY_FLAG\n" );

			StringBuffer processInfo = new StringBuffer( );

			for ( int i = 0; i < files.length; i++ )
			{
				PlannerAnalyze analyze = new PlannerAnalyze( files[i] );
				String[] plannerInfos = analyze.getPlannerInfos( );
				if ( analyze.getErrorMessage( ) != null )
				{
					System.out.println( analyze.getErrorMessage( ) + "\n" );
					if ( plannerInfos != null )
					{
						errorMap.put( files[i], plannerInfos[0] );
					}
					else
					{
						errorMap.put( files[i], null );
					}
				}
				else if ( plannerInfos != null )
				{
					processInfo.append( plannerInfos[6]
							+ ","
							+ plannerInfos[1]
							+ ","
							+ plannerInfos[2]
							+ ","
							+ plannerInfos[3]
							+ ","
							+ plannerInfos[4]
							+ ","
							+ plannerInfos[5]
							+ "\n" );

					if ( analyze.getParseResult( ).trim( ).length( ) > 0 )
					{
						out.write( analyze.getParseResult( ).trim( ) );
						out.write( "\n" );
					}
					else
					{
						debugMap.put( plannerInfos[6], plannerInfos[5] );
					}

					Iterator<String> tableIter = analyze.getSourceResult( )
							.keySet( )
							.iterator( );
					while ( tableIter.hasNext( ) )
					{
						String tableName = tableIter.next( );
						if ( !sourceMap.containsKey( tableName ) )
						{
							sourceMap.put( tableName, new ArrayList<String[]>( ) );
						}
						sourceMap.get( tableName )
								.add( analyze.getSourceResult( )
										.get( tableName ) );
					}
				}
			}

			out.write( "\nPLAN_ID,START_TIME,END_TIME,TOTAL_TIME,RUN_TIME,LOG_LOCATION\n" );
			out.write( processInfo.toString( ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		finally
		{
			try
			{
				if ( out != null )
					out.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		out = null;
		try
		{
			out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( sourceFile,
					true ) ) );
			out.write( "PLAN_ID,DATABASE..TABLE_NAME,COLUMN_LIST\n" );
			Iterator<String> tableNameIter = sourceMap.keySet( ).iterator( );
			while ( tableNameIter.hasNext( ) )
			{
				String tableName = tableNameIter.next( );
				if ( tableName.indexOf( '/' ) != -1
						|| tableName.indexOf( '\\' ) != -1 )
					continue;
				List<String[]> infoList = sourceMap.get( tableName );
				for ( int j = 0; j < infoList.size( ); j++ )
				{
					String[] info = infoList.get( j );
					out.write( info[0] + "," + tableName + "," + info[1] + "\n" );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		finally
		{
			try
			{
				if ( out != null )
					out.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}

		List<String> argList = new ArrayList<String>( );
		argList.addAll( Arrays.asList( args ) );
		argList.remove( 0 );
		if ( argList.indexOf( "-log" ) != -1 )
		{
			File logFile = new File( dir, "pa_" + time + ".log" );

			out = null;
			try
			{
				out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( logFile,
						true ) ) );

				out.append( String.valueOf( files.length ) )
						.append( " queries were parsed, " )
						.append( String.valueOf( errorMap.size( ) ) )
						.append( " were failed.\n\n" );

				Iterator<String> fileIter = errorMap.keySet( ).iterator( );
				while ( fileIter.hasNext( ) )
				{
					String file = fileIter.next( );
					String sql = errorMap.get( file );
					if ( sql == null )
					{
						out.append( file + " : " )
								.append( "Parse pln file failed.\n\n" );
					}
					else
					{
						out.append( file + " : " )
								.append( "Parse sql failed.\n" );
						out.append( "SQL : " ).append( sql ).append( "\n\n" );
					}
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
			finally
			{
				try
				{
					if ( out != null )
						out.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}

		if ( argList.indexOf( "-debug" ) != -1 )
		{
			File logFile = new File( dir, "debug_" + time + ".log" );

			out = null;
			try
			{
				out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( logFile,
						true ) ) );

				out.write( "PLAN_ID,LOG_LOCATION\n" );

				Iterator<String> fileIter = debugMap.keySet( ).iterator( );
				while ( fileIter.hasNext( ) )
				{
					String planId = fileIter.next( );
					String file = debugMap.get( planId );
					out.append( planId )
							.append( "," )
							.append( file )
							.append( "\n" );
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
			finally
			{
				try
				{
					if ( out != null )
						out.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}
	}
}
