package formatsql;
/*
 * Date: 11-3-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testCTE extends TestCase {

    public static void testCTE_NewlineBeforeAs(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "WITH mycte(x)     AS (SELECT x = Convert( VARCHAR(1000), 'hello' )         UNION ALL \n" +
                 "         SELECT Convert( VARCHAR(1000), x + 'a' )          FROM   mycte \n" +
                 "         WHERE  Len( x ) < 10         UNION ALL          SELECT Convert( VARCHAR(1000), x + 'b' ) \n" +
                 "         FROM   mycte          WHERE  Len( x ) < 10)\n" +
                 "SELECT x FROM   mycte ORDER  BY Len( x ),          x;  ";

         sqlparser.parse();
         option.cteNewlineBeforeAs = false;
         String result = FormatterFactory.pp(sqlparser, option);
//        assertTrue(result.trim().equalsIgnoreCase("WITH mycte(x) AS (SELECT x = Convert(VARCHAR(1000), 'hello')\n" +
//                "                  UNION ALL\n" +
//                "                  SELECT Convert(VARCHAR(1000), x + 'a')\n" +
//                "                  FROM   mycte\n" +
//                "                  WHERE  Len(x) < 10\n" +
//                "                  UNION ALL\n" +
//                "                  SELECT Convert(VARCHAR(1000), x + 'b')\n" +
//                "                  FROM   mycte\n" +
//                "                  WHERE  Len(x) < 10) \n" +
//                "  SELECT   x\n" +
//                "  FROM     mycte\n" +
//                "  ORDER BY Len(x),\n" +
//                "           x;"));

        assertTrue(result.trim().equalsIgnoreCase("WITH mycte(x) AS (SELECT x = Convert(VARCHAR(1000), 'hello')\n" +
                "                  UNION ALL\n" +
                "                  SELECT  Convert(VARCHAR(1000), x  +  'a')\n" +
                "                  FROM   mycte\n" +
                "                  WHERE  Len(x) < 10\n" +
                "                  UNION ALL\n" +
                "                  SELECT  Convert(VARCHAR(1000), x  +  'b')\n" +
                "                  FROM   mycte\n" +
                "                  WHERE  Len(x) < 10) \n" +
                "  SELECT   x\n" +
                "  FROM     mycte\n" +
                "  ORDER BY Len(x),\n" +
                "           x;"));
 //       System.out.println(result.trim());
     }
}
