
package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class Function extends ResultSet {

	private String functionName;
	private Pair<Long, Long> startPosition;
	private Pair<Long, Long> endPosition;

	private TParseTreeNode functionObject;

	public Function(TFunctionCall functionObject) {
		super(functionObject, false);

		this.functionObject = functionObject;

		TSourceToken startToken = functionObject.getStartToken();
		TSourceToken endToken = functionObject.getEndToken();

		this.startPosition = new Pair<Long, Long>(startToken.lineNo, startToken.columnNo);
		this.endPosition = new Pair<Long, Long>(endToken.lineNo, endToken.columnNo + endToken.astext.length());

		if (functionObject.getFunctionName() != null) {
			this.functionName = functionObject.getFunctionName().toString();
		}
	}

	public Function(TCaseExpression caseExpression) {
		super(caseExpression, false);

		this.functionObject = caseExpression;

		TSourceToken startToken = functionObject.getStartToken();
		TSourceToken endToken = functionObject.getEndToken();

		this.startPosition = new Pair<Long, Long>(startToken.lineNo, startToken.columnNo);
		this.endPosition = new Pair<Long, Long>(endToken.lineNo, endToken.columnNo + endToken.astext.length());

		
		this.functionName = "case";
		
	}

	public Pair<Long, Long> getStartPosition() {
		return startPosition;
	}

	public Pair<Long, Long> getEndPosition() {
		return endPosition;
	}

	public String getFunctionName() {
		return functionName;
	}

	public TParseTreeNode getFunctionObject() {
		return functionObject;
	}

}
