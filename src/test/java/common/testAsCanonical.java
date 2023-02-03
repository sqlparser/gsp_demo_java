package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testAsCanonical extends TestCase {

    public void testRemoveParenthesis(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "((select name\n" +
                "from emp, dept\n" +
                "where emp.deptid in (\n" +
                "\tselect dept.id from t\n" +
                "\t)\n" +
                ")); -- comment11";
        assertTrue(sqlparser.parse() == 0);
        assertTrue (sqlparser.sqlstatements.get(0).asCanonical().trim().equalsIgnoreCase("select name\n" +
                "from emp, dept\n" +
                "where emp.deptid in (\n" +
                "\tselect dept.id from t\n" +
                "\t)"));
    }

    public void testReplaceConstant(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select name,1,'tiger'\n" +
                "from emp, dept\n" +
                "where emp.deptid in (1,2,3,4)\n" +
                "and emp.id = 10\n" +
                "and emp.name = 'scott'";
        assertTrue(sqlparser.parse() == 0);
        assertTrue (sqlparser.sqlstatements.get(0).asCanonical().trim().equalsIgnoreCase("select name,1,'tiger'\n" +
                "from emp, dept\n" +
                "where emp.deptid in (999)\n" +
                "and emp.id = 999\n" +
                "and emp.name = 'placeholder_str'"));
    }

    public void testReplaceFdecrypt(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "SELECT recordtypeid,\n" +
                "\tf_decrypt(\"name\", 'bkf897aj6cidi91829rk65i3') as name,\n" +
                "\temail,\n" +
                "\tf_decrypt(\"bill_add_street1\", 'bkf897aj6cidi91829rk65i3') as \"bill_add_street1\",\n" +
                "\t\"bill_add_street2\",\n" +
                "\tpublic.f_decrypt(\"bill_add_longitude\", 'bkf897aj6cidi91829rk65i3') as \"bill_add_longitude\",\n" +
                "\tf_decrypt(\"bill_add_latitude\", 'bkf897aj6cidi91829rk65i3') as \"bill_add_latitude\",\n" +
                "\t\"bill_add_countrycode\"\n" +
                "FROM \"na_swr\".\"dim_growers\"";
        TBaseType.as_canonical_f_decrypt_replace_password = true;
        TBaseType.clearCryptFunctions();
        TBaseType.addToCryptFunctions("f_decrypt",2);
        assertTrue(sqlparser.parse() == 0);
        assertTrue (sqlparser.sqlstatements.get(0).asCanonical().trim().equalsIgnoreCase("SELECT recordtypeid,\n" +
                "\tf_decrypt(\"name\", '***') as name,\n" +
                "\temail,\n" +
                "\tf_decrypt(\"bill_add_street1\", '***') as \"bill_add_street1\",\n" +
                "\t\"bill_add_street2\",\n" +
                "\tpublic.f_decrypt(\"bill_add_longitude\", '***') as \"bill_add_longitude\",\n" +
                "\tf_decrypt(\"bill_add_latitude\", '***') as \"bill_add_latitude\",\n" +
                "\t\"bill_add_countrycode\"\n" +
                "FROM \"na_swr\".\"dim_growers\""));

        assertTrue(sqlparser.sqlstatements.get(0).toString().equalsIgnoreCase("SELECT recordtypeid,\n" +
                "\tf_decrypt(\"name\", 'bkf897aj6cidi91829rk65i3') as name,\n" +
                "\temail,\n" +
                "\tf_decrypt(\"bill_add_street1\", 'bkf897aj6cidi91829rk65i3') as \"bill_add_street1\",\n" +
                "\t\"bill_add_street2\",\n" +
                "\tpublic.f_decrypt(\"bill_add_longitude\", 'bkf897aj6cidi91829rk65i3') as \"bill_add_longitude\",\n" +
                "\tf_decrypt(\"bill_add_latitude\", 'bkf897aj6cidi91829rk65i3') as \"bill_add_latitude\",\n" +
                "\t\"bill_add_countrycode\"\n" +
                "FROM \"na_swr\".\"dim_growers\""));
    }

    public void testReplaceFdecrypt2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select employee_guid, f_aes_decrypt_ecb_ctr(gender,'zxygd8yrabpwijs7') as gender,\n" +
                "f_decrypt(name,'2ixu827uzuy88yx889rlv7vr') as accountname\n" +
                "from public.mio037_group_hr_all_employees limit 10\n";
        TBaseType.as_canonical_f_decrypt_replace_password = true;
        TBaseType.clearCryptFunctions();
        TBaseType.addToCryptFunctions("f_decrypt",2);
        TBaseType.addToCryptFunctions("f_aes_decrypt_ecb_ctr",2);

        assertTrue(sqlparser.parse() == 0);
        assertTrue (sqlparser.sqlstatements.get(0).asCanonical().trim().equalsIgnoreCase("select employee_guid, f_aes_decrypt_ecb_ctr(gender,'***') as gender,\n" +
                "f_decrypt(name,'***') as accountname\n" +
                "from public.mio037_group_hr_all_employees limit 10"));

        assertTrue(sqlparser.sqlstatements.get(0).toString().trim().equalsIgnoreCase(sqlparser.sqltext.trim()));
    }

    public void testReplaceFdecrypt3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select business_partner_type,bp_cid, f_decrypt(name,z9rmt42l85qkf8v3dec23tep) as name , \n" +
                "name as encrypted_name, language_name,source_system \n" +
                "from public.mio03_business_partner where bp_cid = 'placeholder_str';";
        TBaseType.as_canonical_f_decrypt_replace_password = true;
        TBaseType.clearCryptFunctions();
        TBaseType.addToCryptFunctions("f_decrypt",2);

        assertTrue(sqlparser.parse() == 0);
//        System.out.println(sqlparser.sqlstatements.get(0).asCanonical().trim());

        assertTrue (sqlparser.sqlstatements.get(0).asCanonical().trim().equalsIgnoreCase("select business_partner_type,bp_cid, f_decrypt(name,'***') as name , \n" +
                "name as encrypted_name, language_name,source_system \n" +
                "from public.mio03_business_partner where bp_cid = 'placeholder_str'"));
    }


}
