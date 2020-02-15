package demos.columnInWhereClause;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class WhereCondition implements IExpressionVisitor
{

	private TExpression condition;

	public WhereCondition( TExpression expr )
	{
		this.condition = expr;
	}

	public void printColumn( )
	{
		this.condition.inOrderTraverse( this );
	}

	boolean is_compare_condition( EExpressionType t )
	{
		return ( ( t == EExpressionType.simple_comparison_t )
				|| ( t == EExpressionType.group_comparison_t ) || ( t == EExpressionType.in_t ) );
	}

	public boolean exprVisit( TParseTreeNode pnode, boolean pIsLeafNode )
	{
		TExpression lcexpr = (TExpression) pnode;
		if ( is_compare_condition( lcexpr.getExpressionType( ) ) )
		{
			TExpression leftExpr = (TExpression) lcexpr.getLeftOperand( );

			System.out.println( "column: " + leftExpr.toString( ) );
			if ( lcexpr.getComparisonOperator( ) != null )
			{
				System.out.println( "Operator: "
						+ lcexpr.getComparisonOperator( ).astext );
			}
			TExpression rightExpr = (TExpression)lcexpr.getRightOperand();
			if ( rightExpr.getExpressionType() == EExpressionType.subquery_t){
				System.out.println( "value: (subquery)");
			}else{
				System.out.println( "value: "
						+ rightExpr.toString( ) );
			}
			System.out.println( "" );
		}
		return true;
	}
}
