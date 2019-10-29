package demos.dlineage.dataflow.model.json;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.annotation.JSONField;

public class Coordinate {
	@JSONField(ordinal = 1)
	private long x;
	@JSONField(ordinal = 2)
	private long y;

	private static final Pattern p = Pattern.compile("\\d+\\,\\d+");

	public static Coordinate[] parse(String coordinate) {
		if (coordinate != null) {
			List<Coordinate> coordinates = new ArrayList<Coordinate>();
			try {
				Matcher m = p.matcher(coordinate);
				while (m.find()) {
					String str = m.group();
					String[] segments = str.trim().split(",");
					if (segments.length == 2) {
						Coordinate item = new Coordinate();
						item.x = Long.parseLong(segments[0]);
						item.y = Long.parseLong(segments[1]);
						coordinates.add(item);
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			return coordinates.toArray(new Coordinate[0]);
		}
		return new Coordinate[0];
	}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}

}
