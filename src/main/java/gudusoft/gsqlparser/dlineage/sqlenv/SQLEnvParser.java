package gudusoft.gsqlparser.dlineage.sqlenv;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.dlineage.json.JSON;
import gudusoft.gsqlparser.dlineage.sqlenv.grabit.GrabitSQLEnv;
import gudusoft.gsqlparser.dlineage.sqlenv.grabit.MultipleGrabitSQLEnv;
import gudusoft.gsqlparser.dlineage.sqlenv.sqldep.MultipleSQLDepSQLEnv;
import gudusoft.gsqlparser.dlineage.sqlenv.sqldep.SQLDepSQLEnv;
import gudusoft.gsqlparser.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;

@SuppressWarnings("rawtypes")
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
			List json = (List) JSON.parseObject(trimSQL);
			if (trimSQL.indexOf("createdBy") != -1) {
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
			Map json = (Map) JSON.parseObject(trimSQL);
			if (json.containsKey("createdBy")) {
				String createdBy = (String) json.get("createdBy");
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
