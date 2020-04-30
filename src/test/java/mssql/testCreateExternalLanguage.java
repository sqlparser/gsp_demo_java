package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TCreateExternalLanguage;
import junit.framework.TestCase;

public class testCreateExternalLanguage extends TestCase {

    public void test1() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE EXTERNAL LANGUAGE Java FROM (CONTENT = N'<path-to-zip>', FILE_NAME = 'javaextension.dll');";
        int result = sqlparser.parse();
        assertTrue(result == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlcreateevexternalLanguage);
        TCreateExternalLanguage createExternalLanguage = (TCreateExternalLanguage) sqlparser.sqlstatements.get(0);
        assertTrue(createExternalLanguage.getLanguageName().toString().equalsIgnoreCase("Java"));
    }
}
