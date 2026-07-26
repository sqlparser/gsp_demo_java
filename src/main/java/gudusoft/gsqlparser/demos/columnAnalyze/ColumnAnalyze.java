
package demos.columnAnalyze;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import demos.antiSQLInjection.columnImpact.ColumnImpact;
import demos.antiSQLInjection.columnImpact.ColumnImpact.TColumn;

public class ColumnAnalyze
{

	private StringBuffer buffer = new StringBuffer( );
	private String errorMessage;
	private Map<String, Map<String, List<ESqlClause>>> tableColumns = new LinkedHashMap<String, Map<String, List<ESqlClause>>>( );

	public ColumnAnalyze( File file )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
		sqlparser.sqlfilename = file.getAbsolutePath( );
		impactSQL( sqlparser );
	}

	public ColumnAnalyze( String sql )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvnetezza );
		sqlparser.sqltext = sql;
		impactSQL( sqlparser );
	}

	private void impactSQL( TGSqlParser sqlparser )
	{
		buffer = new StringBuffer( );
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

			if ( sqlparser.sqlfilename != null )
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
				for ( int j = 0; j < column.tableNames.size( ); j++ )
				{
					String tableName = column.tableNames.get( j ).toUpperCase( );
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
				buffer.append( "TABLE_NAME,COLUMN_NAME,PROJECTION_FLAG,RESTRICTION_FLAG,JOIN_FLAG,GROUP_BY_FLAG,ORDER_BY_FLAG\n" );
				Iterator<String> tableIter = tableColumns.keySet( ).iterator( );
				while ( tableIter.hasNext( ) )
				{
					String tableName = tableIter.next( );
					Map<String, List<ESqlClause>> columns = tableColumns.get( tableName );
					Iterator<String> columnIter = columns.keySet( ).iterator( );
					while ( columnIter.hasNext( ) )
					{
						String columnName = columnIter.next( );
						buffer.append( tableName )
								.append( "," )
								.append( columnName )
								.append( "," );
						List<ESqlClause> locations = columns.get( columnName );

						if ( locations.contains( ESqlClause.resultColumn ) )
						{
							buffer.append( 1 ).append( "," );
						}
						else
						{
							buffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.where ) )
						{
							buffer.append( 1 ).append( "," );
						}
						else
						{
							buffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.joinCondition )
								|| locations.contains( ESqlClause.join ) )
						{
							buffer.append( 1 ).append( "," );
						}
						else
						{
							buffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.groupby ) )
						{
							buffer.append( 1 ).append( "," );
						}
						else
						{
							buffer.append( 0 ).append( "," );
						}

						if ( locations.contains( ESqlClause.orderby ) )
						{
							buffer.append( 1 );
						}
						else
						{
							buffer.append( 0 );
						}
						
						buffer.append( "\n" );
					}
				}
			}
		}
	}

	private void updateClauseType( TColumn column, String tableName )
	{
		Map<String, List<ESqlClause>> columns = tableColumns.get( tableName );
		List<ESqlClause> clauses = columns.get( column.columnName.toUpperCase( ) );
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

	public String getResult( )
	{
		return buffer.toString( );
	}

	public String getErrorMessage( )
	{
		return errorMessage;
	}

	private void impactStatement( TCustomSqlStatement stmt )
	{
		TTableList tables = stmt.tables;
		for ( int i = 0; i < tables.size( ); i++ )
		{
			TTable table = tables.getTable( i );
			if ( table.isBaseTable( ) )
			{
				String tableName = table.getTableName( )
						.toString( )
						.toUpperCase( );

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
					String column = columnName.getColumnNameOnly( )
							.toUpperCase( );
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
		if ( args.length < 2 )
		{
			System.out.println( "Usage: java ColumnAnalyze <input files directory> <output files directory>" );
			return;
		}

		File inputDir = new File( args[0] );
		File outputDir = new File( args[1] );

		if ( !inputDir.isDirectory( ) )
		{
			System.out.println( inputDir + " is not a valid directory." );
			return;
		}

		if ( outputDir.isFile( ) )
		{
			System.out.println( outputDir + " is not a valid directory." );
			return;
		}
		else if ( !outputDir.exists( ) && !outputDir.mkdirs( ) )
		{
			System.out.println( outputDir + " is not a valid directory." );
			return;
		}

		File[] files = inputDir.listFiles( );
		for ( int i = 0; i < files.length; i++ )
		{
			if ( files[i].isFile( ) )
			{
				ColumnAnalyze analyze = new ColumnAnalyze( files[i] );
				if ( analyze.getErrorMessage( ) != null )
				{
					System.out.println( analyze.getErrorMessage( ) );
				}
				else
				{
					int index = files[i].getName( ).lastIndexOf( '.' );
					String outputFileName;
					if ( index == -1 )
					{
						outputFileName = files[i].getName( ) + ".txt";
					}
					else
					{
						outputFileName = files[i].getName( ).substring( 0,
								index )
								+ ".txt";
					}
					try
					{
						FileOutputStream fos = new FileOutputStream( new File( outputDir,
								outputFileName ) );
						fos.write( analyze.getResult( ).getBytes( ) );
						fos.close( );
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}
		}
	}
}
