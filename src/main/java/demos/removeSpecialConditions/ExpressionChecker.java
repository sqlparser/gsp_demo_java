
package demos.removeSpecialConditions;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
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

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionChecker implements IExpressionVisitor
{

	private List<String> conditionList;
	private RemoveCondition removeCondition;

	public ExpressionChecker( RemoveCondition removeCondition )
	{
		this.removeCondition = removeCondition;
	}

	public void checkExpression( TExpression expr,
			List<String> conditionList )
	{
		this.conditionList = conditionList;
		expr.inOrderTraverse( this );
		System.out.println();
	}

	boolean is_compare_condition( EExpressionType t )
	{
		return ( ( t == EExpressionType.simple_comparison_t ) || ( t == EExpressionType.group_comparison_t ) );
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
			if ( rightExpr != null && !checkCondition( rightExpr ))
			{
				removeExpression( expression );
			}
		}
		if ( expression.getExpressionType( ) == EExpressionType.between_t
				&& !checkCondition( expression ) )
		{
			removeExpression( expression );
		}
		if ( expression.getExpressionType( ) == EExpressionType.pattern_matching_t
				&& !checkCondition( expression ) )
		{
			removeExpression( expression );
		}
		if ( expression.getExpressionType( ) == EExpressionType.in_t )
		{
			TExpression expr = expression.getRightOperand( );
			if ( expr.getSubQuery( ) != null )
			{
				expr.getSubQuery( )
						.setString( removeCondition.remove( expr.getSubQuery( ),
								conditionList ) );
			}
			if ( !checkCondition( expression ) )
				removeExpression( expression );
		}
		if ( expression.getFunctionCall( ) != null )
		{
			TFunctionCall func = (TFunctionCall) expression.getFunctionCall( );
			checkFunctionCall( func, conditionList );
			if ( !checkCondition( expression ) )
				removeExpression( expression );
		}
		if ( expression.getSubQuery( ) instanceof TCustomSqlStatement )
		{
			expression.getSubQuery( )
					.setString( removeCondition.remove( expression.getSubQuery( ),
							conditionList ) );
			if ( !checkCondition( expression ) )
				removeExpression( expression );
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
									conditionList ) );
				}
			}
			TExpression defaultExpr = expr.getElse_expr( );
			if ( defaultExpr != null )
			{
				if ( defaultExpr.getSubQuery( ) != null )
				{
					defaultExpr.getSubQuery( )
							.setString( removeCondition.remove( defaultExpr.getSubQuery( ),
									conditionList ) );
				}
			}
			TStatementList defaultStatList = expr.getElse_statement_list( );
			if ( defaultStatList != null && defaultStatList.size( ) > 0 )
			{
				for ( int i = 0; i < defaultStatList.size( ); i++ )
				{
					TCustomSqlStatement stmt = defaultStatList.get( i );
					stmt.setString( removeCondition.remove( stmt, conditionList ) );
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
											conditionList ) );
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
											conditionList ) );
						}
					}
					TStatementList statList = expr.getElse_statement_list( );
					if ( statList != null && statList.size( ) > 0 )
					{
						for ( int j = 0; j < statList.size( ); j++ )
						{
							TCustomSqlStatement stmt = statList.get( j );
							stmt.setString( removeCondition.remove( statList.get( j ),
									conditionList ) );
						}
					}
				}
			}
			if ( !checkCondition( expression ) )
				removeExpression( expression );
		}

		return true;
	}

	private void removeExpression( TExpression expression )
	{
		expression.remove2( );
	}

	public void checkFunctionCall( TFunctionCall func,
			List<String> conditionList )
	{
		if ( func.getArgs( ) != null )
		{
			for ( int k = 0; k < func.getArgs( ).size( ); k++ )
			{
				TExpression expr = func.getArgs( ).getExpression( k );
				if ( expr.getSubQuery( ) != null )
				{
					String text = removeCondition.remove( expr.getSubQuery( ), conditionList );
					if(Objects.nonNull(text)){
						expr.getSubQuery( ).setString( text);
					}
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
										conditionList ) );
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
											conditionList ) );
						}
					}
				}
			}
		}
	}

	StringBuffer buffer = new StringBuffer( );

	private boolean checkCondition( TExpression expression )
	{
		String[] conditions = new String[0];
		if ( conditionList != null && !conditionList.isEmpty( ) )
		{
			conditions = conditionList.toArray( new String[0] );
		}
		String expr = toString( expression );
		Pattern pattern = Pattern.compile( "\\$[^$]+\\$", Pattern.CASE_INSENSITIVE );
		if(expr!=null){
			Matcher matcher = pattern.matcher( expr );
			buffer.delete( 0, buffer.length( ) );
			while ( matcher.find( ) )
			{
				String condition = matcher.group( ).replaceAll( "\\$", "" ).trim( );
				boolean flag = true;
				for ( int i = 0; i < conditions.length; i++ )
				{
					if ( conditions[i].equalsIgnoreCase( condition )
							&& conditionList.contains( conditions[i] ) )
					{
						flag = false;
						break;
					}
				}
				if ( !flag )
					return false;
			}
			matcher.appendTail( buffer );
			if ( !toString( expression ).equals( buffer.toString( ) ) )
				expression.setString( buffer.toString( ) );
		}
		return true;
	}

	StringBuffer tokenBuffer = new StringBuffer( );

	public String toString( TParseTreeNode node )
	{
		return node.toString( );
	}
//	public String toString( TParseTreeNode node )
//	{
//		TSourceToken tsourcetoken = node.getStartToken( );
//		if ( tsourcetoken == null )
//			return null;
//		TSourceToken tsourcetoken1 = node.getEndToken( );
//		if ( tsourcetoken1 == null )
//			return null;
//		TSourceTokenList tsourcetokenlist = tsourcetoken.container;
//		if ( tsourcetokenlist == null )
//			return null;
//		int i = tsourcetoken.posinlist;
//		int j = tsourcetoken1.posinlist;
//		tokenBuffer.delete( 0, tokenBuffer.length( ) );
//		for ( int k = i; k <= j; k++ )
//			tokenBuffer.append( tsourcetokenlist.get( k ).toString( ) );
//		return tokenBuffer.toString( );
//	}
}

