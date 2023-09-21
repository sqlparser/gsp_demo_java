package gettablecolumn;

import demos.traceColumn.TTraceColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import junit.framework.TestCase;


public class testTraceColumn extends TestCase {

    public static void test1(){
        String sqltext = "SELECT a.deptno \"Department\", \n" +
                "       a.num_emp/b.total_count \"Employees\", \n" +
                "       a.sal_sum/b.total_sal \"Salary\"\n" +
                "  FROM\n" +
                "(SELECT deptno, COUNT(*) num_emp, SUM(SAL) sal_sum\n" +
                "    FROM scott.emp\n" +
                "    GROUP BY deptno) a,\n" +
                "(SELECT COUNT(*) total_count, SUM(sal) total_sal\n" +
                "    FROM scott.emp) b";

        //System.out.println(sqltext);
        TTraceColumn traceColumn = new TTraceColumn(EDbVendor.dbvoracle);
        traceColumn.runText(sqltext);
        //System.out.print(traceColumn.getInfos().toString().trim());
        String actualStr = traceColumn.getInfos().toString().trim();
        String requireStr =  "\"Department\"\n" +
                " -->a.deptno(expr)\n" +
                "  -->a.deptno\n" +
                "   -->deptno(expr)\n" +
                "    -->scott.emp.deptno\n" +
                "\"Employees\"\n" +
                "  -->a.num_emp/b.total_count(expr)\n" +
                "   -->a.num_emp\n" +
                "    -->COUNT(*)(expr)\n" +
                "     -->scott.emp.*\n" +
                "   -->b.total_count\n" +
                "    -->COUNT(*)(expr)\n" +
                "     -->scott.emp.*\n" +
                "\"Salary\"\n" +
                "   -->a.sal_sum/b.total_sal(expr)\n" +
                "    -->a.sal_sum\n" +
                "     -->SUM(SAL)(expr)\n" +
                "      -->scott.emp.SAL\n" +
                "    -->b.total_sal\n" +
                "     -->SUM(sal)(expr)\n" +
                "      -->scott.emp.sal";
       // System.out.println ("required:\n"+requireStr+"\n\nActual:\n"+actualStr);
        assertTrue(actualStr.equalsIgnoreCase(requireStr));
    }

    public static void test2(){
        String sqltext = "create or replace view test\n" +
                "(col1,col2)\n" +
                "as \n" +
                "select a, (select b from table2 where table2.c=table1.a) from table1\n" +
                "union \n" +
                "select d, e-g from table2";


        // (select b from table2 where table2.c=table1.a)  该查询中的 b 可用属于 table2， 也可以属于上层的 table1，有两个候选table
        // 在这里选择使用 TBaseType.GUESS_COLUMN_STRATEGY_NEAREST
        int b = TBaseType.GUESS_COLUMN_STRATEGY;
        TBaseType.GUESS_COLUMN_STRATEGY = TBaseType.GUESS_COLUMN_STRATEGY_NEAREST;

        TTraceColumn traceColumn = new TTraceColumn(EDbVendor.dbvoracle);
        traceColumn.runText(sqltext);

        assertTrue(traceColumn.getInfos().toString().trim().equalsIgnoreCase("col1\n" +
                "col2\n" +
                " -->a(expr)\n" +
                "  -->table1.a\n" +
                " -->(select b from table2 where table2.c=table1.a)(expr)\n" +
                "  -->b(expr)\n" +
                "   -->table2.b\n" +
                " -->d(expr)\n" +
                "  -->table2.d\n" +
                " -->e-g(expr)\n" +
                "  -->table2.e\n" +
                "  -->table2.g"));

        TBaseType.GUESS_COLUMN_STRATEGY = b;
    }

}
