package test;
/*
 * Date: 2010-12-31
 * Time: 14:22:44
 */

import gudusoft.gsqlparser.ESequenceOptionType;
import gudusoft.gsqlparser.nodes.TPTNodeList;
import gudusoft.gsqlparser.nodes.TSequenceOption;
import gudusoft.gsqlparser.stmt.TCreateSequenceStmt;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;

public class testCreateSequenceStmt extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE SEQUENCE  \"ABCUSA_CUST\".\"DAYPART_CODE_SEQ\"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 150 CACHE 20 NOORDER  NOCYCLE ;";
        assertTrue(sqlparser.parse() == 0);

        TCreateSequenceStmt sequence = (TCreateSequenceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(sequence.getSequenceName().toString().equalsIgnoreCase("\"ABCUSA_CUST\".\"DAYPART_CODE_SEQ\""));

        TPTNodeList<TSequenceOption> options = sequence.getOptions();
        assertTrue(options.size() == 7);
        assertTrue(options.getElement(0).toString().equalsIgnoreCase("MINVALUE 1"));
        assertTrue(options.getElement(0).getSequenceOptionType() == ESequenceOptionType.minValue);
        assertTrue(options.getElement(1).toString().equalsIgnoreCase("MAXVALUE 999999999999999999999999999"));
        assertTrue(options.getElement(2).toString().equalsIgnoreCase("INCREMENT BY 1"));
        assertTrue(options.getElement(3).toString().equalsIgnoreCase("START WITH 150"));
        assertTrue(options.getElement(4).toString().equalsIgnoreCase("CACHE 20"));
        assertTrue(options.getElement(5).toString().equalsIgnoreCase("NOORDER"));
        assertTrue(options.getElement(6).toString().equalsIgnoreCase("NOCYCLE"));
    }

}
