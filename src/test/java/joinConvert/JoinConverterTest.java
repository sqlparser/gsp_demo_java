package joinConvert;

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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT COUNT(1)\n" +
                        "  FROM (SELECT PRODID\n" +
                        "          FROM (SELECT T.ID PRODID, CD.ID CID\n" +
                        "                  FROM PRODUCT T\n" +
                        "inner join CD CD on T.ID=CD.ID                      \n" +
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
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
                        "inner join ai_course a on b.course_id = a.id \n" +
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "inner join departments d on e.department_id = d.department_id"));
    }

    public void testOracleSql5() {
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
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
                        "right outer join ai_course a on b.course_id = a.id \n" +
                        "  WHERE\n" +
                        "   a.id=?\n" +
                        " )"));
    }

    public void testInnerSql() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "CREATE\n" +
                "OR REPLACE PROCEDURE myDemo01 IS aa number (10);\n" +
                "BEGIN\n" +
                "\tSELECT e.employee_id INTO aa FROM employees e, departments d WHERE e.department_id = d.department_id;\n" +
                "\tdbms_output.put_line (aa);\n" +
                "END myDemo01;";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("CREATE\n" +
                        "OR REPLACE PROCEDURE myDemo01 IS aa number (10);\n" +
                        "BEGIN\n" +
                        "\tSELECT e.employee_id INTO aa FROM employees e\n" +
                        "inner join departments d on e.department_id = d.department_id  ;\n" +
                        "\tdbms_output.put_line (aa);\n" +
                        "END myDemo01;"));
    }



    public void testSqlServerSql1() {
        EDbVendor vendor = EDbVendor.dbvmssql;
        String sql = "SELECT  *\n" +
                "FROM    TableA AS A, TableB AS b\n" +
                "WHERE   A.ColA *= B.ColB;";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT  *\n" +
                        "FROM    TableA A\n" +
                        "left outer join TableB b on A.ColA = B.ColB \n" +
                        ";"));
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
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
                        "         WHERE  m.id =?"));
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
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
                        "\t       WHERE  e.Id=@Id;\n" +
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
                        "left outer join ai_course a on b.course_id = a.id \n" +
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("insert into tableF\n" +
                        "SELECT  *\n" +
                        "FROM    TableA A\n" +
                        "left outer join TableB b on A.ColA = B.ColB \n" +
                        "WHERE   EXISTS(\n" +
                        "\tSELECT  *\n" +
                        "\tFROM    TableC C\n" +
                        "left outer join TableD D on C.ColC = D.ColD \n" +
                        "\t);"));
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
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("select\n" +
                        "b.Amount\n" +
                        "from\n" +
                        "TableA a\n" +
                        "left outer join TableB b on a.inv_no = b.inv_no and a.inv_item = b.inv_item\n" +
                        "right outer join TableC c on c.currency = b.cash_ccy\n" +
                        "right outer join TableD d on d.tx_code = b.cash_receipt  \n" +
                        ";"));
    }

    public void testOracleSql7() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "Select * from table_c as v  cross join table_b, table_c\n";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0);

        //System.out.println(joinConverter.getQuery());

        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("Select * from table_c v\n" +
                        "cross join table_b\n" +
                        "cross join table_c"));
    }

    public void testOracleSql8() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        String sql = "Select * from table_c  v ,table_a  a ,table_b  b where v.id=1 and b.id=2";
        JoinConverter joinConverter = new JoinConverter(sql, vendor);
        assertTrue(joinConverter.convert() == 0);
        assertTrue(joinConverter.getQuery()
                .trim()
                .equalsIgnoreCase("Select * from table_c v\n" +
                        "cross join table_a a\n" +
                        "cross join table_b b   where v.id=1 and b.id=2"));
    }

    public void testCrossJoin() {
        String sqltext = "SELECT e.employee_id,\n"
                + "       e.last_name,\n"
                + "       e.department_id\n"
                + "FROM   employees e,\n"
                + "       departments d";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT e.employee_id,\n"
                        + "       e.last_name,\n"
                        + "       e.department_id\n"
                        + "FROM   employees e\n"
                        + "cross join departments d"));
    }

    public void testInnerJoin() {
        String sqltext = "SELECT e.employee_id,\n"
                + "       e.last_name,\n"
                + "       e.department_id\n"
                + "FROM   employees e,\n"
                + "       departments d\n"
                + "WHERE  e.department_id = d.department_id";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "inner join departments d on e.department_id = d.department_id"));

    }

    public void testTableNotJoined() {
        String sqltext = "SELECT * \n"
                + "FROM   summit.mstr m, \n"
                + "       summit.alt_name altname, \n"
                + "       smmtccon.ccn_user ccu, \n"
                + "       uhelp.deg_coll deg \n"
                + "WHERE  m.id = ? \n"
                + "       AND m.id = altname.id(+) \n"
                + "       AND m.id = ccu.id(+) \n"
                + "       AND 'N' = ccu.admin(+) \n"
                + "       AND altname.grad_name_ind(+) = '*'";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 1);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT * \n" +
                        "FROM   summit.mstr m\n" +
                        "left outer join summit.alt_name altname on m.id = altname.id and altname.grad_name_ind = '*'\n" +
                        "left outer join smmtccon.ccn_user ccu on m.id = ccu.id and 'N' = ccu.admin \n" +
                        "          WHERE  m.id = ?"));
    }

    public void testOutterJoin1() {
        String sqltext = "SELECT m.*,\n"
                + "       altname.last_name  last_name_student,\n"
                + "       altname.first_name first_name_student,\n"
                + "       ccu.date_joined,\n"
                + "       ccu.last_login,\n"
                + "       ccu.photo_id,\n"
                + "       ccu.last_updated\n"
                + "FROM   summit.mstr m,\n"
                + "       summit.alt_name altname,\n"
                + "       smmtccon.ccn_user ccu\n"
                + "WHERE  m.id =?\n"
                + "       AND m.id = altname.id(+)\n"
                + "       AND m.id = ccu.id(+)\n"
                + "       AND altname.grad_name_ind(+) = '*'";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT m.*,\n" +
                        "       altname.last_name  last_name_student,\n" +
                        "       altname.first_name first_name_student,\n" +
                        "       ccu.date_joined,\n" +
                        "       ccu.last_login,\n" +
                        "       ccu.photo_id,\n" +
                        "       ccu.last_updated\n" +
                        "FROM   summit.mstr m\n" +
                        "left outer join summit.alt_name altname on m.id = altname.id and altname.grad_name_ind = '*'\n" +
                        "left outer join smmtccon.ccn_user ccu on m.id = ccu.id\n" +
                        "       WHERE  m.id =?"));
    }

    public void testOutterJoin2() {
        String sqltext = "SELECT *\n"
                + "FROM   smmtccon.ccn_menu menu,\n"
                + "       smmtccon.ccn_page paget\n"
                + "WHERE  ( menu.page_id = paget.page_id(+) )\n"
                + "       AND ( NOT enabled = 'N' )\n"
                + "       AND ( ( :parent_menu_id IS NULL\n"
                + "               AND menu.parent_menu_id IS NULL )\n"
                + "              OR ( menu.parent_menu_id = :parent_menu_id ) )\n"
                + "ORDER  BY item_seq";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT *\n" +
                        "FROM   smmtccon.ccn_menu menu\n" +
                        "left outer join smmtccon.ccn_page paget on menu.page_id = paget.page_id\n" +
                        "       WHERE  ( NOT enabled = 'N' )\n" +
                        "       AND ( ( :parent_menu_id IS NULL\n" +
                        "               AND menu.parent_menu_id IS NULL )\n" +
                        "              OR ( menu.parent_menu_id = :parent_menu_id ) )\n" +
                        "ORDER  BY item_seq"));
    }

    public void testOutterJoin3() {
        String sqltext = "SELECT ppp.project_name proj_name,\n"
                + "       pr.role_title    user_role\n"
                + "FROM   jboss_admin.portal_application_location pal,\n"
                + "       jboss_admin.portal_user_app_location_role pualr,\n"
                + "       jboss_admin.portal_user pu\n"
                + "WHERE  ( pal.application_location_id = pualr.application_location_id\n"
                + "         AND pu.jbp_uid = pualr.jbp_uid\n"
                + "          )";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT ppp.project_name proj_name,\n" +
                        "       pr.role_title    user_role\n" +
                        "FROM   jboss_admin.portal_application_location pal\n" +
                        "inner join jboss_admin.portal_user_app_location_role pualr on pal.application_location_id = pualr.application_location_id\n" +
                        "inner join jboss_admin.portal_user pu on pu.jbp_uid = pualr.jbp_uid"));
    }

    public void testOutterJoin4() {
        String sqltext = "SELECT ppp.project_name proj_name,\n"
                + "       pr.role_title    user_role\n"
                + "FROM   jboss_admin.portal_application_location pal,\n"
                + "       jboss_admin.portal_application pa,\n"
                + "       jboss_admin.portal_user_app_location_role pualr,\n"
                + "       jboss_admin.portal_location pl,\n"
                + "       jboss_admin.portal_role pr,\n"
                + "       jboss_admin.portal_pep_project ppp,\n"
                + "       jboss_admin.portal_user pu\n"
                + "WHERE  (pal.application_location_id = pualr.application_location_id\n"
                + "         AND pu.jbp_uid = pualr.jbp_uid\n"
                + "         AND pu.username = 'USERID')\n"
                + "       AND pal.uidr_uid = pl.uidr_uid\n"
                + "       AND pal.application_id = pa.application_id\n"
                + "       AND pal.application_id = pr.application_id\n"
                + "       AND pualr.role_id = pr.role_id\n"
                + "       AND pualr.project_id = ppp.project_id\n"
                + "       AND pa.application_id = 'APPID'";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT ppp.project_name proj_name,\n" +
                        "       pr.role_title    user_role\n" +
                        "FROM   jboss_admin.portal_application_location pal\n" +
                        "inner join jboss_admin.portal_application pa on pal.application_id = pa.application_id\n" +
                        "inner join jboss_admin.portal_user_app_location_role pualr on pal.application_location_id = pualr.application_location_id\n" +
                        "inner join jboss_admin.portal_location pl on pal.uidr_uid = pl.uidr_uid\n" +
                        "inner join jboss_admin.portal_pep_project ppp on pualr.project_id = ppp.project_id\n" +
                        "inner join jboss_admin.portal_user pu on pu.jbp_uid = pualr.jbp_uid\n" +
                        "inner join jboss_admin.portal_role pr on pal.application_id = pr.application_id and pualr.role_id = pr.role_id\n" +
                        "       WHERE  (pu.username = 'USERID')\n" +
                        "       AND pa.application_id = 'APPID'"));

    }

    public void testOutterJoin5() {
        String sqltext = "select *\n"
                + "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst,\n"
                + "       ods_stg_pnb_stuf_pers_adr pas,\n"
                + "       ods_stg_pnb_stuf_pers_nat nat,\n"
                + "       ods_stg_pnb_stuf_adr adr,\n"
                + "       ods_stg_pnb_stuf_np prs\n"
                + "where  pas.soort_adres = lst.soort_adres\n"
                + "       and prs.id(+) = nat.prs_id\n"
                + "       and adr.id = pas.adr_id\n"
                + "       and prs.id = pas.prs_id\n"
                + "       and lst.persoonssoort = 'PERSOON'\n"
                + "       and pas.einddatumrelatie is null";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase(("select *\n" +
                        "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n" +
                        "inner join ods_stg_pnb_stuf_pers_adr pas on pas.soort_adres = lst.soort_adres\n" +
                        "right outer join ods_stg_pnb_stuf_pers_nat nat on prs.id = nat.prs_id\n" +
                        "inner join ods_stg_pnb_stuf_adr adr on adr.id = pas.adr_id\n" +
                        "inner join ods_stg_pnb_stuf_np prs on prs.id = pas.prs_id\n" +
                        "       where  lst.persoonssoort = 'PERSOON'\n" +
                        "       and pas.einddatumrelatie is null").toLowerCase()));

    }

    public void testOutterJoin6() {
        String sqltext = "select *\n"
                + "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst,\n"
                + "       ods_stg_pnb_stuf_np prs,\n"
                + "       ods_stg_pnb_stuf_pers_adr pas,\n"
                + "       ods_stg_pnb_stuf_pers_nat nat,\n"
                + "       ods_stg_pnb_stuf_adr adr\n"
                + "where  pas.soort_adres = lst.soort_adres\n"
                + "       and prs.id(+) = nat.prs_id\n"
                + "       and adr.id = pas.adr_id\n"
                + "       and prs.id = pas.prs_id\n"
                + "       and lst.persoonssoort = 'PERSOON'\n"
                + "       and pas.einddatumrelatie is null\n";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("select *\n" +
                        "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n" +
                        "inner join ods_stg_pnb_stuf_pers_adr pas on pas.soort_adres = lst.soort_adres\n" +
                        "inner join ods_stg_pnb_stuf_np prs on prs.id = pas.prs_id\n" +
                        "right outer join ods_stg_pnb_stuf_pers_nat nat on prs.id = nat.prs_id\n" +
                        "inner join ods_stg_pnb_stuf_adr adr on adr.id = pas.adr_id\n" +
                        "       where  lst.persoonssoort = 'PERSOON'\n" +
                        "       and pas.einddatumrelatie is null"));


    }

    public void testLeftOutterJoin() {

        String sqltext = "SELECT e.employee_id,\n" +
                "       e.last_name,\n" +
                "       e.department_id\n" +
                "FROM   employees e,\n" +
                "       departments d\n" +
                "WHERE  e.department_id = d.department_id(+)";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "left outer join departments d on e.department_id = d.department_id"));


    }

    public void testRightOutterJoin() {

        String sqltext = "SELECT e.employee_id,\n" +
                "       e.last_name,\n" +
                "       e.department_id\n" +
                "FROM   employees e,\n" +
                "       departments d\n" +
                "WHERE  e.department_id(+) = d.department_id";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "right outer join departments d on e.department_id = d.department_id"));


    }

    public void testOracleCTESql() {

        String sqltext = "WITH DB_PRIV AS\n" +
                "      (\n" +
                "          SELECT DBID\n" +
                "               , DB_NAME\n" +
                "               , ORDERING\n" +
                "            FROM USER_DB_PRIVILEGE \n" +
                "      )\n" +
                "      , CHECK_ACT_DAY AS\n" +
                "      (\n" +
                "          SELECT B.DBID\n" +
                "               , A.CHECK_SEQ\n" +
                "               , A.CHECK_DT\n" +
                "               , TO_CHAR(SYSDATE, 'YYYYMMDD') CHECK_DAY\n" +
                "            FROM tablea A,\n" +
                "                 DB_PRIV B\n" +
                "           WHERE A.DBID(+) = B.DBID\n" +
                "             AND A.CHECK_SEQ(+) = A.MAX_CHECK_SEQ(+)\n" +
                "      )\n" +
                "      SELECT A.DB_NAME\n" +
                "           , B.CHECK_DAY\n" +
                "        FROM DB_PRIV A\n" +
                "           , CHECK_ACT_DAY B";

        JoinConverter converter = new JoinConverter(sqltext, EDbVendor.dbvoracle);
        assertTrue(converter.convert() == 0);
        assertTrue(converter.getQuery()
                .trim()
                .equalsIgnoreCase("WITH DB_PRIV AS\n" +
                        "      (\n" +
                        "          SELECT DBID\n" +
                        "               , DB_NAME\n" +
                        "               , ORDERING\n" +
                        "            FROM USER_DB_PRIVILEGE \n" +
                        "      )\n" +
                        "      , CHECK_ACT_DAY AS\n" +
                        "      (\n" +
                        "          SELECT B.DBID\n" +
                        "               , A.CHECK_SEQ\n" +
                        "               , A.CHECK_DT\n" +
                        "               , TO_CHAR(SYSDATE, 'YYYYMMDD') CHECK_DAY\n" +
                        "            FROM tablea A\n" +
                        "right outer join DB_PRIV B on A.DBID = B.DBID\n" +
                        "                 WHERE A.CHECK_SEQ = A.MAX_CHECK_SEQ\n" +
                        "      )\n" +
                        "      SELECT A.DB_NAME\n" +
                        "           , B.CHECK_DAY\n" +
                        "        FROM DB_PRIV A\n" +
                        "cross join CHECK_ACT_DAY B"));


    }
}
