package demos.dlineage.sqlenv.sqldep;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;

public class MultipleSQLDepSQLEnv extends TSQLEnv {

	private JSONArray jsonArray;

	private Boolean init = false;

	public MultipleSQLDepSQLEnv(EDbVendor dbVendor, JSONArray jsonArray) {
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
				String sql = jsonArray.getJSONObject(x).getString("sql").trim();
				if (!sql.startsWith("{") && !sql.endsWith("}")) {
					continue;
				}

				try {
					JSONObject jsonContent = JSON.parseObject(sql);
					JSONObject databaseModel = jsonContent.getJSONObject("databaseModel");
					if (databaseModel != null) {
						JSONArray databases = databaseModel.getJSONArray("databases");
						if (databases != null) {
							for (int i = 0; i < databases.size(); i++) {
								JSONObject jsonDatabase = databases.getJSONObject(i);
								String databaseName = jsonDatabase.getString("name");
								JSONArray tables = jsonDatabase.getJSONArray("tables");
								for (int j = 0; j < tables.size(); j++) {
									JSONObject jsonTable = tables.getJSONObject(j);
									String schemeName = jsonTable.getString("schema");
									String tableName = jsonTable.getString("name");
									TSQLSchema sqlSchema = getSQLSchema(databaseName + "." + schemeName, true);
									TSQLTable sqlTable = sqlSchema.createTable(tableName);
									if (jsonTable.containsKey("isView")) {
										sqlTable.setView(Boolean.parseBoolean(jsonTable.getString("isView")));
									}
									JSONArray columns = jsonTable.getJSONArray("columns");
									for (int k = 0; k < columns.size(); k++) {
										JSONObject jsonColumn = columns.getJSONObject(k);
										sqlTable.addColumn(jsonColumn.getString("name"));
									}
								}
							}
						}
					}
				} catch (Exception e) {
					Logger.getLogger(MultipleSQLDepSQLEnv.class.getName()).log(Level.WARNING, "Parse json failed.", e);
				}
			}
			
			jsonArray = null;
			init = true;
		}
	}

}
