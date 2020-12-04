package oracle;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropSynonymStmt;
import junit.framework.TestCase;

public class testSynonym  extends TestCase {

    public void testDrop(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DROP PUBLIC SYNONYM emp";
        assertTrue(sqlparser.parse() == 0);

        TDropSynonymStmt synonymStmt = (TDropSynonymStmt)sqlparser.sqlstatements.get(0);
        assertTrue(synonymStmt.getSynonymName().toString().equalsIgnoreCase("emp"));

    }

}
