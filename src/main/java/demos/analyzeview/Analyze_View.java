
package demos.analyzeview;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TGroupByItem;
import gudusoft.gsqlparser.nodes.TInExpr;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.nodes.TTrimArgument;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class Analyze_View
{

	class columnsInExpr implements IExpressionVisitor
	{

		private List<TColumn> columns;
		private TExpression expr;
		private TCustomSqlStatement stmt;
		private TResultColumn field;

		public columnsInExpr( TResultColumn field, TExpression expr,
				List<TColumn> columns, TCustomSqlStatement stmt )
		{
			this.stmt = stmt;
			this.expr = expr;
			this.columns = columns;
			this.field = field;
		}

		private void addColumnToList( TParseTreeNodeList list,
				TCustomSqlStatement stmt )
		{
			if ( list != null )
			{
				for ( int i = 0; i < list.size( ); i++ )
				{
					List<TExpression> exprList = new ArrayList<TExpression>( );
					Object element = list.getElement( i );

					if ( element instanceof TGroupByItem )
					{
						exprList.add( ( (TGroupByItem) element ).getExpr( ) );
					}
					if ( element instanceof TOrderByItem )
					{
						exprList.add( ( (TOrderByItem) element ).getSortKey( ) );
					}
					else if ( element instanceof TExpression )
					{
						exprList.add( (TExpression) element );
					}
					else if ( element instanceof TWhenClauseItem )
					{
						exprList.add( ( (TWhenClauseItem) element ).getComparison_expr( ) );
						exprList.add( ( (TWhenClauseItem) element ).getReturn_expr( ) );
					}

					for ( TExpression expr : exprList )
					{
						expr.inOrderTraverse( this );
					}
				}
			}
		}

		public boolean exprVisit( TParseTreeNode pNode, boolean isLeafNode )
		{
			TExpression lcexpr = (TExpression) pNode;
			if ( lcexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
				columns.add( attrToColumn( lcexpr, stmt ) );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.between_t )
			{
				columns.add( attrToColumn( lcexpr.getBetweenOperand( ), stmt ) );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.function_t )
			{
				TFunctionCall func = (TFunctionCall) lcexpr.getFunctionCall( );
				if ( func.getFunctionType( ) == EFunctionType.trim_t )
				{
					TTrimArgument args = func.getTrimArgument( );
					TExpression expr = args.getStringExpression( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
					expr = args.getTrimCharacter( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.cast_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null
							&& !expr.toString( ).trim( ).equals( "*" )
							|| func.getFunctionType( ) == EFunctionType.extract_t )
					{
						expr.inOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.convert_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
					expr = func.getExpr2( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.contains_t
						|| func.getFunctionType( ) == EFunctionType.freetext_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
					TInExpr inExpr = func.getInExpr( );
					if ( inExpr.getExprList( ) != null )
					{
						for ( int k = 0; k < inExpr.getExprList( ).size( ); k++ )
						{
							expr = inExpr.getExprList( ).getExpression( k );
							if ( expr.toString( ).trim( ).equals( "*" ) )
								continue;
							expr.inOrderTraverse( this );
						}
						if ( expr != null
								&& !expr.toString( ).trim( ).equals( "*" ) )
						{
							expr.inOrderTraverse( this );
						}
					}
					expr = inExpr.getFunc_expr( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.extractxml_t )
				{
					TExpression expr = func.getXMLType_Instance( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
					expr = func.getXPath_String( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
					expr = func.getNamespace_String( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.inOrderTraverse( this );
					}
				}

				if ( func.getFunctionType( ) == EFunctionType.rank_t )
				{
					TOrderByItemList orderByList = func.getOrderByList( );
					if ( orderByList != null )
					{
						for ( int k = 0; k < orderByList.size( ); k++ )
						{
							TExpression expr = orderByList.getOrderByItem( k )
									.getSortKey( );
							if ( expr.toString( ).trim( ).equals( "*" ) )
								continue;
							expr.inOrderTraverse( this );
						}
					}
				}
				else if ( func.getArgs( ) != null )
				{
					for ( int k = 0; k < func.getArgs( ).size( ); k++ )
					{
						TExpression expr = func.getArgs( ).getExpression( k );
						if ( expr.toString( ).trim( ).equals( "*" ) )
							continue;
						expr.inOrderTraverse( this );
					}
				}
				if ( func.getAnalyticFunction( ) != null )
				{
					TParseTreeNodeList list = func.getAnalyticFunction( )
							.getPartitionBy_ExprList( );
					addColumnToList( list, stmt );

					if ( func.getAnalyticFunction( ).getOrderBy( ) != null )
					{
						list = func.getAnalyticFunction( )
								.getOrderBy( )
								.getItems( );
						addColumnToList( list, stmt );
					}
				}

			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.subquery_t )
			{

				for ( int i = 0; i < ( (TSelectSqlStatement) lcexpr.getSubQuery( ) ).getResultColumnList( )
						.size( ); i++ )
				{
					linkFieldToTables( field,
							( (TSelectSqlStatement) lcexpr.getSubQuery( ) ).getResultColumnList( )
									.getResultColumn( i ),
							(TSelectSqlStatement) lcexpr.getSubQuery( ) );
				}

			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.case_t )
			{
				TCaseExpression expr = lcexpr.getCaseExpression( );
				TExpression conditionExpr = expr.getInput_expr( );
				if ( conditionExpr != null )
				{
					conditionExpr.inOrderTraverse( this );
				}
				TExpression defaultExpr = expr.getElse_expr( );
				if ( defaultExpr != null )
				{
					defaultExpr.inOrderTraverse( this );
				}
				TWhenClauseItemList list = expr.getWhenClauseItemList( );
				addColumnToList( list, stmt );
			}
			return true;
		}

		public void searchColumn( )
		{
			this.expr.inOrderTraverse( this );
		}
	}

	class Table
	{

		public String prefixName;
		public String tableAlias;
		public String tableName;
	}

	class TColumn
	{

		public String columnName;
		public String columnPrex;
		public String orignColumn;
		public Point location;
		public List<String> tableNames = new ArrayList<String>( );

		public String getFullName( String tableName )
		{
			if ( tableName != null )
			{
				return tableName + "." + columnName;
			}
			else
			{
				return columnName;
			}
		}

		public String getOrigName( )
		{
			if ( columnPrex != null )
			{
				return columnPrex + "." + columnName;
			}
			else
			{
				return columnName;
			}
		}

	}

	public static void main( String[] args )
	{
		if ( args.length == 0 )
		{
			System.out.println( "Usage: java Analyze_View scriptfile [/o <output file path>]" );
			System.out.println( "/o: Option, write the output stream to the specified file." );
			// Console.Read();
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

		EDbVendor vendor = EDbVendor.dbvteradata;

		index = argList.indexOf( "/t" );

		Analyze_View impact = new Analyze_View( new File( args[0] ), vendor );

		System.out.print( impact.getImpactResult( ) );

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

	private List<TObjectName> outTables = new ArrayList<TObjectName>( );

	private List<TTable> inTables = new ArrayList<TTable>( );

	private Map<TResultColumn, TColumn[]> columnMap = new LinkedHashMap<TResultColumn, TColumn[]>( );

	private StringBuffer result = new StringBuffer( );

	public Analyze_View( File file )
	{
		TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvteradata );
		sqlparser.sqlfilename = file.getAbsolutePath( );
		impactSQL( sqlparser );
	}

	public Analyze_View( File file, EDbVendor dbVendor )
	{
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqlfilename = file.getAbsolutePath( );
		impactSQL( sqlparser );
	}

	private TColumn attrToColumn( TExpression attr, TCustomSqlStatement stmt )
	{
		TColumn column = new TColumn( );
		column.columnName = attr.getEndToken( ).toString( );
		column.location = new Point( (int) attr.getEndToken( ).lineNo,
				(int) attr.getEndToken( ).columnNo );

		if ( attr.toString( ).indexOf( "." ) > 0 )
		{
			column.columnPrex = attr.toString( ).substring( 0,
					attr.toString( ).indexOf( "." ) );

			String tableName = column.columnPrex;
			if ( !column.tableNames.contains( tableName ) )
			{
				column.tableNames.add( tableName );
			}
		}
		else
		{
			TTableList tables = stmt.tables;
			for ( int i = 0; i < tables.size( ); i++ )
			{
				TTable lztable = tables.getTable( i );
				Table table = TLzTaleToTable( lztable );
				if ( !column.tableNames.contains( table.tableName ) )
				{
					column.tableNames.add( table.tableName );
				}
			}
		}

		column.orignColumn = column.columnName;

		return column;
	}

	private List<TColumn> exprToColumn( TResultColumn field, TExpression expr,
			TCustomSqlStatement stmt )
	{
		List<TColumn> columns = new ArrayList<TColumn>( );

		columnsInExpr c = new columnsInExpr( field, expr, columns, stmt );
		c.searchColumn( );

		return columns;
	}

	private String getImpactResult( )
	{
		return result.toString( );
	}

	private void impactSQL( TGSqlParser sqlparser )
	{
		int ret = sqlparser.parse( );

		if ( ret != 0 )
		{
			System.err.println( sqlparser.getErrormessage( ) + "\r\n" );
			return;
		}
		for ( int k = 0; k < sqlparser.sqlstatements.size( ); k++ )
		{
			if ( sqlparser.sqlstatements.get( k ) instanceof TCustomSqlStatement )
			{
				impactSqlFromStatement( (TCustomSqlStatement) sqlparser.sqlstatements.get( k ) );
			}
		}

		result.append( "Output Views Count: " )
				.append( outTables.size( ) )
				.append( "\r\n" );
		result.append( "Output View | Substitution Value" ).append( "\r\n" );
		for ( int i = 0; i < outTables.size( ); i++ )
		{
			result.append( outTables.get( i ).toString( ) )
					.append( " | " )
					.append( "#OUTPUT" )
					.append( i )
					.append( "\r\n" );
		}
		result.append( "\r\n" );
		result.append( "Input Objects Count: " )
				.append( inTables.size( ) )
				.append( "\r\n" );
		result.append( "Input Object | Alias | Substitution Value" )
				.append( "\r\n" );
		for ( int i = 0; i < inTables.size( ); i++ )
		{
			result.append( inTables.get( i ).getFullName( ) )
					.append( " | " )
					.append( toString(inTables.get( i ).getAliasClause( )) )
					.append( " | " )
					.append( "#INPUT" )
					.append( i )
					.append( "\r\n" );
		}
		result.append( "\r\n" );
		result.append( "Output column | Source column | Location" )
				.append( "\r\n" );
		Iterator<TResultColumn> iter = columnMap.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			TResultColumn field = iter.next( );
			StringBuilder buffer = new StringBuilder( );
			if ( field.getAliasClause( ) != null )
				buffer.append( field.getAliasClause( ) ).append( " | " );
			else if ( field.getExpr( ).getSubQuery( ) != null )
			{
				buffer.append( field.toString( ).substring( 0, 15 ) )
						.append( "... | " );
			}
			else
			{
				buffer.append( field.toString( ) ).append( " | " );
			}
			TColumn[] columns = columnMap.get( field );
			for ( int i = 0; i < columns.length; i++ )
			{
				StringBuilder columnBuffer = new StringBuilder( buffer );
				for ( int j = 0; j < columns[i].tableNames.size( ); j++ )
				{
					if ( j != 0 )
						columnBuffer.append( "," );
					String tableName = columns[i].tableNames.get( j );
					columnBuffer.append( columns[i].getFullName( tableName )
							.toString( ) );
				}
				columnBuffer.append( " | " )
						.append( "Line:" )
						.append( columns[i].location.x )
						.append( "," )
						.append( "Column:" )
						.append( columns[i].location.y );
				result.append( columnBuffer ).append( "\r\n" );
			}
		}

		result.append( "\r\n" );
		result.append( "\r\n" );

		for ( int i = 0; i < outTables.size( ); i++ )
		{
			outTables.get( i ).fastSetString( "#OUTPUT" + i );
		}

		for ( int i = 0; i < inTables.size( ); i++ )
		{
			inTables.get( i ).fastSetString( "#INPUT" + i );
		}

		for ( int k = 0; k < sqlparser.sqlstatements.size( ); k++ )
		{
			result.append( sqlparser.sqlstatements.get( k ).toString( ) )
					.append( "\r\n" );
		}
	}

	private Object toString( Object obj )
	{
		return obj == null ? "" : obj.toString( );
	}

	private void impactSqlFromStatement( TCustomSqlStatement select )
	{
		if ( select instanceof TCreateViewSqlStatement )
		{
			TCreateViewSqlStatement createView = (TCreateViewSqlStatement) select;
			outTables.add( createView.getViewName( ) );
			if ( createView.getStatements( ) != null )
			{
				for ( int i = 0; i < createView.getStatements( ).size( ); i++ )
				{
					impactSqlFromStatement( createView.getStatements( ).get( i ) );
				}
			}
		}
		else if ( select instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement stmt = (TSelectSqlStatement) select;
			if ( stmt.getSetOperator( ) != TSelectSqlStatement.SET_OPERATOR_NONE)
			{
				impactSqlFromStatement( stmt.getLeftStmt( ) );
				impactSqlFromStatement( stmt.getRightStmt( ) );
			}
			else
			{
				TTableList tables = stmt.tables;
				if ( tables != null )
				{
					for ( int i = 0; i < tables.size( ); i++ )
					{
						TTable table = tables.getTable( i );
						if ( table.getSubquery( ) == null )
						{
							inTables.add( table );
						}
						else
						{
							impactSqlFromStatement( table.getSubquery( ) );
						}
					}
				}
				for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
				{
					TResultColumn column = select.getResultColumnList( )
							.getResultColumn( i );
					linkFieldToTables( null, column, select );
				}
			}
		}
		else if ( select instanceof TInsertSqlStatement
				&& ( (TInsertSqlStatement) select ).getSubQuery( ) != null )
		{
			impactSqlFromStatement( ( (TInsertSqlStatement) select ).getSubQuery( ) );
		}
	}

	private void linkFieldToTables( TResultColumn origin, TResultColumn field,
			TCustomSqlStatement select )
	{
		switch ( field.getExpr( ).getExpressionType( ) )
		{
			case simple_object_name_t :
			{
				TColumn column = attrToColumn( field.getExpr( ), select );
				if ( origin != null )
				{
					columnMap.put( origin, new TColumn[]{
						column
					} );
				}
				columnMap.put( field, new TColumn[]{
					column
				} );

			}
				break;
			case subquery_t :
			{
				TSelectSqlStatement stmt = (TSelectSqlStatement) field.getExpr( )
						.getSubQuery( );
				for ( int i = 0; i < stmt.getResultColumnList( ).size( ); i++ )
				{
					TResultColumn column = stmt.getResultColumnList( )
							.getResultColumn( i );
					linkFieldToTables( origin == null ? field : origin,
							column,
							select );
				}
			}
				break;
			default :
			{
				List<TColumn> columns = exprToColumn( origin == null ? field
						: origin, field.getExpr( ), select );
				if ( origin != null )
				{
					columnMap.put( origin, columns.toArray( new TColumn[0] ) );
				}
				columnMap.put( field, columns.toArray( new TColumn[0] ) );
			}
		}
	}

	private Table TLzTaleToTable( TTable lztable )
	{
		Table table = new Table( );
		if ( lztable.getSubquery( ) == null && lztable.getTableName( ) != null )
		{
			table.tableName = lztable.getName( );
			if ( lztable.getTableName( ).toString( ).indexOf( "." ) > 0 )
			{
				table.prefixName = lztable.getTableName( )
						.toString( )
						.substring( 0, lztable.getFullName( ).indexOf( '.' ) );
			}
		}

		if ( lztable.getAliasClause( ) != null )
		{
			table.tableAlias = lztable.getAliasClause( ).toString( );
		}
		return table;
	}

}
