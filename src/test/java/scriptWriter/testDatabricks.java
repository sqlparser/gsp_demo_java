package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testDatabricks extends TestCase
{
    public void test1( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select datepart('days',interval 5 days 3 hours 7 minutes);";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test2( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select datepart('seconds',interval 5 hours 30 seconds 1 milliseconds 1 microseconds);\n" +
                "select datepart('month',interval '2021-11' year to month);\n" +
                "select datepart('minute',interval '123 23:55:59.002001' day to second);\n" +
                "select interval '1-1' year to month div interval '-1' month;\n" +
                "select extract(days from interval 5 days 3 hours 7 minutes);\n" +
                "select extract(seconds from interval 5 hours 30 seconds 1 milliseconds 1 microseconds);\n" +
                "select extract(month from interval '2021-11' year to month);\n" +
                "select extract(minute from interval '123 23:55:59.002001' day to second);\n" +
                "select sequence(to_date('2018-01-01'),to_date('2018-03-01'),interval 1 month);\n" +
                "select sequence(to_date('2018-01-01'),to_date('2018-03-01'),interval '0-1' year to month);\n" +
                "select sign(interval -'100' year);\n" +
                "select signum(interval -'100' year);\n" +
                "select try_add(date'2021-01-01',interval 1 year);\n" +
                "select try_add(timestamp'2021-01-01 00:00:00',interval 1 day);\n" +
                "select try_add(interval 1 year,interval 2 year);\n" +
                "select try_divide(interval 2 month,2);\n" +
                "select try_divide(interval 2 month,0);\n" +
                "select try_multiply(interval 2 year,3);\n" +
                "select try_subtract(date'2021-01-01',interval 1 year);\n" +
                "select try_subtract(timestamp'2021-01-02 00:00:00',interval 1 day);\n" +
                "select try_subtract(interval 2 year,interval 1 year);\n" +
                "select width_bucket(interval '0' year,interval '0' year,interval '10' year,10);\n" +
                "select width_bucket(interval '1' year,interval '0' year,interval '10' year,10);\n" +
                "select width_bucket(interval '0' day,interval '0' day,interval '10' day,10);\n" +
                "select width_bucket(interval '1' day,interval '0' day,interval '10' day,10);\n";
        sqlparser.parse( );
        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks,
                    sqlparser.sqlstatements.get(i).toString(), sqlparser.sqlstatements.get(i).toScript()));
        }
    }

    public void test3( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select contains('spark sql','spark')";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test4( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select exists(array(1,2,3),x -> x % 2 == 0)";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test5( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select date_part('year',timestamp '2019-08-12 01:00:00.123456')";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }
}
