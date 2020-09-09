package demos.dlineage.dataflow.model;

public class SqlInfo {
	private String fileName;
	private String sql;
	private int originIndex;
	private int index;
	private String group;
	private int originLineStart;
	private int originLineEnd;
	private int lineStart;
	private int lineEnd;
	private String hash;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getOriginIndex() {
		return originIndex;
	}

	public void setOriginIndex(int originIndex) {
		this.originIndex = originIndex;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getOriginLineStart() {
		return originLineStart;
	}

	public void setOriginLineStart(int originLineStart) {
		this.originLineStart = originLineStart;
	}

	public int getOriginLineEnd() {
		return originLineEnd;
	}

	public void setOriginLineEnd(int originLineEnd) {
		this.originLineEnd = originLineEnd;
	}

	public int getLineStart() {
		return lineStart;
	}

	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
	}

	public int getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(int lineEnd) {
		this.lineEnd = lineEnd;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
