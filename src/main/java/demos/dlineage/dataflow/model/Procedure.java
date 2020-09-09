package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

import demos.dlineage.util.Pair3;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;
import gudusoft.gsqlparser.stmt.teradata.TTeradataCreateProcedure;

public class Procedure {
	private int id;
	private String database;
	private String schema;
	private String name;
	private String fullName;
	private Pair3<Long, Long, String> startPosition;
	private Pair3<Long, Long, String> endPosition;
	private List<Argument> arguments = new ArrayList<Argument>();
	private ESqlStatementType type;
	private TStoredProcedureSqlStatement procedureObject;

	public Procedure(TStoredProcedureSqlStatement procedure) {
		if (procedure == null) {
			throw new IllegalArgumentException("Procedure arguments can't be null.");
		} else {
			this.id = ++ModelBindingManager.get().TABLE_COLUMN_ID;
			this.procedureObject = procedure;
			TSourceToken startToken = getProcedureName().getStartToken();
			TSourceToken endToken = getProcedureName().getEndToken();
			this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo, startToken.columnNo, ModelBindingManager.getGlobalHash());
			this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo,
					endToken.columnNo + (long) endToken.astext.length(), ModelBindingManager.getGlobalHash());
			this.fullName = getProcedureName().toString();
			this.name = getProcedureName().toString();
			
			if (!SQLUtil.isEmpty(getProcedureName().getSchemaString())) {
				this.schema = getProcedureName().getSchemaString();
			} else {
				this.schema = ModelBindingManager.getGlobalSchema();
			}
			
			if (!SQLUtil.isEmpty(getProcedureName().getDatabaseString())) {
				this.database = getProcedureName().getDatabaseString();
			} else {
				this.database = ModelBindingManager.getGlobalDatabase();
			}
			
			this.type = procedure.sqlstatementtype;
		}
	}

	private TObjectName getProcedureName() {
		if(procedureObject instanceof TTeradataCreateProcedure)
		{
			return ((TTeradataCreateProcedure)procedureObject).getProcedureName();
		}
		return this.procedureObject.getStoredProcedureName();
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pair3<Long, Long, String> getStartPosition() {
		return this.startPosition;
	}

	public Pair3<Long, Long, String> getEndPosition() {
		return this.endPosition;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<Argument> getArguments() {
		return this.arguments;
	}

	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	public void addArgument(Argument argument) {
		if (argument != null && !this.arguments.contains(argument)) {
			this.arguments.add(argument);
		}

	}

	public ESqlStatementType getType() {
		return this.type;
	}

	public void setType(ESqlStatementType type) {
		this.type = type;
	}

	public TStoredProcedureSqlStatement getProcedureObject() {
		return this.procedureObject;
	}

	public void setProcedureObject(TStoredProcedureSqlStatement procedureObject) {
		this.procedureObject = procedureObject;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStartPosition(Pair3<Long, Long, String> startPosition) {
		this.startPosition = startPosition;
	}

	public void setEndPosition(Pair3<Long, Long, String> endPosition) {
		this.endPosition = endPosition;
	}

	public String getDatabase() {
		return database;
	}

	public String getSchema() {
		return schema;
	}

}
