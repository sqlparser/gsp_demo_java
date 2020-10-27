
package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair;
import demos.dlineage.util.Pair3;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TResultColumn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultColumn {

	protected ResultSet resultSet;

	protected int id;

	protected String alias;
	protected Pair3<Long, Long, String> aliasStartPosition;
	protected Pair3<Long, Long, String> aliasEndPosition;

	protected String fullName;
	protected String name;

	protected Pair3<Long, Long, String> startPosition;
	protected Pair3<Long, Long, String> endPosition;

	protected TParseTreeNode columnObject;

	protected Map<String, Set<TObjectName>> starLinkColumns = new LinkedHashMap<String, Set<TObjectName>>();

	private boolean showStar = true;

	protected boolean isFunction = false;

	public ResultColumn() {

	}

	public ResultColumn(ResultSet resultSet, TParseTreeNode columnObject) {
		if (columnObject == null || resultSet == null)
			throw new IllegalArgumentException("ResultColumn arguments can't be null.");

		id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

		this.resultSet = resultSet;
		resultSet.addColumn(this);

		this.columnObject = columnObject;

		TSourceToken startToken = columnObject.getStartToken();
		TSourceToken endToken = columnObject.getEndToken();

		if (columnObject instanceof TObjectName) {
			if (((TObjectName) columnObject).getColumnNameOnly() != null
					&& !"".equals(((TObjectName) columnObject).getColumnNameOnly())) {
				this.name = ((TObjectName) columnObject).getColumnNameOnly();
			} else {
				this.name = ((TObjectName) columnObject).toString();
			}
		} else
			this.name = columnObject.toString();

		this.name = SQLUtil.trimColumnStringQuote(name);

		this.fullName = columnObject.toString();

		this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo,
				ModelBindingManager.getGlobalHash());
		this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo, endToken.columnNo + endToken.astext.length(),
				ModelBindingManager.getGlobalHash());
	}

	public ResultColumn(ResultSet resultSet, TResultColumn resultColumnObject) {
		if (resultColumnObject == null || resultSet == null)
			throw new IllegalArgumentException("ResultColumn arguments can't be null.");

		id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

		this.resultSet = resultSet;
		resultSet.addColumn(this);

		this.columnObject = resultColumnObject;

		if (resultColumnObject.getAliasClause() != null) {
			this.alias = resultColumnObject.getAliasClause().toString();
			TSourceToken startToken = resultColumnObject.getAliasClause().getStartToken();
			TSourceToken endToken = resultColumnObject.getAliasClause().getEndToken();
			this.aliasStartPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo,
					ModelBindingManager.getGlobalHash());
			this.aliasEndPosition = new Pair3<Long, Long, String>(endToken.lineNo,
					endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

			this.name = this.alias;
		} else {
			if (resultColumnObject.getExpr().getExpressionType() == EExpressionType.simple_constant_t) {
				this.name = resultColumnObject.toString();
			} else if (resultColumnObject.getExpr()
					.getExpressionType() == EExpressionType.sqlserver_proprietary_column_alias_t) {
				this.alias = resultColumnObject.getExpr().getLeftOperand().toString();
				TSourceToken startToken = resultColumnObject.getExpr().getLeftOperand().getStartToken();
				TSourceToken endToken = resultColumnObject.getExpr().getLeftOperand().getEndToken();
				this.aliasStartPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo,
						ModelBindingManager.getGlobalHash());
				this.aliasEndPosition = new Pair3<Long, Long, String>(endToken.lineNo,
						endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

				this.name = this.alias;
			} else if (resultColumnObject.getExpr().getExpressionType() == EExpressionType.function_t) {
				this.name = resultColumnObject.getExpr().getFunctionCall().getFunctionName().toString();
			} else if (resultColumnObject.getColumnNameOnly() != null
					&& !"".equals(resultColumnObject.getColumnNameOnly())) {
				this.name = resultColumnObject.getColumnNameOnly();
			} else {
				this.name = resultColumnObject.toString();
			}
		}

		if (resultColumnObject.getExpr().getExpressionType() == EExpressionType.function_t) {
			this.fullName = resultColumnObject.getExpr().getFunctionCall().getFunctionName().toString();
		} else {
			this.fullName = resultColumnObject.toString();
		}

		this.name = SQLUtil.trimColumnStringQuote(name);

		TSourceToken startToken = resultColumnObject.getStartToken();
		TSourceToken endToken = resultColumnObject.getEndToken();
		this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo,
				ModelBindingManager.getGlobalHash());
		this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo, endToken.columnNo + endToken.astext.length(),
				ModelBindingManager.getGlobalHash());
	}

	public ResultColumn(SelectResultSet resultSet, Pair<TResultColumn, TObjectName> starColumnPair) {
		if (starColumnPair == null || resultSet == null)
			throw new IllegalArgumentException("ResultColumn arguments can't be null.");

		id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

		this.resultSet = resultSet;
		resultSet.addColumn(this);

		this.columnObject = starColumnPair.first;

		TSourceToken startToken = columnObject.getStartToken();
		TSourceToken endToken = columnObject.getEndToken();

		this.name = ((TObjectName) columnObject).getColumnNameOnly();
		if(name == null){
			name =  ((TObjectName) columnObject).toString();
		}
		this.fullName = columnObject.toString();

		this.name = SQLUtil.trimColumnStringQuote(name);

		this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo,
				ModelBindingManager.getGlobalHash());
		this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo, endToken.columnNo + endToken.astext.length(),
				ModelBindingManager.getGlobalHash());
	}

	// private int getIndexOf(TResultColumnList resultColumnList,
	// TResultColumn resultColumnObject) {
	// for (int i = 0; i < resultColumnList.size(); i++) {
	// if (resultColumnList.getResultColumn(i) == resultColumnObject)
	// return i;
	// }
	// return -1;
	// }

	public ResultSet getResultSet() {
		return resultSet;
	}

	public int getId() {
		return id;
	}

	public String getAlias() {
		return alias;
	}

	public Pair3<Long, Long, String> getAliasStartPosition() {
		return aliasStartPosition;
	}

	public Pair3<Long, Long, String> getAliasEndPosition() {
		return aliasEndPosition;
	}

	public String getFullName() {
		return fullName;
	}

	public Pair3<Long, Long, String> getStartPosition() {
		return startPosition;
	}

	public Pair3<Long, Long, String> getEndPosition() {
		return endPosition;
	}

	public TParseTreeNode getColumnObject() {
		return columnObject;
	}

	public String getName() {
		return name;
	}

	public void bindStarLinkColumn(TObjectName objectName) {
		if (objectName == null) {
			return;
		}

		String columnName = SQLUtil.getColumnName(objectName);

		if ("*".equals(columnName)) {
			return;
		}
		if (!starLinkColumns.containsKey(columnName)) {
			starLinkColumns.putIfAbsent(columnName, new LinkedHashSet<>());
		}

		starLinkColumns.get(columnName).add(objectName);
	}
	
	public boolean hasStarLinkColumn(){
		return starLinkColumns!=null && !starLinkColumns.isEmpty();
	}

	public Map<String, Set<TObjectName>> getStarLinkColumns() {
		return starLinkColumns;
	}

	public List<TObjectName> getStarLinkColumnList() {
		List<TObjectName> columns = new ArrayList<>();
		if (starLinkColumns != null) {
			for (Set<TObjectName> columnSet : starLinkColumns.values()) {
				columns.addAll(columnSet);
			}
		}
		return columns;
	}
	
	public List<String> getStarLinkColumnNames() {
		List<String> columns = new ArrayList<>();
		if (starLinkColumns != null) {
			columns.addAll(starLinkColumns.keySet());
		}
		return columns;
	}

	public boolean isShowStar() {
		return showStar;
	}

	public void setShowStar(boolean showStar) {
		this.showStar = showStar;
	}

	public boolean isFunction() {
		return isFunction;
	}

	public void setFunction(boolean isFunction) {
		this.isFunction = isFunction;
	}



}
