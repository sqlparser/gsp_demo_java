package demos.sqlenv.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;

public interface TSQLEnvParser {
    TSQLEnv parseSQLEnv(EDbVendor vendor, String metadata);
}
