package gudusoft.gsqlparser.dlineage.json;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class JSONObject implements Serializable {

	private static final Map<Character, Character> TRANSEFER_CHAR_MAP = new HashMap<Character, Character>() {
		private static final long serialVersionUID = -7676628703258784840L;
		{
			put('"', '"');
			put('\\', '\\');
			put('/', '/');
			put('b', '\b');
			put('f', '\f');
			put('n', '\n');
			put('r', '\r');
			put('t', '\t');
		}
	};

	private static final long serialVersionUID = 3696246208486260599L;

	private static final Object OBJECT_END = new Object();
	private static final Object ARRAY_END = new Object();
	private static final Object COLON = new Object();
	private static final Object COMMA = new Object();

	private transient CharacterIterator it = null;
	private char c = 0;
	private transient Object jsonObj = null;
	private StringBuilder builder = new StringBuilder();

	public JSONObject(String sourceStr) {
		it = new StringCharacterIterator(sourceStr);
		c = it.first();
		read();
	}

	private Object read() {
		while (Character.isWhitespace(c))
			c = it.next();
		char ch = c;
		c = it.next();
		switch (ch) {
		case '"':
			jsonObj = matchString();
			break;
		case '[':
			jsonObj = matchArray();
			break;
		case ']':
			jsonObj = ARRAY_END;
			break;
		case ',':
			jsonObj = COMMA;
			break;
		case '{':
			jsonObj = matchObject();
			break;
		case '}':
			jsonObj = OBJECT_END;
			break;
		case ':':
			jsonObj = COLON;
			break;
		case 't':
			loopNext(3);
			jsonObj = Boolean.TRUE;
			break;
		case 'f':
			loopNext(4);
			jsonObj = Boolean.FALSE;
			break;
		case 'n':
			loopNext(3);
			jsonObj = null;
			break;
		default:
			c = it.previous();
			if (Character.isDigit(c) || c == '-')
				jsonObj = matchNumber();
			else
				throw new RuntimeException("json4bean unknown value type! check is have null value and fix it!");
		}
		return jsonObj;
	}

	private void loopNext(int times) {
		for (int i = 0; i < times; i++)
			c = it.next();
	}

	private Object matchObject() {
		Map<Object, Object> result = new LinkedHashMap<Object, Object>();
		Object key = read();
		while (jsonObj != OBJECT_END) {
			read();
			if (jsonObj != OBJECT_END) {
				result.put(key, read());
				if (read() == COMMA)
					key = read();
			}
		}
		return result;
	}

	private Object matchArray() {
		List<Object> result = new ArrayList<Object>();
		Object value = read();
		while (jsonObj != ARRAY_END) {
			result.add(value);
			if (read() == COMMA) {
				value = read();
			}
		}
		return result;
	}

	private Object matchNumber() {
		int length = 0;
		boolean isFloatingPoint = false;
		builder.setLength(0);

		if (c == '-') {
			add(c);
		}
		length += addDigits();
		if (c == '.') {
			add(c);
			length += addDigits();
			isFloatingPoint = true;
		}
		if (c == 'e' || c == 'E') {
			add(c);
			if (c == '+' || c == '-') {
				add(c);
			}
			addDigits();
			isFloatingPoint = true;
		}

		String s = builder.toString();
		return isFloatingPoint ? (length < 17) ? (Object) Double.valueOf(s) : new BigDecimal(s)
				: (length < 19) ? (Object) Long.valueOf(s) : new BigInteger(s);
	}

	private int addDigits() {
		int result;
		for (result = 0; Character.isDigit(c); ++result)
			add(c);
		return result;
	}

	private Object matchString() {
		builder.setLength(0);
		while (c != '"') {
			if (c == '\\') {
				c = it.next();
				add(c == 'u' ? unicode() : TRANSEFER_CHAR_MAP.get(c));
			} else
				add(c);
		}
		c = it.next();
		return builder.toString();
	}

	private void add(Character cc) {
		if (cc == null)
			return;
		builder.append(cc);
		c = it.next();
	}

	private char unicode() {
		int value = 0;
		for (int i = 0; i < 4; ++i) {
			switch (c = it.next()) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				value = (value << 4) + c - '0';
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
				value = (value << 4) + (c - 'a') + 10;
				break;
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				value = (value << 4) + (c - 'A') + 10;
				break;
			default:
				;
			}
		}
		return (char) value;
	}

	public Object getJsonObj() {
		return jsonObj;
	}
}
