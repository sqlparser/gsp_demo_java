
package demos.gettablecolumns;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

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

	private boolean parseSuccess = false;

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
		if ( metaInfo != null )
		{
			sqlparser.setMetaDatabase( new metaDB( ) );
		}
		this.metaInfo = metaInfo;
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

		if(!parseSuccess){
			int ret = sqlparser.parse( );
			if ( ret != 0 )
			{
				msg = "syntax error: " + sqlparser.getErrormessage( );
				return -1;
			}
			parseSuccess = true;
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
					column.getSchemaToken().setString("");
					if (column.getSchemaToken().getNextTokenInChain().astext.equals(".")){
						column.getSchemaToken().getNextTokenInChain().setString("");
					}

					this.renamedObjectsNum++;
				}
			}

			if ( table.getTableName( )!=null 
					&& table.getTableName( ).getSchemaString( ) != null
					&& table.getTableName( ).getSchemaString( )
							.equalsIgnoreCase( this.sourceSchema ) )
			{
				table.getTableName().getSchemaToken().setString("");
				if (table.getTableName().getSchemaToken().getNextTokenInChain().astext.equals(".")){
					table.getTableName().getSchemaToken().getNextTokenInChain().setString("");
				}
						
				this.renamedObjectsNum++;
			}
		}

		for ( int j = 0; j < stmt.getStatements( ).size( ); j++ )
		{
			removeSchema( stmt.getStatements( ).get( j ) );
		}
	}

	private boolean checkSubQuery(TSelectSqlStatement subquery){
		boolean isThisTable = false;
		for ( int i = 0; i < subquery.tables.size( ); i++ ){
			TTable subTable = subquery.tables.getTable( i );
			if ( subTable.getTableName( ) == null )
				continue;
			if(ETableSource.subquery.equals(subTable.getTableType()) && subTable.getSubquery() != null){
				isThisTable = checkSubQuery(subTable.getSubquery());
			}
			else{
				isThisTable = subTable.getTableName( ).getTableString( ).equalsIgnoreCase( this.sourceTable );
				if(isThisTable){
					boolean isThisColumn = false;
					for ( int m = 0; m < subTable.getLinkedColumns( ).size( ); m++ ) {
						TObjectName column = subTable.getLinkedColumns( ).getObjectName( m );
						if ( column.getColumnToken( ) != null ) {
							if ( column.getColumnNameOnly( ).equalsIgnoreCase( this.sourceColumn ) ) {
								isThisColumn = true;
								break;
							}
						}
					}
					isThisTable = isThisColumn;
				}
			}
		}
		return isThisTable;
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
				if(ETableSource.subquery.equals(table.getTableType()) && table.getSubquery() != null){
					isThisTable = checkSubQuery(table.getSubquery());
				}
			}
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

		if(!parseSuccess){
			int ret = sqlparser.parse( );
			if ( ret != 0 )
			{
				msg = "syntax error: " + sqlparser.getErrormessage( );
				return -1;
			}
			parseSuccess = true;
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
//		String text = "SELECT SUM(product_price * product_quantity) as total_revenue\n" +
//				"FROM (\n" +
//				"  SELECT product_price, product_quantity\n" +
//				"  FROM public.sales_information\n" +
//				"  ORDER BY product_quantity DESC\n" +
//				"  LIMIT 2\n" +
//				") as top_products;";
		String text ="select  public.tablea.col1 from public.tablea";
		Map<String, List<String>> metaInfo = new HashMap<String, List<String>>( );
		metaInfo.put( "sales_information".toLowerCase( ),
				Arrays.asList( new String[]{
					"product_price".toLowerCase( )
				} ) );
		tableColumnRename ro = new tableColumnRename( EDbVendor.dbvpostgresql,
				text,
				metaInfo );
		ro.removeSchema("public");
		int ret = ro.renameColumn( "sales_information.product_price",
				"price" );
		System.out.println( "Message: " + ro.msg );

		System.out.println( "Result: " );
		System.out.println( ro.getModifiedText( ) );

	}


}