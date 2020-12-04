package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFetchFirstClause;
import gudusoft.gsqlparser.nodes.TOffsetClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

 public class testOffsetClause extends TestCase {
        public void test1(){
            TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
            sqlparser.sqltext = "SELECT name FROM Temp_Test\n" +
                    "ORDER BY name\n" +
                    "OFFSET 2 ROWS FETCH NEXT 4 ROWS ONLY;";
            assertTrue(sqlparser.parse() == 0);

            TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
            TOffsetClause offsetClause = select.getOffsetClause();
            assertTrue(offsetClause.getSelectOffsetValue().toString().equalsIgnoreCase("2"));
            TFetchFirstClause fetchFirstClause = select.getFetchFirstClause();
            assertTrue(fetchFirstClause.getFetchValue().toString().equalsIgnoreCase("4"));
        }
    }
