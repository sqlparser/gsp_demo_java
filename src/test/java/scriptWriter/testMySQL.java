package test.scriptWriter;


import gudusoft.gsqlparser.*;
import junit.framework.TestCase;

public class testMySQL extends TestCase
{

    public void testOnduplicate( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmysql );
        sqlparser.sqltext = "insert into user (id, name, summa) \n" +
                "values(104,'Melony',2999) on duplicate key update sum=values(suma)";

        sqlparser.parse( );

        //System.out.println(sqlparser.sqlstatements.get(0).toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmysql, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void testUpdateJoins( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmysql );
        sqlparser.sqltext = "update user u \n" +
                "join user_info ui on u.id=ui.userid\n" +
                "set u.summa = 999\n" +
                "where u.id=1";

        sqlparser.parse( );

        //System.out.println(sqlparser.sqlstatements.get(0).toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmysql, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void testDeleteFrom( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmysql );
        sqlparser.sqltext = "delete from user  \n" +
                "where id=1";

        sqlparser.parse( );

       // System.out.println(sqlparser.sqlstatements.get(0).toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmysql, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void testTimeStampDiff( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmysql );
        sqlparser.sqltext = "select TIMESTAMPDIFF(SECOND,lt.loginDate,lt.logoutDate) as temp from table";

        sqlparser.parse( );

       //  System.out.println(sqlparser.sqlstatements.get(0).toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmysql, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }


}
