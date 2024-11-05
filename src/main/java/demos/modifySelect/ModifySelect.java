
package demos.modifySelect;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.HashMap;
import java.util.Map;

public class ModifySelect
{

	class Table
	{

		public String tableName;
		public String tableAlias;
	}

	public ModifySelect( )
	{

		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmssql );
		sqlparser.sqltext = "SELECT A.COLUMN1, B.COLUMN2 from TABLE1 A, TABLE2 B where A.COLUMN1=B.COLUMN1";
		System.out.println( "Original SQL:" );
		System.out.println( sqlparser.sqltext );

		String newColumn1 = "table1.newcolumn";
		String newColumn2 = "table2.newcolumn";

		sqlparser.parse( );

		TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements.get( 0 );

		TTableList tables = select.tables;

		Map<String, Table> tableMap = new HashMap<String, Table>( );

		for ( int i = 0; i < tables.size( ); i++ )
		{
			TTable table = tables.getTable( i );
			Table model = new Table( );
			if ( table.getName( ) != null )
			{
				model.tableName = table.getName( );
				tableMap.put( model.tableName.toLowerCase( ), model );
			}
			if ( table.getAliasClause( ) != null )
				model.tableAlias = table.getAliasClause( )
						.getAliasName( )
						.toString( );
		}

		TExpression whereCondition = select.getWhereClause( ).getCondition( );
		whereCondition.setString( whereCondition.toString( )
				+ " AND "
				+ getAliasColumn( newColumn1, tableMap )
				+ "="
				+ getAliasColumn( newColumn2, tableMap ) );

		whereCondition.setString( whereCondition.toString( )
				+ " AND "
				+ getAliasColumn( newColumn1, tableMap )
				+ "=?" );

		whereCondition.setString( whereCondition.toString( )
				+ " AND "
				+ getAliasColumn( newColumn2, tableMap )
				+ "=?" );

		select.getResultColumnList( )
				.addResultColumn( getAliasColumn( newColumn1, tableMap ) );
		select.getResultColumnList( )
				.addResultColumn( getAliasColumn( newColumn2, tableMap ) );

		System.out.println( "Modified SQL:" );
		System.out.println( select.toString( ) );

	}

	private String getAliasColumn( String tableColumn,
			Map<String, Table> tableMap )
	{
		String[] splits = tableColumn.trim( ).split( "\\." );
		if ( splits.length == 2 )
		{
			if ( tableMap.containsKey( splits[0].toLowerCase( ) ) )
			{
				Table table = tableMap.get( splits[0].toLowerCase( ) );
				if ( table.tableAlias != null )
					return table.tableAlias + "." + splits[1];
				else
					return tableColumn;
			}
		}
		return tableColumn;
	}

	public static void main( String[] args )
	{
		new ModifySelect( );
	}
}
