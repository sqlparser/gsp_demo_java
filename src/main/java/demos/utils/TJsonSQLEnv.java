package demos.utils;

import java.util.Iterator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;

public class TJsonSQLEnv extends TSQLEnv implements Iterable<Query> {

	private JSONObject jsonContent;

	private Boolean init = false;

	public TJsonSQLEnv(EDbVendor dbVendor, String jsonFilePath) {
		super(dbVendor);
		this.jsonContent = JSON.parseObject(SQLUtil.getFileContent(jsonFilePath));
		initSQLEnv();
	}

	public TJsonSQLEnv(EDbVendor dbVendor, JSONObject jsonContent) {
		super(dbVendor);
		this.jsonContent = jsonContent;
		initSQLEnv();
	}

	@Override
	public void initSQLEnv() {
		synchronized (init) {
			if (init)
				return;

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
				init = true;
			}
		}
	}

	@Override
	public Iterator<Query> iterator() {
		return jsonContent.getJSONArray("queries").toJavaList(Query.class).iterator();
	}
}

class Query {
	private String name;
	private String database;
	private String schema;
	private String groupName;
	private String sourceCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
}
