
package demos.removeCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.ENodeStatus;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;

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
		
		if(expression == null){
			return false;
		}
		
		String expr = expression.toString();
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
		if ( !buffer.toString( ).equals( expression.toString()) )
			expression.setString( buffer.toString( ) );
		return true;
	}

	public void checkExpression( TExpression expr,
			Map<String, String> conditionMap )
	{
		this.conditionMap = conditionMap;
		expr.postOrderTraverse( this );
	}

	public void checkFunctionCall( TParseTreeNode parent, TFunctionCall func,
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
				else if ( !checkCondition( expr ) ){
					if(parent instanceof TExpression){
						((TExpression)parent).removeMe();
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
										conditionMap ) );
					}
					else if ( !checkCondition( expr ) ){
						expr.removeMe();
						list.removeElementAt(i);
						i--;
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
						else if ( !checkCondition( sortKey ) ){
							sortKey.removeMe();
							orderByItemList.removeElementAt(i);
							i--;
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
				expression.removeMe();
			}
			if ( rightExpr != null && !checkCondition( rightExpr ) )
			{
				expression.removeMe();
			}
			if ( ( leftExpr == null || toExprString( leftExpr.toString( ) ) == null )
					|| ( rightExpr == null || toExprString( rightExpr.toString( ) ) == null ) )
			{
				expression.removeMe();
			}
		}
		if ( expression.getExpressionType( ) == EExpressionType.between_t )
		{
			TExpression leftExpr = (TExpression) expression.getLeftOperand( );
			TExpression rightExpr = (TExpression) expression.getRightOperand( );
			if ( ( leftExpr == null || toExprString( leftExpr.toString( ) ) == null )
					|| ( rightExpr == null || toExprString( rightExpr.toString( ) ) == null ) )
			{
				expression.removeMe();
			}
			else if ( !checkCondition( expression ) )
			{
				expression.removeMe();
			}
			else if ( expression.getOperatorToken( ) != null
					&& toExprString( expression.getOperatorToken( ).toString( ) ) == null )
			{
				expression.removeMe();
			}

		}
		if ( expression.getExpressionType( ) == EExpressionType.pattern_matching_t )
		{
			if ( !checkCondition( expression ) )
				expression.removeMe();
			if ( expression.getOperatorToken( ) != null
					&& toExprString( expression.getOperatorToken( ).toString( ) ) == null )
			{
				expression.removeMe();
			}
		}
		if ( expression.getExpressionType( ) == EExpressionType.in_t )
		{
			TExpression left = expression.getLeftOperand( );
			if ( !checkCondition( left ) )
			{
				expression.removeMe();
				return true;
			}

			TExpression right = expression.getRightOperand( );
			if ( right!=null && right.getSubQuery( ) != null )
			{
				right.getSubQuery( )
						.setString( removeCondition.remove( right.getSubQuery( ),
								conditionMap ) );
			}
			else if ( !checkCondition( right ) )
			{
				expression.removeMe();
			}

			if ( expression.getOperatorToken( ) != null
					&& toExprString( expression.getOperatorToken( ).toString( ) ) == null )
			{
				expression.removeMe();
			}
		}
		if ( expression.getFunctionCall( ) != null )
		{
			TFunctionCall func = (TFunctionCall) expression.getFunctionCall( );
			checkFunctionCall( expression, func, conditionMap );
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
				expression.removeMe();
			}
		}

	
		if ( expression.getLeftOperand( ) == null )
		{
			if (expression.getExpressionType() == EExpressionType.list_t) {
				int size = expression.getExprList().size();
				List<TExpression> removeItems = new ArrayList<TExpression>();
				boolean needRemoveAfterComma = false;
				for (int i = size - 1; i >= 0; i--) {
					TExpression item = expression.getExprList().getExpression(i);
					if (!checkCondition(item)) {
						if (i >= 1) {
							TParseTreeNode.removeTokensBetweenNodes(expression.getExprList().getExpression(i - 1),
									item);
						}
						if (needRemoveAfterComma && i < size - 1) {
							TParseTreeNode.removeTokensBetweenNodes(expression.getExprList().getExpression(i),
									expression.getExprList().getExpression(i + 1));
						}
						removeItems.add(item);
						needRemoveAfterComma = false;
					}
					else {
						needRemoveAfterComma = true;
					}
				}
				for(TExpression item:removeItems) {
					item.removeMe();
				}
				
				if (toExprString(expression.toString()) == null) {
					expression.removeMe();
				}
			}
			else {
				if (!checkCondition(expression)) {
					expression.removeMe();
				}

				if (expression.getExpressionType() == EExpressionType.parenthesis_t) {
					expression.removeMe();
				}
			}
		}
		
		if(expression.getExpressionType() == EExpressionType.logical_and_t 
				|| expression.getExpressionType() == EExpressionType.logical_or_t ){
			if(expression.getLeftOperand() == null && expression.getRightOperand() == null){
				expression.removeMe();
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

	boolean is_compare_condition( EExpressionType t )
	{
		return ( ( t == EExpressionType.simple_comparison_t ) || ( t == EExpressionType.group_comparison_t ) );
	}
}
