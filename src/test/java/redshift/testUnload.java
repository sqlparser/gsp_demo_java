package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftUnload;
import junit.framework.TestCase;


public class testUnload extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "unload ('select * from venue')\n" +
                "to 's3://mybucket/venue_pipe_' credentials\n" +
                "'aws_access_key_id=<access-key-id>;aws_secret_access_key=<secret-access-key>'\n" +
                "manifest;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftUnload);
        TRedshiftUnload unload = (TRedshiftUnload) sqlparser.sqlstatements.get(0);
        assertTrue(unload.getSelectStmt().equalsIgnoreCase("'select * from venue'"));
        assertTrue(unload.getS3().equalsIgnoreCase("'s3://mybucket/venue_pipe_'"));
        assertTrue(unload.getCredentials().equalsIgnoreCase("'aws_access_key_id=<access-key-id>;aws_secret_access_key=<secret-access-key>'"));


    }
}