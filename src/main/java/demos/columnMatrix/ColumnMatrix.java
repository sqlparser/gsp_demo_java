
package demos.columnMatrix;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TGroupByItem;
import gudusoft.gsqlparser.nodes.TInExpr;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.nodes.TTrimArgument;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ColumnMatrix
{

	enum ClauseType {
		connectby, groupby, join, orderby, select, startwith, undefine, where
	}

	class columnsInExpr implements IExpressionVisitor
	{

		private List<TColumn> columns;
		private TExpression expr;
		private ColumnMatrix matrix;
		private TCustomSqlStatement stmt;

		public columnsInExpr( ColumnMatrix matrix, TExpression expr,
				List<TColumn> columns, TCustomSqlStatement stmt )
		{
			this.stmt = stmt;
			this.matrix = matrix;
			this.expr = expr;
			this.columns = columns;
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
						expr.preOrderTraverse( this );
					}
				}
			}
		}

		public boolean exprVisit( TParseTreeNode pNode, boolean isLeafNode )
		{
			TExpression lcexpr = (TExpression) pNode;
			if ( lcexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
				columns.add( matrix.attrToColumn( lcexpr, stmt ) );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.between_t )
			{
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.in_t )
			{
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.simple_comparison_t )
			{
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.is_true_t
					|| lcexpr.getExpressionType( ) == EExpressionType.is_false_t )
			{
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.is_t )
			{
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
						expr.preOrderTraverse( this );
					}
					expr = args.getTrimCharacter( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.cast_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null
							&& !expr.toString( ).trim( ).equals( "*" )
							|| func.getFunctionType( ) == EFunctionType.extract_t )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.convert_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					expr = func.getExpr2( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.contains_t
						|| func.getFunctionType( ) == EFunctionType.freetext_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					TInExpr inExpr = func.getInExpr( );
					if ( inExpr.getExprList( ) != null )
					{
						for ( int k = 0; k < inExpr.getExprList( ).size( ); k++ )
						{
							expr = inExpr.getExprList( ).getExpression( k );
							if ( expr.toString( ).trim( ).equals( "*" ) )
								continue;
							expr.preOrderTraverse( this );
						}
						if ( expr != null
								&& !expr.toString( ).trim( ).equals( "*" ) )
						{
							expr.preOrderTraverse( this );
						}
					}
					expr = inExpr.getFunc_expr( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.extractxml_t )
				{
					TExpression expr = func.getXMLType_Instance( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					expr = func.getXPath_String( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					expr = func.getNamespace_String( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}

				if ( func.getFunctionType( ) == EFunctionType.rank_t )
				{
					TOrderByItemList orderByList = func.getOrderByList( );
					for ( int k = 0; k < orderByList.size( ); k++ )
					{
						TExpression expr = orderByList.getOrderByItem( k )
								.getSortKey( );
						if ( expr.toString( ).trim( ).equals( "*" ) )
							continue;
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getArgs( ) != null )
				{
					for ( int k = 0; k < func.getArgs( ).size( ); k++ )
					{
						TExpression expr = func.getArgs( ).getExpression( k );
						if ( expr.toString( ).trim( ).equals( "*" ) )
							continue;
						expr.preOrderTraverse( this );
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
				matrix.analyzeExpressionSubquery( lcexpr );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.case_t )
			{
				TCaseExpression expr = lcexpr.getCaseExpression( );
				TExpression conditionExpr = expr.getInput_expr( );
				if ( conditionExpr != null )
				{
					conditionExpr.preOrderTraverse( this );
				}
				TExpression defaultExpr = expr.getElse_expr( );
				if ( defaultExpr != null )
				{
					defaultExpr.preOrderTraverse( this );
				}
				TWhenClauseItemList list = expr.getWhenClauseItemList( );
				addColumnToList( list, stmt );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.list_t )
			{
				TExpressionList exprs = lcexpr.getExprList( );
				for ( int i = 0; i < exprs.size( ); i++ )
				{
					exprs.getExpression( i ).preOrderTraverse( this );
				}
			}
			return true;
		}

		public void searchColumn( )
		{
			this.expr.preOrderTraverse( this );
		}
	}

	public static interface IMetaDatabaseFilter
	{

		boolean metaDatabaseTableColumn( String tableOwner, String tableName,
				String columnName );
	}

	class matrixInExpr implements IExpressionVisitor
	{

		private TExpression expr;
		private ColumnMatrix matrix;
		private TCustomSqlStatement stmt;

		public matrixInExpr( ColumnMatrix matrix, TExpression expr,
				TCustomSqlStatement stmt )
		{
			this.stmt = stmt;
			this.matrix = matrix;
			this.expr = expr;
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
						expr.preOrderTraverse( this );
					}
				}
			}
		}

		public boolean exprVisit( TParseTreeNode pNode, boolean isLeafNode )
		{
			TExpression lcexpr = (TExpression) pNode;
			if ( lcexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.between_t )
			{
				TExpression leftExpr = lcexpr.getBetweenOperand( );
				List<TColumn> leftColumns = new ArrayList<TColumn>( );
				leftExpr.preOrderTraverse( new columnsInExpr( matrix,
						expr,
						leftColumns,
						stmt ) );

				List<TColumn> rigthColumns = new ArrayList<TColumn>( );
				if ( lcexpr.getLeftOperand( ) != null )
					lcexpr.getLeftOperand( )
							.preOrderTraverse( new columnsInExpr( matrix,
									expr,
									rigthColumns,
									stmt ) );
				if ( lcexpr.getRightOperand( ) != null )
					lcexpr.getRightOperand( )
							.preOrderTraverse( new columnsInExpr( matrix,
									expr,
									rigthColumns,
									stmt ) );

				if ( !leftColumns.isEmpty( ) && !rigthColumns.isEmpty( ) )
				{
					matrix.collectMatrixColumns( leftColumns, rigthColumns );
				}
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.in_t )
			{
				TExpression leftExpr = lcexpr.getLeftOperand( );
				List<TColumn> leftColumns = new ArrayList<TColumn>( );
				leftExpr.preOrderTraverse( new columnsInExpr( matrix,
						expr,
						leftColumns,
						stmt ) );

				List<TColumn> rigthColumns = new ArrayList<TColumn>( );
				TExpression rightExpr = lcexpr.getRightOperand( );
				rightExpr.preOrderTraverse( new columnsInExpr( matrix,
						expr,
						rigthColumns,
						stmt ) );

				if ( !leftColumns.isEmpty( ) && !rigthColumns.isEmpty( ) )
				{
					matrix.collectMatrixColumns( leftColumns, rigthColumns );
				}
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.simple_comparison_t )
			{
				TExpression leftExpr = lcexpr.getLeftOperand( );
				List<TColumn> leftColumns = new ArrayList<TColumn>( );
				leftExpr.preOrderTraverse( new columnsInExpr( matrix,
						expr,
						leftColumns,
						stmt ) );

				List<TColumn> rigthColumns = new ArrayList<TColumn>( );
				TExpression rightExpr = lcexpr.getRightOperand( );
				rightExpr.preOrderTraverse( new columnsInExpr( matrix,
						expr,
						rigthColumns,
						stmt ) );

				if ( !leftColumns.isEmpty( ) && !rigthColumns.isEmpty( ) )
				{
					matrix.collectMatrixColumns( leftColumns, rigthColumns );
				}
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.is_true_t
					|| lcexpr.getExpressionType( ) == EExpressionType.is_false_t )
			{
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.is_t )
			{
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
						expr.preOrderTraverse( this );
					}
					expr = args.getTrimCharacter( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.cast_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null
							&& !expr.toString( ).trim( ).equals( "*" )
							|| func.getFunctionType( ) == EFunctionType.extract_t )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.convert_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					expr = func.getExpr2( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.contains_t
						|| func.getFunctionType( ) == EFunctionType.freetext_t )
				{
					TExpression expr = func.getExpr1( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					TInExpr inExpr = func.getInExpr( );
					if ( inExpr.getExprList( ) != null )
					{
						for ( int k = 0; k < inExpr.getExprList( ).size( ); k++ )
						{
							expr = inExpr.getExprList( ).getExpression( k );
							if ( expr.toString( ).trim( ).equals( "*" ) )
								continue;
							expr.preOrderTraverse( this );
						}
						if ( expr != null
								&& !expr.toString( ).trim( ).equals( "*" ) )
						{
							expr.preOrderTraverse( this );
						}
					}
					expr = inExpr.getFunc_expr( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getFunctionType( ) == EFunctionType.extractxml_t )
				{
					TExpression expr = func.getXMLType_Instance( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					expr = func.getXPath_String( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
					expr = func.getNamespace_String( );
					if ( expr != null && !expr.toString( ).trim( ).equals( "*" ) )
					{
						expr.preOrderTraverse( this );
					}
				}

				if ( func.getFunctionType( ) == EFunctionType.rank_t )
				{
					TOrderByItemList orderByList = func.getOrderByList( );
					for ( int k = 0; k < orderByList.size( ); k++ )
					{
						TExpression expr = orderByList.getOrderByItem( k )
								.getSortKey( );
						if ( expr.toString( ).trim( ).equals( "*" ) )
							continue;
						expr.preOrderTraverse( this );
					}
				}
				else if ( func.getArgs( ) != null )
				{
					for ( int k = 0; k < func.getArgs( ).size( ); k++ )
					{
						TExpression expr = func.getArgs( ).getExpression( k );
						if ( expr.toString( ).trim( ).equals( "*" ) )
							continue;
						expr.preOrderTraverse( this );
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
				matrix.analyzeExpressionSubquery( lcexpr );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.case_t )
			{
				TCaseExpression expr = lcexpr.getCaseExpression( );
				TExpression conditionExpr = expr.getInput_expr( );
				if ( conditionExpr != null )
				{
					conditionExpr.preOrderTraverse( this );
				}
				TExpression defaultExpr = expr.getElse_expr( );
				if ( defaultExpr != null )
				{
					defaultExpr.preOrderTraverse( this );
				}
				TWhenClauseItemList list = expr.getWhenClauseItemList( );
				addColumnToList( list, stmt );
			}
			else if ( lcexpr.getExpressionType( ) == EExpressionType.list_t )
			{
				TExpressionList exprs = lcexpr.getExprList( );
				for ( int i = 0; i < exprs.size( ); i++ )
				{
					exprs.getExpression( i ).preOrderTraverse( this );
				}
			}
			return true;
		}

		public void searchColumn( )
		{
			this.expr.preOrderTraverse( this );
		}
	}

	class MatrixResult
	{

		public String leftTableName;
		public String leftColumnName;
		public String rightTableName;
		public String rightColumnName;

		public MatrixResult( String leftTable, String leftColumnName,
				String rightTable, String rightColumnName )
		{
			this.leftTableName = leftTable;
			this.rightTableName = rightTable;
			this.leftColumnName = leftColumnName;
			this.rightColumnName = rightColumnName;
		}

		public boolean equals( Object o )
		{
			if ( o == this )
				return true;
			if ( o instanceof MatrixResult )
			{
				MatrixResult result = (MatrixResult) o;
				return ( result.leftTableName.equals( this.leftTableName )
						&& result.leftColumnName.equals( this.leftColumnName )
						&& result.rightTableName.equals( this.rightTableName ) && result.rightColumnName.equals( this.rightColumnName ) )
						|| ( result.leftTableName.equals( this.rightTableName )
								&& result.leftColumnName.equals( this.rightColumnName )
								&& result.rightTableName.equals( this.leftTableName ) && result.rightColumnName.equals( this.leftColumnName ) );
			}
			return false;
		}

		public int hashCode( )
		{
			return leftTableName.hashCode( )
					+ leftColumnName.hashCode( )
					+ rightTableName.hashCode( )
					+ rightColumnName.hashCode( );
		}
	}

	static class MetaDatabaseFilter implements IMetaDatabaseFilter
	{

		public boolean metaDatabaseTableColumn( String tableOwner,
				String tableName, String columnName )
		{
			if ( "other_table".equalsIgnoreCase( tableName ) )
			{
				if ( "c1".equalsIgnoreCase( columnName ) )
					return true;
				else
					return false;
			}
			if ( "some_table".equalsIgnoreCase( tableName ) )
			{
				if ( "c1".equalsIgnoreCase( columnName ) )
					return false;
				else
					return true;
			}
			return true;
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
		public TCustomSqlStatement stmt;

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
			System.out.println( "Usage: java ColumnMatrix scriptfile [/o <output file path>]" );
			System.out.println( "/o: Option, write the output stream to the specified file." );
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

		EDbVendor vendor = EDbVendor.dbvoracle;

		ColumnMatrix matrix = new ColumnMatrix( new File( args[0] ),
				vendor,
				null );

		System.out.print( matrix.getMatrixResults( ) );

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

	Set<MatrixResult> matrixResultSet = new HashSet<MatrixResult>( );

	private IMetaDatabaseFilter filter;

	private HashMap<String, TCustomSqlStatement> cteMap = new HashMap<String, TCustomSqlStatement>( );

	public ColumnMatrix( File file, EDbVendor dbVendor,
			IMetaDatabaseFilter filter )
	{
		this.filter = filter;
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqlfilename = file.getAbsolutePath( );;
		analyzeSQL( sqlparser );
	}

	public ColumnMatrix( String sql, EDbVendor dbVendor,
			IMetaDatabaseFilter filter )
	{
		this.filter = filter;
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqltext = sql;
		analyzeSQL( sqlparser );
	}

	private void analyzeCondition( TCustomSqlStatement selectStmt,
			TExpression condition )
	{
		exprToColumn( condition, selectStmt );
	}

	private void analyzeExpressionSubquery( TExpression expr )
	{
		if ( expr != null && expr.getSubQuery( ) != null )
		{
			analyzeSqlStatement( expr.getSubQuery( ), false );
		}
		if ( expr.getLeftOperand( ) != null
				&& expr.getLeftOperand( ).getSubQuery( ) != null )
		{
			analyzeSqlStatement( expr.getLeftOperand( ).getSubQuery( ), false );
		}
		if ( expr.getRightOperand( ) != null
				&& expr.getRightOperand( ).getSubQuery( ) != null )
		{
			analyzeSqlStatement( expr.getRightOperand( ).getSubQuery( ), false );
		}
	}

	void analyzeProcedure( TPlsqlCreateProcedure procedure )
	{
		for ( int i = 0; i < procedure.getStatements( ).size( ); i++ )
		{
			TCustomSqlStatement stmt = procedure.getStatements( ).get( i );
			analyzeSqlStatement( stmt, true );
		}
	}

	private void analyzeSqlStatement( TCustomSqlStatement stmt, boolean isTop )
	{
		if ( isTop )
		{
			cteMap.clear( );
			if ( stmt.getCteList( ) != null && stmt.getCteList( ).size( ) > 0 )
			{
				for ( int i = 0; i < stmt.getCteList( ).size( ); i++ )
				{
					TCTE expression = (TCTE) stmt.getCteList( ).getCTE( i );
					cteMap.put( expression.getTableName( ).toString( ),
							expression.getSubquery( ) );
				}
			}
		}
		if ( stmt instanceof TCommonBlock)
		{
			TCommonBlock block = (TCommonBlock) stmt;
			if ( block.getBodyStatements( ) != null )
			{
				for ( int i = 0; i < block.getBodyStatements( ).size( ); i++ )
				{
					analyzeSqlStatement( block.getBodyStatements( ).get( i ),
							true );
				}
			}
		}
		else if ( stmt instanceof TCreateTableSqlStatement )
		{

		}
		else if ( stmt instanceof TInsertSqlStatement )
		{
			TInsertSqlStatement insertStmt = (TInsertSqlStatement) stmt;
			if ( insertStmt.getSubQuery( ) != null )
			{
				analyzeSqlStatement( insertStmt.getSubQuery( ), false );
			}
		}
		else if ( stmt instanceof TUpdateSqlStatement )
		{
			TUpdateSqlStatement updateStmt = (TUpdateSqlStatement) stmt;
			if ( updateStmt.getWhereClause( ) != null
					&& updateStmt.getWhereClause( ).getCondition( ) != null )
			{
				analyzeCondition( updateStmt, updateStmt.getWhereClause( )
						.getCondition( ) );
			}
			if ( updateStmt.getResultColumnList( ) != null )
			{
				for ( int i = 0; i < updateStmt.getResultColumnList( ).size( ); i++ )
				{
					TExpression expr = updateStmt.getResultColumnList( )
							.getResultColumn( i )
							.getExpr( );
					analyzeCondition( updateStmt, expr );
					analyzeExpressionSubquery( expr );
				}
			}
		}
		else if ( stmt instanceof TDeleteSqlStatement )
		{
			TDeleteSqlStatement deleteStmt = (TDeleteSqlStatement) stmt;
			if ( deleteStmt.getWhereClause( ) != null
					&& deleteStmt.getWhereClause( ).getCondition( ) != null )
			{
				analyzeCondition( deleteStmt, deleteStmt.getWhereClause( )
						.getCondition( ) );
			}
		}
		else if ( stmt instanceof TDropTableSqlStatement )
		{
			TDropTableSqlStatement dropStmt = (TDropTableSqlStatement) stmt;
			if ( dropStmt.getWhereClause( ) != null
					&& dropStmt.getWhereClause( ).getCondition( ) != null )
			{
				analyzeCondition( dropStmt, dropStmt.getWhereClause( )
						.getCondition( ) );
			}

		}
		else if ( stmt instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement selectStmt = (TSelectSqlStatement) stmt;

			if ( selectStmt.getSetOperator( ) != TSelectSqlStatement.SET_OPERATOR_NONE)
			{
				analyzeSqlStatement( selectStmt.getLeftStmt( ), false );
				analyzeSqlStatement( selectStmt.getRightStmt( ), false );
			}

			if ( selectStmt.getResultColumnList( ) != null )
			{
				for ( int i = 0; i < selectStmt.getResultColumnList( ).size( ); i++ )
				{
					TExpression expr = selectStmt.getResultColumnList( )
							.getResultColumn( i )
							.getExpr( );
					analyzeCondition( selectStmt, expr );
					analyzeExpressionSubquery( expr );
				}
			}
			if ( selectStmt.getWhereClause( ) != null
					&& selectStmt.getWhereClause( ).getCondition( ) != null )
			{
				analyzeCondition( selectStmt, selectStmt.getWhereClause( )
						.getCondition( ) );
			}
			if ( selectStmt.getGroupByClause( ) != null
					&& selectStmt.getGroupByClause( ).getHavingClause( ) != null )
			{
				analyzeCondition( selectStmt, selectStmt.getGroupByClause( )
						.getHavingClause( ) );
			}
			if ( selectStmt.joins != null )
			{
				for ( int i = 0; i < selectStmt.joins.size( ); i++ )
				{
					TJoin join = selectStmt.joins.getJoin( i );
					if ( join.getJoinItems( ) != null )
					{
						for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
						{
							TExpression onCondition = join.getJoinItems( )
									.getJoinItem( j )
									.getOnCondition( );
							if ( onCondition != null )
							{
								analyzeCondition( selectStmt, onCondition );
							}
						}
					}
				}
			}
		}
		else
		{
			// System.out.println( stmt.toString( ) );
		}
	}

	private TColumn attrToColumn( TExpression attr, TCustomSqlStatement stmt )
	{
		TColumn column = new TColumn( );
		column.columnName = attr.getEndToken( ).toString( );
		column.stmt = stmt;
		column.location = new Point( (int) attr.getEndToken( ).lineNo,
				(int) attr.getEndToken( ).columnNo );

		if ( attr.toString( ).indexOf( "." ) > 0 )
		{
			column.columnPrex = attr.toString( ).substring( 0,
					attr.toString( ).lastIndexOf( "." ) );

			String tableName = column.columnPrex;
			if ( tableName.indexOf( "." ) > 0 )
			{
				tableName = tableName.substring( tableName.lastIndexOf( "." ) + 1 );
			}
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
				if ( filter != null )
				{
					if ( filter.metaDatabaseTableColumn( table.prefixName,
							table.tableName,
							column.columnName ) )
					{
						column.tableNames.add( table.tableName );
					}
				}
				else
				{
					if ( i > 0 )
						continue;
					if ( !column.tableNames.contains( table.tableName ) )
					{
						column.tableNames.add( table.tableName );
					}
				}
			}
		}

		column.orignColumn = column.columnName;

		return column;
	}

	public void collectMatrixColumns( List<TColumn> leftColumns,
			List<TColumn> rigthColumns )
	{
		for ( int i = 0; i < leftColumns.size( ); i++ )
		{
			TColumn leftColumn = leftColumns.get( i );
			String[] leftTables = getColumnTables( leftColumn );
			for ( int j = 0; j < rigthColumns.size( ); j++ )
			{
				TColumn rightColumn = rigthColumns.get( i );
				String[] rightTables = getColumnTables( rightColumn );

				for ( int x = 0; x < leftTables.length; x++ )
				{
					String leftTable = leftTables[x];
					if ( leftTable == null )
						continue;
					for ( int y = 0; y < rightTables.length; y++ )
					{
						String rightTable = rightTables[y];
						if ( !leftTable.equalsIgnoreCase( rightTable ) )
						{
							if ( rightTable == null )
								continue;
							matrixResultSet.add( new MatrixResult( leftTable,
									leftColumn.columnName,
									rightTable,
									rightColumn.columnName ) );
						}
					}
				}
			}
		}
	}

	private List<TColumn> exprToColumn( TExpression expr,
			TCustomSqlStatement stmt )
	{
		List<TColumn> columns = new ArrayList<TColumn>( );

		matrixInExpr c = new matrixInExpr( this, expr, stmt );
		c.searchColumn( );

		return columns;
	}

	private String[] getColumnTables( TColumn column )
	{
		List<String> tableNames = column.tableNames;
		Set<String> tableNameSet = new HashSet<String>( );
		for ( int i = 0; i < tableNames.size( ); i++ )
		{
			tableNameSet.add( getTableName( tableNames.get( i ), column.stmt ) );
		}
		return tableNameSet.toArray( new String[0] );
	}

	public String getMatrixResults( )
	{
		Document doc = null;
		Element matrixResults = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder( );
			doc = builder.newDocument( );
			doc.setXmlVersion( "1.0" );
			matrixResults = doc.createElement( "matrixResults" );
			doc.appendChild( matrixResults );

			Iterator<MatrixResult> iter = matrixResultSet.iterator( );
			while ( iter.hasNext( ) )
			{
				MatrixResult result = iter.next( );
				Element matrixResult = doc.createElement( "matrixResult" );
				matrixResult.setAttribute( "leftTable", result.leftTableName );
				matrixResult.setAttribute( "leftColumn", result.leftColumnName );
				matrixResult.setAttribute( "rightTable", result.rightTableName );
				matrixResult.setAttribute( "rightColumn",
						result.rightColumnName );
				matrixResults.appendChild( matrixResult );
			}

			return format(doc, 2);
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		return "";
	}

	private String getTableName( String name, TCustomSqlStatement stmt )
	{
		while ( stmt != null )
		{
			TTableList tables = stmt.tables;
			for ( int i = 0; i < tables.size( ); i++ )
			{
				TTable table = tables.getTable( i );
				if ( table.getName( ).equalsIgnoreCase( name )
						|| table.getAliasClause( )
								.getAliasName( )
								.toString( )
								.equalsIgnoreCase( name ) )
				{
					if ( table.isBaseTable( ) )
						return table.getFullName( );
					else if ( table.getSubquery( ) != null )
					{
						return getTableName( table.getSubquery( ) );
					}
				}
			}
			stmt = stmt.getParentStmt( );
		}
		return null;
	}

	private String getTableName( TSelectSqlStatement query )
	{
		TTable table = query.getTargetTable( );
		if ( table.getSubquery( ) != null )
			return getTableName( table.getSubquery( ) );
		else
			return table.getFullName( );
	}

	private void analyzeSQL( TGSqlParser sqlparser )
	{
		int ret = sqlparser.parse( );

		if ( ret != 0 )
		{
			System.err.println( sqlparser.getErrormessage( ) + "\r\n" );
		}
		for ( int i = 0; i < sqlparser.sqlstatements.size( ); i++ )
		{
			TCustomSqlStatement sql = sqlparser.sqlstatements.get( i );
			if ( sql instanceof TPlsqlCreateProcedure )
			{
				analyzeProcedure( (TPlsqlCreateProcedure) sql );
			}
			else
			{
				analyzeSqlStatement( sql, true );
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
	
	private String format( Document doc, int indent ) throws Exception
	{
		DOMSource domSource = new DOMSource( doc );
		Transformer transformer = TransformerFactory.newInstance( )
				.newTransformer( );
		transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
		transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
		transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
		transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount",
				String.valueOf( indent ) );
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		StringWriter sw = new StringWriter( );
		StreamResult sr = new StreamResult( sw );
		transformer.transform( domSource, sr );
		String result = sw.toString( ).trim( );
		sw.close( );
		return result;
	}
}
