package gudusoft.gsqlparser.oracleTest;
/*
 * Date: 11-6-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TSequenceOption;
import gudusoft.gsqlparser.stmt.TCreateSequenceStmt;
import junit.framework.TestCase;

public class testSequence extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE SEQUENCE SOME_SEQ increment by 1 start with 1 MAXVALUE 1.0E28 MINVALUE 1 NOCYCLE NOCACHE NOORDER;";
        assertTrue(sqlparser.parse() == 0);

        TCreateSequenceStmt sequenceStmt = (TCreateSequenceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(sequenceStmt.getSequenceName().toString().equalsIgnoreCase("SOME_SEQ"));
        for(int i=0;i<sequenceStmt.getOptions().size();i++){
            TSequenceOption sequenceOption = sequenceStmt.getOptions().get(i);
           // System.out.println(dummy.toString());
        }
        //System.out.println(sequenceStmt.getSequenceName().toString());
    }

}
