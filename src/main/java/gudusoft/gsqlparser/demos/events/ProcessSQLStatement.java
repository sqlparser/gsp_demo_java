package gudusoft.gsqlparser.demos.events;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ISQLStatementHandle;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.File;

/**
 * Handles each statement as the parser reaches it, instead of waiting for the
 * whole script.
 *
 * <p>{@link TGSqlParser#setSqlStatementHandle} registers a callback that fires
 * per statement during the parse, which is what you want for a script too large
 * to hold as a finished tree, or when you want to stop early: returning
 * {@code true} from the callback aborts the parse.
 */
public class ProcessSQLStatement {
    public static void main(String args[]) {
        // This used to be a hardcoded absolute path into one developer's
        // Downloads folder, so the demo could not run anywhere else: the file
        // was missing, parse() failed, and the error branch below then threw
        // StringIndexOutOfBoundsException instead of printing the error.
        if (args.length < 1) {
            System.out.println("Usage: java ProcessSQLStatement <sqlfile> [/t <database type>]");
            System.out.println("  <sqlfile>   The SQL script to parse.");
            System.out.println("  /t <type>   Optional. Database dialect, default mssql.");
            System.out.println();
            System.out.println("Prints one line per statement as the parser reaches it,");
            System.out.println("rather than after the whole script has been parsed.");
            return;
        }

        String sqlfile = null;
        EDbVendor vendor = EDbVendor.dbvmssql;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("/t")) {
                if (i + 1 >= args.length) {
                    System.out.println("/t needs a database type, e.g. /t oracle");
                    return;
                }
                vendor = TGSqlParser.getDBVendorByName(args[++i]);
            } else {
                sqlfile = args[i];
            }
        }

        if (sqlfile == null || !new File(sqlfile).isFile()) {
            System.out.println("No such file: " + sqlfile);
            return;
        }

        mySQLStatementHandle sqlStatementHandle = new mySQLStatementHandle();
        sqlStatementHandle.lastTime = System.currentTimeMillis();

        TGSqlParser sqlparser = new TGSqlParser(vendor);
        sqlparser.sqlfilename = sqlfile;
        sqlparser.setSqlStatementHandle(sqlStatementHandle);

        int ret = sqlparser.parse();
        if (ret != 0) {
            // substring(0, 1000) unconditionally was the second half of the bug:
            // it throws whenever the message is shorter than that, which is most
            // of the time.
            String msg = sqlparser.getErrormessage();
            System.out.println();
            System.out.println(msg.length() > 1000 ? msg.substring(0, 1000) + "..." : msg);
            return;
        }

        System.out.println("Time Escaped: " + (System.currentTimeMillis() - sqlStatementHandle.lastTime));
    }
}

class mySQLStatementHandle implements ISQLStatementHandle {

    long total = 0,lastTime = 0;
    int sqlCount = 0;

    public boolean processSQLStatement(TCustomSqlStatement stmt, TGSqlParser sqlParser){

        boolean stopParsing = false;
        long t = System.currentTimeMillis();
        long delta = t - lastTime;
        total += delta;
        lastTime = t;
        sqlCount++;

        System.out.println("Time Escaped: " + (total) + ", sql:\t"+sqlCount+"/"+ sqlParser.sqlstatements.size() +"," + stmt.sqlstatementtype+",Line: "+stmt.getStartToken().lineNo);

//        if (sqlCount == 40000){
//            stopParsing = true;
//        }

        return stopParsing;
    }

}
