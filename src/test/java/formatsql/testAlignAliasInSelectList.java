package formatsql;
/*
 * Date: 12-1-29
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testAlignAliasInSelectList extends TestCase {
    public static void test1()
    {
        GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT\n" +
                "col1 AS mycolumn\n" +
                ", col2 AS yourcolumn\n" +
                ", Sum(col3) AS thesum\n" +
                ", CASE\n" +
                "WHEN Lower(a) = 23 THEN 'blue'\n" +
                "ELSE NULL\n" +
                "END AS mycase\n" +
                ", Trim(TRAILING FROM col1) AS trim_col\n" +
                "FROM\n" +
                "table1\n" +
                "INNER JOIN table2\n" +
                "ON col1=col2 AND col3=col4\n" +
                "WHERE col4 > col5\n" +
                "AND col6 = 1000";

        sqlparser.parse();


        option.selectColumnlistComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        option.fromClauseInNewLine = true;
        option.selectItemInNewLine = true;
        option.andOrUnderWhere = true;
        option.fromClauseInNewLine = true;
        option.caseWhenThenInSameLine = true;

        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SELECT\n" +
                "  col1                       AS mycolumn\n" +
                "  , col2                     AS yourcolumn\n" +
                "  , Sum(col3)                AS thesum\n" +
                "  , CASE\n" +
                "      WHEN Lower(a) = 23 THEN 'blue'\n" +
                "      ELSE NULL\n" +
                "    END                      AS mycase\n" +
                "  , Trim(TRAILING FROM col1) AS trim_col\n" +
                "FROM  \n" +
                "  table1\n" +
                "  INNER JOIN table2\n" +
                "  ON col1 = col2\n"+
                "     AND col3 = col4\n" +
                "WHERE  col4 > col5\n" +
                "   AND col6 = 1000"));

    }

}
