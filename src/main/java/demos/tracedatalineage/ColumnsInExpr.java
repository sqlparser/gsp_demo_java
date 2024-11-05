
package demos.tracedatalineage;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TGroupByItem;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeNodeList;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColumnsInExpr implements IExpressionVisitor
{

	private TExpression expr;
	private List<Column> columns;
	private TCustomSqlStatement stmt;
	private traceDataLineage traceDataLineage;

	public ColumnsInExpr( traceDataLineage traceDataLineage, TExpression expr,
			List<Column> columns, TCustomSqlStatement stmt )
	{
		this.stmt = stmt;
		this.expr = expr;
		this.columns = columns;
		this.traceDataLineage = traceDataLineage;
	}

	public boolean exprVisit( TParseTreeNode pNode, boolean isLeafNode )
	{
		TExpression lcexpr = (TExpression) pNode;
		if ( lcexpr.getExpressionType( ) == EExpressionType.simple_object_name_t )
		{
			Table table = traceDataLineage.getTable( lcexpr.toString( ), stmt );
			String columnName = traceDataLineage.getColumnName( lcexpr.toString( ) );
			if ( table != null && columnName != null )
			{
				Map<String, Column> columnMap = table.getColumns( );
				if ( columnMap != null )
				{
					Column temp = columnMap.get( columnName );
					if ( temp != null )
					{
						columns.add( temp );
					}
				}
			}

		}
		else if ( lcexpr.getExpressionType( ) == EExpressionType.function_t )
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
				addColumnToList( list, stmt );

				if ( func.getAnalyticFunction( ).getOrderBy( ) != null )
				{
					list = func.getAnalyticFunction( ).getOrderBy( ).getItems( );
					addColumnToList( list, stmt );
				}
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

	public void searchColumn( )
	{
		this.expr.inOrderTraverse( this );
	}

}
