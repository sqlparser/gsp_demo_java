package gudusoft.gsqlparser.dlineage.json;

import gudusoft.gsqlparser.dlineage.util.BeanUtils;

public class JSONRender {

	public String toJsonString(Object object) {
		return JSONSerializer.serialize(BeanUtils.bean2Map(object));
	}
	
	private static class JsonRenderHolder {
		private static JSONRender INSTANCE = new JSONRender();
	}

	public static JSONRender getInstance() {
		return JsonRenderHolder.INSTANCE;
	}

	private JSONRender() {
	}
}
