package gudusoft.gsqlparser.dlineage.json;

public class JSONException extends RuntimeException {

	private static final long serialVersionUID = -4984069465393520699L;

	public JSONException(Throwable cause) {
		super("JSON exception", cause);
	}

}
