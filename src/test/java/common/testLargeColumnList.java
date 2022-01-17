package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testLargeColumnList extends TestCase {

    public static void test1(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlParser.sqlfilename = gspCommon.BASE_SQL_DIR_PRIVATE_JAVA+"redshift/syngenta/large-column-list.sql";
        sqlParser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement)sqlParser.sqlstatements.get(0);
        StringBuilder b = new StringBuilder();

        for(TResultColumn rc : select.getResultColumnList()){
            b.append(rc.getDisplayName()+"\t->\t"+rc.getExpr().getObjectOperand().getSourceColumn().getDisplayName()+TBaseType.windowsLinebreak);
        }

        assertTrue(TBaseType.compareStringBuilderToFile(b,gspCommon.BASE_SQL_DIR_PRIVATE_JAVA+"redshift/syngenta/large-column-list.txt"));

    }
}
