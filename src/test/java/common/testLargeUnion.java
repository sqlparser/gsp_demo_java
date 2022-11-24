package common;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testLargeUnion extends TestCase {

    public static void test1(){
        //  递归使用的是栈里面的内存，JVM默认一个线程的栈内存是1MB，单独跑这个case，
        //  线程栈里只有这个case的内容，一起跑，栈里可能还有其它的内容，挤压了这个case可用栈内存的空间。
        //
        //增加JVM参数 -Xss2M 就可以解决这个问题
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
