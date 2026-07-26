package demos.sqldetect;

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;

public class ColumnDetect {
	public void detect(QueryModel queryModel, TResultColumn field) {
		new ExpressionDetect().detect(queryModel, field.getExpr());
		if (field.getExpr().getExpressionType() == EExpressionType.simple_object_name_t) {
			TObjectName objectName = field.getExpr().getObjectOperand();
			ColumnModel model = queryModel.getColumnModelByName(objectName);
			if (model != null) {
				FieldModel fieldModel = new FieldModel();
				fieldModel.setModel(field);
				fieldModel.setRefColumn(model);
				model.setRefResultColumn(fieldModel);
			}
		}
		if (field.getExpr().getExpressionType() == EExpressionType.assignment_t) {
			TExpression leftExpr = field.getExpr().getLeftOperand();
			if (leftExpr.getExpressionType() == EExpressionType.simple_object_name_t) {
				TObjectName objectName = leftExpr.getObjectOperand();
				ColumnModel model = queryModel.getColumnModelByName(objectName);
				if (model != null) {
					FieldModel fieldModel = new FieldModel();
					fieldModel.setModel(field);
					fieldModel.setRefColumn(model);
					model.setRefResultColumn(fieldModel);
				}
				model.setWrote(true);
			}
		}
	}
}
