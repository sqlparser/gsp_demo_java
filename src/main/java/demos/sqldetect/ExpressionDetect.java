package demos.sqldetect;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class ExpressionDetect {

	public void detect(QueryModel queryModel, TExpression expr) {
		expr.inOrderTraverse(new ExpressionVisitor(queryModel));
		if (expr.getSubQuery() != null) {
			queryModel.getSqlDetect().detectSubQuery(queryModel,
					expr.getSubQuery());
		}
	}

	class ExpressionVisitor implements IExpressionVisitor {

		private QueryModel queryModel;

		public ExpressionVisitor(QueryModel queryModel) {
			this.queryModel = queryModel;
		}

		public boolean exprVisit(TParseTreeNode pNode, boolean isLeafNode) {
			TExpression lcexpr = (TExpression) pNode;
			if (lcexpr.getExpressionType() == EExpressionType.simple_object_name_t) {

			} else if (lcexpr.getExpressionType() == EExpressionType.between_t) {

			} else if (lcexpr.getExpressionType() == EExpressionType.function_t) {

			} else if (lcexpr.getExpressionType() == EExpressionType.subquery_t) {
				queryModel.getSqlDetect().detectSubQuery(queryModel,
						lcexpr.getSubQuery());
			} else if (lcexpr.getExpressionType() == EExpressionType.case_t) {

			}
			return true;
		}

	}
}
