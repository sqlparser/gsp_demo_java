
package demos.dlineageBasic.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import demos.dlineageBasic.columnImpact.ColumnImpact;
import demos.dlineageBasic.model.ddl.schema.foreignKey;
import demos.dlineageBasic.model.ddl.schema.index;
import demos.dlineageBasic.model.ddl.schema.indexColumn;
import demos.dlineageBasic.model.ddl.schema.reference;
import demos.dlineageBasic.model.ddl.schema.unique;
import demos.dlineageBasic.model.ddl.schema.uniqueColumn;
import demos.dlineageBasic.model.metadata.ColumnMetaData;
import demos.dlineageBasic.model.metadata.ProcedureMetaData;
import demos.dlineageBasic.model.metadata.TableMetaData;
import demos.dlineageBasic.model.view.ColumnImpactModel;
import demos.dlineageBasic.model.view.ColumnModel;
import demos.dlineageBasic.model.view.ReferenceModel;
import demos.dlineageBasic.model.xml.procedureImpactResult;
import demos.dlineageBasic.util.Pair;
import demos.dlineageBasic.util.SQLUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EKeyActionType;
import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TKeyAction;
import gudusoft.gsqlparser.nodes.TMergeWhenClause;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTableElement;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TCommentOnSqlStmt;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.TUseDatabase;

public class DDLParser
{

	private Map<TableMetaData, List<ColumnMetaData>> tableColumns;
	private Pair<procedureImpactResult, List<ProcedureMetaData>> procedures;
	private boolean strict = false;
	private String database = null;
	private EDbVendor vendor = EDbVendor.dbvmssql;
	private Set<String> stmtList = new HashSet<String>( );

	public DDLParser( Map<TableMetaData, List<ColumnMetaData>> tableColumns,
			Pair<procedureImpactResult, List<ProcedureMetaData>> procedures,
			EDbVendor vendor, TGSqlParser parser, boolean strict, String database )
	{
		this.strict = strict;
		this.vendor = vendor;
		this.database = database;
		this.tableColumns = tableColumns;
		this.procedures = procedures;
		
		checkDDL( parser );
		stmtList.clear( );
	}

	private void checkDDL( TGSqlParser sqlparser )
	{
		TStatementList stmts = sqlparser.sqlstatements;
		parseStatementList(stmts);
	}

