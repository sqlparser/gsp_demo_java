package common;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testLargeUnion extends TestCase {

    public static void test1(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlParser.sqlfilename = gspCommon.BASE_SQL_DIR_PRIVATE_JAVA+"snowflake/atlan/large_union.sql";
        sqlParser.parse();
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)sqlParser.sqlstatements.get(0);
        TSelectSqlStatement select = createTable.getSubQuery();
        TCTE cte = select.getCteList().getCTE(0);
        assertTrue(cte.getTableName().toString().equalsIgnoreCase("catalog_columns"));
        select = cte.getSubquery();
        ArrayList<TSelectSqlStatement> selectList = select.getFlattenedSelects();
        assertTrue(selectList.size() == 1937);
        TSelectSqlStatement first = selectList.get(0);
        TSelectSqlStatement last = selectList.get(selectList.size()-1);
        assertTrue(first.getStartToken().lineNo == 6);
        assertTrue(first.getEndToken().lineNo == 15);
        assertTrue(last.getStartToken().lineNo == 23238);
        assertTrue(last.getEndToken().lineNo == 23247);

    }
}
