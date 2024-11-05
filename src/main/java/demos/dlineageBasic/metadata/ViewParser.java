
package demos.dlineageBasic.metadata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TColumnDefinitionList;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TViewAliasItemList;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import demos.dlineageBasic.columnImpact.ColumnImpact;
import demos.dlineageBasic.model.metadata.ColumnMetaData;
import demos.dlineageBasic.model.metadata.TableMetaData;
import demos.dlineageBasic.model.view.ColumnImpactModel;
import demos.dlineageBasic.model.view.ColumnModel;
import demos.dlineageBasic.model.view.ReferenceModel;
import demos.dlineageBasic.util.SQLUtil;

public class ViewParser
{

	private Map<TableMetaData, List<ColumnMetaData>> tableColumns;
	private boolean strict = false;
	private EDbVendor vendor = EDbVendor.dbvmssql;
	private String database = null;
	private Set<String> stmtList = new HashSet<String>( );

	public ViewParser( Map<TableMetaData, List<ColumnMetaData>> tableColumns,
			EDbVendor vendor, TGSqlParser parser, boolean strict, String database )
	{
		this.database = database;
		this.strict = strict;
		this.vendor = vendor;
		this.tableColumns = tableColumns;
		checkDDL( parser );
		stmtList.clear( );
	}

	private void checkDDL( TGSqlParser sqlparser )
	{
		TStatementList stmts = sqlparser.sqlstatements;
		for ( int i = 0; i < stmts.size( ); i++ )
		{
			TCustomSqlStatement stmt = stmts.get( i );
			parseStatement( stmt );
		}
	}

	private void parseStatement( TCustomSqlStatement stmt )
	{
		if ( !stmtList.contains( stmt.toString( ) ) )
		{
			stmtList.add( stmt.toString( ) );
		}
		else
		{
			return;
		}
		if ( stmt instanceof TCreateViewSqlStatement )
		{
			TCreateViewSqlStatement createView = ( (TCreateViewSqlStatement) stmt );
			parseCreateView( createView );
		}
		else if ( stmt instanceof TCreateTableSqlStatement
				&& ( (TCreateTableSqlStatement) stmt ).getSubQuery( ) != null )
		{
			TCreateTableSqlStatement createTable = ( (TCreateTableSqlStatement) stmt );
			parseCreateTable( createTable );
		}
		else if ( stmt instanceof TInsertSqlStatement
				&& ( (TInsertSqlStatement) stmt ).getSubQuery( ) != null )
		{
			TInsertSqlStatement insert = ( (TInsertSqlStatement) stmt );
			parseInsertStmt( insert );
		}
		if ( stmt instanceof TUseDatabase )
		{
			TUseDatabase use = (TUseDatabase) stmt;
			database = use.getDatabaseName( ).toString( );
		}
	}

	private void parseInsertStmt( TInsertSqlStatement insert )
	{
		if ( insert.getTargetTable( ).getTableName( ) != null )
		{
			String tableName = insert.getTargetTable( )
					.getTableName( )
					.getTableString( );
			String tableSchema = insert.getTargetTable( )
					.getTableName( )
					.getSchemaString( );
			String databaseName = insert.getTargetTable( )
					.getTableName( )
					.getDatabaseString( );
			TableMetaData tableMetaData = new TableMetaData( vendor, strict );
			tableMetaData.setName( tableName );
			tableMetaData.setSchemaName( tableSchema );
			if ( isNotEmpty( databaseName ) )
			{
				tableMetaData.setCatalogName( databaseName );
			}
			else
				tableMetaData.setCatalogName( database );
			tableMetaData.setView( false );
			if ( !tableColumns.containsKey( tableMetaData ) )
			{
				tableColumns.put( tableMetaData,
						new ArrayList<ColumnMetaData>( ) );
			}
			else
			{
				List<TableMetaData> tables = (List<TableMetaData>) Arrays.asList( tableColumns.keySet( )
						.toArray( new TableMetaData[0] ) );
				tableMetaData = (TableMetaData) tables.get( tables.indexOf( tableMetaData ) );
				tableMetaData.setView( false );
			}
			if ( insert.getSubQuery( ) != null )
			{
				ColumnImpact impact = new ColumnImpact( insert.getSubQuery( )
						.toString( ), insert.dbvendor, tableColumns, strict );
				impact.ignoreTopSelect( true );
				impact.setDebug( false );
				impact.setShowUIInfo( true );
				impact.setTraceErrorMessage( false );
				impact.impactSQL( );
				ColumnImpactModel columnImpactModel = impact.generateModel( );
				parseSubQueryColumnDefinition( insert,
						insert.getSubQuery( ),
						tableMetaData,
						columnImpactModel );

			}
		}

	}

