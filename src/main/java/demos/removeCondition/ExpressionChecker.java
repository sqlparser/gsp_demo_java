
package demos.removeCondition;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionChecker implements IExpressionVisitor
{

	private Map<String, String> conditionMap;
	private removeCondition removeCondition;

	StringBuffer buffer = new StringBuffer( );

	StringBuffer tokenBuffer = new StringBuffer( );

	public ExpressionChecker( removeCondition removeCondition )
	{
		this.removeCondition = removeCondition;
	}

	private boolean checkCondition( TExpression expression )
	{
		String[] conditions = new String[0];
		if ( conditionMap != null && !conditionMap.isEmpty( ) )
		{
			conditions = conditionMap.keySet( ).toArray( new String[0] );
		}
		String expr = toString( expression );
		if ( expr == null )
			return false;
		Pattern pattern = Pattern.compile( "\\$[^$]+\\$",
				Pattern.CASE_INSENSITIVE );
		Matcher matcher = pattern.matcher( expr );
		buffer.delete( 0, buffer.length( ) );
		while ( matcher.find( ) )
		{
			String condition = matcher.group( ).replaceAll( "\\$", "" ).trim( );
			boolean flag = false;
			for ( int i = 0; i < conditions.length; i++ )
			{
				if ( conditions[i].equalsIgnoreCase( condition )
						&& conditionMap.get( conditions[i] ) != null )
				{
					flag = true;
					matcher.appendReplacement( buffer,
							conditionMap.get( conditions[i] ) );
					break;
				}
			}
			if ( !flag )
				return false;
		}
		matcher.appendTail( buffer );
		if ( !toString( expression ).equals( buffer.toString( ) ) )
			expression.setString( buffer.toString( ) );
		return true;
	}

	public void checkExpression( TExpression expr,
			Map<String, String> conditionMap )
	{
		this.conditionMap = conditionMap;
		expr.postOrderTraverse( this );
	}

	public void checkFunctionCall( TFunctionCall func,
			Map<String, String> conditionMap )
	{
		if ( func.getArgs( ) != null )
		{
			for ( int k = 0; k < func.getArgs( ).size( ); k++ )
			{
				TExpression expr = func.getArgs( ).getExpression( k );
				if ( expr.getSubQuery( ) != null )
				{
					expr.getSubQuery( )
							.setString( removeCondition.remove( expr.getSubQuery( ),
									conditionMap ) );
				}
			}
		}
		if ( func.getAnalyticFunction( ) != null )
		{
			TExpressionList list = func.getAnalyticFunction( )
					.getPartitionBy_ExprList( );
			if ( list != null && list.size( ) > 0 )
			{
				for ( int i = 0; i < list.size( ); i++ )
				{
					TExpression expr = list.getExpression( i );
					if ( expr.getSubQuery( ) != null )
					{
						expr.getSubQuery( )
								.setString( removeCondition.remove( expr.getSubQuery( ),
										conditionMap ) );
					}
				}
			}
			if ( func.getAnalyticFunction( ).getOrderBy( ) != null )
			{
				TOrderByItemList orderByItemList = func.getAnalyticFunction( )
						.getOrderBy( )
						.getItems( );
				if ( orderByItemList != null && orderByItemList.size( ) > 0 )
				{
					for ( int i = 0; i < orderByItemList.size( ); i++ )
					{
						TExpression sortKey = orderByItemList.getOrderByItem( i )
								.getSortKey( );
						if ( sortKey.getSubQuery( ) != null )
						{
							sortKey.getSubQuery( )
									.setString( removeCondition.remove( sortKey.getSubQuery( ),
											conditionMap ) );
						}
					}
				}
			}
		}
	}

	public boolean exprVisit( TParseTreeNode pnode, boolean pIsLeafNode )
	{
		TExpression expression = (TExpression) pnode;
		if ( is_compare_condition( expression.getExpressionType( ) ) )
		{
			TExpression leftExpr = (TExpression) expression.getLeftOperand( );
			TExpression rightExpr = (TExpression) expression.getRightOperand( );
			if ( leftExpr != null && !checkCondition( leftExpr ) )
			{
				removeExpression( expression );
			}
			if ( rightExpr != null && !checkCondition( rightExpr ) )
			{
				removeExpression( expression );
			}
			if ( ( leftExpr != null && toExprString( leftExpr.toString( ) ) == null )
					|| ( rightExpr != null && toExprString( rightExpr.toString( ) ) == null ) )
			{
				removeExpression( expression );
			}
		}
		if ( expression.getExpressionType( ) == EExpressionType.between_t )
		{
			if ( !checkCondition( expression ) )
				removeExpression( expression );
			if ( expression.getOperatorToken( ) != null
					&& toExprString( expression.getOperatorToken( ).toString( ) ) == null )
			{
				removeExpression( expression );
			}

		}
		if ( expression.getExpressionType( ) == EExpressionType.pattern_matching_t )
		{
			if ( !checkCondition( expression ) )
				removeExpression( expression );
			if ( expression.getOperatorToken( ) != null
					&& toExprString( expression.getOperatorToken( ).toString( ) ) == null )
			{
				removeExpression( expression );
			}
		}
		if ( expression.getExpressionType( ) == EExpressionType.in_t )
		{
			TExpression left = expression.getLeftOperand( );
			if ( !checkCondition( left ) )
			{
				removeExpression( expression );
				return true;
			}

			TExpression right = expression.getRightOperand( );
			if ( right.getSubQuery( ) != null )
			{
				right.getSubQuery( )
						.setString( removeCondition.remove( right.getSubQuery( ),
								conditionMap ) );
			}
			else if ( !checkCondition( right ) )
			{
				removeExpression( expression );
			}

			if ( expression.getOperatorToken( ) != null
					&& toExprString( expression.getOperatorToken( ).toString( ) ) == null )
			{
				removeExpression( expression );
			}
		}
		if ( expression.getFunctionCall( ) != null )
		{
			TFunctionCall func = (TFunctionCall) expression.getFunctionCall( );
			checkFunctionCall( func, conditionMap );
		}
		if ( expression.getSubQuery( ) instanceof TCustomSqlStatement )
		{
			expression.getSubQuery( )
					.setString( removeCondition.remove( expression.getSubQuery( ),
							conditionMap ) );
		}
		if ( expression.getCaseExpression( ) != null )
		{
			TCaseExpression expr = expression.getCaseExpression( );
			TExpression conditionExpr = expr.getInput_expr( );
			if ( conditionExpr != null )
			{
				if ( conditionExpr.getSubQuery( ) != null )
				{
					conditionExpr.getSubQuery( )
							.setString( removeCondition.remove( conditionExpr.getSubQuery( ),
									conditionMap ) );
				}
			}
			TExpression defaultExpr = expr.getElse_expr( );
			if ( defaultExpr != null )
			{
				if ( defaultExpr.getSubQuery( ) != null )
				{
					defaultExpr.getSubQuery( )
							.setString( removeCondition.remove( defaultExpr.getSubQuery( ),
									conditionMap ) );
				}
			}
			TStatementList defaultStatList = expr.getElse_statement_list( );
			if ( defaultStatList != null && defaultStatList.size( ) > 0 )
			{
				for ( int i = 0; i < defaultStatList.size( ); i++ )
				{
					TCustomSqlStatement stmt = defaultStatList.get( i );
					stmt.setString( removeCondition.remove( stmt, conditionMap ) );
				}
			}

			TWhenClauseItemList list = expr.getWhenClauseItemList( );
			if ( list != null && list.size( ) > 0 )
			{
				for ( int i = 0; i < list.size( ); i++ )
				{
					TWhenClauseItem item = list.getWhenClauseItem( i );
					if ( item.getComparison_expr( ) != null )
					{
						if ( item.getComparison_expr( ).getSubQuery( ) != null )
						{
							item.getComparison_expr( )
									.getSubQuery( )
									.setString( removeCondition.remove( item.getComparison_expr( )
											.getSubQuery( ),
											conditionMap ) );
						}
					}
					if ( item.getReturn_expr( ) != null )
					{
						if ( item.getReturn_expr( ).getSubQuery( ) != null )
						{
							item.getReturn_expr( )
									.getSubQuery( )
									.setString( removeCondition.remove( item.getReturn_expr( )
											.getSubQuery( ),
											conditionMap ) );
						}
					}
					TStatementList statList = expr.getElse_statement_list( );
					if ( statList != null && statList.size( ) > 0 )
					{
						for ( int j = 0; j < statList.size( ); j++ )
						{
							TCustomSqlStatement stmt = statList.get( j );
							stmt.setString( removeCondition.remove( statList.get( j ),
									conditionMap ) );
						}
					}
				}
			}

			if ( expression.getOperatorToken( ) != null
					&& toExprString( expression.getOperatorToken( ).toString( ) ) == null )
			{
				removeExpression( expression );
			}
		}

		if ( expression.getLeftOperand( ) == null )
		{
			if ( !checkCondition( expression ) )
			{
				TExpression parentExpr = expression.getParentExpr( );
				removeExpression( expression );
				parentExpr.getOperatorToken( ).setString( "" );
				trimExpr( parentExpr );
			}
		}
		return true;
	}

	private String toExprString( String string )
	{
		if ( string == null || string.length( ) == 0 )
			return null;
		string = string.trim( );
		if ( string.startsWith( "(" ) && string.endsWith( ")" ) )
		{
			string = string.substring( 1, string.length( ) - 1 );
			return toExprString( string.trim( ) );
		}
		return string;
	}

	private void trimExpr( TExpression expr )
	{
		if ( expr == null )
			return;
		if ( expr.toString( ) != null )
		{
			expr.setString( expr.toString( ).trim( ) );
		}
		else
		{
			trimExpr( expr.getParentExpr( ) );
		}
	}

	boolean is_compare_condition( EExpressionType t )
	{
		return ( ( t == EExpressionType.simple_comparison_t ) || ( t == EExpressionType.group_comparison_t ) );
	}

	private void removeExpression( TExpression expression )
	{
		expression.remove2( );
	}

	protected String toString( TParseTreeNode node )
	{
		TSourceToken tsourcetoken = node.getStartToken( );
		if ( tsourcetoken == null )
			return null;
		TSourceToken tsourcetoken1 = node.getEndToken( );
		if ( tsourcetoken1 == null )
			return null;
		TSourceTokenList tsourcetokenlist = tsourcetoken.container;
		if ( tsourcetokenlist == null )
			return null;
		int i = tsourcetoken.posinlist;
		int j = tsourcetoken1.posinlist;
		tokenBuffer.delete( 0, tokenBuffer.length( ) );
		for ( int k = i; k <= j; k++ )
			tokenBuffer.append( tsourcetokenlist.get( k ).toString( ) );
		return tokenBuffer.toString( );
	}
}
