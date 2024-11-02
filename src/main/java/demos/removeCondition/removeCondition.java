
package demos.removeCondition;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.ENodeStatus;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TJoinList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

/**
 * Replace user defined variable in where clause with specified value,
 * If no value is specified, then the condition include the variable will be removed
 * <pre>
 *     {@code
 *  d.id = summit.mstr.id
 * AND d.system_gift_type IN ( 'OG', 'PLP', 'PGP' )
 * AND d.fund_coll_attrb IN ( '$Institute$' )
 * AND d.fund_acct IN ( '$Fund$' )
 * AND d.cntrb_date >= '$From_Date$'
 * AND d.cntrb_date <= '$Thru_Date$'
 *     }
 * </pre>
 *
 * After specify the value for variable: $Institute$ and $Fund$,
 * the where condition becomes something like this:
 * <pre>
 *     {@code
 *  d.id = summit.mstr.id
 * 	AND d.system_gift_type IN ( 'OG', 'PLP', 'PGP' )
 * 	AND d.fund_coll_attrb IN ( 'ShanXi University' )
 * 	AND d.fund_acct IN ( 'Eclipse.org' )
 *     }
 * </pre>
 */
public class removeCondition
{

	public static void main( String[] args )
	{
		String sql = "SELECT SUM (d.amt)\r\n"
				+ "FROM summit.cntrb_detail d\r\n"
				+ "WHERE d.fund_coll_attrb IN ( '$Institute$' )\r\n"
				+ "AND d.fund_acct IN ( '$Fund$' )\r\n"
				+ "AND d.cntrb_date >= '$From_Date$'\r\n"
				+ "AND d.cntrb_date <= '$Thru_Date$'\r\n"
				+ "GROUP BY d.id;";
		Map<String, String> conditionMap = new HashMap<String, String>( );
		conditionMap.put( "Institute", "ShanXi University" );
		conditionMap.put( "Fund", "Eclipse.org" );

		removeCondition remove = new removeCondition( sql,
				EDbVendor.dbvmssql,
				conditionMap );

		System.out.println( remove.getRemoveResult( ) );

	}

	private String result;

	private StringBuffer replaceBuffer = new StringBuffer( );

	public removeCondition( File sqlFile, EDbVendor vendor,
			Map<String, String> conditionMap )
	{
		TGSqlParser sqlparser = new TGSqlParser( vendor );
		sqlparser.setSqlfilename( sqlFile.getAbsolutePath( ) );
		remove( sqlparser, conditionMap );
	}

	private String leftParenthese = null;
	private String rightParenthese = null;

	public removeCondition( String sql, EDbVendor vendor,
			Map<String, String> conditionMap )
	{
		TGSqlParser sqlparser = new TGSqlParser( vendor );
		String noquoteString = removeQuote( sql.trim( ) );
		int index = sql.indexOf( noquoteString );
		if ( index > 0 )
		{
			leftParenthese = sql.substring( 0, index );
			rightParenthese = sql.substring( index + noquoteString.length( ) );
		}
		sqlparser.setSqltext( noquoteString );
		remove( sqlparser, conditionMap );

		if ( leftParenthese != null && rightParenthese != null )
		{
			result = ( leftParenthese + result + rightParenthese );
		}
	}

	private String removeQuote( String sql )
	{
		if ( sql.startsWith( "(" ) && sql.endsWith( ")" ) )
		{
			sql = sql.substring( 1, sql.length( ) - 1 ).trim( );
		}
		if ( sql.startsWith( "(" ) && sql.endsWith( ")" ) )
		{
			return removeQuote( sql );
		}
		return sql;
	}

	private void getParserString( TCustomSqlStatement query,
			Map<String, String> conditionMap )
	{
		String oldString = toString( query );
		if ( oldString == null )
			return;
		String newString = remove( query, conditionMap );
		if ( newString == null )
			return;
		if ( newString != null && !oldString.equals( newString ) )
		{
			query.setString( newString );
			return;
		}
	}

	public String getRemoveResult( )
	{
		return result;
	}

