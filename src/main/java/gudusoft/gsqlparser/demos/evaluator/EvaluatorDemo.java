package gudusoft.gsqlparser.demos.evaluator;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TLog;
import gudusoft.gsqlparser.compiler.TASTEvaluator;
import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class EvaluatorDemo {

    public static void main( String[] args )
    {
        long t = System.currentTimeMillis();

        if (args.length < 1){
            System.out.println("Usage: java EvaluatorDemo [/f <path_to_sql_file>]  [/t <database type>]");
            return;
        }


        EDbVendor vendor = EDbVendor.dbvmssql;
        String sqlfile = null;

        List<String> argList = Arrays.asList(args);

        if ( argList.indexOf( "/f" ) != -1
                && argList.size( ) > argList.indexOf( "/f" ) + 1 )
        {
            sqlfile =  args[argList.indexOf( "/f" ) + 1];
            if (!new File(sqlfile).exists()){
                System.out.println("File not exists:" + sqlfile);
                return;
            }
        }

        int index = argList.indexOf( "/t" );

        if ( index != -1 && args.length > index + 1 )
        {
            vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
        }

//        String inputSQL = "DECLARE\n" +
//                "a INTEGER := -100;\n" +
//                "BEGIN\n" +
//                "DBMS_OUTPUT.PUT_LINE(ABS(a));\n" +
//                "END;";
//        vendor = EDbVendor.dbvoracle;

        TGSqlParser sqlparser = new TGSqlParser(vendor);
        sqlparser.sqlfilename  = sqlfile;
//        sqlparser.sqltext  = inputSQL;
        int ret = sqlparser.parse();
        if (ret != 0) {
            System.out.println("SQL syntax error: "+sqlparser.getErrormessage());
            return ;
        }

        TSQLEnv sqlEnv = new TSQLEnv(vendor) {
            @Override
            public void initSQLEnv() {
            }
        };

        TGlobalScope globalScope = new TGlobalScope(sqlEnv);
        TLog.clearLogs();
        TLog.enableInterpreterLogOnly();
        TLog.setOutputSimpleMode(true);

        TASTEvaluator astEvaluator = new TASTEvaluator(sqlparser.sqlstatements,globalScope);
        astEvaluator.eval();

        System.out.println("Output:");
        System.out.println(TBaseType.dumpLogsToString());
        System.out.println("DbVendor:"+vendor+", Time Escaped: "+ (System.currentTimeMillis() - t) + "ms");
    } // main
}
