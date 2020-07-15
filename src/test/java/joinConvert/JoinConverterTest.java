package gudusoft.gsqlparser.test.joinConvert;

import demos.joinConvert.JoinConverter;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;


public class JoinConverterTest extends TestCase {

    public void testOracleSql1() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "INSERT INTO T SELECT e.employee_id,\n" +
                "       e.last_name,\n" +
                "       e.department_id\n" +
                "FROM   employees e,\n" +
                "       departments d\n" +
                "WHERE  e.department_id(+) = d.department_id\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("INSERT INTO T SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "right outer join departments d on e.department_id = d.department_id"));
    }


    public void testOracleSql2() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "SELECT COUNT(1)\n" +
                "  FROM (SELECT PRODID\n" +
                "          FROM (SELECT T.ID PRODID, CD.ID CID\n" +
                "                  FROM PRODUCT          T,                      \n" +
                "                  CD            CD\n" +
                "                 WHERE T.ID=CD.ID)\n" +
                "         GROUP BY PRODID\n" +
                "        HAVING COUNT(*) > 1);\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT COUNT(1)\n" +
                        "  FROM (SELECT PRODID\n" +
                        "          FROM (SELECT T.ID PRODID, CD.ID CID\n" +
                        "                  FROM PRODUCT T\n" +
                        "inner join CD CD on T.ID=CD.ID\n" +
                        "                  )\n" +
                        "         GROUP BY PRODID\n" +
                        "        HAVING COUNT(*) > 1);"));
    }


    public void testOracleSql3() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "SELECT\n" +
                " *\n" +
                "FROM\n" +
                " ai_course a\n" +
                "WHERE\n" +
                " a.algorithmt(+) = (\n" +
                "  SELECT\n" +
                "   b.coverUrl\n" +
                "  FROM\n" +
                "   ai_course_dataset_relation b, ai_course a\n" +
                "  WHERE\n" +
                "   b.course_id = a.id\n" +
                "   and a.id=?\n" +
                " )";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT\n" +
                        " *\n" +
                        "FROM\n" +
                        " ai_course a\n" +
                        "WHERE\n" +
                        " a.algorithmt(+) = (\n" +
                        "  SELECT\n" +
                        "   b.coverUrl\n" +
                        "  FROM\n" +
                        "   ai_course_dataset_relation b\n" +
                        "inner join ai_course a on b.course_id = a.id\n" +
                        "  WHERE\n" +
                        "   a.id=?\n" +
                        " )"));
    }


    public void testOracleSql4() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "SELECT e.employee_id,\n" +
                "       e.last_name,\n" +
                "       e.department_id\n" +
                "FROM   employees e,\n" +
                "       departments d\n" +
                "WHERE  e.department_id = d.department_id \n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "inner join departments d on e.department_id = d.department_id"));
    }

    public void testOracleSql5() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "CREATE\n" +
                "OR REPLACE PROCEDURE myDemo01 IS aa number (10);\n" +
                "BEGIN\n" +
                "\tSELECT e.employee_id INTO aa FROM employees e, departments d WHERE e.department_id = d.department_id;\n" +
                "\tdbms_output.put_line (aa);\n" +
                "END myDemo01;";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("CREATE\n" +
                        "OR REPLACE PROCEDURE myDemo01 IS aa number (10);\n" +
                        "BEGIN\n" +
                        "\tSELECT e.employee_id INTO aa FROM employees e\n" +
                        "inner join departments d on e.department_id = d.department_id  ;\n" +
                        "\tdbms_output.put_line (aa);\n" +
                        "END myDemo01;"));
    }

    public void testOracleSql6() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "SELECT\n" +
                " *\n" +
                "FROM\n" +
                " ai_course a\n" +
                "WHERE\n" +
                " a.algorithmt(+) = (\n" +
                "  SELECT\n" +
                "   b.coverUrl\n" +
                "  FROM\n" +
                "   ai_course_dataset_relation b, ai_course a\n" +
                "  WHERE\n" +
                "   b.course_id(+) = a.id\n" +
                "   and \n" +
                "   a.id=?\n" +
                " )\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT\n" +
                        " *\n" +
                        "FROM\n" +
                        " ai_course a\n" +
                        "WHERE\n" +
                        " a.algorithmt(+) = (\n" +
                        "  SELECT\n" +
                        "   b.coverUrl\n" +
                        "  FROM\n" +
                        "   ai_course_dataset_relation b\n" +
                        "right outer join ai_course a on b.course_id = a.id\n" +
                        "  WHERE\n" +
                        "   a.id=?\n" +
                        " )"));
    }

    public void testSqlServerSql1() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "SELECT  *\n" +
                "FROM    TableA AS A, TableB AS b\n" +
                "WHERE   A.ColA *= B.ColB;";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT  *\n" +
                        "FROM    TableA A\n" +
                        "left outer join TableB b on A.ColA = B.ColB\n" +
                        " ;"));
    }

    public void testSqlServerSql2() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "SELECT m.*, \n" +
                "       altname.last_name  last_name_student, \n" +
                "       altname.first_name first_name_student, \n" +
                "       ccu.date_joined, \n" +
                "       ccu.last_login, \n" +
                "       ccu.photo_id, \n" +
                "       ccu.last_updated \n" +
                "FROM   summit.mstr m, \n" +
                "       summit.alt_name altname, \n" +
                "       smmtccon.ccn_user ccu \n" +
                "WHERE  m.id =?\n" +
                "       AND m.id *= altname.id \n" +
                "       AND m.id =* ccu.id \n" +
                "       AND altname.grad_name_ind *= ?\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT m.*, \n" +
                        "       altname.last_name  last_name_student, \n" +
                        "       altname.first_name first_name_student, \n" +
                        "       ccu.date_joined, \n" +
                        "       ccu.last_login, \n" +
                        "       ccu.photo_id, \n" +
                        "       ccu.last_updated \n" +
                        "FROM   summit.mstr m\n" +
                        "left outer join summit.alt_name altname on m.id = altname.id and altname.grad_name_ind = ?\n" +
                        "right outer join smmtccon.ccn_user ccu on m.id = ccu.id \n" +
                        "WHERE  m.id =?"));
    }

    public void testSqlServerSql3() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "if (exists (select * from sys.objects where name = 'GetUser')) drop proc GetUser  \n" +
                "go\n" +
                "create proc GetUser  \n" +
                "@Id int \n" +
                "as \n" +
                "set nocount on;  \n" +
                "begin \n" +
                "\tSELECT e.employee_id,\n" +
                "\t       e.last_name,\n" +
                "\t       e.department_id\n" +
                "\tFROM   employees e,\n" +
                "\t       departments d\n" +
                "\tWHERE  e.department_id *= d.department_id and e.Id=@Id;\n" +
                "end;\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("if (exists (select * from sys.objects where name = 'GetUser')) drop proc GetUser  \n" +
                        "gocreate proc GetUser  \n" +
                        "@Id int \n" +
                        "as \n" +
                        "set nocount on;  \n" +
                        "begin \n" +
                        "\tSELECT e.employee_id,\n" +
                        "\t       e.last_name,\n" +
                        "\t       e.department_id\n" +
                        "\tFROM   employees e\n" +
                        "left outer join departments d on e.department_id = d.department_id\n" +
                        "\tWHERE  e.Id=@Id;\n" +
                        "end;"));
    }

    public void testSqlServerSql4() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "SELECT\n" +
                " *\n" +
                "FROM\n" +
                " ai_course a\n" +
                "WHERE\n" +
                " a.algorithmt *= (\n" +
                "  SELECT\n" +
                "   b.coverUrl\n" +
                "  FROM\n" +
                "   ai_course_dataset_relation b, ai_course a\n" +
                "  WHERE\n" +
                "   b.course_id *= a.id\n" +
                "   and \n" +
                "   a.id=?\n" +
                " )\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT\n" +
                        " *\n" +
                        "FROM\n" +
                        " ai_course a\n" +
                        "WHERE\n" +
                        " a.algorithmt *= (\n" +
                        "  SELECT\n" +
                        "   b.coverUrl\n" +
                        "  FROM\n" +
                        "   ai_course_dataset_relation b\n" +
                        "left outer join ai_course a on b.course_id = a.id\n" +
                        "  WHERE\n" +
                        "   a.id=?\n" +
                        " )"));
    }

    public void testSqlServerSql5() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "INSERT INTO T select  \n" +
                "    b.Amount\n" +
                "from \n" +
                "    TableA a, TableB b\n" +
                "where \n" +
                "    a.inv_no *= b.inv_no";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("INSERT INTO T select  \n" +
                        "    b.Amount\n" +
                        "from \n" +
                        "    TableA a\n" +
                        "left outer join TableB b on a.inv_no = b.inv_no"));
    }


    public void testSqlServerSql6() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "insert into tableF\n" +
                "SELECT  *\n" +
                "FROM    TableA AS A, TableB AS b\n" +
                "WHERE   A.ColA *= B.ColB\n" +
                "and EXISTS(\n" +
                "\tSELECT  *\n" +
                "\tFROM    TableC AS C, TableD AS D\n" +
                "\tWHERE   C.ColC *= D.ColD\n" +
                ");";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("insert into tableF\n" +
                        "SELECT  *\n" +
                        "FROM    TableA A\n" +
                        "left outer join TableB b on A.ColA = B.ColB\n" +
                        "WHERE   EXISTS(\n" +
                        "\tSELECT  *\n" +
                        "\tFROM    TableC C\n" +
                        "left outer join TableD D on C.ColC = D.ColD\n" +
                        ");"));
    }

    public void testSqlServerSql7() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "select\n" +
                "b.Amount\n" +
                "from\n" +
                "TableA a, TableB b,TableC c, TableD d\n" +
                "where\n" +
                "a.inv_no *= b.inv_no and\n" +
                "a.inv_item *= b.inv_item and\n" +
                "c.currency *= b.cash_ccy and\n" +
                "d.tx_code *= b.cash_receipt;";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("select\n" +
                        "b.Amount\n" +
                        "from\n" +
                        "TableA a\n" +
                        "left outer join TableB b on a.inv_no = b.inv_no and a.inv_item = b.inv_item\n" +
                        "right outer join TableC c on c.currency = b.cash_ccy\n" +
                        "right outer join TableD d on d.tx_code = b.cash_receipt\n" +
                        " ;"));
    }

    public void testOracleSql7() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "Select * from table_c as v  cross join table_b, table_c\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("Select * from table_c v\n" +
                        "cross join table_b\n" +
                        "cross join table_c"));
    }

    public void testOracleSql8() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "Select * from table_c  v ,table_a  a ,table_b  b where v.id=1 and b.id=2";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0 && joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("Select * from table_c v\n" +
                        "cross join table_a a\n" +
                        "cross join table_b b   where v.id=1 and b.id=2"));
    }

}
