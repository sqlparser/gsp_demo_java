package common;
/*
 * Date: 13-8-14
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import junit.framework.TestCase;

public class testLongStatement extends TestCase {

    public void test1(){

            TGSqlParser parser = new TGSqlParser(EDbVendor.dbvmysql);
                StringBuffer stringBuffer = new StringBuffer("REPLACE INTO sales.testTable (var1) VALUES ('");

                for (int j = 0; j < 10000; j++) {
                        if (j % 1000 == 0) {
                               // System.out.println("Appended VALUES " + j + " times");
                        }
                        stringBuffer.append("- This is only part" + j + " of a really long value -");
                }
                stringBuffer.append("')");

                parser.sqltext = stringBuffer.toString();

                int response = parser.parse();
                assertTrue(response == 0);
        }



}