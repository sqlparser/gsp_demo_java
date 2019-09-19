package test.teradata;
/*
 * Date: 2010-9-10
 * Time: 15:05:48
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.*;

public class testTeradataTokenize extends TestCase {

    public void test1(){
    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
    sqlparser.sqltext = "SELECT col\n" +
            "FROM '0E42E342C142C242F10F4B414E'XN;" ;
    sqlparser.tokenizeSqltext();
    TSourceToken  st = sqlparser.sourcetokenlist.get(0);
        assertTrue(st.toString().equalsIgnoreCase("select"));
        st = sqlparser.sourcetokenlist.get(2);
        assertTrue(st.toString().equalsIgnoreCase("col"));
        st = sqlparser.sourcetokenlist.get(4);
        assertTrue(st.toString().equalsIgnoreCase("from"));
        st = sqlparser.sourcetokenlist.get(6);
        assertTrue(st.toString().equalsIgnoreCase("'0E42E342C142C242F10F4B414E'XN"));
        st = sqlparser.sourcetokenlist.get(7);
        assertTrue(st.toString().equalsIgnoreCase(";"));
    }

    public void test2(){
    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
    sqlparser.sqltext = "CREATE PROCEDURE spSample (OUT po1 VARCHAR(50),\n" +
            "OUT po2 VARCHAR(50))\n" +
            "BEGIN\n" +
            "DECLARE i INTEGER DEFAULT 0;\n" +
            "L1: BEGIN\n" +
            "DECLARE var1 VARCHAR(25) DEFAULT 'ABCD';\n" +
            "DECLARE CONTINUE HANDLER FOR SQLSTATE '42000'\n" +
            "SET po1 = 'Table does not exist in L1';\n" +
            "INSERT INTO tDummy (10, var1);\n" +
            "-- Table Does not exist\n" +
            "END L1;\n" +
            "L2: BEGIN\n" +
            "DECLARE var1 VARCHAR(25) DEFAULT 'XYZ';\n" +
            "DECLARE CONTINUE HANDLER FOR SQLSTATE '42000'\n" +
            "SET po2 = 'Table does not exist in L2';\n" +
            "INSERT INTO tDummy (i, var1);\n" +
            "-- Table Does not exist\n" +
            "END L2;\n" +
            "END;";
    sqlparser.tokenizeSqltext();
     //   for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
     //       System.out.println(i+":"+sqlparser.sourcetokenlist.get(i).toString());
     //   }
    }

}
