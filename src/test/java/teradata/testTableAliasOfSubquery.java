package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testTableAliasOfSubquery extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "USING (empno INTEGER,\n" +
                "salary INTEGER)\n" +
                "MERGE INTO employee AS t\n" +
                "USING (SELECT :empno, :salary, name\n" +
                "FROM names\n" +
                "WHERE empno=:empno) AS s(empno, salary, name)\n" +
                "ON t.empno=s.empno\n" +
                "WHEN MATCHED THEN UPDATE\n" +
                "SET salary=s.salary, name = s.name\n" +
                "WHEN NOT MATCHED THEN INSERT (empno, name, salary)\n" +
                "VALUES (s.empno, s.name, s.salary);";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).getSyntaxHints().size() == 0);
    }
}
