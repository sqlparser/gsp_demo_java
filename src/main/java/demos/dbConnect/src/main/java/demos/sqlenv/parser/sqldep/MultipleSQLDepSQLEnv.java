package demos.sqlenv.parser.sqldep;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import gudusoft.gsqlparser.util.json.JSON;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public class MultipleSQLDepSQLEnv extends TSQLEnv {

	private List jsonArray;

	private Boolean init = false;

	public MultipleSQLDepSQLEnv(EDbVendor dbVendor, List jsonArray) {
		super(dbVendor);
		this.jsonArray = jsonArray;
		initSQLEnv();
	}

	@Override
	public void initSQLEnv() {
		synchronized (init) {
			if (jsonArray == null || init)
				return;

			for (int x = 0; x < jsonArray.size(); x++) {
				String sql = ((Map) jsonArray.get(x)).get("sql").toString().trim();
				if (!sql.startsWith("{") && !sql.endsWith("}")) {
					continue;
				}

				try {
					Map jsonContent = (Map) JSON.parseObject(sql);
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
					}
				} catch (Exception e) {
					Logger.getLogger(gudusoft.gsqlparser.sqlenv.parser.sqldep.MultipleSQLDepSQLEnv.class.getName()).log(Level.WARNING, "Parse json failed.", e);
				}
			}

			jsonArray = null;
			init = true;
		}
	}

}
