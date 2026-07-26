
package demos.scantable;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class scantable
{

	private Map<String, Map<String, Set<String>>> objectMap = new HashMap<String, Map<String, Set<String>>>( );
	private StringBuffer result = new StringBuffer( );

	public scantable( File file, EDbVendor vendor )
	{
		TGSqlParser sqlparser = new TGSqlParser( vendor );
		sqlparser.sqlfilename = file.getAbsolutePath( );
		sqlparser.parse( );
		int ret = sqlparser.parse( );
		if ( ret == 0 )
		{
			for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
			{
				analyzeStmt( sqlparser.sqlstatements.get( i ) );
			}

			Iterator<String> tableIter = objectMap.keySet( ).iterator( );
			while ( tableIter.hasNext( ) )
			{
				String table = tableIter.next( );
				result.append( "Table: " ).append( table ).append( "\r\n" );
				Map<String, Set<String>> columnMap = objectMap.get( table );
				if ( !columnMap.isEmpty( ) )
				{
					SortedSet<String> keys = new TreeSet<String>( columnMap.keySet( ) );
					Iterator<String> clauseIter = keys.iterator( );
					while ( clauseIter.hasNext( ) )
					{
						String clause = clauseIter.next( );
						Set<String> columnSet = columnMap.get( clause );
						if ( !columnSet.isEmpty( ) )
						{
							result.append( clause ).append( ": " );
							String[] columns = columnSet.toArray( new String[0] );
							for ( int i = 0; i < columns.length; i++ )
							{
								if ( i < columns.length - 1 )
								{
									result.append( columns[i].toString( ) )
											.append( ", " );
								}
								else
								{
									result.append( columns[i].toString( ) );
								}
							}
							result.append( "\r\n" );
						}
					}
				}
				result.append( "\r\n" );
			}
		}
		else
		{
			result.append( sqlparser.getErrormessage( ) + "\n" );
		}

	}

	public static void main( String[] args )
	{
		if ( args.length == 0 )
		{
			System.out.println( "Usage: java scantable scriptfile [/o <output file path>] [/t <database type>]" );
			System.out.println( "/o: Option, write the output stream to the specified file." );
			System.out.println( "/t: Option, set the database type. Support oracle, mysql, mssql and db2, the default type is mssql." );
			return;
		}

		List<String> argList = Arrays.asList( args );

		String outputFile = null;

		int index = argList.indexOf( "/o" );

		if ( index != -1 && args.length > index + 1 )
		{
			outputFile = args[index + 1];
		}

		FileOutputStream writer = null;
		if ( outputFile != null )
		{
			try
			{
				writer = new FileOutputStream( outputFile );
				System.setOut( new PrintStream( writer ) );
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace( );
			}
		}

		EDbVendor vendor = EDbVendor.dbvmssql;

		index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);

		}

		scantable scan = new scantable( new File( args[0] ), vendor );

		System.out.print( scan.getScanResult( ) );

		try
		{
			if ( writer != null )
			{
				writer.close( );
			}

		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

	} // main

	public String getScanResult( )
	{
		return result.toString( );
	}

	protected void analyzeStmt( TCustomSqlStatement stmt )
	{
		for ( int i = 0; i < stmt.tables.size( ); i++ )
		{
			TTable table = stmt.tables.getTable( i );
			if ( table.isBaseTable( ) )
			{
				String tableName = table.getFullName( );
				if ( !objectMap.containsKey( tableName ) )
				{
					objectMap.put( tableName,
							new HashMap<String, Set<String>>( ) );
				}

				if ( table.getLinkedColumns( ) != null )
				{
					Map<String, Set<String>> columnMap = objectMap.get( tableName );
					for ( int j = 0; j < table.getLinkedColumns( )
							.size( ); j++ )
					{
						TObjectName objectName = table.getLinkedColumns( )
								.getObjectName( j );
						ESqlClause clause = objectName.getLocation( );
						String clauseString = clause.toString( );
						if ( clause == ESqlClause.set
								&& ( stmt instanceof TUpdateSqlStatement ) )
						{
							TUpdateSqlStatement update = (TUpdateSqlStatement) stmt;
							if ( update.getTargetTable( ) == table )
							{
								clauseString = "update " + clause.toString( );
							}
							else
							{
								clauseString = "select " + clause.toString( );
							}
						}
						if ( !columnMap.containsKey( clauseString ) )
						{
							columnMap.put( clauseString,
									new LinkedHashSet<String>( ) );
						}

						Set<String> columns = columnMap.get( clauseString );
						columns.add( objectName.getColumnNameOnly( ).toString( ) );
					}
				}
			}
		}

		for ( int i = 0; i < stmt.getStatements( ).size( ); i++ )
		{
			analyzeStmt( stmt.getStatements( ).get( i ) );
		}
	}
}