	private void parseCreateView( TCreateViewSqlStatement createView )
	{
		if ( createView.getViewName( ) != null )
		{
			String tableName = createView.getViewName( ).getTableString( );
			String tableSchema = createView.getViewName( ).getSchemaString( );
			String databaseName = createView.getViewName( ).getDatabaseString( );
			TableMetaData viewMetaData = new TableMetaData( vendor, strict );
			viewMetaData.setName( tableName );
			viewMetaData.setSchemaName( tableSchema );
			if ( isNotEmpty( databaseName ) )
			{
				viewMetaData.setCatalogName( databaseName );
			}
			else
				viewMetaData.setCatalogName( database );
			viewMetaData.setView( true );
			if ( !tableColumns.containsKey( viewMetaData ) )
			{
				tableColumns.put( viewMetaData, new ArrayList<ColumnMetaData>( ) );
			}
			else
			{
				List<TableMetaData> tables = (List<TableMetaData>) Arrays.asList( tableColumns.keySet( )
						.toArray( new TableMetaData[0] ) );
				viewMetaData = (TableMetaData) tables.get( tables.indexOf( viewMetaData ) );
				viewMetaData.setView( true );
			}
			if ( createView.getSubquery( ) != null )
			{
				ColumnImpact impact = new ColumnImpact( createView.getSubquery( )
						.toString( ),
						createView.dbvendor,
						tableColumns,
						strict );
				impact.ignoreTopSelect( true );
				impact.setDebug( false );
				impact.setShowUIInfo( true );
				impact.setTraceErrorMessage( false );
				impact.impactSQL( );
				ColumnImpactModel columnImpactModel = impact.generateModel( );
				parseSubQueryColumnDefinition( createView,
						createView.getSubquery( ),
						viewMetaData,
						columnImpactModel );

			}
		}
	}

	private void parseCreateTable( TCreateTableSqlStatement createTable )
	{
		if ( createTable.getTableName( ) != null )
		{
			String tableName = createTable.getTableName( ).getTableString( );
			String tableSchema = createTable.getTableName( ).getSchemaString( );
			String databaseName = createTable.getTableName( )
					.getDatabaseString( );
			TableMetaData tableMetaData = new TableMetaData( vendor, strict );
			tableMetaData.setName( tableName );
			tableMetaData.setSchemaName( tableSchema );
			if ( isNotEmpty( databaseName ) )
			{
				tableMetaData.setCatalogName( databaseName );
			}
			else
				tableMetaData.setCatalogName( database );
			tableMetaData.setView( false );
			if ( !tableColumns.containsKey( tableMetaData ) )
			{
				tableColumns.put( tableMetaData,
						new ArrayList<ColumnMetaData>( ) );
			}
			else
			{
				List<TableMetaData> tables = (List<TableMetaData>) Arrays.asList( tableColumns.keySet( )
						.toArray( new TableMetaData[0] ) );
				tableMetaData = (TableMetaData) tables.get( tables.indexOf( tableMetaData ) );
				tableMetaData.setView( false );
			}
			if ( createTable.getSubQuery( ) != null )
			{
				ColumnImpact impact = new ColumnImpact( removeParentheses( createTable.getSubQuery( )
						.toString( )
						.trim( ) ),
						createTable.dbvendor,
						tableColumns,
						strict );
				impact.ignoreTopSelect( true );
				impact.setDebug( false );
				impact.setShowUIInfo( true );
				impact.setTraceErrorMessage( false );
				impact.impactSQL( );
				ColumnImpactModel columnImpactModel = impact.generateModel( );
				parseSubQueryColumnDefinition( createTable,
						createTable.getSubQuery( ),
						tableMetaData,
						columnImpactModel );

			}
		}
	}

