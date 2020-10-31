package gudusoft.gsqlparser.dlineage.json;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gudusoft.gsqlparser.dlineage.util.BeanUtils;

@SuppressWarnings("rawtypes")
abstract class JSONSerializer implements Serializable {

	private static final long serialVersionUID = -947149613761603469L;

	private static void validate(Object json) {
		if (json == null || !(json instanceof LinkedHashMap))
			throw new IllegalArgumentException("jsonstr validate exception");
	}

	static String serialize(Map jsonObj) {
		validate(jsonObj);
		StringBuilder stringBuilder = new StringBuilder("{");
		Iterator keyIter = jsonObj.keySet().iterator();
		while(keyIter.hasNext()){
			String key = (String) keyIter.next();
			Object value = jsonObj.get(key);
			if (value instanceof List) {
				List list = (List) value;
				int length = list.size();
				StringBuilder str = new StringBuilder();
				for (int i = 0; i < length; i++) {
					Object obj = list.get(i);
					str.append(serialize(BeanUtils.bean2Map(obj)));
					if (i < length - 1) {
						str.append(",");
					}
				}
				stringBuilder.append("\"").append(key).append("\"").append(":").append("[").append(str).append("]").append(",");
			} else if (value.getClass().isArray()) {
				int length = Array.getLength(value);
				StringBuilder str = new StringBuilder();
				for (int i = 0; i < length; i++) {
					Object obj = Array.get(value, i);
					String objJson = serialize(BeanUtils.bean2Map(obj));
					str.append(objJson);
					if (i < length - 1) {
						str.append(",");
					}
				}
				stringBuilder.append("\"").append(key).append("\"").append(":").append("[").append(str).append("]").append(",");
			} else if (value instanceof LinkedHashMap) {
				stringBuilder.append("\"").append(key).append("\"").append(":")
						.append(serialize(BeanUtils.bean2Map(value))).append(",");
			} else {
				appendEntry(stringBuilder, key, value);
			}
		};
		String json = stringBuilder.toString().endsWith(",")
				? stringBuilder.substring(0, stringBuilder.toString().length() - 1).concat("}")
				: stringBuilder.toString().concat("}");
		return json;
	}

	private static void appendEntry(StringBuilder stringBuilder, String key, Object value) {
		boolean needQuote = false;
		if (!(value instanceof String || value instanceof Integer || value instanceof Long || value instanceof Boolean)) {
			value = serialize(BeanUtils.bean2Map(value));
		}
		else if(value instanceof String){
			value = escape((String)value);
			needQuote = true;
		}
		stringBuilder.append("\"").append(key).append("\"").append(":")
				.append(needQuote ? "\"" : "").append(value)
				.append(needQuote ? "\"" : "").append(",");
	}
	
	private static String escape(String value) {
		final StringBuilder buffer = new StringBuilder();
 
		StringCharacterIterator iterator = new StringCharacterIterator(value);
		char myChar = iterator.current();
 
		while (myChar != StringCharacterIterator.DONE) {
			if (myChar == '\"') {
				buffer.append("\\\"");
			} else if (myChar == '\t') {
				buffer.append("\\t");
			} else if (myChar == '\f') {
				buffer.append("\\f");
			} else if (myChar == '\n') {
				buffer.append("\\n");
			} else if (myChar == '\r') {
				buffer.append("\\r");
			} else if (myChar == '\\') {
				buffer.append("\\\\");
			} else if (myChar == '/') {
				buffer.append("\\/");
			} else if (myChar == '\b') {
				buffer.append("\\b");
			} else {
				buffer.append(myChar);
			}
			myChar = iterator.next();
		}
		return buffer.toString();
	}
}
