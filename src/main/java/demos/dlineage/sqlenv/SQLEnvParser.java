package demos.dlineage.sqlenv;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import demos.dlineage.sqlenv.sqldep.SQLDepSQLEnv;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;

public class SQLEnvParser {

	public static TSQLEnv getSQLEnv(EDbVendor vendor, String sql) {
		if (SQLUtil.isEmpty(sql))
			return null;

		String trimSQL = sql.trim();
		if (trimSQL.startsWith("{") && trimSQL.endsWith("}")) {
			return getJSONSQLEnv(vendor, trimSQL);
		}

		return null;
	}

	private static TSQLEnv getJSONSQLEnv(EDbVendor vendor, String trimSQL) {
		try {
			JSONObject json = JSON.parseObject(trimSQL);
			if (json.containsKey("createdBy")) {
				String createdBy = json.getString("createdBy");
				if (createdBy.toLowerCase().indexOf("sqldep") != -1) {
					return new SQLDepSQLEnv(vendor, json);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(SQLEnvParser.class.getName()).log(Level.WARNING, "Parse json failed.", e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		TSQLEnv sqlEnv = SQLEnvParser.getSQLEnv(EDbVendor.dbvmssql, SQLUtil.getFileContent("C:\\Users\\z\\Desktop\\DBexport20191119.txt"));
		sqlEnv.initSQLEnv();
		System.out.println(JSON.toJSONString(sqlEnv));
	}
}
