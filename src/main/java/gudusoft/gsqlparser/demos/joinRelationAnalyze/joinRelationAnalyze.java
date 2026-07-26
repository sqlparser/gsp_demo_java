
package demos.joinRelationAnalyze;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EJoinType;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

enum ClauseType {
	where, connectby, startwith, orderby, casewhen, casethen
}

class JoinCondition
{

	public String lefttable, righttable, leftcolumn, rightcolumn,
			leftTableLocation, rightTableLocation, leftColumnLocation,
			rightColumnLocation, joinType, operator;
	public List<TCustomSqlStatement> sql = new ArrayList<TCustomSqlStatement>( );

	public int hashCode( )
	{
		int hashCode = 0;
		if ( lefttable != null )
			hashCode += lefttable.hashCode( );
		if ( righttable != null )
			hashCode += righttable.hashCode( );
		if ( leftcolumn != null )
			hashCode += leftcolumn.hashCode( );
		if ( rightcolumn != null )
			hashCode += rightcolumn.hashCode( );

		for ( TCustomSqlStatement stmt : sql )
		{
			hashCode += stmt.hashCode( );
		}

		return hashCode;
	}

	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( !( obj instanceof JoinCondition ) )
			return false;

		JoinCondition join = (JoinCondition) obj;

		if ( this.leftcolumn != null
				&& !this.leftcolumn.equals( join.leftcolumn ) )
			return false;
		if ( this.rightcolumn != null
				&& !this.rightcolumn.equals( join.rightcolumn ) )
			return false;
		if ( this.lefttable != null && !this.lefttable.equals( join.lefttable ) )
			return false;
		if ( this.righttable != null
				&& !this.righttable.equals( join.righttable ) )
			return false;

		if ( join.righttable != null
				&& !join.righttable.equals( this.righttable ) )
			return false;
		if ( join.lefttable != null && !join.lefttable.equals( this.lefttable ) )
			return false;
		if ( join.rightcolumn != null
				&& !join.rightcolumn.equals( this.rightcolumn ) )
			return false;
		if ( join.leftcolumn != null
				&& !join.leftcolumn.equals( this.leftcolumn ) )
			return false;

		if ( join.sql.size( ) != this.sql.size( ) )
			return false;
		for ( int i = 0; i < join.sql.size( ); i++ )
		{
			if ( !join.sql.get( i ).equals( this.sql.get( i ) ) )
				return false;
		}

		return true;
	}
}

class TColumn
{

	public List<String> tableNames = new ArrayList<String>( );
	public String columnName;
	public String columnPrex;
	public String columnAlias;
	public String columnLocation;
	public String tableLocation;

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

class TTable
{

	public String tableName;
	public String prefixName;
	public String tableAlias;
	public String tableLocation;
}

class joinConditonsInExpr implements IExpressionVisitor
{

	private TExpression expr;
	private joinRelationAnalyze analysis;
	private TCustomSqlStatement stmt;
	private String joinType;

	public joinConditonsInExpr( joinRelationAnalyze analysis, TExpression expr,
			TCustomSqlStatement stmt, String joinType )
	{
		this.stmt = stmt;
		this.analysis = analysis;
		this.expr = expr;
		this.joinType = joinType;
	}

	boolean is_compare_condition( EExpressionType t )
	{
		return ( ( t == EExpressionType.simple_comparison_t )
				|| ( t == EExpressionType.group_comparison_t ) || ( t == EExpressionType.in_t ) );
	}

	private String getExpressionTable( TExpression expr )
	{
		if ( expr.getObjectOperand( ) != null )
			return expr.getObjectOperand( ).getObjectString( );
		else if ( expr.getLeftOperand( ) != null
				&& expr.getLeftOperand( ).getObjectOperand( ) != null )
			return expr.getLeftOperand( ).getObjectOperand( ).getObjectString( );
		else if ( expr.getRightOperand( ) != null
				&& expr.getRightOperand( ).getObjectOperand( ) != null )
			return expr.getRightOperand( )
					.getObjectOperand( )
					.getObjectString( );
		else
			return null;
	}

	private String getExpressionTableLocation( TExpression expr )
	{
		TSourceToken tableToken = null;
		if ( expr.getObjectOperand( ) != null )
		{
			tableToken = expr.getObjectOperand( ).getTableToken( );
		}
		else if ( expr.getLeftOperand( ) != null
				&& expr.getLeftOperand( ).getObjectOperand( ) != null )
		{
			tableToken = expr.getLeftOperand( )
					.getObjectOperand( )
					.getTableToken( );
		}
		else if ( expr.getRightOperand( ) != null
				&& expr.getRightOperand( ).getObjectOperand( ) != null )
		{
			tableToken = expr.getRightOperand( )
					.getObjectOperand( )
					.getTableToken( );
		}
		if ( tableToken != null )
		{
			return "(" + tableToken.lineNo + "," + tableToken.columnNo + ")";
		}
		return null;
	}

	private void analyzeMssqlJoinCondition( TExpression expr )
	{
		String joinType = null;
		if ( expr.getExpressionType( ) == EExpressionType.left_join_t )
		{
			joinType = EJoinType.left.name( );
		}
		if ( expr.getExpressionType( ) == EExpressionType.right_join_t )
		{
			joinType = EJoinType.right.name( );
		}

		dealCompareCondition( expr.getLeftOperand( ),
				expr.getRightOperand( ),
				expr.getOperatorToken( ).toString( ),
				joinType );

	}

