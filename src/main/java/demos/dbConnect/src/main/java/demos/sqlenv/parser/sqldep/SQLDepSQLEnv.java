package demos.sqlenv.parser.sqldep;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class SQLDepSQLEnv extends TSQLEnv {

	private Map jsonContent;

	private Boolean init = false;

	public SQLDepSQLEnv(EDbVendor dbVendor, Map jsonContent) {
		super(dbVendor);
		this.jsonContent = jsonContent;
		initSQLEnv();
	}

	@Override
	public void initSQLEnv() {
		synchronized (init) {
			if (jsonContent == null || init)
				return;
			if(jsonContent.containsKey("databaseModel")){
				jsonContent = (Map)jsonContent.get("databaseModel");
			}
			List databases = (List) jsonContent.get("databases");
			if (databases != null) {
				for (int i = 0; i < databases.size(); i++) {
					Map jsonDatabase = (Map) databases.get(i);
					String databaseName = (String) jsonDatabase.get("name");
					List tables = (List) jsonDatabase.get("tables");
					for (int j = 0; j < tables.size(); j++) {
						Map jsonTable = (Map) tables.get(j);
						String schemeName = (String) jsonTable.get("schema");
						String tableName = (String) jsonTable.get("name");
						TSQLSchema sqlSchema = getSQLSchema(databaseName + "." + schemeName, true);
						TSQLTable sqlTable = sqlSchema.createTable(tableName);
						if (jsonTable.containsKey("isView")) {
							sqlTable.setView(Boolean.parseBoolean((String) jsonTable.get("isView")));
						}
						List columns = (List) jsonTable.get("columns");
						for (int k = 0; k < columns.size(); k++) {
							Map jsonColumn = (Map) columns.get(k);
							sqlTable.addColumn((String) jsonColumn.get("name"));
						}
					}
				}

				jsonContent = null;
				init = true;
			}
		}
	}

}
