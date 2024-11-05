package demos.sqlenv;

import demos.utils.SQLQuery;
import demos.utils.TJsonSQLEnv;
import gudusoft.gsqlparser.EDbVendor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class runSQLEnv {
    public static void main(String args[]) {
        long t = System.currentTimeMillis();

        if (args.length < 1) {
            System.out.println("Usage: java runSQLEnv [/f <path_to_sql_file>]");
            return;
        }
        List<String> argList = Arrays.asList(args);
        String jsonfile = args[argList.indexOf( "/f" ) + 1];
        //System.out.println(jsonfile);

        TJsonSQLEnv jsonSQLEnv = new TJsonSQLEnv(EDbVendor.dbvmssql,jsonfile);
        Iterator<SQLQuery> iterator = jsonSQLEnv.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getSourceCode());
        }
    }
}
