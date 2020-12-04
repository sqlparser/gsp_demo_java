package mysql;
/*
 * Date: 12-2-27
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testQuoteInLiteral extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "INSERT INTO umResponse (reservationId, timestamp, cmd, response) VALUES ('1234',       20120221171116,      'ADD',      '<HTML>\n" +
                "<HEAD>\n" +
                "<TITLE>500 Internal Server Error</TITLE>\n" +
                "</HEAD><BODY>\n" +
                "<H1>Internal Server Error</H1>\n" +
                "The server encountered an internal error or\n" +
                "misconfiguration and was unable to complete\n" +
                "your request.<P>\n" +
                "Please contact the server administrator to inform of the time the error occurred\n" +
                "and of anything you might have done that may have\n" +
                "caused the error.<P>\n" +
                "More information about this error may be available\n" +
                "in the server error log.<P>\n" +
                "<HR>\n" +
                "<ADDRESS>\n" +
                "Web Server at website.com\n" +
                "</ADDRESS>\n" +
                "</BODY>\n" +
                "</HTML>\n" +
                "\n" +
                "<!--\n" +
                "   - Unfortunately, Microsoft has added a clever new\n" +
                "   - \"feature\" to Internet Explorer. If the text of\n" +
                "   - an error\\'s message is \"too small\", specifically\n" +
                "   - less than 512 bytes, Internet Explorer returns\n" +
                "   - its own error message. You can turn that off,\n" +
                "   - but it\\'s pretty tricky to find switch called\n" +
                "   - \"smart error messages\". That means, of course,\n" +
                "   - that short error messages are censored by default.\n" +
                "   - IIS always returns error messages that are long\n" +
                "   - enough to make Internet Explorer happy. The\n" +
                "   - workaround is pretty simple: pad the error\n" +
                "   - message with a big comment like this to push it\n" +
                "   - over the five hundred and twelve bytes minimum.\n" +
                "   - Of course, that\\'s exactly what you\\'re reading\n" +
                "   - right now.\n" +
                "   -->\n" +
                "')";

        assertTrue(sqlparser.parse() == 0);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "INSERT INTO umResponse (reservationId, timestamp, cmd, response)"
                           + " VALUES ('1234',"
                           + "       20120221171116,"
                           + "      'ADD',"
                           + "      '<HTML>\n<HEAD>\n<TITLE>500 Internal Server Error</TITLE>\n</HEAD><BODY>\n<H1>Internal Server Error</H1>\nThe server encountered an internal error or\nmisconfiguration and was unable to complete\nyour request.<P>\nPlease contact the server administrator to inform of the time the error occurred\nand of anything you might have done that may have\ncaused the error.<P>\nMore information about this error may be available\nin the server error log.<P>\n<HR>\n<ADDRESS>\nWeb Server at website.com\n</ADDRESS>\n</BODY>\n</HTML>\n\n\n')";

        assertTrue(sqlparser.parse() == 0);
    }


}
