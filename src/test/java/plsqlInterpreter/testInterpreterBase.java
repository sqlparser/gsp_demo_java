package plsqlInterpreter;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.compiler.TASTEvaluator;
import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;

public class testInterpreterBase extends TestCase {
    boolean doEvaluate(EDbVendor dbVendor, String inputSQL, String expectedValue){
       // EDbVendor dbVendor = EDbVendor.dbvpostgresql;
        TSQLEnv sqlEnv = new TSQLEnv(dbVendor) {
            @Override
            public void initSQLEnv() {
            }
        };

        TGlobalScope globalScope = new TGlobalScope(sqlEnv);
        TGSqlParser sqlParser = new TGSqlParser(dbVendor);
        sqlParser.sqltext = inputSQL;

        boolean enableInterpreter = TBaseType.ENABLE_INTERPRETER;
        TBaseType.ENABLE_INTERPRETER = true;
        int ret = sqlParser.parse();
        if (ret != 0){
            System.out.println(sqlParser.getErrormessage());
            return false;
        }

        TLog.clearLogs();
        TLog.enableInterpreterLogOnly();
        TLog.setOutputSimpleMode(true);

        TASTEvaluator astEvaluator = new TASTEvaluator(sqlParser.sqlstatements,globalScope);
        String retValue = astEvaluator.eval();
        TBaseType.ENABLE_INTERPRETER = enableInterpreter;

        return TBaseType.compareStringsLineByLine(expectedValue,TBaseType.dumpLogsToString());

    }
    public void test1() {
        assertTrue(1==1);
    }
}
