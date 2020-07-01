package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.TCallStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;

import java.io.File;
import java.io.IOException;

public class searchSQLObject {
    public static void main(String args[]) throws IOException
    {
        long t;

        t = System.currentTimeMillis();

        if (args.length != 1){
            System.out.println("Usage: java searchFunction sqlfile.sql");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

        EDbVendor dbVendor = EDbVendor.dbvmssql;
        System.out.println("Selected SQL dialect: "+dbVendor.toString());

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
        sqlparser.sqlfilename  = args[0];

        int ret = sqlparser.parse();
        if (ret == 0){
            objectNameVisitor objectNameVisitor = new objectNameVisitor();
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                System.out.println(sqlStatement.sqlstatementtype);
                sqlStatement.acceptChildren(objectNameVisitor);
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }

}

class objectNameVisitor extends TParseTreeVisitor {
    public void preVisit(TObjectName node){
        System.out.println("--> "+node.toString()+"("+ node.getStartToken().lineNo+","+node.getStartToken().columnNo +")"+", \ttype:"+node.getDbObjectType().toString());
        switch (node.getDbObjectType()){
            case column:
                System.out.println("\t\tPart token:"+node.getPartToken().toString());
                if (node.getObjectToken() != null){
                    System.out.println("\t\tObject token:"+node.getObjectToken().toString());
                }
                break;
            case table:
                System.out.println("\t\tObject token:"+node.getObjectToken().toString());
                if (node.getSchemaToken() != null){
                    System.out.println("\t\tSchema token:"+node.getSchemaToken().toString());
                }
                if (node.getSchemaToken() != null){
                    System.out.println("\t\tSchema token:"+node.getSchemaToken().toString());
                }
                if (node.getDatabaseToken() != null){
                    System.out.println("\t\tDatabase token:"+node.getDatabaseToken().toString());
                }
                if (node.getServerToken() != null){
                    System.out.println("\t\tServer token:"+node.getServerToken().toString());
                }
                break;
            default:
                break;
        }
    }
}