	String remove( TCustomSqlStatement stat, Map<String, String> conditionMap )
	{
		if ( stat.getResultColumnList( ) != null )
		{
			for ( int j = 0; j < stat.getResultColumnList( ).size( ); j++ )
			{
				TResultColumn column = stat.getResultColumnList( )
						.getResultColumn( j );
				if ( column.getExpr( ) != null
						&& column.getExpr( ).getSubQuery( ) instanceof TCustomSqlStatement )
				{
					TCustomSqlStatement query = (TCustomSqlStatement) column.getExpr( )
							.getSubQuery( );
					getParserString( query, conditionMap );
				}
			}
		}
		if ( stat.getCteList( ) != null )
		{
			for ( int i = 0; i < stat.getCteList( ).size( ); i++ )
			{
				TCTE cte = stat.getCteList( ).getCTE( i );
				if ( cte.getSubquery( ) != null )
				{
					getParserString( cte.getSubquery( ), conditionMap );
				}
				if ( cte.getInsertStmt( ) != null )
				{
					getParserString( cte.getInsertStmt( ), conditionMap );
				}
				if ( cte.getUpdateStmt( ) != null )
				{
					getParserString( cte.getUpdateStmt( ), conditionMap );
				}
				if ( cte.getPreparableStmt( ) != null )
				{
					getParserString( cte.getPreparableStmt( ), conditionMap );
				}
				if ( cte.getDeleteStmt( ) != null )
				{
					getParserString( cte.getDeleteStmt( ), conditionMap );
				}
			}
		}

		if ( stat instanceof TSelectSqlStatement
				&& ( (TSelectSqlStatement) stat ).getSetOperatorType() != ESetOperatorType.none )
		{
			TSelectSqlStatement select = ( (TSelectSqlStatement) stat );
			getParserString( select.getLeftStmt( ), conditionMap );
			getParserString( select.getRightStmt( ), conditionMap );
			return toString( select );
		}

		if ( stat.getStatements( ) != null && stat.getStatements( ).size( ) > 0 )
		{
			for ( int i = 0; i < stat.getStatements( ).size( ); i++ )
			{
				getParserString( stat.getStatements( ).get( i ), conditionMap );
			}
		}
		if ( stat.getReturningClause( ) != null )
		{
			if ( stat.getReturningClause( ).getColumnValueList( ) != null )
			{
				for ( int i = 0; i < stat.getReturningClause( )
						.getColumnValueList( )
						.size( ); i++ )
				{
					if ( stat.getReturningClause( )
							.getColumnValueList( )
							.getExpression( i )
							.getSubQuery( ) != null )
					{
						getParserString( stat.getReturningClause( )
								.getColumnValueList( )
								.getExpression( i )
								.getSubQuery( ), conditionMap );
					}
				}
			}
			if ( stat.getReturningClause( ).getVariableList( ) != null )
			{
				for ( int i = 0; i < stat.getReturningClause( )
						.getVariableList( )
						.size( ); i++ )
				{
					if ( stat.getReturningClause( )
							.getVariableList( )
							.getExpression( i )
							.getSubQuery( ) != null )
					{
						getParserString( stat.getReturningClause( )
								.getVariableList( )
								.getExpression( i )
								.getSubQuery( ), conditionMap );
					}
				}
			}
		}
		if ( stat instanceof TSelectSqlStatement )
		{
			TTableList list = ( (TSelectSqlStatement) stat ).tables;
			for ( int i = 0; i < list.size( ); i++ )
			{
				TTable table = list.getTable( i );
				if ( table.getSubquery( ) != null )
				{
					getParserString( table.getSubquery( ), conditionMap );
				}
				if ( table.getFuncCall( ) != null )
				{
					ExpressionChecker w = new ExpressionChecker( this );
					w.checkFunctionCall( table, table.getFuncCall( ), conditionMap );
				}
			}
		}

		if ( stat instanceof TSelectSqlStatement )
		{
			TJoinList list = ( (TSelectSqlStatement) stat ).joins;
			for ( int i = 0; i < list.size( ); i++ )
			{
				TJoin join = list.getJoin( i );
				for ( int j = 0; j < join.getJoinItems( ).size( ); j++ )
				{
					TJoinItem joinItem = join.getJoinItems( ).getJoinItem( j );
					if ( joinItem.getTable( ) != null )
					{
						if ( joinItem.getTable( ).getSubquery( ) != null )
						{
							getParserString( joinItem.getTable( ).getSubquery( ),
									conditionMap );
						}
						if ( joinItem.getTable( ).getFuncCall( ) != null )
						{
							ExpressionChecker w = new ExpressionChecker( this );
							w.checkFunctionCall( joinItem.getTable( ), joinItem.getTable( )
									.getFuncCall( ), conditionMap );
						}
						if ( joinItem.getOnCondition( ) != null )
						{
							ExpressionChecker w = new ExpressionChecker( this );
							w.checkExpression( joinItem.getOnCondition( ),
									conditionMap );
						}
					}
				}
			}
		}

		if ( stat instanceof TSelectSqlStatement )
		{
			TSelectSqlStatement select = (TSelectSqlStatement) stat;
			for ( int i = 0; i < select.getResultColumnList( ).size( ); i++ )
			{
				TResultColumn field = select.getResultColumnList( )
						.getResultColumn( i );
				TExpression expr = field.getExpr( );
				if ( expr != null
						&& expr.getExpressionType( ) == EExpressionType.subquery_t )
				{
					getParserString( expr.getSubQuery( ), conditionMap );
				}
				else if(expr!=null){
					ExpressionChecker w = new ExpressionChecker( this );
					w.checkExpression( expr, conditionMap );
					
					if(expr == null || expr.getNodeStatus() == ENodeStatus.nsRemoved){
						field.setExpr(null);
						select.getResultColumnList().removeElementAt(i);
						i--;
					}
				}
				
			}
		}

		if ( stat.getWhereClause( ) != null
				&& stat.getWhereClause( ).getCondition( ) != null )
		{
			TExpression whereExpression = stat.getWhereClause( ).getCondition( );
			if ( whereExpression.toString( ) == null )
			{
				removeCondition removeCondition = new removeCondition( stat.toString( ),
						stat.dbvendor,
						conditionMap );
				return removeCondition.result;
			}
			else
			{
				ExpressionChecker w = new ExpressionChecker( this );
				w.checkExpression( whereExpression, conditionMap );
				
				if(whereExpression == null || whereExpression.getNodeStatus() == ENodeStatus.nsRemoved){
					stat.setWhereClause(null);
				}
			}
		}
		if ( ( stat instanceof TSelectSqlStatement )
				&& ( (TSelectSqlStatement) stat ).getGroupByClause( ) != null
				&& ( (TSelectSqlStatement) stat ).getGroupByClause( )
						.getHavingClause( ) != null )
		{

			TExpression havingExpression = ( (TSelectSqlStatement) stat ).getGroupByClause( )
					.getHavingClause( );

			if ( havingExpression.toString( ) == null )
			{
				removeCondition removeCondition = new removeCondition( stat.toString( ),
						stat.dbvendor,
						conditionMap );
				return removeCondition.result;
			}
			else
			{
				ExpressionChecker w = new ExpressionChecker( this );
				w.checkExpression( havingExpression, conditionMap );
				
				if(havingExpression == null || havingExpression.getNodeStatus() == ENodeStatus.nsRemoved){
					 ( (TSelectSqlStatement) stat ).getGroupByClause( ).setHavingClause(null);
				}
			}
		}
		return stat.toString();

	}

