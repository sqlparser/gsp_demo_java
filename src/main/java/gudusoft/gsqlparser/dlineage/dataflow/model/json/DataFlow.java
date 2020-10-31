package gudusoft.gsqlparser.dlineage.dataflow.model.json;

public class DataFlow {
	private String dbvendor;
	private DBObject[] dbobjs;
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