	private String removeParentheses( String sql )
	{
		if ( sql.startsWith( "(" ) && sql.endsWith( ")" ) )
		{
			sql = sql.substring( 1, sql.length( ) - 1 ).trim( );
			return removeParentheses( sql );
		}
		return sql;
	}

	private void parseSubQueryColumnDefinition(
			TCreateViewSqlStatement createView, TSelectSqlStatement stmt,
			TableMetaData viewMetaData, ColumnImpactModel columnImpactModel )
	{
		if ( stmt.getSetOperatorType( ) != ESetOperatorType.none )
		{
			parseSubQueryColumnDefinition( createView,
					stmt.getLeftStmt( ),
					viewMetaData,
					columnImpactModel );
			parseSubQueryColumnDefinition( createView,
					stmt.getRightStmt( ),
					viewMetaData,
					columnImpactModel );
		}
		else
		{
			int columnCount = stmt.getResultColumnList( ).size( );
			String[] aliasNames = new String[columnCount];
			if ( createView.getViewAliasClause( ) != null )
			{
				columnCount = createView.getViewAliasClause( )
						.getViewAliasItemList( )
						.size( );
				aliasNames = new String[columnCount];
				TViewAliasItemList items = createView.getViewAliasClause( )
						.getViewAliasItemList( );
				for ( int i = 0; i < items.size( ); i++ )
				{
					if ( items.getViewAliasItem( i ).getAlias( ) != null )
					{
						aliasNames[i] = items.getViewAliasItem( i )
								.getAlias( )
								.toString( );
					}
				}
			}
			for ( int i = 0; i < columnCount; i++ )
			{
				TResultColumn resultColumn = stmt.getResultColumnList( )
						.getResultColumn( i );
				parseColumnDefinition( resultColumn,
						viewMetaData,
						columnImpactModel,
						aliasNames[i] );
			}
		}
	}

	private void parseSubQueryColumnDefinition(
			TCreateTableSqlStatement createTable, TSelectSqlStatement stmt,
			TableMetaData tableMetaData, ColumnImpactModel columnImpactModel )
	{
		if ( stmt.getSetOperatorType( ) != ESetOperatorType.none )
		{
			parseSubQueryColumnDefinition( createTable,
					stmt.getLeftStmt( ),
					tableMetaData,
					columnImpactModel );
			parseSubQueryColumnDefinition( createTable,
					stmt.getRightStmt( ),
					tableMetaData,
					columnImpactModel );
		}
		else
		{
			int columnCount = stmt.getResultColumnList( ).size( );
			String[] aliasNames = new String[columnCount];
			if ( createTable.getColumnList( ) != null
					&& createTable.getColumnList( ).size( ) > 0 )
			{
				columnCount = createTable.getColumnList( ).size( );
				aliasNames = new String[columnCount];
				TColumnDefinitionList items = createTable.getColumnList( );
				for ( int i = 0; i < items.size( ); i++ )
				{
					aliasNames[i] = items.getColumn( i ).toString( );
				}
			}
			for ( int i = 0; i < columnCount; i++ )
			{
				TResultColumn resultColumn = stmt.getResultColumnList( )
						.getResultColumn( i );
				parseColumnDefinition( resultColumn,
						tableMetaData,
						columnImpactModel,
						aliasNames[i] );
			}
		}
	}

