package informix;
/*
 * Date: 13-1-24
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.informix.TInformixDropRowTypeStmt;
import junit.framework.TestCase;

public class testDropObject extends TestCase {


    public void testRowType(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "DROP ROW TYPE employee_t RESTRICT";
        assertTrue(sqlparser.parse() == 0);

        TInformixDropRowTypeStmt stmt = (TInformixDropRowTypeStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getRowTypeName().toString().equalsIgnoreCase("employee_t"));

    }

    public void testSequence(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "DROP SEQUENCE Invoice_Numbers;";
        assertTrue(sqlparser.parse() == 0);

        TDropSequenceStmt stmt = (TDropSequenceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getSequenceName().toString().equalsIgnoreCase("Invoice_Numbers"));

    }

    public void testSynonym(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "DROP SYNONYM cathyg.nj_cust;";
        assertTrue(sqlparser.parse() == 0);

        TDropSynonymStmt stmt = (TDropSynonymStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getSynonymName().toString().equalsIgnoreCase("cathyg.nj_cust"));

    }

    public void testIndex(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "DROP INDEX stores_demo:joed.o_num_ix;";
        assertTrue(sqlparser.parse() == 0);

        TDropIndexSqlStatement stmt = (TDropIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getIndexName().toString().equalsIgnoreCase("stores_demo:joed.o_num_ix"));

    }

    public void testTable(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "DROP TABLE stores_demo@accntg:joed.state;";
        assertTrue(sqlparser.parse() == 0);

        TDropTableSqlStatement stmt = (TDropTableSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(stmt.getTableName().toString().equalsIgnoreCase("stores_demo@accntg:joed.state"));

    }

    public void testView(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "DROP VIEW cust1";
        assertTrue(sqlparser.parse() == 0);

        TDropViewSqlStatement stmt = (TDropViewSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(stmt.getViewName().toString().equalsIgnoreCase("cust1"));

    }

}
