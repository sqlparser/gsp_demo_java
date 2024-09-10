package demos.events;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ISQLStatementHandle;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;

public class ProcessSQLStatement {
    public static void main(String args[]) {
        String sqlfile = "C:\\Users\\DELL\\Downloads\\20240311110800487_mssql_sql\\data.sql";
        mySQLStatementHandle sqlStatementHandle = new mySQLStatementHandle();
        sqlStatementHandle.lastTime = System.currentTimeMillis();


        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqlfilename = sqlfile;
        sqlparser.setSqlStatementHandle(sqlStatementHandle);


        int ret = sqlparser.parse();
        if (ret != 0){
            System.out.println("\n\n"+sqlparser.getErrormessage().substring(0,1000));
            return;
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - sqlStatementHandle.lastTime)  );
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
