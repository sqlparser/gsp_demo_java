package gudusoft.gsqlparser.dlineage.json;

public abstract class JSON {

	public static String toJsonString(Object object) {
		return JSONRender.getInstance().toJsonString(object);
	}

	public static Object parseObject(String jsonStr) {
		return new JSONObject(jsonStr).getJsonObj();
	}

}
