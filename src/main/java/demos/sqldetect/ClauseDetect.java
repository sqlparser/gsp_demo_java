package demos.sqldetect;

import gudusoft.gsqlparser.nodes.TForUpdate;
import gudusoft.gsqlparser.nodes.TGroupBy;
import gudusoft.gsqlparser.nodes.TGroupByItem;
import gudusoft.gsqlparser.nodes.TIntoClause;
import gudusoft.gsqlparser.nodes.TLimitClause;
import gudusoft.gsqlparser.nodes.TMergeInsertClause;
import gudusoft.gsqlparser.nodes.TMergeUpdateClause;
import gudusoft.gsqlparser.nodes.TMergeWhenClause;
import gudusoft.gsqlparser.nodes.TOrderBy;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TWhereClause;

public class ClauseDetect {
	public void detectWhereClause(QueryModel queryModel,
			TWhereClause whereClause) {
		if (whereClause != null && whereClause.getCondition() != null) {
			new ExpressionDetect().detect(queryModel,
					whereClause.getCondition());
		}
	}

	public void detectOrderByClause(QueryModel queryModel, TOrderBy orderBy) {
		if (orderBy != null) {
			if (orderBy.getResetWhenCondition() != null) {
				new ExpressionDetect().detect(queryModel,
						orderBy.getResetWhenCondition());
			}
			if (orderBy.getItems() != null) {
				for (int i = 0; i < orderBy.getItems().size(); i++) {
					TOrderByItem item = orderBy.getItems().getOrderByItem(i);
					if (item.getSortKey() != null) {
						new ExpressionDetect().detect(queryModel,
								item.getSortKey());
					}
				}
			}
		}
	}

	public void detectGroupByClause(QueryModel queryModel, TGroupBy groupBy) {
		if (groupBy != null) {
			if (groupBy.getHavingClause() != null) {
				new ExpressionDetect().detect(queryModel,
						groupBy.getHavingClause());
			}
			if (groupBy.getItems() != null) {
				for (int i = 0; i < groupBy.getItems().size(); i++) {
					TGroupByItem item = groupBy.getItems().getGroupByItem(i);
					if (item.getExpr() != null) {
						new ExpressionDetect().detect(queryModel,
								item.getExpr());
					}
				}
			}
		}
	}

	public void detectLimitClause(QueryModel queryModel,
			TLimitClause limitClause) {
		if (limitClause.getOffset() != null) {
			new ExpressionDetect().detect(queryModel, limitClause.getOffset());
		}
		if (limitClause.getRow_count() != null) {
			new ExpressionDetect().detect(queryModel,
					limitClause.getRow_count());
		}
		if (limitClause.getSelectFetchFirstValue() != null) {
			new ExpressionDetect().detect(queryModel,
					limitClause.getSelectFetchFirstValue());
		}
	}

	public void detectIntoClause(QueryModel queryModel, TIntoClause intoClause) {
		if (intoClause.getExprList() != null) {
			for (int i = 0; i < intoClause.getExprList().size(); i++) {
				new ExpressionDetect().detect(queryModel, intoClause
						.getExprList().getExpression(i));

			}
		}
	}

	public void detectMergeUpdateClause(QueryModel queryModel,
			TMergeUpdateClause updateClause) {
		if (updateClause.getDeleteWhereClause() != null) {
			new ExpressionDetect().detect(queryModel,
					updateClause.getDeleteWhereClause());
		}
		if (updateClause.getUpdateColumnList() != null) {
			queryModel.getSqlDetect().detectResultColumns(queryModel,
					updateClause.getUpdateColumnList());
		}
		if (updateClause.getUpdateWhereClause() != null) {
			new ExpressionDetect().detect(queryModel,
					updateClause.getUpdateWhereClause());
		}
	}

	public void detectMergeWhenClause(QueryModel queryModel,
			TMergeWhenClause whenClause) {
		if (whenClause.getCondition() != null) {
			new ExpressionDetect()
					.detect(queryModel, whenClause.getCondition());
		}
		if (whenClause.getDeleteClause() != null) {
			
		}
		if (whenClause.getInsertClause() != null) {
			TMergeInsertClause insert = whenClause.getInsertClause();
			if (insert.getColumnList() != null) {
				queryModel.getSqlDetect().detectColumnLists(queryModel,
						insert.getColumnList());
			}
			if (insert.getInsertWhereClause() != null) {
				new ExpressionDetect().detect(queryModel,
						insert.getInsertWhereClause());
			}
			if (insert.getValuelist() != null) {
				queryModel.getSqlDetect().detectResultColumns(queryModel,
						insert.getValuelist());
			}
		}
		if (whenClause.getUpdateClause() != null) {
			detectMergeUpdateClause(queryModel, whenClause.getUpdateClause());
		}
	}

	public void detectForUpdateClause(QueryModel queryModel,
			TForUpdate forUpdateClause) {
		if (forUpdateClause.getColumnRefs() != null) {
			queryModel.getSqlDetect().detectColumnLists(queryModel,
					forUpdateClause.getColumnRefs());
		}
	}
}
