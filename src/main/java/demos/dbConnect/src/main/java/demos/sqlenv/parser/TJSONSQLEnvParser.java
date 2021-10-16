package demos.sqlenv.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.parser.TSQLEnvParser;
import gudusoft.gsqlparser.sqlenv.parser.grabit.GrabitSQLEnv;
import gudusoft.gsqlparser.sqlenv.parser.grabit.MultipleGrabitSQLEnv;
import gudusoft.gsqlparser.sqlenv.parser.sqldep.MultipleSQLDepSQLEnv;
import gudusoft.gsqlparser.sqlenv.parser.sqldep.SQLDepSQLEnv;
import gudusoft.gsqlparser.util.SQLUtil;
import gudusoft.gsqlparser.util.json.JSON;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public class TJSONSQLEnvParser implements TSQLEnvParser {

    public TSQLEnv parseSQLEnv(EDbVendor vendor, String sql) {
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

    private TSQLEnv getMultipleJSONSQLEnv(EDbVendor vendor, String trimSQL) {
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
            Logger.getLogger(gudusoft.gsqlparser.sqlenv.parser.TJSONSQLEnvParser.class.getName()).log(Level.WARNING, "Parse json failed.", e);
        }
        return null;
    }

    private TSQLEnv getJSONSQLEnv(EDbVendor vendor, String trimSQL) {
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
            Logger.getLogger(gudusoft.gsqlparser.sqlenv.parser.TJSONSQLEnvParser.class.getName()).log(Level.WARNING, "Parse json failed.", e);
        }
        return null;
    }
}