	void remove( TGSqlParser sqlparser, Map<String, String> conditionMap )
	{
		int i = sqlparser.parse( );
		if ( i == 0 )
		{
			TCustomSqlStatement stat = sqlparser.sqlstatements.get( 0 );
			getParserString( stat, conditionMap );
			result = stat.toString();
			if ( result != null )
			{
				result = replaceCondition( result, conditionMap );
			}
		}
		else
			System.err.println( sqlparser.getErrormessage( ) );
	}

	private String replaceCondition( String content,
			Map<String, String> conditionMap )
	{
		String[] conditions = new String[0];
		if ( conditionMap != null && !conditionMap.isEmpty( ) )
		{
			conditions = conditionMap.keySet( ).toArray( new String[0] );
		}
		Pattern pattern = Pattern.compile( "\\$[^$]+\\$",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( content );
		replaceBuffer.delete( 0, replaceBuffer.length( ) );
		while ( matcher.find( ) )
		{
			String condition = matcher.group( ).replaceAll( "\\$", "" ).trim( );
			for ( int i = 0; i < conditions.length; i++ )
			{
				if ( conditions[i].equalsIgnoreCase( condition )
						&& conditionMap.get( conditions[i] ) != null )
				{
					matcher.appendReplacement( replaceBuffer,
							conditionMap.get( conditions[i] ) );
					break;
				}
			}
		}
		matcher.appendTail( replaceBuffer );
		return replaceBuffer.toString( );
	}

	protected String toString( TParseTreeNode node )
	{	
		return node.toString();
	}

}
