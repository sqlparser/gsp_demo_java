package formatsql;
/*
 * Date: 13-2-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignOption;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

public class testPlsql1 extends TestCase {
    public static void test1(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvoracle);
                sqlParser
                        .setSqltext("CREATE OR REPLACE PROCEDURE RULE14002TESTPROC1( A1 IN NUMBER )\n"
                                    + "IS\n"
                                    + "PROCEDURE INPROC1 (A1 NUMBER); -- Declaration\n"
                                    + "PROCEDURE INPROC2 (A1 NUMBER) -- Definition\n"
                                    + "IS\n"
                                    + "BEGIN\n"
                                    + "INSERT INTO T1 VALUES(1, A1);\n"
                                    + "END;\n"
                                    + "BEGIN\n"
                                    + "INPROC1(TEMP1.M1);\n"
                                    + "END;\n"
                                    + "/");
                int ret = sqlParser.parse();
                if (ret != 0)
                {
                    System.out.println(sqlParser.getErrormessage());
                }

                TPlsqlCreateProcedure stmt = (TPlsqlCreateProcedure) sqlParser.getSqlstatements().get(0);

                GFmtOpt sqlFormatOption = GFmtOptFactory.newInstance();
                sqlFormatOption.beStyleCreatetableLeftBEOnNewline = false;
                sqlFormatOption.beStyleCreatetableRightBEOnNewline = false;
                sqlFormatOption.createtableListitemInNewLine = false;
                sqlFormatOption.createtableFieldlistAlignOption = TAlignOption.AloLeft;
                sqlFormatOption.defaultAligntype = TAlignStyle.AsStacked;
                sqlFormatOption.defaultCommaOption = TLinefeedsCommaOption.LfAfterComma;
                sqlFormatOption.cteNewlineBeforeAs = true;
                sqlFormatOption.linebreakBeforeParamInExec = true;
                sqlFormatOption.linebreakAfterDeclare = true;
                sqlFormatOption.beStyleFunctionFirstParamInNewline = true;

               //System.out.println(stmt.toString());
                //System.out.println(FormatterFactory.pp(sqlParser, sqlFormatOption));
            }
}
