package demos.dlineage.dataflow.model.json;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = { "dbvendor", "dbobjs", "relations" })
public class DataFlow {
	@JSONField(ordinal = 1)
	private String dbvendor;
	@JSONField(ordinal = 2)
	private DBObject[] dbobjs;
	@JSONField(ordinal = 3)
	private Relation[] relations;

	public String getDbvendor() {
		return dbvendor;
	}

	public void setDbvendor(String dbvendor) {
		this.dbvendor = dbvendor;
	}

	public DBObject[] getDbobjs() {
		return dbobjs;
	}

	public void setDbobjs(DBObject[] dbobjs) {
		this.dbobjs = dbobjs;
	}

	public Relation[] getRelations() {
		return relations;
	}

	public void setRelations(Relation[] relations) {
		this.relations = relations;
	}

}
