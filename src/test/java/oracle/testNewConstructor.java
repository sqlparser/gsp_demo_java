package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TReturnStmt;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import junit.framework.TestCase;


public class testNewConstructor extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "  function LogPerformance(\n" +
                "    pCodStatoElaborazione in varchar2,\n" +
                "    pDataInizio in timestamp,\n" +
                "    pDataFine in timestamp default null) return GOL_AGGREGATORI.AGGO_LOG_Performance\n" +
                "  is\n" +
                "  begin\n" +
                "    return new\n" +
                "      GOL_AGGREGATORI.AGGO_LOG_Performance(\n" +
                "        pCodStatoElaborazione,\n" +
                "        pDataInizio,\n" +
                "        pDataFine);\n" +
                "  end LogPerformance;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateFunction function = (TPlsqlCreateFunction)sqlparser.sqlstatements.get(0);
        TReturnStmt returnStmt = (TReturnStmt)function.getBodyStatements().get(0);
        assertTrue(returnStmt.getExpression().getExpressionType() == EExpressionType.type_constructor_t);

    }
}
