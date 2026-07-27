package demos.sqlenv.constant;

public enum DbOperation {
	TODDL("2DDL"), TOJSON("2JSON");

	private String operation;

	private DbOperation(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}

}
