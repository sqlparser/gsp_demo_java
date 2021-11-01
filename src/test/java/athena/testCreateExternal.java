package athena;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateExternal extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvathena);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE cloudtrail_logs (\n" +
                "eventversion STRING,\n" +
                "userIdentity STRUCT<\n" +
                "               type:STRING,\n" +
                "               principalid:STRING,\n" +
                "               arn:STRING,\n" +
                "               accountid:STRING,\n" +
                "               invokedby:STRING,\n" +
                "               accesskeyid:STRING,\n" +
                "               userName:STRING,\n" +
                "sessioncontext:STRUCT<\n" +
                "attributes:STRUCT<\n" +
                "               mfaauthenticated:STRING,\n" +
                "               creationdate:STRING>,\n" +
                "sessionIssuer:STRUCT<  \n" +
                "               type:STRING,\n" +
                "               principalId:STRING,\n" +
                "               arn:STRING, \n" +
                "               accountId:STRING,\n" +
                "               userName:STRING>>>,\n" +
                "eventTime STRING,\n" +
                "eventSource STRING,\n" +
                "eventName STRING,\n" +
                "awsRegion STRING,\n" +
                "sourceIpAddress STRING,\n" +
                "userAgent STRING,\n" +
                "errorCode STRING,\n" +
                "errorMessage STRING,\n" +
                "requestParameters STRING,\n" +
                "responseElements STRING,\n" +
                "additionalEventData STRING,\n" +
                "requestId STRING,\n" +
                "eventId STRING,\n" +
                "resources ARRAY<STRUCT<\n" +
                "               ARN:STRING,\n" +
                "               accountId:STRING,\n" +
                "               type:STRING>>,\n" +
                "eventType STRING,\n" +
                "apiVersion STRING,\n" +
                "readOnly STRING,\n" +
                "recipientAccountId STRING,\n" +
                "serviceEventDetails STRING,\n" +
                "sharedEventID STRING,\n" +
                "vpcEndpointId STRING\n" +
                ")\n" +
                "ROW FORMAT SERDE 'com.amazon.emr.hive.serde.CloudTrailSerde'\n" +
                "STORED AS INPUTFORMAT 'com.amazon.emr.cloudtrail.CloudTrailInputFormat'\n" +
                "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\n" +
                "LOCATION 's3://cloudtrail_bucket_name/AWSLogs/Account_ID/';\n";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createtable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createtable.getColumnList().size() == 23);
        TColumnDefinition c0 = createtable.getColumnList().getColumn(0);
        assertTrue(c0.getColumnName().toString().equalsIgnoreCase("eventversion"));
        assertTrue(c0.getDatatype().getDataType() == EDataType.string_t);

        TColumnDefinition c1 = createtable.getColumnList().getColumn(1);
        assertTrue(c1.getColumnName().toString().equalsIgnoreCase("userIdentity"));
        assertTrue(c1.getDatatype().getDataType() == EDataType.struct_t);
        assertTrue(c1.getDatatype().getColumnDefList().size() == 8);
        TColumnDefinition c10 = c1.getDatatype().getColumnDefList().getColumn(0);
        assertTrue(c10.getColumnName().toString().equalsIgnoreCase("type"));
        assertTrue(c10.getDatatype().getDataType() == EDataType.string_t);
        TColumnDefinition c17 = c1.getDatatype().getColumnDefList().getColumn(7);
        assertTrue(c17.getColumnName().toString().equalsIgnoreCase("sessioncontext"));
        assertTrue(c17.getDatatype().getDataType() == EDataType.struct_t);
    }
}