	private void parseStatementList( TStatementList stmts )
	{
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
		if ( stmt instanceof TCreateTableSqlStatement
				&& ( (TCreateTableSqlStatement) stmt ).getSubQuery( ) == null )
		{
			TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) stmt;
			parseCreateTable( createTable );
		}
		else if ( stmt instanceof TCommentOnSqlStmt )
		{
			TCommentOnSqlStmt commentOn = (TCommentOnSqlStmt) stmt;
			parseCommentOn( commentOn );
		}
		else if ( stmt instanceof TAlterTableStatement )
		{
			TAlterTableStatement alterTable = (TAlterTableStatement) stmt;
			TableMetaData tableMetaData = new TableMetaData( vendor, strict );
			tableMetaData
					.setName( alterTable.getTableName( ).getTableString( ) );
			tableMetaData.setSchemaName(
					alterTable.getTableName( ).getSchemaString( ) );
			if ( isNotEmpty( alterTable.getTableName( ).getDatabaseString( ) ) )
			{
				tableMetaData.setCatalogName(
						alterTable.getTableName( ).getDatabaseString( ) );
			}
			else
				tableMetaData.setCatalogName( database );
			tableMetaData = getTableMetaData( tableMetaData );

			parseAlterTable( alterTable, tableMetaData );
		}
		else if ( stmt instanceof TCreateIndexSqlStatement )
		{
			TCreateIndexSqlStatement createIndex = (TCreateIndexSqlStatement) stmt;
			parseCreateIndex( createIndex );
		}
		else if ( stmt instanceof TUseDatabase )
		{
			TUseDatabase use = (TUseDatabase) stmt;
			database = use.getDatabaseName( ).toString( );
		}
		else if ( stmt instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement selectStmt = (TSelectSqlStatement) stmt;
			parseSelectStmt( selectStmt );
		}
		else if ( stmt instanceof TInsertSqlStatement
				&& ( (TInsertSqlStatement) stmt ).getSubQuery( ) == null )
		{
			TInsertSqlStatement insertStmt = (TInsertSqlStatement) stmt;
			parseInsertStmt( insertStmt );
		}
		else if ( stmt instanceof TUpdateSqlStatement )
		{
			TUpdateSqlStatement updateStmt = (TUpdateSqlStatement) stmt;
			parseUpdateStmt( updateStmt );
		}
		else if ( stmt instanceof TMergeSqlStatement )
		{
			TMergeSqlStatement mergeStmt = (TMergeSqlStatement) stmt;
			parseMergeStmt( mergeStmt );
		}
		else if ( stmt instanceof TStoredProcedureSqlStatement )
		{
			TStoredProcedureSqlStatement procedureStmt = (TStoredProcedureSqlStatement) stmt;
			parseProcedureStmt( procedureStmt );

			if ( stmt.getStatements( ) != null
					&& stmt.getStatements( ).size( ) > 0 )
			{
				parseStatementList( stmt.getStatements( ) );
			}
		}
		else if ( stmt.getStatements( ) != null
				&& stmt.getStatements( ).size( ) > 0 )
		{
			parseStatementList( stmt.getStatements( ) );
		}
		else
		{
			// System.err.println( stmt );
		}
	}

	private void parseProcedureStmt(
			TStoredProcedureSqlStatement procedureStmt )
	{
		if ( procedureStmt.getStoredProcedureName( ) == null )
		{
			return;
		}
		ProcedureMetaData procedureMetaData = getProcedureMetaData(
				procedureStmt.getStoredProcedureName( ) );
		procedureMetaData = getProcedureMetaData( procedureMetaData, true );
		String stmtType = procedureStmt.sqlstatementtype.name( )
				.toLowerCase( )
				.trim( );
		if ( stmtType.endsWith( "procedure" ) )
		{
			procedureMetaData.setFunction( false );
			procedureMetaData.setTrigger( false );
		}
		else if ( stmtType.endsWith( "function" ) )
		{
			procedureMetaData.setFunction( true );
			procedureMetaData.setTrigger( false );
		}
		else if ( stmtType.endsWith( "trigger" ) )
		{
			procedureMetaData.setFunction( false );
			procedureMetaData.setTrigger( true );
		}

	}

	public ProcedureMetaData getProcedureMetaData(
			ProcedureMetaData parentProcedure, TObjectName procedureName )
	{
		ProcedureMetaData procedureMetaData = new ProcedureMetaData( vendor,
				strict );
		procedureMetaData.setName( procedureName.getPartString( ) == null
				? procedureName.getObjectString( )
				: procedureName.getPartString( ) );
		if ( procedureName.getSchemaString( ) != null )
		{
			procedureMetaData.setSchemaName( procedureName.getSchemaString( ) );
		}
		else
		{
			procedureMetaData.setSchemaName( parentProcedure.getSchemaName( ) );
			procedureMetaData.setSchemaDisplayName(
					parentProcedure.getSchemaDisplayName( ) );
		}

		if ( isNotEmpty( procedureName.getDatabaseString( ) ) )
		{
			procedureMetaData
					.setCatalogName( procedureName.getDatabaseString( ) );
		}
		else
		{
			procedureMetaData
					.setCatalogName( parentProcedure.getCatalogName( ) );
			procedureMetaData.setCatalogDisplayName(
					parentProcedure.getCatalogDisplayName( ) );
		}
		return procedureMetaData;
	}

	private ProcedureMetaData getProcedureMetaData( TObjectName procedureName )
	{
		ProcedureMetaData procedureMetaData = new ProcedureMetaData( vendor,
				strict );
		procedureMetaData.setName( procedureName.getPartString( ) == null
				? procedureName.getObjectString( )
				: procedureName.getPartString( ) );
		procedureMetaData.setSchemaName( procedureName.getSchemaString( ) );
		if ( isNotEmpty( procedureName.getDatabaseString( ) ) )
		{
			procedureMetaData
					.setCatalogName( procedureName.getDatabaseString( ) );
		}
		else
			procedureMetaData.setCatalogName( database );
		return procedureMetaData;
	}

	private void parseInsertStmt( TInsertSqlStatement insertStmt )
	{
		ColumnImpact impact = new ColumnImpact( insertStmt.toString( ),
				insertStmt.dbvendor,
				tableColumns,
				strict );
		impact.setDebug( false );
		impact.setShowUIInfo( true );
		impact.setTraceErrorMessage( false );
		impact.impactSQL( );
		ColumnImpactModel columnImpactModel = impact.generateModel( );

		if ( insertStmt.getSubQuery( ) != null )
		{
			parseStatement( insertStmt.getSubQuery( ) );
		}

		if ( insertStmt.getResultColumnList( ) != null )
		{
			int columnCount = insertStmt.getResultColumnList( ).size( );

			for ( int i = 0; i < columnCount; i++ )
			{
				TResultColumn resultColumn = insertStmt.getResultColumnList( )
						.getResultColumn( i );
				parseColumnDefinition( resultColumn, columnImpactModel );
			}
		}

		if ( insertStmt.getColumnList( ) != null )
		{
			int columnCount = insertStmt.getColumnList( ).size( );

			TableMetaData tableMetaData = new TableMetaData( vendor, strict );
			tableMetaData.setName( insertStmt.getTargetTable( )
					.getTableName( )
					.getTableString( ) );
			tableMetaData.setSchemaName( insertStmt.getTargetTable( )
					.getTableName( )
					.getSchemaString( ) );
			if ( isNotEmpty( insertStmt.getTargetTable( )
					.getTableName( )
					.getDatabaseString( ) ) )
			{
				tableMetaData.setCatalogName( insertStmt.getTargetTable( )
						.getTableName( )
						.getDatabaseString( ) );
			}
			else
				tableMetaData.setCatalogName( database );
			tableMetaData = getTableMetaData( tableMetaData );
			
			String procedureParent = getProcedureParentName(insertStmt);
			if(procedureParent!=null)
			{
				tableMetaData.setParent(procedureParent);
			}

			for ( int i = 0; i < columnCount; i++ )
			{
				TObjectName resultColumn = insertStmt.getColumnList( )
						.getObjectName( i );
				getColumnMetaData( tableMetaData, resultColumn );
				parseColumnDefinition( resultColumn, columnImpactModel );
			}
		}

	}
	
	private String getProcedureParentName(TCustomSqlStatement stmt) {
		
		stmt = stmt.getParentStmt();
		if(stmt == null)
			return null;
		
		if(stmt instanceof TStoredProcedureSqlStatement)
		{
			return ((TStoredProcedureSqlStatement)stmt).getStoredProcedureName().toString();
		}
	
		return getProcedureParentName(stmt);
	}

	private void parseUpdateStmt( TUpdateSqlStatement updateStmt )
	{
		ColumnImpact impact = new ColumnImpact( updateStmt.toString( ),
				updateStmt.dbvendor,
				tableColumns,
				strict );
		impact.setDebug( false );
		impact.setShowUIInfo( true );
		impact.setTraceErrorMessage( false );
		impact.impactSQL( );
		ColumnImpactModel columnImpactModel = impact.generateModel( );

		if ( updateStmt.getResultColumnList( ) != null )
		{
			int columnCount = updateStmt.getResultColumnList( ).size( );

			for ( int i = 0; i < columnCount; i++ )
			{
				TResultColumn resultColumn = updateStmt.getResultColumnList( )
						.getResultColumn( i );
				parseColumnDefinition( resultColumn, columnImpactModel );
			}
		}
	}

	private void parseMergeStmt( TMergeSqlStatement mergeStmt )
	{
		ColumnImpact impact = new ColumnImpact( mergeStmt.toString( ),
				mergeStmt.dbvendor,
				tableColumns,
				strict );
		impact.setDebug( false );
		impact.setShowUIInfo( true );
		impact.setTraceErrorMessage( false );
		impact.impactSQL( );
		ColumnImpactModel columnImpactModel = impact.generateModel( );

		TMergeSqlStatement merge = (TMergeSqlStatement) mergeStmt;
		for ( int i = 0; i < merge.getWhenClauses( ).size( ); i++ )
		{
			TMergeWhenClause whenClause = merge.getWhenClauses( )
					.getElement( i );
			if ( whenClause.getUpdateClause( ) != null )
			{
				// int columnCount = whenClause.getUpdateClause( )
				// .getUpdateColumnList( )
				// .size( );
				// String[] aliasNames = new String[columnCount];
				//
				// for ( int j = 0; j < whenClause.getUpdateClause( )
				// .getUpdateColumnList( )
				// .size( ); j++ )
				// {
				// TResultColumn resultColumn = whenClause.getUpdateClause( )
				// .getUpdateColumnList( )
				// .getResultColumn( j );
				// parseColumnDefinition( resultColumn,
				// columnImpactModel,
				// aliasNames[j] );
				// }
			}
			else if ( whenClause.getInsertClause( ) != null )
			{
				if ( whenClause.getInsertClause( ).getColumnList( ) != null )
				{
					int columnCount = whenClause.getInsertClause( )
							.getColumnList( )
							.size( );

					TableMetaData tableMetaData = new TableMetaData( vendor,
							strict );
					tableMetaData.setName( merge.getTargetTable( )
							.getTableName( )
							.getTableString( ) );
					tableMetaData.setSchemaName( merge.getTargetTable( )
							.getTableName( )
							.getSchemaString( ) );
					if ( isNotEmpty( merge.getTargetTable( )
							.getTableName( )
							.getDatabaseString( ) ) )
					{
						tableMetaData.setCatalogName( merge.getTargetTable( )
								.getTableName( )
								.getDatabaseString( ) );
					}
					else
						tableMetaData.setCatalogName( database );
					tableMetaData = getTableMetaData( tableMetaData );

					for ( int j = 0; j < columnCount; j++ )
					{
						TObjectName resultColumn = whenClause.getInsertClause( )
								.getColumnList( )
								.getObjectName( j );
						getColumnMetaData( tableMetaData, resultColumn );
						parseColumnDefinition( resultColumn,
								columnImpactModel );
					}
				}
			}
			else if ( whenClause.getDeleteClause( ) != null )
			{

			}
		}
	}

	private void parseSelectStmt( TSelectSqlStatement selectStmt )
	{
		if ( selectStmt.getParentStmt( ) != null )
			return;
		ColumnImpact impact = new ColumnImpact( selectStmt.toString( ),
				selectStmt.dbvendor,
				tableColumns,
				strict );
		impact.setDebug( false );
		impact.setShowUIInfo( true );
		impact.setTraceErrorMessage( false );
		impact.setVirtualTableName(
				SQLUtil.generateVirtualTableName( selectStmt ) );
		impact.impactSQL( );
		ColumnImpactModel columnImpactModel = impact.generateModel( );

		TableMetaData viewMetaData = new TableMetaData( vendor, strict );
		viewMetaData.setName( SQLUtil.generateVirtualTableName( selectStmt ) );
		if ( selectStmt.getTargetTable( ) != null
				&& selectStmt.getTargetTable( ).getTableName( ) != null )
		{
			if ( selectStmt.getTargetTable( )
					.getTableName( )
					.getSchemaString( ) != null )
				viewMetaData.setSchemaName( selectStmt.getTargetTable( )
						.getTableName( )
						.getSchemaString( ) );
			if ( selectStmt.getTargetTable( )
					.getTableName( )
					.getDatabaseString( ) != null )
				viewMetaData.setCatalogName( selectStmt.getTargetTable( )
						.getTableName( )
						.getDatabaseString( ) );
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
			List<TableMetaData> tables = (List<TableMetaData>) Arrays.asList(
					tableColumns.keySet( ).toArray( new TableMetaData[0] ) );
			viewMetaData = (TableMetaData) tables
					.get( tables.indexOf( viewMetaData ) );
			viewMetaData.setView( true );
		}

		parseSubQueryColumnDefinition( selectStmt,
				viewMetaData,
				columnImpactModel );
	}

	private void parseSubQueryColumnDefinition( TSelectSqlStatement stmt,
			TableMetaData viewMetaData, ColumnImpactModel columnImpactModel )
	{
		if ( stmt.getSetOperatorType( ) != ESetOperatorType.none )
		{
			parseSubQueryColumnDefinition( stmt.getLeftStmt( ),
					viewMetaData,
					columnImpactModel );
			parseSubQueryColumnDefinition( stmt.getRightStmt( ),
					viewMetaData,
					columnImpactModel );
		}
		else
		{
			int columnCount = stmt.getResultColumnList( ).size( );

			for ( int i = 0; i < columnCount; i++ )
			{
				TResultColumn resultColumn = stmt.getResultColumnList( )
						.getResultColumn( i );
				parseColumnDefinition( resultColumn, columnImpactModel );
				parseColumnDefinition( resultColumn,
						viewMetaData,
						columnImpactModel );
			}
		}
	}

	private void parseColumnDefinition( TResultColumn resultColumn,
			TableMetaData viewMetaData, ColumnImpactModel columnImpactModel )
	{
		ColumnMetaData columnMetaData = new ColumnMetaData( );
		columnMetaData.setTable( viewMetaData );

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
			ColumnImpactModel columnImpactModel )
	{
		if ( resultColumn.getExpr( )
				.getExpressionType( ) == EExpressionType.assignment_t )
		{
			TExpression leftExpr = getColumnExpression(
					resultColumn.getExpr( ).getLeftOperand( ) );
			TExpression rightExpr = getColumnExpression(
					resultColumn.getExpr( ).getRightOperand( ) );
			if ( leftExpr
					.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
				parseColumnDefinition( leftExpr.getObjectOperand( ),
						columnImpactModel );
			}
			if ( rightExpr
					.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
				parseColumnDefinition( rightExpr.getObjectOperand( ),
						columnImpactModel );
			}
		}
		else
			getRefTableColumns( resultColumn, columnImpactModel );
	}

	private TExpression getColumnExpression( TExpression expr )
	{
		if ( expr.getExpressionType( ) == EExpressionType.simple_object_name_t )
		{
			return expr;
		}
		else if ( expr.getLeftOperand( ) != null )
		{
			return getColumnExpression( expr.getLeftOperand( ) );
		}
		else
			return expr;
	}

	private void parseColumnDefinition( TObjectName resultColumn,
			ColumnImpactModel columnImpactModel )
	{
		getRefTableColumns( resultColumn, columnImpactModel );
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
			List<TableMetaData> tables = (List<TableMetaData>) Arrays.asList(
					tableColumns.keySet( ).toArray( new TableMetaData[0] ) );
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
					if ( removeQuote( referModel.getAlias( ).getName( ) )
							.equalsIgnoreCase( removeQuote( aliasName ) ) )
					{
						ColumnMetaData columnMetaData = getColumn(
								referModel.getColumn( ) );
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
						if ( removeQuote(
								resultColumn.getFieldAttr( ).toString( ) )
										.equalsIgnoreCase( removeQuote(
												referModel.getField( )
														.getFullName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn(
									referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );
						}
					}
					else if ( resultColumn.getExpr( ).getLeftOperand( ) != null
							&& resultColumn.getExpr( )
									.getLeftOperand( )
									.getExprList( ) != null )
					{
						for ( int j = 0; j < resultColumn.getExpr( )
								.getLeftOperand( )
								.getExprList( )
								.size( ); j++ )
						{
							if ( removeQuote( resultColumn.getExpr( )
									.getLeftOperand( )
									.getExprList( )
									.getExpression( j )
									.toString( ) ).equalsIgnoreCase(
											removeQuote( referModel.getField( )
													.getFullName( ) ) ) )
							{
								ColumnMetaData columnMetaData = getColumn(
										referModel.getColumn( ) );
								if ( !columns.contains( columnMetaData ) )
									columns.add( columnMetaData );

							}
						}
					}
					else if ( resultColumn.getExpr( ) != null )
					{

						if ( removeQuote( resultColumn.getExpr( ).toString( ) )
								.equalsIgnoreCase(
										removeQuote( referModel.getField( )
												.getFullName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn(
									referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );

						}

					}
					else
					{
						if ( removeQuote( resultColumn.toString( ) )
								.equalsIgnoreCase(
										removeQuote( referModel.getField( )
												.getFullName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn(
									referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );

						}
					}
				}
			}
		}
		return columns.toArray( new ColumnMetaData[0] );
	}

	private ColumnMetaData[] getRefTableColumns( TObjectName resultColumn,
			ColumnImpactModel columnImpactModel )
	{
		ReferenceModel[] referenceModels = columnImpactModel.getReferences( );
		List<ColumnMetaData> columns = new ArrayList<ColumnMetaData>( );

		for ( int i = 0; i < referenceModels.length; i++ )
		{
			ReferenceModel referModel = referenceModels[i];
			// if ( referModel.getClause( ) != Clause.SELECT )
			// continue;
			if ( referModel.getField( ) != null )
			{
				if ( resultColumn != null )
				{
					if ( resultColumn.getSchemaString( ) != null )
					{
						if ( removeQuote( resultColumn.toString( ) )
								.equalsIgnoreCase(
										removeQuote( referModel.getField( )
												.getFullName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn(
									referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );
						}
					}
					else
					{
						if ( removeQuote( resultColumn.toString( ) )
								.equalsIgnoreCase( removeQuote(
										referModel.getField( ).getName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn(
									referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );
						}
					}
				}
			}
			else if ( referModel.getAlias( ) != null )
			{
				if ( resultColumn != null )
				{
					if ( resultColumn.getSchemaString( ) != null )
					{
						if ( removeQuote( resultColumn.toString( ) )
								.equalsIgnoreCase( removeQuote(
										referModel.getAlias( ).getName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn(
									referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );
						}
					}
					else
					{
						if ( removeQuote( resultColumn.toString( ) )
								.equalsIgnoreCase( removeQuote(
										referModel.getAlias( ).getName( ) ) ) )
						{
							ColumnMetaData columnMetaData = getColumn(
									referModel.getColumn( ) );
							if ( !columns.contains( columnMetaData ) )
								columns.add( columnMetaData );
						}
					}
				}
			}
		}

		return columns.toArray( new ColumnMetaData[0] );
	}

	private void parseCreateIndex( TCreateIndexSqlStatement createIndex )
	{
		if ( createIndex.getCreateIndexNode( ) == null
				|| createIndex.getCreateIndexNode( ).getTableName( ) == null )
			return;
		String tableName = createIndex.getCreateIndexNode( )
				.getTableName( )
				.getTableString( );
		String tableSchema = createIndex.getCreateIndexNode( )
				.getTableName( )
				.getSchemaString( );
		TableMetaData tableMetaData = new TableMetaData( vendor, strict );
		tableMetaData.setName( tableName );
		tableMetaData.setSchemaName( tableSchema );
		if ( isNotEmpty( createIndex.getCreateIndexNode( )
				.getTableName( )
				.getDatabaseString( ) ) )
		{
			tableMetaData.setCatalogName( createIndex.getCreateIndexNode( )
					.getTableName( )
					.getDatabaseString( ) );
		}
		else
			tableMetaData.setCatalogName( database );
		tableMetaData = getTableMetaData( tableMetaData );

		if ( createIndex.getIndexName( ) != null )
		{
			index index = new index( );
			index.setName( createIndex.getIndexName( ).toString( ) );
			tableMetaData.getIndices( ).add( index );

			if ( createIndex.getColumnNameList( ) != null )
			{
				for ( int i = 0; i < createIndex.getColumnNameList( )
						.size( ); i++ )
				{
					indexColumn indexColumn = new indexColumn( );
					indexColumn.setName( createIndex.getColumnNameList( )
							.getOrderByItem( i )
							.toString( ) );
					index.getIndexColumns( ).add( indexColumn );
					ColumnMetaData columnMetaData = getColumnMetaData(
							tableMetaData,
							indexColumn.getName( ) );
					if ( columnMetaData != null )
					{
						columnMetaData.setIndex( true );
					}
				}
			}
		}
	}

	private void parseAlterTable( TAlterTableStatement alterTable,
			TableMetaData tableMetaData )
	{
		if ( alterTable.getAlterTableOptionList( ) == null
				&& alterTable.getTableElementList( ) == null )

			return;

		if ( alterTable.getAlterTableOptionList( ) != null )
		{
			for ( int i = 0; i < alterTable.getAlterTableOptionList( )
					.size( ); i++ )
			{
				parseAlterTableOption(
						alterTable.getAlterTableOptionList( )
								.getAlterTableOption( i ),
						tableMetaData );
			}
		}
		if ( alterTable.getTableElementList( ) != null )
		{
			for ( int i = 0; i < alterTable.getTableElementList( )
					.size( ); i++ )
			{
				parseAlterTableElement(
						alterTable.getTableElementList( ).getTableElement( i ),
						tableMetaData );
			}
		}
	}

	private void parseAlterTableElement( TTableElement tableElement,
			TableMetaData tableMetaData )
	{
		switch ( tableElement.getType( ) )
		{
			case TTableElement.type_table_constraint :
			{
				parseTableConstraint( tableElement.getConstraint( ),
						tableMetaData );
			}
			default :

		}

	}

	private void parseAlterTableOption( TAlterTableOption alterTableOption,
			TableMetaData tableMetaData )
	{
		switch ( alterTableOption.getOptionType( ) )
		{
			case AddColumn :
				for ( int i = 0; i < alterTableOption.getColumnDefinitionList( )
						.size( ); i++ )
				{
					parseColumnDefinition(
							alterTableOption.getColumnDefinitionList( )
									.getColumn( i ),
							tableMetaData );
				}
				break;
			case ModifyColumn :
				for ( int i = 0; i < alterTableOption.getColumnDefinitionList( )
						.size( ); i++ )
				{
					parseColumnDefinition(
							alterTableOption.getColumnDefinitionList( )
									.getColumn( i ),
							tableMetaData );
				}
				break;
			case AddConstraint :
				for ( int i = 0; i < alterTableOption.getConstraintList( )
						.size( ); i++ )
				{
					parseTableConstraint(
							alterTableOption.getConstraintList( )
									.getConstraint( i ),
							tableMetaData );
				}
			default :

		}
	}

	private static boolean isNotEmpty( String str )
	{
		return str != null && str.trim( ).length( ) > 0;
	}

	private void parseCreateTable( TCreateTableSqlStatement createTable )
	{
		if ( createTable.getTableName( ) != null )
		{
			TObjectName tableName = createTable.getTableName( );
			TableMetaData tableMetaData = getTableMetaData( tableName );
			if ( createTable.getTableComment( ) != null )
			{
				tableMetaData.setComment(
						createTable.getTableComment( ).toString( ) );
			}
			if ( createTable.getColumnList( ) != null )
			{
				for ( int i = 0; i < createTable.getColumnList( ).size( ); i++ )
				{
					TColumnDefinition columnDef = createTable.getColumnList( )
							.getColumn( i );
					parseColumnDefinition( columnDef, tableMetaData );
				}
			}
			else if ( createTable.getSubQuery( ) != null )
			{
				TResultColumnList columns = createTable.getSubQuery( )
						.getResultColumnList( );
				for ( int i = 0; i < columns.size( ); i++ )
				{
					TResultColumn column = columns.getResultColumn( i );
					if ( column.getAliasClause( ) != null )
					{
						getColumnMetaData( tableMetaData,
								column.getAliasClause( ).getAliasName( ) );
					}
					else
					{
						if ( column.getFieldAttr( ) != null )
						{
							getColumnMetaData( tableMetaData,
									column.getFieldAttr( ) );
						}
					}
				}
			}
			if ( createTable.getTableConstraints( ) != null )
			{
				for ( int i = 0; i < createTable.getTableConstraints( )
						.size( ); i++ )
				{
					TConstraint constraint = createTable.getTableConstraints( )
							.getConstraint( i );
					parseTableConstraint( constraint, tableMetaData );
				}
			}
		}
	}

	private void parseTableConstraint( TConstraint constraint,
			TableMetaData tableMetaData )
	{
		if ( constraint.getColumnList( ) == null )
			return;
		switch ( constraint.getConstraint_type( ) )
		{
			case primary_key :
				setColumnMetaDataPrimaryKey( tableMetaData, constraint );
				break;
			case unique :
				setColumnMetaDataUnique( tableMetaData, constraint );
				break;
			case foreign_key :
				setColumnMetaDataForeignKey( tableMetaData, constraint );
				break;
			case index :
				setColumnMetaDataIndex( tableMetaData, constraint );
				break;
			default :
				break;
		}
	}

	private void setColumnMetaDataIndex( TableMetaData tableMetaData,
			TConstraint constraint )
	{
		index index = new index( );
		if ( constraint.getConstraintName( ) != null )
			index.setName( constraint.getConstraintName( ).toString( ) );
		for ( int i = 0; i < constraint.getColumnList( ).size( ); i++ )
		{
			TObjectName object = constraint.getColumnList( ).getElement( i ).getColumnName();
			ColumnMetaData columnMetaData = getColumnMetaData( tableMetaData,
					object );
			columnMetaData.setIndex( true );
			indexColumn indexColumn = new indexColumn( );
			indexColumn.setName( columnMetaData.getDisplayName( ) );
			index.getIndexColumns( ).add( indexColumn );
		}
		tableMetaData.getIndices( ).add( index );

	}

	private void setColumnMetaDataForeignKey( TableMetaData tableMetaData,
			TConstraint constraint )
	{
		foreignKey foreignKey = new foreignKey( );
		if ( constraint.getConstraintName( ) != null )
			foreignKey.setName( constraint.getConstraintName( ).toString( ) );
		if ( constraint.getReferencedObject( ) != null )
			foreignKey.setForeignTable(
					constraint.getReferencedObject( ).toString( ) );
		if ( constraint.getColumnList( ) != null
				&& constraint.getReferencedColumnList( ) != null )
		{
			for ( int i = 0; i < constraint.getColumnList( ).size( ); i++ )
			{
				TObjectName object = constraint.getColumnList( )
						.getElement( i ).getColumnName();
				ColumnMetaData columnMetaData = getColumnMetaData(
						tableMetaData,
						object );
				columnMetaData.setForeignKey( true );
				reference reference = new reference( );
				reference.setLocal( columnMetaData.getDisplayName( ) );
				if ( constraint.getReferencedColumnList( ).size( ) > i )
				{
					reference.setForeign( constraint.getReferencedColumnList( )
							.getObjectName( i )
							.toString( ) );
				}
				foreignKey.getReferences( ).add( reference );
			}
		}
		if ( constraint.getKeyActions( ) != null )
		{
			for ( int i = 0; i < constraint.getKeyActions( ).size( ); i++ )
			{
				TKeyAction keyAction = constraint.getKeyActions( )
						.getElement( i );
				if ( keyAction.getActionType( ) == EKeyActionType.delete )
				{
					foreignKey.setOnDelete( String.valueOf( true ) );
				}
				else if ( keyAction.getActionType( ) == EKeyActionType.update )
				{
					foreignKey.setOnUpdate( String.valueOf( true ) );
				}
			}
		}
		tableMetaData.getForeignKeys( ).add( foreignKey );
	}

	private void setColumnMetaDataUnique( TableMetaData tableMetaData,
			TConstraint constraint )
	{
		unique unique = new unique( );
		if ( constraint.getConstraintName( ) != null )
			unique.setName( constraint.getConstraintName( ).toString( ) );
		for ( int i = 0; i < constraint.getColumnList( ).size( ); i++ )
		{
			TObjectName object = constraint.getColumnList( ).getElement( i ).getColumnName();
			ColumnMetaData columnMetaData = getColumnMetaData( tableMetaData,
					object );
			columnMetaData.setUnique( true );
			uniqueColumn uniqueColumn = new uniqueColumn( );
			uniqueColumn.setName( columnMetaData.getDisplayName( ) );
			unique.getUniqueColumns( ).add( uniqueColumn );
		}
		tableMetaData.getUniques( ).add( unique );
	}

	private void setColumnMetaDataPrimaryKey( TableMetaData tableMetaData,
			TConstraint constraint )
	{
		for ( int i = 0; i < constraint.getColumnList( ).size( ); i++ )
		{
			TObjectName object = constraint.getColumnList( ).getElement( i ).getColumnName();
			ColumnMetaData columnMetaData = getColumnMetaData( tableMetaData,
					object );
			columnMetaData.setPrimaryKey( true );
		}
	}

	private void parseColumnDefinition( TColumnDefinition columnDef,
			TableMetaData tableMetaData )
	{
		if ( columnDef.getColumnName( ) != null )
		{
			TObjectName object = columnDef.getColumnName( );
			ColumnMetaData columnMetaData = getColumnMetaData( tableMetaData,
					object );

			if ( object.getCommentString( ) != null )
			{
				String columnComment = object.getCommentString( ).toString( );
				columnMetaData.setComment( columnComment );
			}

			if ( columnDef.getDefaultExpression( ) != null )
			{
				columnMetaData.setDefaultVlaue(
						columnDef.getDefaultExpression( ).toString( ) );
			}

			if ( columnDef.getDatatype( ) != null )
			{
				TTypeName type = columnDef.getDatatype( );
				String typeName = type.toString( );
				int typeNameIndex = typeName.indexOf( "(" );
				if ( typeNameIndex != -1 )
					typeName = typeName.substring( 0, typeNameIndex );
				columnMetaData.setTypeName( typeName );
				if ( type.getScale( ) != null )
				{
					try
					{
						columnMetaData.setScale( Integer
								.parseInt( type.getScale( ).toString( ) ) );
					}
					catch ( NumberFormatException e1 )
					{
					}
				}
				if ( type.getPrecision( ) != null )
				{
					try
					{
						columnMetaData.setPrecision( Integer
								.parseInt( type.getPrecision( ).toString( ) ) );
					}
					catch ( NumberFormatException e )
					{
					}
				}
				if ( type.getLength( ) != null )
				{
					try
					{
						columnMetaData.setColumnDisplaySize(
								type.getLength( ).toString( ) );
					}
					catch ( NumberFormatException e )
					{
					}
				}
			}

			if ( columnDef.isNull( ) )
			{
				columnMetaData.setNull( true );
			}

			if ( columnDef.getConstraints( ) != null )
			{
				for ( int i = 0; i < columnDef.getConstraints( ).size( ); i++ )
				{
					TConstraint constraint = columnDef.getConstraints( )
							.getConstraint( i );
					switch ( constraint.getConstraint_type( ) )
					{
						case notnull :
							columnMetaData.setNotNull( true );
							break;
						case primary_key :
							columnMetaData.setPrimaryKey( true );
							break;
						case unique :
							columnMetaData.setUnique( true );
							break;
						case check :
							columnMetaData.setCheck( true );
							break;
						case foreign_key :
							columnMetaData.setForeignKey( true );
							break;
						case fake_auto_increment :
							columnMetaData.setAutoIncrement( true );
							break;
						case fake_comment :
							// Can't get comment information.
						default :
							break;
					}
				}
			}
		}
	}

	private ColumnMetaData getColumnMetaData( TableMetaData tableMetaData,
			TObjectName object )
	{
		if ( object == null )
			return null;
		return getColumnMetaData( tableMetaData, object.getColumnNameOnly( ) );
	}

	private TableMetaData getTableMetaData( TObjectName tableObjectName )
	{
		String tableName = tableObjectName.getTableString( );
		String tableSchema = tableObjectName.getSchemaString( );
		TableMetaData tableMetaData = new TableMetaData( vendor, strict );
		tableMetaData.setName( tableName );
		tableMetaData.setSchemaName( tableSchema );
		if ( isNotEmpty( tableObjectName.getDatabaseString( ) ) )
		{
			tableMetaData
					.setCatalogName( tableObjectName.getDatabaseString( ) );
		}
		else
			tableMetaData.setCatalogName( database );
		tableMetaData = getTableMetaData( tableMetaData );
		return tableMetaData;
	}

	private ColumnMetaData getColumnMetaData( TableMetaData tableMetaData,
			String columnName )
	{
		ColumnMetaData columnMetaData = new ColumnMetaData( );
		columnMetaData.setName( columnName );
		columnMetaData.setTable( tableMetaData );

		int index = tableColumns.get( tableMetaData ).indexOf( columnMetaData );
		if ( index != -1 )
		{
			columnMetaData = tableColumns.get( tableMetaData ).get( index );
		}
		else
		{
			tableColumns.get( tableMetaData ).add( columnMetaData );
		}
		return columnMetaData;
	}

	private TableMetaData getTableMetaData( TableMetaData tableMetaData )
	{
		List<TableMetaData> tables = Arrays.asList(
				tableColumns.keySet( ).toArray( new TableMetaData[0] ) );
		int index = tables.indexOf( tableMetaData );
		if ( index != -1 )
		{
			return tables.get( index );
		}
		else
		{
			tableColumns.put( tableMetaData, new ArrayList<ColumnMetaData>( ) );
			return tableMetaData;
		}
	}

	private ProcedureMetaData getProcedureMetaData(
			ProcedureMetaData procedureMetaData, boolean replace )
	{
		int index = procedures.second.indexOf( procedureMetaData );
		if ( index != -1 )
		{
			if ( replace )
			{
				procedures.second.remove( index );
				procedures.second.add( procedureMetaData );
				return procedureMetaData;
			}
			else
				return procedures.second.get( index );
		}
		else
		{
			procedures.second.add( procedureMetaData );
			return procedureMetaData;
		}
	}

	private void parseCommentOn( TCommentOnSqlStmt commentOn )
	{
		if ( commentOn.getDbObjType( ) == TObjectName.ttobjTable )
		{
			String tableName = commentOn.getObjectName( ).getPartString( );
			String tableSchema = commentOn.getObjectName( ).getObjectString( );
			TableMetaData tableMetaData = new TableMetaData( vendor, strict );
			tableMetaData.setName( tableName );
			tableMetaData.setSchemaName( tableSchema );
			if ( isNotEmpty( commentOn.getObjectName( ).getDatabaseString( ) ) )
			{
				tableMetaData.setCatalogName(
						commentOn.getObjectName( ).getDatabaseString( ) );
			}
			else
				tableMetaData.setCatalogName( database );
			tableMetaData = getTableMetaData( tableMetaData );
			tableMetaData.setComment( commentOn.getMessage( ).toString( ) );
		}
		else if ( commentOn.getDbObjType( ) == TObjectName.ttobjColumn )
		{
			ColumnMetaData columnMetaData = new ColumnMetaData( );
			String columnName = commentOn.getObjectName( ).getColumnNameOnly( );
			columnMetaData.setName( columnName );

			TableMetaData tableMetaData = new TableMetaData( vendor, strict );
			if ( isNotEmpty( commentOn.getObjectName( ).getTableString( ) ) )
			{
				tableMetaData.setName(
						commentOn.getObjectName( ).getTableString( ) );
			}
			if ( isNotEmpty( commentOn.getObjectName( ).getSchemaString( ) ) )
			{
				tableMetaData.setSchemaName(
						commentOn.getObjectName( ).getSchemaString( ) );
			}
			if ( isNotEmpty( commentOn.getObjectName( ).getDatabaseString( ) ) )
			{
				tableMetaData.setCatalogName(
						commentOn.getObjectName( ).getSchemaString( ) );
			}
			else
				tableMetaData.setCatalogName( database );
			tableMetaData = getTableMetaData( tableMetaData );
			columnMetaData.setTable( tableMetaData );

			int index = tableColumns.get( tableMetaData )
					.indexOf( columnMetaData );
			if ( index != -1 )
			{
				tableColumns.get( tableMetaData ).get( index ).setComment(
						commentOn.getMessage( ).toString( ) );
			}
			else
			{
				columnMetaData
						.setComment( commentOn.getMessage( ).toString( ) );
				tableColumns.get( tableMetaData ).add( columnMetaData );
			}
		}
	}

	public String getDatabase( )
	{
		return database;
	}
}
