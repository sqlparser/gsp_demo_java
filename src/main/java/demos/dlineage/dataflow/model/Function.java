
package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair3;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TParseTreeNode;

public class Function extends ResultSet {

	private String functionName;
	private Pair3<Long, Long, String> startPosition;
	private Pair3<Long, Long, String> endPosition;

	private TParseTreeNode functionObject;

	public Function(TFunctionCall functionObject) {
		super(functionObject, false);

		this.functionObject = functionObject;

		TSourceToken startToken = functionObject.getStartToken();
		TSourceToken endToken = functionObject.getEndToken();

		this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo, ModelBindingManager.getGlobalHash());
		this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo, endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

		if (functionObject.getFunctionName() != null) {
			this.functionName = functionObject.getFunctionName().toString();
		}
	}

	public Function(TCaseExpression caseExpression) {
		super(caseExpression, false);

		this.functionObject = caseExpression;

		TSourceToken startToken = functionObject.getStartToken();
		TSourceToken endToken = functionObject.getEndToken();

		this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo, ModelBindingManager.getGlobalHash());
		this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo, endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

		
		this.functionName = "case";
		
	}

	public Pair3<Long, Long, String> getStartPosition() {
		return startPosition;
	}

	public Pair3<Long, Long, String> getEndPosition() {
		return endPosition;
	}

	public String getFunctionName() {
		return functionName;
	}

	public TParseTreeNode getFunctionObject() {
		return functionObject;
	}

}
