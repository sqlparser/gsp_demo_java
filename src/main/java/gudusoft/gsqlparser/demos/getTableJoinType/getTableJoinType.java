
package demos.getTableJoinType;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EJoinType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class getTableJoinType
{

	public static void main( String args[] )
	{
		if ( args.length != 1 )
		{
			System.out.println( "Usage: java getTableJoinType sqlfile.sql" );
			return;
		}
		File file = new File( args[0] );
		if ( !file.exists( ) )
		{
			System.out.println( "File not exists:" + args[0] );
			return;
		}

		EDbVendor dbVendor = EDbVendor.dbvoracle;
		String msg = "Please select SQL dialect: 1: SQL Server, 2: Oracle, 3: MySQL, 4: DB2, 5: PostGRESQL, 6: Teradata, default is 2: Oracle";
		System.out.println( msg );

		BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
		try
		{
			int db = Integer.parseInt( br.readLine( ) );
			if ( db == 1 )
			{
				dbVendor = EDbVendor.dbvmssql;
			}
			else if ( db == 2 )
			{
				dbVendor = EDbVendor.dbvoracle;
			}
			else if ( db == 3 )
			{
				dbVendor = EDbVendor.dbvmysql;
			}
			else if ( db == 4 )
			{
				dbVendor = EDbVendor.dbvdb2;
			}
			else if ( db == 5 )
			{
				dbVendor = EDbVendor.dbvpostgresql;
			}
			else if ( db == 6 )
			{
				dbVendor = EDbVendor.dbvteradata;
			}
		}
		catch ( IOException i )
		{
		}
		catch ( NumberFormatException numberFormatException )
		{
		}

		System.out.println( "Selected SQL dialect: " + dbVendor.toString( ) );

		new getTableJoinType( file, dbVendor );
	}

	public getTableJoinType( File file, EDbVendor dbVendor )
	{
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqlfilename = file.getAbsolutePath( );
		int ret = sqlparser.parse( );
		if ( ret != 0 )
		{
			System.err.println( sqlparser.getErrormessage( ) );
		}
		else
		{
			System.out.println( "Join Type table Name" );
			System.out.println( "---------------------------------------" );
			for ( int k = 0; k < sqlparser.sqlstatements.size( ); k++ )
			{
				if ( sqlparser.sqlstatements.get( k ) instanceof TCustomSqlStatement )
				{
					analyzeStatement( (TCustomSqlStatement) sqlparser.sqlstatements.get( k ) );
				}
			}
		}
	}

	public getTableJoinType( String sql, EDbVendor dbVendor )
	{
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqltext = sql;
		int ret = sqlparser.parse( );
		if ( ret != 0 )
		{
			System.err.println( sqlparser.getErrormessage( ) );
		}
		else
		{
			System.out.println( "Join Type table Name" );
			System.out.println( "---------------------------------------" );
			for ( int k = 0; k < sqlparser.sqlstatements.size( ); k++ )
			{
				if ( sqlparser.sqlstatements.get( k ) instanceof TCustomSqlStatement )
				{
					analyzeStatement( (TCustomSqlStatement) sqlparser.sqlstatements.get( k ) );
				}
			}
		}
	}

	public void analyzeStatement( TCustomSqlStatement stmt )
	{
		if ( stmt.joins != null )
		{
			for ( int i = 0; i < stmt.joins.size( ); i++ )
			{
				TJoin join = stmt.joins.getJoin( i );
				if ( join.getJoinItems( ) != null )
				{
					for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
					{
						TJoinItem joinItem = join.getJoinItems( )
								.getJoinItem( j );
						if ( joinItem.getTable( ).getFullName( ) != null )
						{
							System.out.println( valueOf( joinItem.getJoinType( ) )
									+ "\t"
									+ joinItem.getTable( ).getFullName( ) );
						}
						else
						{
							System.out.println( valueOf( joinItem.getJoinType( ) )
									+ "\t"
									+ joinItem.getTable( ).getAliasName( ) );
						}
					}
				}
			}
		}
		if ( stmt.getStatements( ) != null )
		{
			for ( int i = 0, length = stmt.getStatements( ).size( ); i < length; i++ )
			{
				analyzeStatement( stmt.getStatements( ).get( i ) );
			}
		}
	}

	private String valueOf( EJoinType joinType )
	{
		switch ( joinType )
		{
			case cross :
				return "Cross Join";
			case crossapply :
				return "Cross Apply";
			case full :
				return "Full Join";
			case fullouter :
				return "Full Outer Join";
			case inner :
				return "Inner Join";
			case join :
				return "Inner Join";
			case left :
				return "Left Join";
			case leftouter :
				return "Left Outer Join";
			case leftsemi :
				return "Left Semi Join";
			case natural :
				return "Natural Join";
			case natural_full :
				return "Natural Full Join";
			case natural_fullouter :
				return "Natural Full Outer Join";
			case natural_inner :
				return "Natural Inner Join";
			case natural_left :
				return "Natural Left Join";
			case natural_leftouter :
				return "Natural Left Outer Join";
			case natural_right :
				return "Natural Right Join";
			case natural_rightouter :
				return "Natural Right Outer Join";
			case nested :
				return "Nested Join";
			case outerapply :
				return "Outer Apply";
			case right :
				return "Right Join";
			case rightouter :
				return "Right Outer Join";
			case straight :
				return "Straight Join";
			case union :
				return "Union Join";
			default :
				break;
		}
		return "";
	}
}
