package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftCopy;
import junit.framework.TestCase;

public class testCopy extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "copy listing\n" +
                "from 's3://mybucket/data/listings_pipe.txt'\n" +
                "credentials 'aws_access_key_id=<temporary-access-key-id><aws_secret_ac \n" +
                "cess_key=<temporary-secret-access-key>;token=<temporary-token>;master_symmet \n" +
                "ric_key=<master_key>'\n" +
                "encrypted;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftCopy);
        TRedshiftCopy copy = (TRedshiftCopy) sqlparser.sqlstatements.get(0);
        assertTrue(copy.getTableName().toString().equalsIgnoreCase("listing"));
       assertTrue(copy.getFromSource().equalsIgnoreCase("'s3://mybucket/data/listings_pipe.txt'"));
        assertTrue(copy.getAuthorizationClause().getCredentials().equalsIgnoreCase("'aws_access_key_id=<temporary-access-key-id><aws_secret_ac \n" +
                "cess_key=<temporary-secret-access-key>;token=<temporary-token>;master_symmet \n" +
                "ric_key=<master_key>'"));


    }
}