package common;
/*
 * Date: 2010-8-25
 * Time: 16:26:32
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

public class testFakeConstraint extends TestCase {
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "create table t(f1 number collate Latin1_General_CI_AS null CONSTRAINT [DF_DoRig_TipoPC]  DEFAULT (' ')," +
                "f2 [char](5) identity(2,10) rowguidcol)";
        assertTrue(parser.parse() == 0);

        TCreateTableSqlStatement ct = (TCreateTableSqlStatement)parser.sqlstatements.get(0);
        TColumnDefinition cd = ct.getColumnList().getColumn(0);
        assertTrue(cd.isNull());
        assertTrue(cd.getCollationName().equalsIgnoreCase("Latin1_General_CI_AS"));
        assertTrue(cd.getConstraints().size() == 1);

        TColumnDefinition cd1 = ct.getColumnList().getColumn(1);
        assertTrue(cd1.isRowGuidCol());
        assertTrue(cd1.isIdentity());
        assertTrue(cd1.getSeed().toString().equalsIgnoreCase("2"));
        assertTrue(cd1.getIncrement().toString().equalsIgnoreCase("10"));
        assertTrue(cd1.getConstraints().size() == 0);

    }

}
