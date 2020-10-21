package demos.dlineage.sqlenv;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import demos.dlineage.sqlenv.grabit.GrabitSQLEnv;
import demos.dlineage.sqlenv.grabit.MultipleGrabitSQLEnv;
import demos.dlineage.sqlenv.sqldep.MultipleSQLDepSQLEnv;
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
		
		if (trimSQL.startsWith("[") && trimSQL.endsWith("]")) {
			return getMultipleJSONSQLEnv(vendor, trimSQL);
		}

		return null;
	}

	private static TSQLEnv getMultipleJSONSQLEnv(EDbVendor vendor, String trimSQL) {
		try {
			JSONArray json = JSON.parseArray(trimSQL);
			if (trimSQL.indexOf("createdBy")!=-1) {
				if (trimSQL.toLowerCase().indexOf("sqldep") != -1) {
					return new MultipleSQLDepSQLEnv(vendor, json);
				}
				if (trimSQL.toLowerCase().indexOf("grabit") != -1) {
					return new MultipleGrabitSQLEnv(vendor, json);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(SQLEnvParser.class.getName()).log(Level.WARNING, "Parse json failed.", e);
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
				if (createdBy.toLowerCase().indexOf("grabit") != -1) {
					return new GrabitSQLEnv(vendor, json);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(SQLEnvParser.class.getName()).log(Level.WARNING, "Parse json failed.", e);
		}
		return null;
	}
}
