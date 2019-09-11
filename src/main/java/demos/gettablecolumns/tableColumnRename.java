
package demos.gettablecolumns;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class tableColumnRename
{

	TGSqlParser sqlparser;
	public String msg;
	public int renamedObjectsNum = 0;
	private String sourceTable, targetTable, sourceColumn, targetColumn,
			sourceSchema, targetSchema;
	private Map<String, List<String>> metaInfo;

	public String getModifiedText( )
	{
		StringBuilder sb = new StringBuilder( 1024 );
		for ( int i = 0; i < sqlparser.sourcetokenlist.size( ); i++ )
		{
			sb.append( sqlparser.sourcetokenlist.get( i ).astext );
		}

		return sb.toString( );
	}

	private class metaDB implements IMetaDatabase
	{

		@Override
		public boolean checkColumn( String pServer, String pDatabase,
				String pSchema, String pTable, String pColumn )
		{
			String tablePrefix = pTable;
			if ( pSchema != null && pSchema.length( ) > 0 )
			{
				tablePrefix = pSchema + "." + tablePrefix;
			}
			if ( pDatabase != null && pDatabase.length( ) > 0 )
			{
				tablePrefix = pDatabase + "." + tablePrefix;
			}
			if ( pServer != null && pServer.length( ) > 0 )
			{
				tablePrefix = pServer + "." + tablePrefix;
			}
			return metaInfo.containsKey( tablePrefix.trim( ).toLowerCase( ) )
					&& metaInfo.get( tablePrefix.toLowerCase( ) )
							.contains( pColumn.trim( ).toLowerCase( ) );
		}
	}

	public tableColumnRename( EDbVendor vendor, String sqltext,
			Map<String, List<String>> metaInfo )
	{
		sqlparser = new TGSqlParser( vendor );
		sqlparser.sqltext = sqltext;
		this.metaInfo = metaInfo;
		if ( metaInfo != null )
		{
			sqlparser.setMetaDatabase( new metaDB( ) );
		}
	}

	public int renameColumn( String sourceColumn, String targetColumn )
	{
		String[] names = sourceColumn.split( "\\." );
		if ( names.length == 2 )
		{
			this.sourceSchema = null;
			this.sourceTable = names[0];
			this.sourceColumn = names[1];
		}
		else if ( names.length == 3 )
		{
			this.sourceSchema = names[0];
			this.sourceTable = names[1];
			this.sourceColumn = names[2];
		}
		else
		{
			this.msg = "source column name must in syntax like this: schema.tablename.column or tablename.column";
			return -1;
		}

		this.targetColumn = targetColumn;
		names = targetColumn.split( "\\." );
		if ( names.length != 1 )
		{
			this.msg = "target column name must in syntax like this: column";
			return -1;
		}

		int ret = sqlparser.parse( );
		if ( ret != 0 )
		{
			msg = "syntax error: " + sqlparser.getErrormessage( );
			return -1;
		}

		for ( int i = 0; i < sqlparser.getSqlstatements( ).size( ); i++ )
		{
			TCustomSqlStatement sql = sqlparser.getSqlstatements( ).get( i );
			modifyColumnName( sql );
		}

		this.msg = "renamed column occurs:" + this.renamedObjectsNum;
		return renamedObjectsNum;
	}

	private void modifyTableName( TCustomSqlStatement stmt )
	{

		for ( int k = 0; k < stmt.tables.size( ); k++ )
		{
			TTable table = stmt.tables.getTable( k );

			boolean isfound = false;
			for ( int m = 0; m < table.getLinkedColumns( ).size( ); m++ )
			{
				TObjectName column = table.getLinkedColumns( )
						.getObjectName( m );
				isfound = false;
				if ( column.getTableString( ) != null
						&& column.getTableString( )
								.equalsIgnoreCase( this.sourceTable ) )
				{
					isfound = true;
					if ( this.sourceSchema != null )
					{ // check schema of this
						// table
						if ( column.getSchemaString( ) != null )
						{
							isfound = this.sourceSchema.equalsIgnoreCase( column.getSchemaString( ) );
						}
						else
						{
							isfound = false;
						}
					}
				}

				if ( isfound )
				{
					// rename this table
					TSourceToken st = column.getColumnToken( );
					if ( column.getTableString( ) != null )
					{
						column.getTableToken( ).setString( this.targetTable );
					}
					else
					{
						st.setString( this.targetTable + "." + st.astext );
					}

					if ( this.targetSchema != null )
					{
						if ( column.getSchemaString( ) != null )
						{
							column.getSchemaToken( )
									.setString( this.targetSchema );
						}
						else if ( column.getTableString( ) != null )
						{
							column.getTableToken( )
									.setString( this.targetSchema
											+ "."
											+ column.getTableString( ) );
						}
						else
						{
							st.setString( this.targetSchema + "." + st.astext );
						}
					}
					else
					{
						if ( column.getSchemaString( ) != null )
						{
							column.setString( column.toString( )
									.replaceFirst( Pattern.quote( column.getSchemaString( )
											+ "." ),
											"" ) );
						}
					}

					this.renamedObjectsNum++;
				}
			}

			if ( table.getTableName( ) != null
					&& table.getTableName( ).getTableString( ) != null
					&& table.getTableName( )
							.getTableString( )
							.equalsIgnoreCase( this.sourceTable ) )
			{
				isfound = true;
				if ( this.sourceSchema != null )
				{ // check schema of this table
					if ( table.getTableName( ).getSchemaString( ) != null )
					{
						isfound = this.sourceSchema.equalsIgnoreCase( table.getTableName( )
								.getSchemaString( ) );
					}
					else
					{
						isfound = false;
					}
				}
			}

			if ( isfound )
			{
				if ( table.getTableName( ) != null )
				{
					// rename this table
					TSourceToken st = table.getTableName( ).getTableToken( );
					st.setString( this.targetTable );
					if ( this.targetSchema != null )
					{
						if ( table.getPrefixSchema( ) != null )
						{
							table.getTableName( )
									.getSchemaToken( )
									.setString( this.targetSchema );
						}
						else
						{
							st.setString( this.targetSchema + "." + st.astext );
						}
					}
					else
					{
						if ( table.getPrefixSchema( ) != null )
						{
							table.setString( table.toString( )
									.replaceFirst( Pattern.quote( table.getTableName( )
											.getSchemaString( )
											+ "." ),
											"" ) );
						}
					}
					this.renamedObjectsNum++;
				}
			}
		}

		for ( int j = 0; j < stmt.getStatements( ).size( ); j++ )
		{
			modifyTableName( stmt.getStatements( ).get( j ) );
		}
	}
	
	private void removeSchema( TCustomSqlStatement stmt )
	{
		for ( int k = 0; k < stmt.tables.size( ); k++ )
		{
			TTable table = stmt.tables.getTable( k );
			for ( int m = 0; m < table.getLinkedColumns( ).size( ); m++ )
			{
				TObjectName column = table.getLinkedColumns( )
						.getObjectName( m );
				if ( column.getSchemaString( ) != null
						&& column.getSchemaString( )
								.equalsIgnoreCase( this.sourceSchema ) )
				{
					column.setString( column.toString( )
							.replaceFirst( Pattern.quote( column.getSchemaString( )
									+ "." ),
									"" ) );

					this.renamedObjectsNum++;
				}
			}

			if ( table.getTableName( )!=null 
					&& table.getTableName( ).getSchemaString( ) != null
					&& table.getTableName( ).getSchemaString( )
							.equalsIgnoreCase( this.sourceSchema ) )
			{
				table.setString( table.toString( )
						.replaceFirst( Pattern.quote( table.getTableName( )
								.getSchemaString( )
								+ "." ),
								"" ) );
						
				this.renamedObjectsNum++;
			}
		}

		for ( int j = 0; j < stmt.getStatements( ).size( ); j++ )
		{
			removeSchema( stmt.getStatements( ).get( j ) );
		}
	}

	private void modifyColumnName( TCustomSqlStatement stmt )
	{

		for ( int k = 0; k < stmt.tables.size( ); k++ )
		{
			TTable table = stmt.tables.getTable( k );

			if ( table.getTableName( ) == null )
				continue;

			boolean isThisTable = true;

			isThisTable = table.getTableName( )
					.getTableString( )
					.equalsIgnoreCase( this.sourceTable );
			if ( !isThisTable )
			{
				continue;
			}

			if ( this.sourceSchema != null && table.getTableName( ) != null )
			{
				if ( table.getTableName( ).getSchemaString( ) != null )
				{
					isThisTable = table.getTableName( )
							.getSchemaString( )
							.equalsIgnoreCase( this.sourceSchema );
				}
				else
				{
					isThisTable = false;
				}
			}

			if ( !isThisTable )
			{
				continue;
			}

			for ( int m = 0; m < table.getLinkedColumns( ).size( ); m++ )
			{
				TObjectName column = table.getLinkedColumns( )
						.getObjectName( m );
				if ( column.getColumnToken( ) != null )
				{
					if ( column.getColumnNameOnly( )
							.equalsIgnoreCase( this.sourceColumn ) )
					{
						column.getColumnToken( ).setString( this.targetColumn );
						this.renamedObjectsNum++;
					}
				}
			}
		}

		for ( int j = 0; j < stmt.getStatements( ).size( ); j++ )
		{
			modifyColumnName( stmt.getStatements( ).get( j ) );
		}
	}

	public int removeSchema( String schemaName )
	{
		this.renamedObjectsNum = 0;
		String[] names = schemaName.split( "\\." );
		if ( names.length == 1 )
		{
			this.sourceSchema = schemaName;
		}
		else
		{
			this.msg = "schame name could not contain '.'";
			return -1;
		}

		int ret = sqlparser.parse( );
		if ( ret != 0 )
		{
			msg = "syntax error: " + sqlparser.getErrormessage( );
			return -1;
		}

		for ( int i = 0; i < sqlparser.getSqlstatements( ).size( ); i++ )
		{
			TCustomSqlStatement sql = sqlparser.getSqlstatements( ).get( i );
			removeSchema( sql );
		}

		this.msg = "removed schema occurs:" + this.renamedObjectsNum;

		return renamedObjectsNum;

	}
	
	public int renameTable( String sourceTable, String targetTable )
	{
		this.renamedObjectsNum = 0;
		String[] names = sourceTable.split( "\\." );
		if ( names.length == 1 )
		{
			this.sourceTable = sourceTable;
			this.sourceSchema = null;
		}
		else if ( names.length == 2 )
		{
			this.sourceSchema = names[0];
			this.sourceTable = names[1];
		}
		else
		{
			this.msg = "source table name must in syntax like this: schema.tablename, or tablename";
			return -1;
		}

		names = targetTable.split( "\\." );

		if ( names.length == 1 )
		{
			this.targetTable = targetTable;
			this.targetSchema = null;
		}
		else if ( names.length == 2 )
		{
			this.targetSchema = names[0];
			this.targetTable = names[1];
		}
		else
		{
			this.msg = "target table name must in syntax like this: schema.tablename, or tablename";
			return -1;
		}

		int ret = sqlparser.parse( );
		if ( ret != 0 )
		{
			msg = "syntax error: " + sqlparser.getErrormessage( );
			return -1;
		}

		for ( int i = 0; i < sqlparser.getSqlstatements( ).size( ); i++ )
		{
			TCustomSqlStatement sql = sqlparser.getSqlstatements( ).get( i );
			modifyTableName( sql );
		}

		this.msg = "renamed table occurs:" + this.renamedObjectsNum;

		return renamedObjectsNum;

	}

	public static void main( String[] args )
	{
		String text = "CREATE PROCEDURE [dbo].[Testprocedure_2]\n"
				+ "        @BusinessID NVARCHAR(100)\n"
				+ " AS\n"
				+ " BEGIN\n"
				+ "   SET NOCOUNT  ON;\n"
				+

				"   SELECT dbo.tb_Rentals.*,\n"
				+ "          MinimalRentalID,\n"
				+ "          SEA.Name,\n"
				+ "          SEA.BeginDay,\n"
				+ "          SEA.EndDay,\n"
				+ "          dbo.tb_RentalTypes.Name AS TypeName\n"
				+ "   FROM   dbo.tb_Rentals,\n"
				+ "          dbo.tb_Seasons SEA,\n"
				+ "          dbo.tb_RentalTypes,\n"
				+ "          dbo.tb_RentalToSeason\n"
				+ "   WHERE  dbo.tb_Rentals.BusinessID_XXX = SEA.BusinessID \n"
				+ "          AND dbo.tb_Rentals.RentalTypeID = dbo.tb_RentalTypes.RentalTypeID\n"
				+ "          AND dbo.tb_RentalToSeason.RentalID = dbo.tb_Rentals.RentalID\n"
				+ "          AND dbo.tb_RentalToSeason.SeasonID = SEA.SeasonID\n"
				+ "          AND dbo.tb_Rentals.BusinessID = @BusinessID \n"
				+ "          AND @BusinessID IN (SELECT DISTINCT dbo.tb_Rentals.BusinessID_XXX\n"
				+ "                              FROM   dbo.tb_Rentals\n"
				+ "                              WHERE  dbo.tb_Rentals.BusinessID = @BusinessID)\n"
				+ " END";
		Map<String, List<String>> metaInfo = new HashMap<String, List<String>>( );
		metaInfo.put( "dbo.tb_Seasons".toLowerCase( ),
				Arrays.asList( new String[]{
					"MinimalRentalID".toLowerCase( )
				} ) );
		tableColumnRename ro = new tableColumnRename( EDbVendor.dbvmssql,
				text,
				metaInfo );
		int ret = ro.renameColumn( "dbo.tb_Seasons.MinimalRentalID",
				"MinimalRentalID_xx" );
		System.out.println( "Message: " + ro.msg );
		if ( ret > 0 )
		{
			System.out.println( "Result: " );
			System.out.println( ro.getModifiedText( ) );
		}

	}


}