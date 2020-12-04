package oracle;

import gudusoft.gsqlparser.EAlterTriggerOption;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterTriggerStmt;
import junit.framework.TestCase;

public class testAlterTrigger extends TestCase {

        public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "ALTER TRIGGER \"ADMORA\".\"GA_ACT_EST_AUT\" ENABLE";
        assertTrue(sqlparser.parse() == 0);

            TAlterTriggerStmt alterTriggerStmt = (TAlterTriggerStmt)sqlparser.sqlstatements.get(0);
            assertTrue(alterTriggerStmt.getTriggerName().toString().equalsIgnoreCase("\"ADMORA\".\"GA_ACT_EST_AUT\""));
            assertTrue(alterTriggerStmt.getAlterTriggerOption() == EAlterTriggerOption.enable
            );
        }
}