	private void parseSubQueryColumnDefinition( TInsertSqlStatement insert,
			TSelectSqlStatement stmt, TableMetaData tableMetaData,
			ColumnImpactModel columnImpactModel )
	{
		if ( stmt.getSetOperatorType( ) != ESetOperatorType.none )
		{
			parseSubQueryColumnDefinition( insert,
					stmt.getLeftStmt( ),
					tableMetaData,
					columnImpactModel );
			parseSubQueryColumnDefinition( insert,
					stmt.getRightStmt( ),
					tableMetaData,
					columnImpactModel );
		}
		else
		{
			if ( insert.getColumnList( ) != null )
			{
				TObjectNameList items = insert.getColumnList( );
				int columnCount = items.size( );
				String[] aliasNames = new String[columnCount];

				for ( int i = 0; i < items.size( ); i++ )
				{
					aliasNames[i] = items.getObjectName( i ).toString( );
				}

				for ( int i = 0; i < columnCount; i++ )
				{
					TResultColumn resultColumn = null;
					if ( i < stmt.getResultColumnList( ).size( ) )
						resultColumn = stmt.getResultColumnList( )
								.getResultColumn( i );
					else
						resultColumn = stmt.getResultColumnList( )
								.getResultColumn( stmt.getResultColumnList( )
										.size( ) - 1 );
					parseInsertColumnDefinition( resultColumn,
							tableMetaData,
							columnImpactModel,
							aliasNames[i] );
				}
			}
			else if ( insert.getSubQuery( ) != null
					&& insert.getSubQuery( ).getResultColumnList( ) != null )
			{
				int columnCount = insert.getSubQuery( )
						.getResultColumnList( )
						.size( );
				String[] aliasNames = new String[columnCount];
				for ( int i = 0; i < columnCount; i++ )
				{
					TResultColumn resultColumn = insert.getSubQuery( )
							.getResultColumnList( )
							.getResultColumn( i );
					parseColumnDefinition( resultColumn,
							tableMetaData,
							columnImpactModel,
							aliasNames[i] );
				}
			}
		}
	}

	private void parseInsertColumnDefinition( TResultColumn resultColumn,
			TableMetaData viewMetaData, ColumnImpactModel columnImpactModel,
			String aliasName )
	{
		ColumnMetaData columnMetaData = new ColumnMetaData( );
		columnMetaData.setTable( viewMetaData );

		if ( aliasName != null )
		{
			columnMetaData.setDisplayName( aliasName );
			columnMetaData.setName( aliasName );
		}

		if ( tableColumns.get( viewMetaData ) == null )
			return;

		int index = tableColumns.get( viewMetaData ).indexOf( columnMetaData );
		if ( index != -1 )
		{
			columnMetaData = tableColumns.get( viewMetaData ).get( index );
		}
		else
		{
			tableColumns.get( viewMetaData ).add( columnMetaData );
		}

		ColumnMetaData[] referColumns = getRefTableColumns( resultColumn,
				columnImpactModel );
		for ( int i = 0; i < referColumns.length; i++ )
		{
			columnMetaData.addReferColumn( referColumns[i] );
		}
	}

	private void parseColumnDefinition( TResultColumn resultColumn,
			TableMetaData viewMetaData, ColumnImpactModel columnImpactModel,
			String aliasName )
	{
		ColumnMetaData columnMetaData = new ColumnMetaData( );
		columnMetaData.setTable( viewMetaData );

		if ( resultColumn != null )
		{
			if ( resultColumn.getAliasClause( ) != null )
			{
				columnMetaData.setName( resultColumn.getAliasClause( )
						.getAliasName( )
						.toString( ) );
			}
			else if ( isNotEmpty( resultColumn.getColumnNameOnly( ) ) )
			{
				columnMetaData.setName( resultColumn.getColumnNameOnly( ) );
			}
			else
			{
				columnMetaData.setName( resultColumn.toString( ) );
			}
		}
		if ( aliasName != null )
		{
			columnMetaData.setDisplayName( aliasName );
		}

		if ( tableColumns.get( viewMetaData ) == null )
			return;

		int index = tableColumns.get( viewMetaData ).indexOf( columnMetaData );
		if ( index != -1 )
		{
			columnMetaData = tableColumns.get( viewMetaData ).get( index );
		}
		else
		{
			tableColumns.get( viewMetaData ).add( columnMetaData );
		}

		if ( resultColumn != null )
		{
			ColumnMetaData[] referColumns = getRefTableColumns( resultColumn,
					columnImpactModel );
			for ( int i = 0; i < referColumns.length; i++ )
			{
				columnMetaData.addReferColumn( referColumns[i] );
			}
		}
	}