	public boolean exprVisit( TParseTreeNode pnode, boolean flag )
	{
		TExpression lcexpr = (TExpression) pnode;

		TExpression slexpr, srexpr, lc_expr = lcexpr;

		if ( lc_expr.getGsqlparser( ).getDbVendor( ) == EDbVendor.dbvmssql )
		{
			if ( lc_expr.getExpressionType( ) == EExpressionType.left_join_t
					|| lc_expr.getExpressionType( ) == EExpressionType.right_join_t )
			{
				analyzeMssqlJoinCondition( lc_expr );
			}
		}

		if ( is_compare_condition( lc_expr.getExpressionType( ) ) )
		{
			slexpr = lc_expr.getLeftOperand( );
			srexpr = lc_expr.getRightOperand( );

			if ( srexpr.getFunctionCall( ) != null
					&& srexpr.getFunctionCall( )
							.getFunctionName( )
							.toString( )
							.equalsIgnoreCase( "ISNULL" ) )
			{
				TExpressionList list = srexpr.getFunctionCall( ).getArgs( );
				for ( int i = 0; i < list.size( ); i++ )
				{
					dealCompareCondition( slexpr,
							list.getExpression( i ),
							lc_expr.getOperatorToken( ).toString( ),
							null );
				}
			}
			else
				dealCompareCondition( slexpr,
						srexpr,
						lc_expr.getOperatorToken( ).toString( ),
						null );
		}

		if ( lcexpr.getExpressionType( ) == EExpressionType.function_t )
		{
			TFunctionCall func = (TFunctionCall) lcexpr.getFunctionCall( );
			if ( func.getArgs( ) != null )
			{
				for ( int k = 0; k < func.getArgs( ).size( ); k++ )
				{
					TExpression expr = func.getArgs( ).getExpression( k );
					expr.inOrderTraverse( this );
				}
			}
			if ( func.getAnalyticFunction( ) != null )
			{
				TParseTreeNodeList list = func.getAnalyticFunction( )
						.getPartitionBy_ExprList( );
				searchJoinInList( list, stmt );

				if ( func.getAnalyticFunction( ).getOrderBy( ) != null )
				{
					list = func.getAnalyticFunction( ).getOrderBy( ).getItems( );
					searchJoinInList( list, stmt );
				}
			}

		}
		else if ( lcexpr.getExpressionType( ) == EExpressionType.subquery_t )
		{
			if ( lcexpr.getSubQuery( ) instanceof TSelectSqlStatement )
			{
				TSelectSqlStatement query = lcexpr.getSubQuery( );
				analysis.searchSubQuery( query );
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
			searchJoinInList( list, stmt );
		}
		else if ( lcexpr.getExpressionType( ) == EExpressionType.exists_t )
		{
			if ( lcexpr.getRightOperand( ) != null
					&& lcexpr.getRightOperand( ).getSubQuery( ) != null )
			{
				TSelectSqlStatement query = lcexpr.getRightOperand( )
						.getSubQuery( );
				analysis.searchSubQuery( query );
			}
		}
		return true;
	}

	private void dealCompareCondition( TExpression slexpr, TExpression srexpr,
			String operator, String type )
	{
		if ( ( ( slexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
				|| ( slexpr.isOracleOuterJoin( ) ) || ( srexpr.isOracleOuterJoin( ) && slexpr.getExpressionType( ) == EExpressionType.simple_constant_t ) )
				&& ( ( srexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
						|| ( srexpr.isOracleOuterJoin( ) )
						|| ( slexpr.isOracleOuterJoin( ) && srexpr.getExpressionType( ) == EExpressionType.simple_constant_t ) || ( slexpr.isOracleOuterJoin( ) && srexpr.getExpressionType( ) == EExpressionType.case_t ) )
				|| ( slexpr.getExpressionType( ) == EExpressionType.simple_object_name_t && srexpr.getExpressionType( ) == EExpressionType.subquery_t )
				|| ( slexpr.getExpressionType( ) == EExpressionType.subquery_t && srexpr.getExpressionType( ) == EExpressionType.simple_object_name_t ) )
		{
			TExpression lattr = null, rattr = null;
			JoinCondition jr = new JoinCondition( );
			jr.sql.add( stmt );
			jr.operator = operator;

			if ( type != null )
			{
				jr.joinType = type;
			}
			else if ( joinType != null )
			{
				jr.joinType = joinType;
			}

			if ( slexpr.isOracleOuterJoin( ) )
			{
				lattr = slexpr;
				jr.lefttable = lattr != null ? getExpressionTable( lattr )
						: null;
				jr.leftTableLocation = lattr != null ? getExpressionTableLocation( lattr )
						: null;

				TSourceToken columnToken = getBeforeToken( lattr.getEndToken( ) );
				jr.leftcolumn = lattr != null ? columnToken.toString( ) : null;
				if ( columnToken != null )
				{
					jr.leftColumnLocation = "("
							+ columnToken.lineNo
							+ ","
							+ columnToken.columnNo
							+ ")";
				}
				jr.joinType = EJoinType.leftouter.name( );
			}
			else if ( slexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
				lattr = slexpr;
				jr.lefttable = lattr != null ? getExpressionTable( lattr )
						: null;
				jr.leftTableLocation = lattr != null ? getExpressionTableLocation( lattr )
						: null;

				jr.leftcolumn = lattr != null ? lattr.getEndToken( ).toString( )
						: null;

				TSourceToken columnToken = lattr.getEndToken( );
				if ( columnToken != null )
				{
					jr.leftColumnLocation = "("
							+ columnToken.lineNo
							+ ","
							+ columnToken.columnNo
							+ ")";
				}
			}

			if ( srexpr.isOracleOuterJoin( ) )
			{
				rattr = srexpr;
				jr.righttable = rattr != null ? getExpressionTable( rattr )
						: null;
				jr.rightTableLocation = rattr != null ? getExpressionTableLocation( rattr )
						: null;

				TSourceToken columnToken = getBeforeToken( rattr.getEndToken( ) );
				jr.rightcolumn = rattr != null ? columnToken.toString( ) : null;
				if ( columnToken != null )
				{
					jr.rightColumnLocation = "("
							+ columnToken.lineNo
							+ ","
							+ columnToken.columnNo
							+ ")";
				}

				if ( slexpr.getExpressionType( ) != EExpressionType.subquery_t )
				{
					analysis.joinRelationSet.add( jr );
				}
				jr.joinType = EJoinType.rightouter.name( );
			}
			else if ( srexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
			{
				rattr = srexpr;
				jr.righttable = rattr != null ? getExpressionTable( rattr )
						: null;
				jr.rightTableLocation = rattr != null ? getExpressionTableLocation( rattr )
						: null;
				jr.rightcolumn = rattr != null ? rattr.getEndToken( )
						.toString( ) : null;

				TSourceToken columnToken = rattr.getEndToken( );
				if ( columnToken != null )
				{
					jr.rightColumnLocation = "("
							+ columnToken.lineNo
							+ ","
							+ columnToken.columnNo
							+ ")";
				}

				if ( slexpr.getExpressionType( ) != EExpressionType.subquery_t )
				{
					analysis.joinRelationSet.add( jr );
				}
			}
			else if ( srexpr.getExpressionType( ) == EExpressionType.case_t )
			{
				TCaseExpression expr = srexpr.getCaseExpression( );

				TWhenClauseItemList list = expr.getWhenClauseItemList( );
				for ( int i = 0; i < list.size( ); i++ )
				{
					TExpression thenexpr = ( (TWhenClauseItem) list.getWhenClauseItem( i ) ).getReturn_expr( );
					if ( thenexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
					{
						rattr = thenexpr;
					}
					JoinCondition condtion = new JoinCondition( );
					condtion.leftcolumn = jr.leftcolumn;
					condtion.lefttable = jr.lefttable;
					condtion.leftTableLocation = jr.leftTableLocation;
					condtion.leftColumnLocation = jr.leftColumnLocation;
					condtion.sql = jr.sql;
					condtion.righttable = rattr != null ? getExpressionTable( rattr )
							: null;
					condtion.rightTableLocation = rattr != null ? getExpressionTableLocation( rattr )
							: null;

					if ( rattr != null )
					{
						if ( rattr.isOracleOuterJoin( ) )
						{
							condtion.joinType = EJoinType.rightouter.name( );
							TSourceToken columnToken = getBeforeToken( rattr.getEndToken( ) );
							condtion.rightcolumn = columnToken.toString( );
							if ( columnToken != null )
							{
								jr.rightColumnLocation = "("
										+ columnToken.lineNo
										+ ","
										+ columnToken.columnNo
										+ ")";
							}
						}
						else
						{
							condtion.rightcolumn = rattr.getEndToken( )
									.toString( );
							TSourceToken columnToken = rattr.getEndToken( );
							if ( columnToken != null )
							{
								jr.rightColumnLocation = "("
										+ columnToken.lineNo
										+ ","
										+ columnToken.columnNo
										+ ")";
							}
						}
					}
					else
						condtion.rightcolumn = null;

					analysis.joinRelationSet.add( condtion );
				}
				if ( expr.getElse_expr( ) != null )
				{
					TExpression elseexpr = expr.getElse_expr( );
					if ( elseexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
					{
						rattr = elseexpr;
					}

					JoinCondition condtion = new JoinCondition( );
					condtion.leftcolumn = jr.leftcolumn;
					condtion.lefttable = jr.lefttable;
					condtion.leftColumnLocation = jr.leftColumnLocation;
					condtion.leftTableLocation = jr.leftTableLocation;
					condtion.sql = jr.sql;
					condtion.righttable = rattr != null ? getExpressionTable( rattr )
							: null;
					condtion.rightTableLocation = rattr != null ? getExpressionTableLocation( rattr )
							: null;
					if ( rattr != null )
					{
						if ( rattr.isOracleOuterJoin( ) )
						{
							TSourceToken columnToken = getBeforeToken( rattr.getEndToken( ) );
							condtion.rightcolumn = columnToken.toString( );
							if ( columnToken != null )
							{
								jr.rightColumnLocation = "("
										+ columnToken.lineNo
										+ ","
										+ columnToken.columnNo
										+ ")";
							}
						}
						else
						{
							condtion.rightcolumn = rattr.getEndToken( )
									.toString( );
							TSourceToken columnToken = rattr.getEndToken( );
							if ( columnToken != null )
							{
								jr.rightColumnLocation = "("
										+ columnToken.lineNo
										+ ","
										+ columnToken.columnNo
										+ ")";
							}
						}
					}
					else
						condtion.rightcolumn = null;
					analysis.joinRelationSet.add( condtion );
				}
			}

			if ( srexpr.getExpressionType( ) == EExpressionType.subquery_t )
			{
				TSelectSqlStatement subquery = (TSelectSqlStatement) srexpr.getSubQuery( );
				addSubqueryJoin( jr, subquery, false );
			}

			if ( slexpr.getExpressionType( ) == EExpressionType.subquery_t )
			{
				TSelectSqlStatement subquery = (TSelectSqlStatement) slexpr.getSubQuery( );
				addSubqueryJoin( jr, subquery, true );
			}
		}
	}

	private TSourceToken getBeforeToken( TSourceToken token )
	{
		TSourceTokenList tokens = token.container;
		int index = token.posinlist;

		for ( int i = index - 1; i >= 0; i-- )
		{
			TSourceToken currentToken = tokens.get( i );
			if ( currentToken.toString( ).trim( ).length( ) == 0 )
			{
				continue;
			}
			else
			{
				return currentToken;
			}
		}
		return token;
	}

	private void addSubqueryJoin( JoinCondition jr,
			TSelectSqlStatement subquery, Boolean isLeft )
	{
		if ( subquery.isCombinedQuery( ) )
		{
			addSubqueryJoin( jr, subquery.getLeftStmt( ), isLeft );
			addSubqueryJoin( jr, subquery.getRightStmt( ), isLeft );
		}
		else
		{
			for ( int i = 0; i < subquery.getResultColumnList( ).size( ); i++ )
			{
				TResultColumn field = subquery.getResultColumnList( )
						.getResultColumn( i );
				TColumn column = analysis.attrToColumn( field, subquery );
				for ( String tableName : column.tableNames )
				{
					JoinCondition condtion = new JoinCondition( );
					if ( isLeft )
					{
						condtion.rightcolumn = jr.rightcolumn;
						condtion.righttable = jr.righttable;
						condtion.rightColumnLocation = jr.rightColumnLocation;
						condtion.rightTableLocation = jr.rightTableLocation;
						condtion.sql.add( stmt );
						condtion.sql.add( subquery );
						condtion.lefttable = tableName;
						condtion.leftcolumn = column.columnName;
						condtion.leftColumnLocation = column.columnLocation;
						condtion.leftTableLocation = column.tableLocation;
					}
					else
					{
						condtion.leftcolumn = jr.leftcolumn;
						condtion.leftColumnLocation = jr.leftColumnLocation;
						condtion.lefttable = jr.lefttable;
						condtion.leftTableLocation = jr.leftTableLocation;
						condtion.sql.add( stmt );
						condtion.sql.add( subquery );
						condtion.righttable = tableName;
						condtion.rightcolumn = column.columnName;
						condtion.rightColumnLocation = column.columnLocation;
						condtion.rightTableLocation = column.tableLocation;
					}
					analysis.joinRelationSet.add( condtion );
				}
			}
		}
	}

	private void searchJoinInList( TParseTreeNodeList list,
			TCustomSqlStatement stmt )
	{
		if ( list != null )
		{
			for ( int i = 0; i < list.size( ); i++ )
			{
				List<TExpression> exprList = new ArrayList<TExpression>( );

				if ( list.getElement( i ) instanceof TOrderByItem )
				{
					exprList.add( (TExpression) ( (TOrderByItem) list.getElement( i ) ).getSortKey( ) );
				}
				else if ( list.getElement( i ) instanceof TExpression )
				{
					exprList.add( (TExpression) list.getElement( i ) );
				}
				else if ( list.getElement( i ) instanceof TWhenClauseItem )
				{
					exprList.add( ( (TWhenClauseItem) list.getElement( i ) ).getComparison_expr( ) );
					exprList.add( ( (TWhenClauseItem) list.getElement( i ) ).getReturn_expr( ) );
				}

				for ( TExpression lcexpr : exprList )
				{
					lcexpr.inOrderTraverse( this );
				}
			}
		}
	}

	public void searchExpression( )
	{
		this.expr.inOrderTraverse( this );
	}
}

public class joinRelationAnalyze
{

	private static boolean isOutputFile;
	private StringBuilder buffer = new StringBuilder( );
	private HashMap cteMap = new HashMap( );
	private HashMap tableAliasMap = new HashMap( );
	private List<TCustomSqlStatement> searchInSubQuerys = new ArrayList<TCustomSqlStatement>( );
	private List<TCustomSqlStatement> searchInTables = new ArrayList<TCustomSqlStatement>( );
	private List<TCustomSqlStatement> searchInClauses = new ArrayList<TCustomSqlStatement>( );
	public HashMap queryAliasMap = new HashMap( );
	public HashSet<JoinCondition> joinRelationSet = new HashSet<JoinCondition>( );
	private List<JoinCondition> conditions = new ArrayList<JoinCondition>( );

	public String getAnalysisResult( )
	{
		return buffer.toString( );
	}

	public List<JoinCondition> getJoinConditions( )
	{
		return conditions;
	}

	public static void main( String[] args )
	{
		if ( args.length == 0 )
		{
			System.out.println( "Usage: joinRelationAnalyze <sql script file path> <output file path> [/t <database type>]" );
			System.out.println( "sql script file path: The sql file will be analyzed." );
			System.out.println( "output file path: Option, write the analysis result to the specified file." );
			System.out.println( "/t: Option, set the database type. Support oracle, mysql, mssql and db2, the default type is oracle" );
			// Console.Read();
			return;
		}

		String outputFile = null;
		FileOutputStream writer = null;
		if ( args.length > 1 && !args[1].equalsIgnoreCase( "/t" ) )
		{
			outputFile = args[1];
			isOutputFile = true;
		}
		try
		{
			if ( outputFile != null )
			{

				writer = new FileOutputStream( outputFile );
				System.setOut( new PrintStream( writer ) );

			}

			EDbVendor vendor = EDbVendor.dbvoracle;

			List<String> argList = Arrays.asList( args );

			int index = argList.indexOf( "/t" );

			if ( index != -1 && args.length > index + 1 )
			{
				vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
			}

			joinRelationAnalyze analysis = new joinRelationAnalyze( new File( args[0] ),
					vendor );
			System.out.print( analysis.getAnalysisResult( ) );
			// if (args.length <= 1)
			// {
			// Console.Read();
			// }
			// else
			{
				if ( writer != null )
				{
					writer.close( );
				}
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

	} // main

	public joinRelationAnalyze( String sql, EDbVendor dbVendor )
	{
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqltext = sql;
		analyzeSQL( sqlparser, false );
	}

	public joinRelationAnalyze( File file, EDbVendor dbVendor )
	{
		TGSqlParser sqlparser = new TGSqlParser( dbVendor );
		sqlparser.sqlfilename = file.getAbsolutePath( );
		analyzeSQL( sqlparser, false );
	}

	public joinRelationAnalyze( TGSqlParser sqlparser, boolean showLocation )
	{
		analyzeSQL( sqlparser, showLocation );
	}

	private void analyzeSQL( TGSqlParser sqlparser, boolean showLocation )
	{
		int ret = sqlparser.parse( );

		if ( ret != 0 )
		{
			buffer.append( sqlparser.getErrormessage( ) );
			return;
		}
		else
		{
			for ( int j = 0; j < sqlparser.getSqlstatements( ).size( ); j++ )
			{
				TCustomSqlStatement select = (TCustomSqlStatement) sqlparser.sqlstatements.get( j );
				analyzeStmt( select );
			}
		}

		buffer.append( "JoinTable1\tJoinColumn1\tJoinTable2\tJoinColumn2\tJoinType\tJoinOperator\r\n" );

		conditions.clear( );

		for ( JoinCondition join : joinRelationSet )
		{
			String lefttable = join.lefttable;
			String righttable = join.righttable;
			String leftcolumn = join.leftcolumn;
			String rightcolumn = join.rightcolumn;
			String leftColumnLocation = join.leftColumnLocation;
			String rightColumnLocation = join.rightColumnLocation;
			String leftTableLocation = join.leftTableLocation;
			String rightTableLocation = join.rightTableLocation;
			String joinType = join.joinType;
			String operator = join.operator;

			if ( ( lefttable == null || lefttable.length( ) == 0 )
					&& ( righttable == null || righttable.length( ) == 0 ) )
				continue;

			List<String[]> leftJoinNameList = getRealName( lefttable,
					leftcolumn,
					join.sql );
			List<String[]> rightJoinNameList = getRealName( righttable,
					rightcolumn,
					join.sql );

			for ( String[] leftJoinNames : leftJoinNameList )
			{
				for ( String[] rightJoinNames : rightJoinNameList )
				{
					if ( leftJoinNames[0] != null
							&& rightJoinNames[0] != null
							&& leftJoinNames[1] != null
							&& rightJoinNames[1] != null )
					{
						JoinCondition condition = new JoinCondition( );
						condition.lefttable = leftJoinNames[0];
						condition.righttable = rightJoinNames[0];
						condition.leftcolumn = leftJoinNames[1];
						condition.rightcolumn = rightJoinNames[1];
						condition.leftColumnLocation = leftColumnLocation;
						condition.rightColumnLocation = rightColumnLocation;
						condition.leftTableLocation = leftTableLocation;
						condition.rightTableLocation = rightTableLocation;
						condition.joinType = joinType;
						condition.operator = operator;

						if ( !conditions.contains( condition ) )
						{
							conditions.add( condition );
							if ( showLocation )
							{
								buffer.append( fillString( condition.lefttable
										+ condition.leftTableLocation )
										+ "\t"
										+ fillString( condition.leftcolumn
												+ condition.leftColumnLocation )
										+ "\t"
										+ fillString( condition.righttable
												+ condition.rightTableLocation )
										+ "\t"
										+ fillString( condition.rightcolumn
												+ condition.rightColumnLocation )
										+ "\t"
										+ fillString( condition.joinType )
										+ "\t"
										+ fillString( condition.operator )
										+ "\r\n" );
							}
							else
							{
								buffer.append( fillString( condition.lefttable )
										+ "\t"
										+ fillString( condition.leftcolumn )
										+ "\t"
										+ fillString( condition.righttable )
										+ "\t"
										+ fillString( condition.rightcolumn )
										+ "\t"
										+ fillString( condition.joinType )
										+ "\t"
										+ fillString( condition.operator )
										+ "\r\n" );
							}
						}
					}
				}
			}
		}
	}

	private void analyzeStmt( TCustomSqlStatement select )
	{
		if ( select.getCteList( ) != null && select.getCteList( ).size( ) > 0 )
		{
			for ( int i = 0; i < select.getCteList( ).size( ); i++ )
			{
				TCTE expression = (TCTE) select.getCteList( ).getCTE( i );
				cteMap.put( expression.getTableName( ),
						expression.getSubquery( ) );
			}
		}

		analyzeStatement( select );
	}

	private void analyzeStatement( TCustomSqlStatement select )
	{
		if ( select instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement stmt = (TSelectSqlStatement) select;

			searchJoinFromStatement( stmt );

			if ( stmt.isCombinedQuery( ) )
			{
				analyzeStatement( stmt.getLeftStmt( ) );
				analyzeStatement( stmt.getRightStmt( ) );
			}
			else
			{
				for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
				{
					TResultColumn field = select.getResultColumnList( )
							.getResultColumn( i );
					searchFields( field, select );
				}
			}
		}
		else if ( select instanceof TPlsqlCreateProcedure )
		{
			TPlsqlCreateProcedure createProcedure = (TPlsqlCreateProcedure) select;
			if ( createProcedure.getBodyStatements( ) != null )
			{
				for ( int i = 0; i < createProcedure.getBodyStatements( )
						.size( ); i++ )
				{
					analyzeStmt( createProcedure.getBodyStatements( ).get( i ) );
				}
			}
		}
		else if ( select instanceof TMssqlCreateProcedure )
		{
			TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure) select;
			if ( createProcedure.getBodyStatements( ) != null )
			{
				for ( int i = 0; i < createProcedure.getBodyStatements( )
						.size( ); i++ )
				{
					analyzeStmt( createProcedure.getBodyStatements( ).get( i ) );
				}
			}
		}
		else if ( select instanceof TMssqlBlock )
		{
			TMssqlBlock block = (TMssqlBlock) select;
			if ( block.getBodyStatements( ) != null )
			{
				for ( int i = 0; i < block.getBodyStatements( ).size( ); i++ )
				{
					analyzeStmt( block.getBodyStatements( ).get( i ) );
				}
			}
		}
		else if ( select.getResultColumnList( ) != null )
		{
			for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
			{
				TResultColumn field = select.getResultColumnList( )
						.getResultColumn( i );
				searchFields( field, select );
			}
		}
	}

	private void searchJoinFromStatement( TSelectSqlStatement stmt )
	{
		if ( stmt.joins != null )
		{
			for ( int i = 0; i < stmt.joins.size( ); i++ )
			{
				TJoin join = stmt.joins.getJoin( i );
				handleJoin( stmt, join );
			}
		}
	}

	private void handleJoin( TSelectSqlStatement stmt, TJoin join )
	{
		if ( join.getJoin( ) != null )
		{
			handleJoin( stmt, join.getJoin( ) );
		}
		if ( join.getJoinItems( ) != null )
		{
			for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
			{
				TJoinItem joinItem = join.getJoinItems( ).getJoinItem( j );
				TExpression expr = joinItem.getOnCondition( );
				if ( expr != null )
				{
					searchExpression( expr, stmt, joinItem.getJoinType( )
							.name( ) );
				}
			}
		}
	}

	private List<String[]> getRealName( String tableAlias, String columnAlias,
			List<TCustomSqlStatement> stmtList )
	{
		List<String[]> nameList = new ArrayList<String[]>( );
		for ( TCustomSqlStatement stmt : stmtList )
		{

			gudusoft.gsqlparser.nodes.TTable table = null;
			String columnName = columnAlias;
			if ( ( tableAlias == null || tableAlias.length( ) == 0 )
					&& stmt instanceof TSelectSqlStatement
					&& ( (TSelectSqlStatement) stmt ).tables.size( ) == 1
					&& ( (TSelectSqlStatement) stmt ).tables.getTable( 0 )
							.getAliasClause( ) == null )
			{
				table = ( (TSelectSqlStatement) stmt ).tables.getTable( 0 );
				getTableNames( nameList, table, columnName );
				continue;
			}
			else if ( tableAlias == null || tableAlias.length( ) == 0 )
			{
				nameList.add( new String[]{
						null, columnName
				} );
				continue;
			}

			if ( tableAliasMap.containsKey( tableAlias.toLowerCase( )
					+ ":"
					+ stmt.toString( ) ) )
			{
				table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap.get( tableAlias.toLowerCase( )
						+ ":"
						+ stmt.toString( ) );
				getTableNames( nameList, table, columnName );
				continue;
			}
			else if ( tableAliasMap.containsKey( tableAlias.toLowerCase( ) )
					&& !containsKey( tableAliasMap, tableAlias.toLowerCase( )
							+ ":" ) )
			{
				table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap.get( tableAlias.toLowerCase( ) );
				getTableNames( nameList, table, columnName );
				continue;
			}
			else
			{
				if ( queryAliasMap.containsKey( tableAlias.toLowerCase( ) ) )
				{
					Object value = queryAliasMap.get( tableAlias.toLowerCase( ) );
					if ( value instanceof TSelectSqlStatement )
					{
						TSelectSqlStatement sql = (TSelectSqlStatement) value;
						getRealNameFromSql( nameList, columnAlias, stmt, sql );
					}
					continue;
				}
				else if ( stmt instanceof TSelectSqlStatement )
				{
					findTableByAlias( nameList,
							(TSelectSqlStatement) stmt,
							tableAlias,
							columnAlias,
							new ArrayList<TSelectSqlStatement>( ) );
					continue;
				}
				continue;
			}
		}
		return nameList;
	}

	private void getTableNames( List<String[]> nameList,
			gudusoft.gsqlparser.nodes.TTable table, String columnName )
	{
		if ( !( table.getSubquery( ) instanceof TSelectSqlStatement ) )
		{
			nameList.add( new String[]{
					table.getFullName( ), columnName
			} );
		}
		else
		{
			TSelectSqlStatement stmt = (TSelectSqlStatement) table.getSubquery( );
			getRealNameFromSql( nameList, columnName, null, stmt );
		}
	}

	private void getRealNameFromSql( List<String[]> nameList,
			String columnAlias, TCustomSqlStatement stmt,
			TSelectSqlStatement sql )
	{
		gudusoft.gsqlparser.nodes.TTable table = null;
		String columnName = null;

		if ( sql.isCombinedQuery( ) )
		{
			getRealNameFromSql( nameList, columnAlias, stmt, sql.getLeftStmt( ) );
			getRealNameFromSql( nameList, columnAlias, stmt, sql.getRightStmt( ) );
		}
		else
		{
			for ( int i = 0; i < sql.getResultColumnList( ).size( ); i++ )
			{
				TResultColumn field = sql.getResultColumnList( )
						.getResultColumn( i );
				switch ( field.getExpr( ).getExpressionType( ) )
				{
					case simple_object_name_t :
						TColumn column = attrToColumn( field, sql );
						if ( ( ( column.columnAlias == null || column.columnAlias.length( ) == 0 ) && columnAlias.trim( )
								.equalsIgnoreCase( column.columnName.trim( ) ) )
								|| ( ( column.columnAlias != null && column.columnAlias.length( ) > 0 ) && columnAlias.trim( )
										.equals( column.columnAlias.trim( ) ) )
								|| column.columnName.equals( "*" ) )
						{
							if ( column.columnPrex != null )
							{
								if ( stmt != null
										&& tableAliasMap.containsKey( column.columnPrex.toLowerCase( )
												+ ":"
												+ stmt.toString( ) ) )
								{
									table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap.get( column.columnPrex.toLowerCase( )
											+ ":"
											+ stmt.toString( ) );
								}
								else if ( tableAliasMap.containsKey( column.columnPrex.toLowerCase( ) ) )
								{
									table = (gudusoft.gsqlparser.nodes.TTable) tableAliasMap.get( column.columnPrex.toLowerCase( ) );
								}
							}
							else
							{
								table = sql.tables.getTable( 0 );
							}

							if ( column.columnName.equals( "*" ) )
							{
								columnName = columnAlias;
							}
							else
							{
								columnName = column.columnName;
							}
						}
						break;
				}
			}
			if ( table != null )
			{
				nameList.add( new String[]{
						getTableName( table ), columnName
				} );
			}
		}
	}

	private String getTableName( gudusoft.gsqlparser.nodes.TTable table )
	{
		if ( table.getSubquery( ) != null
				&& table.getSubquery( ).tables != null
				&& table.getSubquery( ).tables.size( ) > 0 )
		{
			return getTableName( table.getSubquery( ).tables.getTable( 0 ) );
		}
		return table.getFullName( );
	}

	private void findTableByAlias( List<String[]> nameList,
			TSelectSqlStatement stmt, String tableAlias, String columnAlias,
			List<TSelectSqlStatement> stats )
	{
		if ( stats.contains( stmt ) )
			return;
		else
			stats.add( stmt );

		if ( stmt.isCombinedQuery( ) )
		{
			findTableByAlias( nameList,
					stmt.getLeftStmt( ),
					tableAlias,
					columnAlias,
					stats );
			findTableByAlias( nameList,
					stmt.getRightStmt( ),
					tableAlias,
					columnAlias,
					stats );
		}
		else
		{
			for ( int i = 0; i < stmt.tables.size( ); i++ )
			{
				gudusoft.gsqlparser.nodes.TTable table = stmt.tables.getTable( i );
				if ( table.getAliasClause( ) != null
						&& table.getAliasClause( ).toString( ).length( ) > 0 )
				{
					if ( table.getAliasClause( )
							.toString( )
							.equalsIgnoreCase( tableAlias ) )
					{
						nameList.add( new String[]{
								table.getTableName( ).toString( ), columnAlias
						} );
						return;
					}
				}
				else if ( table.getTableName( ) != null )
				{
					if ( table.getTableName( )
							.toString( )
							.equalsIgnoreCase( tableAlias ) )
					{
						nameList.add( new String[]{
								table.getTableName( ).toString( ), columnAlias
						} );
						return;
					}
				}
			}
		}
		if ( nameList.size( ) == 0
				&& stmt.getParentStmt( ) instanceof TSelectSqlStatement )
		{
			findTableByAlias( nameList,
					(TSelectSqlStatement) stmt.getParentStmt( ),
					tableAlias,
					columnAlias,
					stats );
		}

	}

	private boolean containsKey( HashMap tableAliasMap, String key )
	{
		String[] collection = (String[]) tableAliasMap.keySet( )
				.toArray( new String[0] );
		for ( String str : collection )
		{
			if ( str.toLowerCase( ).startsWith( key.toLowerCase( ) ) )
				return true;
		}
		return false;
	}

	private String fillString( String text )
	{
		if ( text == null )
		{
			return "";
		}

		int tablength = 8;
		if ( isOutputFile )
		{
			tablength = 9;
		}

		if ( text.length( ) < tablength )
			text += "\t";
		return text;
	}

	public void searchFields( TResultColumn field, TCustomSqlStatement select )
	{
		switch ( field.getExpr( ).getExpressionType( ) )
		{
			case simple_object_name_t :
				searchTables( select );
				searchClauses( select );
				break;
			case simple_constant_t :
				searchExpression( field.getExpr( ), select, null );
				searchTables( select );
				searchClauses( select );
				break;
			case case_t :
				searchExpression( field.getExpr( ), select, null );
				searchTables( select );
				searchClauses( select );
				break;
			case function_t :
				searchExpression( field.getExpr( ), select, null );
				searchTables( select );
				searchClauses( select );

				TFunctionCall func = field.getExpr( ).getFunctionCall( );
				// buffer.AppendLine("function name {0}",
				// func.funcname.AsText);

				// check column : function arguments
				if ( func.getArgs( ) != null )
				{
					for ( int k = 0; k < func.getArgs( ).size( ); k++ )
					{
						TExpression expr = (TExpression) func.getArgs( )
								.getExpression( k );
						searchExpression( expr, select, null );
					}
				}
				else
				{
					if ( select.tables.getTable( 0 ).getAliasClause( ) != null )
					{
						String alias = select.tables.getTable( 0 )
								.getAliasClause( )
								.toString( );
						if ( !tableAliasMap.containsKey( alias.toLowerCase( )
								.trim( ) + ":" + select.toString( ) ) )
						{
							tableAliasMap.put( alias.toLowerCase( ).trim( )
									+ ":"
									+ select.toString( ),
									select.tables.getTable( 0 ) );
						}
						if ( !tableAliasMap.containsKey( alias.toLowerCase( )
								.trim( ) ) )
						{
							tableAliasMap.put( alias.toLowerCase( ).trim( ),
									select.tables.getTable( 0 ) );
						}
					}
				}

				if ( func.getAnalyticFunction( ) != null )
				{
					TParseTreeNodeList list = func.getAnalyticFunction( )
							.getPartitionBy_ExprList( );

					searchExpressionList( select, list );

					if ( func.getAnalyticFunction( ).getOrderBy( ) != null )
					{
						list = func.getAnalyticFunction( )
								.getOrderBy( )
								.getItems( );
						searchExpressionList( select, list );
					}
				}

				// check order by clause
				// if (select instanceof TSelectSqlStatement &&
				// ((TSelectSqlStatement)select).GroupbyClause != null)
				// {
				// for (int j = 0; j <
				// ((TSelectSqlStatement)select).GroupbyClause.GroupItems.Count();
				// j++)
				// {
				// TLzGroupByItem i =
				// (TLzGroupByItem)((TSelectSqlStatement)select).GroupbyClause.GroupItems[j];
				// searchExpression((TExpression)i._ndExpr, select);
				// searchTables(select);
				// }

				// }

				break;
			case subquery_t :
				if ( field.getExpr( ).getSubQuery( ) instanceof TSelectSqlStatement )
				{
					searchSubQuery( field.getExpr( ).getSubQuery( ) );
				}
				break;
			default :
				buffer.append( "searchFields of type: "
						+ field.getExpr( ).getExpressionType( )
						+ " not implemented yet\r\n" );
				break;
		}
	}

	private void searchExpressionList( TCustomSqlStatement select,
			TParseTreeNodeList list )
	{
		if ( list == null )
			return;

		for ( int i = 0; i < list.size( ); i++ )
		{
			TExpression lcexpr = null;
			if ( list.getElement( i ) instanceof TOrderByItem )
			{
				lcexpr = (TExpression) ( (TOrderByItem) list.getElement( i ) ).getSortKey( );
			}
			else if ( list.getElement( i ) instanceof TExpression )
			{
				lcexpr = (TExpression) list.getElement( i );
			}

			if ( lcexpr != null )
			{
				searchExpression( lcexpr, select, null );
			}
		}
	}

	private void searchClauses( TCustomSqlStatement select )
	{
		if ( !searchInClauses.contains( select ) )
		{
			searchInClauses.add( select );
		}
		else
		{
			return;
		}
		if ( select instanceof TSelectSqlStatement )
		{

			TSelectSqlStatement statement = (TSelectSqlStatement) select;
			HashMap clauseTable = new HashMap( );

			// if (statement.SortClause != null)
			// {
			// TLzOrderByList sortList = (TLzOrderByList)statement.SortClause;
			// for (int i = 0; i < sortList.Count(); i++)
			// {
			// TLzOrderBy orderBy = sortList[i];
			// TExpression expr = orderBy.SortExpr;
			// clauseTable.add(expr, ClauseType.orderby);
			// }
			// }

			if ( statement.getWhereClause( ) != null )
			{
				clauseTable.put( ( statement.getWhereClause( ).getCondition( ) ),
						ClauseType.where );
			}
			// if (statement.ConnectByClause != null)
			// {
			// clauseTable.add((TExpression)statement.ConnectByClause,
			// ClauseType.connectby);
			// }
			// if (statement.StartwithClause != null)
			// {
			// clauseTable.add((TExpression)statement.StartwithClause,
			// ClauseType.startwith);
			// }
			for ( TExpression expr : (TExpression[]) clauseTable.keySet( )
					.toArray( new TExpression[0] ) )
			{
				ClauseType type = (ClauseType) clauseTable.get( expr );
				searchExpression( expr,
						select,
						type == null ? null : type.name( ) );
				searchTables( select );

			}
		}
	}

	void searchTables( TCustomSqlStatement select )
	{
		if ( !searchInTables.contains( select ) )
		{
			searchInTables.add( select );
		}
		else
		{
			return;
		}

		gudusoft.gsqlparser.nodes.TTableList tables = select.tables;

		if ( tables.size( ) == 1 )
		{
			gudusoft.gsqlparser.nodes.TTable lzTable = tables.getTable( 0 );
			if ( ( lzTable.getTableType( ) == ETableSource.objectname )
					&& ( lzTable.getAliasClause( ) == null || lzTable.getAliasClause( )
							.toString( )
							.trim( )
							.length( ) == 0 ) )
			{
				if ( cteMap.containsKey( lzTable.getTableName( ).toString( ) ) )
				{
					searchSubQuery( (TSelectSqlStatement) cteMap.get( lzTable.getTableName( )
							.toString( ) ) );
				}
				else
				{
					if ( lzTable.getAliasClause( ) != null )
					{
						String alias = lzTable.getAliasClause( ).toString( );
						if ( !tableAliasMap.containsKey( alias.toLowerCase( )
								.trim( ) + ":" + select.toString( ) ) )
						{
							tableAliasMap.put( alias.toLowerCase( ).trim( )
									+ ":"
									+ select.toString( ), lzTable );
						}
						if ( !tableAliasMap.containsKey( alias.toLowerCase( )
								.trim( ) ) )
						{
							tableAliasMap.put( alias.toLowerCase( ).trim( ),
									lzTable );
						}
					}
				}
			}
		}

		for ( int i = 0; i < tables.size( ); i++ )
		{
			gudusoft.gsqlparser.nodes.TTable lztable = tables.getTable( i );
			switch ( lztable.getTableType( ) )
			{
				case objectname :
					TTable table = TLzTaleToTable( lztable );
					String alias = table.tableAlias;
					if ( alias != null )
						alias = alias.trim( );
					else if ( table.tableName != null )
						alias = table.tableName.trim( );

					if ( cteMap.containsKey( lztable.getTableName( ).toString( ) ) )
					{
						searchSubQuery( (TSelectSqlStatement) cteMap.get( lztable.getTableName( )
								.toString( ) ) );
					}
					else
					{
						if ( alias != null )
						{
							if ( !tableAliasMap.containsKey( alias.toLowerCase( )
									.trim( )
									+ ":"
									+ select.toString( ) ) )
							{
								tableAliasMap.put( alias.toLowerCase( ).trim( )
										+ ":"
										+ select.toString( ), lztable );
							}
							if ( !tableAliasMap.containsKey( alias.toLowerCase( )
									.trim( ) ) )
							{
								tableAliasMap.put( alias.toLowerCase( ).trim( ),
										lztable );
							}
						}
					}
					break;
				case subquery :
					if ( lztable.getAliasClause( ) != null )
					{
						String tableAlias = lztable.getAliasClause( )
								.toString( )
								.trim( );
						if ( !queryAliasMap.containsKey( tableAlias.toLowerCase( ) ) )
						{
							queryAliasMap.put( tableAlias.toLowerCase( ),
									(TSelectSqlStatement) lztable.getSubquery( ) );
						}
					}
					searchSubQuery( (TSelectSqlStatement) lztable.getSubquery( ) );
					break;
				default :
					break;
			}
		}
	}

	public void searchSubQuery( TSelectSqlStatement select )
	{
		if ( !searchInSubQuerys.contains( select ) )
		{
			searchInSubQuerys.add( select );
		}
		else
		{
			return;
		}

		searchJoinFromStatement( select );

		if ( select.isCombinedQuery( ) )
		{
			searchSubQuery( select.getLeftStmt( ) );
			searchSubQuery( select.getRightStmt( ) );
		}
		else
		{
			for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
			{
				TResultColumn field = select.getResultColumnList( )
						.getResultColumn( i );
				searchFields( field, select );
			}
		}
	}

	public TColumn attrToColumn( TResultColumn field, TCustomSqlStatement stmt )
	{
		TColumn column = new TColumn( );

		TExpression attr = field.getExpr( );

		column.columnAlias = field.getAliasClause( ) == null ? null
				: field.getAliasClause( ).toString( );
		TSourceToken columnToken = attr.getEndToken( );
		column.columnName = columnToken.toString( );
		if ( columnToken != null )
		{
			column.columnLocation = "("
					+ columnToken.lineNo
					+ ","
					+ columnToken.columnNo
					+ ")";
		}

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

				if ( attr.getObjectOperand( ) != null
						&& attr.getObjectOperand( ).getTableToken( ) != null )
				{
					TSourceToken tableToken = attr.getObjectOperand( )
							.getTableToken( );
					if ( tableToken != null )
					{
						column.tableLocation = "("
								+ tableToken.lineNo
								+ ","
								+ tableToken.columnNo
								+ ")";
					}
				}
			}
		}
		else
		{
			TTableList tables = stmt.tables;
			for ( int i = 0; i < tables.size( ); i++ )
			{
				gudusoft.gsqlparser.nodes.TTable lztable = tables.getTable( i );
				TTable table = TLzTaleToTable( lztable );
				if ( !column.tableNames.contains( table.tableName ) )
				{
					column.tableNames.add( table.tableName );
					column.tableLocation = table.tableLocation;
				}
			}
		}

		return column;
	}

	TTable TLzTaleToTable( gudusoft.gsqlparser.nodes.TTable lztable )
	{
		TTable table = new TTable( );
		if ( lztable.getSubquery( ) == null && lztable.getTableName( ) != null )
		{
			table.tableName = lztable.getName( );
			if ( lztable.getTableName( ).toString( ).indexOf( "." ) > 0 )
			{
				table.prefixName = lztable.getTableName( )
						.toString( )
						.substring( 0, lztable.getFullName( ).indexOf( '.' ) );
			}
			table.tableLocation = lztable.getTableName( ).coordinate( );
		}

		if ( lztable.getAliasClause( ) != null )
		{
			table.tableAlias = lztable.getAliasClause( ).toString( );
			if ( table.tableLocation == null )
			{
				table.tableLocation = lztable.getAliasClause( )
						.getAliasName( )
						.coordinate( );
			}
		}
		return table;
	}

	void searchExpression( TExpression expr, TCustomSqlStatement stmt,
			String joinType )
	{
		joinConditonsInExpr c = new joinConditonsInExpr( this,
				expr,
				stmt,
				joinType );
		c.searchExpression( );
	}

}
