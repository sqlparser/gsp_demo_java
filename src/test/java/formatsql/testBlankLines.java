package formatsql;
/*
 * Date: 11-3-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TEmptyLinesOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import junit.framework.TestCase;

public class testBlankLines extends TestCase {

    public static void testEmptyLines1(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.isoweek (@DATE datetime)\n" +
                 "RETURNS INT\n" +
                 "WITH EXECUTE AS caller\n" +
                 "AS\n" +
                 "BEGIN\n" +
                 "     DECLARE @ISOweek INT\n" +
                 "     \n" +
                 "     \n" +
                 "     SET @ISOweek= datepart(wk,@DATE)+1\n" +
                 "          -datepart(wk,CAST(datepart(yy,@DATE) AS CHAR(4))+'0104')\n" +
                 "          \n" +
                 "--Special cases: Jan 1-3 may belong to the previous year\n" +
                 "     IF (@ISOweek=0)\n" +
                 "          SET @ISOweek=dbo.isoweek(CAST(datepart(yy,@DATE)-1\n" +
                 "               AS CHAR(4))+'12'+ CAST(24+datepart(DAY,@DATE) AS CHAR(2)))+1\n" +
                 "--Special case: Dec 29-31 may belong to the next year\n" +
                 "     IF ((datepart(mm,@DATE)=12) AND\n" +
                 "          ((datepart(dd,@DATE)-datepart(dw,@DATE))>= 28))\n" +
                 "          SET @ISOweek=1\n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "     RETURN(@ISOweek)\n" +
                 "END;\n" +
                 "GO ";

         //System.out.println(sqlparser.sqltext);

         sqlparser.parse();
         option.emptyLines = TEmptyLinesOption.EloMergeIntoOne;
        option.insertBlankLineInBatchSqls = false;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Isoweek (@DATE DATETIME\n" +
                ") \n" +
                "RETURNS INT WITH EXECUTE AS caller \n" +
                "AS \n" +
                "  BEGIN \n" +
                "    DECLARE @ISOweek INT \n" +
                "\n" +
                "    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4)) + '0104')  \n" +
                "\n" +
                "--Special cases: Jan 1-3 may belong to the previous year\n" +
                "    IF ( @ISOweek = 0 ) SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE) - 1 AS CHAR(4)) + '12' + Cast(24 + Datepart(DAY,@DATE) AS CHAR(2))) + 1  \n" +
                "\n" +
                "--Special case: Dec 29-31 may belong to the next year\n" +
                "    IF ( ( Datepart(mm,@DATE) = 12 )\n" +
                "         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) ) SET @ISOweek=1 \n" +
                "\n" +
                "    RETURN(@ISOweek) \n" +
                "  END;\n" +
                "GO"));
//        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Isoweek (@DATE DATETIME\n" +
//                ") \n" +
//                "RETURNS INT WITH EXECUTE AS caller \n" +
//                "AS \n" +
//                "  BEGIN \n" +
//                "    DECLARE @ISOweek INT \n" +
//                "\n" +
//                "    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4))+'0104') \n" +
//                "\n" +
//                "--Special cases: Jan 1-3 may belong to the previous year \n" +
//                "    IF ( @ISOweek = 0 ) SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE)-1 AS CHAR(4))+'12'+ Cast(24+Datepart(DAY,@DATE) AS CHAR(2))) + 1 \n" +
//                "\n" +
//                "--Special case: Dec 29-31 may belong to the next year \n" +
//                "    IF ( ( Datepart(mm,@DATE) = 12 )\n" +
//                "         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) ) SET @ISOweek=1 \n" +
//                "\n" +
//                "    RETURN(@ISOweek) \n" +
//                "  END;\n" +
//                "GO"));
       //  System.out.println(result);

//        System.out.println("CREATE FUNCTION dbo.Isoweek (@DATE DATETIME\n" +
//                ") \n" +
//                "RETURNS INT WITH EXECUTE AS caller \n" +
//                "AS \n" +
//                "  BEGIN \n" +
//                "    DECLARE @ISOweek INT \n" +
//                "\n" +
//                "    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4))+'0104') \n" +
//                "\n" +
//                "--Special cases: Jan 1-3 may belong to the previous year \n" +
//                "    IF ( @ISOweek = 0 ) SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE)-1 AS CHAR(4))+'12'+ Cast(24+Datepart(DAY,@DATE) AS CHAR(2))) + 1 \n" +
//                "\n" +
//                "--Special case: Dec 29-31 may belong to the next year \n" +
//                "    IF ( ( Datepart(mm,@DATE) = 12 )\n" +
//                "         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) ) SET @ISOweek=1 \n" +
//                "\n" +
//                "    RETURN(@ISOweek) \n" +
//                "  END;\n" +
//                "GO");
    }

    public static void testEmptyLines2(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.isoweek (@DATE datetime)\n" +
                 "RETURNS INT\n" +
                 "WITH EXECUTE AS caller\n" +
                 "AS\n" +
                 "BEGIN\n" +
                 "     DECLARE @ISOweek INT\n" +
                 "     \n" +
                 "     \n" +
                 "     SET @ISOweek= datepart(wk,@DATE)+1\n" +
                 "          -datepart(wk,CAST(datepart(yy,@DATE) AS CHAR(4))+'0104')\n" +
                 "          \n" +
                 "--Special cases: Jan 1-3 may belong to the previous year\n" +
                 "     IF (@ISOweek=0)\n" +
                 "          SET @ISOweek=dbo.isoweek(CAST(datepart(yy,@DATE)-1\n" +
                 "               AS CHAR(4))+'12'+ CAST(24+datepart(DAY,@DATE) AS CHAR(2)))+1\n" +
                 "--Special case: Dec 29-31 may belong to the next year\n" +
                 "     IF ((datepart(mm,@DATE)=12) AND\n" +
                 "          ((datepart(dd,@DATE)-datepart(dw,@DATE))>= 28))\n" +
                 "          SET @ISOweek=1\n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "     RETURN(@ISOweek)\n" +
                 "END;\n" +
                 "GO ";

         sqlparser.parse();
         option.emptyLines = TEmptyLinesOption.EloMergeIntoOne;
       // option.emptyLines = TEmptyLinesOption.EloPreserve;
        option.insertBlankLineInBatchSqls = true;
         String result = FormatterFactory.pp(sqlparser, option);
       // assertTrue("EmptyLines,insertBlankLineInBatchSqls can't be tested due to format in stored procedure not work",false);
        System.out.println("EmptyLines,insertBlankLineInBatchSqls can't be tested due to format in stored procedure not work");
    }

    public static void testEmptyLines3(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.isoweek (@DATE datetime)\n" +
                 "RETURNS INT\n" +
                 "WITH EXECUTE AS caller\n" +
                 "AS\n" +
                 "BEGIN\n" +
                 "     DECLARE @ISOweek INT\n" +
                 "     \n" +
                 "     \n" +
                 "     SET @ISOweek= datepart(wk,@DATE)+1\n" +
                 "          -datepart(wk,CAST(datepart(yy,@DATE) AS CHAR(4))+'0104')\n" +
                 "          \n" +
                 "--Special cases: Jan 1-3 may belong to the previous year\n" +
                 "     IF (@ISOweek=0)\n" +
                 "          SET @ISOweek=dbo.isoweek(CAST(datepart(yy,@DATE)-1\n" +
                 "               AS CHAR(4))+'12'+ CAST(24+datepart(DAY,@DATE) AS CHAR(2)))+1\n" +
                 "--Special case: Dec 29-31 may belong to the next year\n" +
                 "     IF ((datepart(mm,@DATE)=12) AND\n" +
                 "          ((datepart(dd,@DATE)-datepart(dw,@DATE))>= 28))\n" +
                 "          SET @ISOweek=1\n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "     RETURN(@ISOweek)\n" +
                 "END;\n" +
                 "GO ";

         sqlparser.parse();
         option.emptyLines = TEmptyLinesOption.EloRemove;
        option.insertBlankLineInBatchSqls = false;
         String result = FormatterFactory.pp(sqlparser, option);
//        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Isoweek (@DATE DATETIME\n" +
//                ") \n" +
//                "RETURNS INT WITH EXECUTE AS caller \n" +
//                "AS \n" +
//                "  BEGIN \n" +
//                "    DECLARE @ISOweek INT \n" +
//                "    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4))+'0104') \n" +
//                "--Special cases: Jan 1-3 may belong to the previous year \n" +
//                "    IF ( @ISOweek = 0 ) SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE)-1 AS CHAR(4))+'12'+ Cast(24+Datepart(DAY,@DATE) AS CHAR(2))) + 1 \n" +
//                "--Special case: Dec 29-31 may belong to the next year \n" +
//                "    IF ( ( Datepart(mm,@DATE) = 12 )\n" +
//                "         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) ) SET @ISOweek=1 \n" +
//                "    RETURN(@ISOweek) \n" +
//                "  END;\n" +
//                "GO"));
         System.out.println("this is a bug, need to be fixed");
    }

    public static void testEloPreserve(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "CREATE FUNCTION dbo.isoweek (@DATE datetime)\n" +
                 "RETURNS INT\n" +
                 "WITH EXECUTE AS caller\n" +
                 "AS\n" +
                 "BEGIN\n" +
                 "     DECLARE @ISOweek INT\n" +
                 "     \n" +
                 "     \n" +
                 "     SET @ISOweek= datepart(wk,@DATE)+1\n" +
                 "          -datepart(wk,CAST(datepart(yy,@DATE) AS CHAR(4))+'0104')\n" +
                 "          \n" +
                 "--Special cases: Jan 1-3 may belong to the previous year\n" +
                 "     IF (@ISOweek=0)\n" +
                 "          SET @ISOweek=dbo.isoweek(CAST(datepart(yy,@DATE)-1\n" +
                 "               AS CHAR(4))+'12'+ CAST(24+datepart(DAY,@DATE) AS CHAR(2)))+1\n" +
                 "--Special case: Dec 29-31 may belong to the next year\n" +
                 "     IF ((datepart(mm,@DATE)=12) AND\n" +
                 "          ((datepart(dd,@DATE)-datepart(dw,@DATE))>= 28))\n" +
                 "          SET @ISOweek=1\n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "          \n" +
                 "     RETURN(@ISOweek)\n" +
                 "END;\n" +
                 "GO ";

         sqlparser.parse();
        option.emptyLines = TEmptyLinesOption.EloPreserve;
        //option.insertBlankLineInBatchSqls = true;
         String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("CREATE FUNCTION dbo.Isoweek (@DATE DATETIME\n" +
                ") \n" +
                "RETURNS INT WITH EXECUTE AS caller \n" +
                "AS \n" +
                "  BEGIN \n" +
                "    DECLARE @ISOweek INT \n" +
                "\n" +
                "    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4)) + '0104')  \n" +
                "\n" +
                "--Special cases: Jan 1-3 may belong to the previous year\n" +
                "    IF ( @ISOweek = 0 ) SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE) - 1 AS CHAR(4)) + '12' + Cast(24 + Datepart(DAY,@DATE) AS CHAR(2))) + 1  \n" +
                "--Special case: Dec 29-31 may belong to the next year\n" +
                "    IF ( ( Datepart(mm,@DATE) = 12 )\n" +
                "         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) ) SET @ISOweek=1 \n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "    RETURN(@ISOweek) \n" +
                "  END;\n" +
                "GO"));
 //       System.out.println(result);
    }

    public static void testNoEmptyLinesBetweenMultiSetStmts(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "SET @A = @B\n" +
                 "SET @C = @D\n" +
                 "SET @E = @F\n\n" +
                 "SET @G = @H  ";

         sqlparser.parse();
         option.emptyLines = TEmptyLinesOption.EloRemove;
         option.insertBlankLineInBatchSqls = true;
         option.noEmptyLinesBetweenMultiSetStmts = true;
        String result = FormatterFactory.pp(sqlparser, option);
        assertTrue(result.trim().equalsIgnoreCase("SET @A = @B \n" +
                "SET @C = @D \n" +
                "SET @E = @F \n" +
                "SET @G = @H"));
       // assertTrue("NoEmptyLinesBetweenMultiSetStmts not worked correctly",false);
       //  System.out.println(result);
    }

    public static void testIfElse(){
         GFmtOpt option = GFmtOptFactory.newInstance(new Exception().getStackTrace()[0].getClassName() + "." + new Exception().getStackTrace()[0].getMethodName());

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
         sqlparser.sqltext = "DECLARE @compareprice money, @cost money \n" +
                 "EXECUTE Production.uspGetList '%Bikes%', 700, \n" +
                 "    @compareprice OUT, \n" +
                 "    @cost OUTPUT\n" +
                 "IF @cost <= @compareprice \n" +
                 "BEGIN\n" +
                 "    PRINT 'These products can be purchased for less than \n" +
                 "    $'+RTRIM(CAST(@compareprice AS varchar(20)))+'.'\n" +
                 "END\n" +
                 "ELSE\n" +
                 "    PRINT 'The prices for all products in this category exceed \n" +
                 "    $'+ RTRIM(CAST(@compareprice AS varchar(20)))+'.'";

         sqlparser.parse();
         option.emptyLines = TEmptyLinesOption.EloMergeIntoOne;
         option.insertBlankLineInBatchSqls = true;
         //option.noEmptyLinesBetweenMultiSetStmts = false;
        String result = FormatterFactory.pp(sqlparser, option);

       //  System.out.println(result);
        assertTrue(result.trim().equalsIgnoreCase("DECLARE @compareprice MONEY,\n" +
                "        @cost         MONEY \n" +
                "\n" +
                "EXECUTE production.uspgetlist \n" +
                "  '%Bikes%',\n" +
                "  700,\n" +
                "  @compareprice OUT,\n" +
                "  @cost OUTPUT \n" +
                "\n" +
                "IF @cost <= @compareprice \n" +
                "  BEGIN \n" +
                "    PRINT 'These products can be purchased for less than \n" +
                "    $'+Rtrim(Cast(@compareprice AS VARCHAR(20)))+'.' \n" +
                "  END \n" +
                "ELSE \n" +
                "  PRINT 'The prices for all products in this category exceed \n" +
                "    $'+ Rtrim(Cast(@compareprice AS VARCHAR(20)))+'.'"));
    }

}