	private ColumnMetaData[] getRefTableColumns( TResultColumn resultColumn,
			ColumnImpactModel columnImpactModel )
	{
		ReferenceModel[] referenceModels = columnImpactModel.getReferences( );
		List<ColumnMetaData> columns = new ArrayList<ColumnMetaData>( );
		if ( resultColumn.getAliasClause( ) != null )
		{
			for ( int i = 0; i < referenceModels.length; i++ )
			{
				ReferenceModel referModel = referenceModels[i];
				// if ( referModel.getClause( ) != Clause.SELECT )
				// continue;
				if ( referModel.getAlias( ) != null )
				{
					String aliasName = resultColumn.getAliasClause( )
							.getAliasName( )
							.toString( );
					if ( removeQuote( referModel.getAlias( ).getName( ) ).equalsIgnoreCase( removeQuote( aliasName ) ) )
					{
						ColumnMetaData columnMetaData = getColumn( referModel.getColumn( ) );
						if ( columnMetaData != null
								&& !columns.contains( columnMetaData ) )
							columns.add( columnMetaData );
					}
				}
			}
		}
		else
		{
			for ( int i = 0; i < referenceModels.length; i++ )
			{
				ReferenceModel referModel = referenceModels[i];
				// if ( referModel.getClause( ) != Clause.SELECT )
				// continue;
				if ( referModel.getField( ) != null )
				{
					if ( resultColumn.getFieldAttr( ) != null )
					{
						if ( removeQuote( resultColumn.getFieldAttr( )
								.toString( ) ).equalsIgnoreCase( removeQuote( referModel.getField( )
								.getFullName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn( referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );
						}
					}
					else
					{
						if ( removeQuote( resultColumn.toString( ) ).equalsIgnoreCase( removeQuote( referModel.getField( )
								.getFullName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn( referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );

						}
					}
				}
			}
		}
		return columns.toArray( new ColumnMetaData[0] );
	}

	private String removeQuote( String string )
	{
		if ( string != null && string.indexOf( '.' ) != -1 )
		{
			String[] splits = string.split( "\\." );
			StringBuffer result = new StringBuffer( );
			for ( int i = 0; i < splits.length; i++ )
			{
				result.append( removeQuote( splits[i] ) );
				if ( i < splits.length - 1 )
				{
					result.append( "." );
				}
			}
			return result.toString( );
		}
		return SQLUtil.trimColumnStringQuote( string );
	}

	private ColumnMetaData getColumn( ColumnModel column )
	{
		if ( column == null )
			return null;
		String tableFullName = column.getTable( ).getFullName( );
		List<String> splits = Arrays.asList( tableFullName.split( "\\." ) );
		ColumnMetaData columnMetadata = new ColumnMetaData( );
		columnMetadata.setName( column.getName( ) );
		TableMetaData tableMetaData = new TableMetaData( vendor, strict );
		if ( splits.size( ) > 0 )
		{
			tableMetaData.setName( splits.get( splits.size( ) - 1 ) );
		}
		if ( splits.size( ) > 1 )
		{
			tableMetaData.setSchemaName( splits.get( splits.size( ) - 2 ) );
		}
		if ( splits.size( ) > 2 )
		{
			tableMetaData.setCatalogName( splits.get( splits.size( ) - 3 ) );
		}
		if ( !tableColumns.containsKey( tableMetaData ) )
		{
			tableColumns.put( tableMetaData, new ArrayList<ColumnMetaData>( ) );
		}
		else
		{
			List<TableMetaData> tables = (List<TableMetaData>) Arrays.asList( tableColumns.keySet( )
					.toArray( new TableMetaData[0] ) );
			tableMetaData = tables.get( tables.indexOf( tableMetaData ) );
		}
		columnMetadata.setTable( tableMetaData );
		List<ColumnMetaData> columns = tableColumns.get( tableMetaData );
		if ( columns.contains( columnMetadata ) )
		{
			columnMetadata = columns.get( columns.indexOf( columnMetadata ) );
		}
		else
		{
			columns.add( columnMetadata );
		}
		return columnMetadata;
	}

	private static boolean isNotEmpty( String str )
	{
		return str != null && str.trim( ).length( ) > 0;
	}

	public String getDatabase( )
	{
		return database;
	}
}
