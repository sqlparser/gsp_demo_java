package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;

import java.io.File;
import java.io.IOException;

public class visitStarColumn {
    public static void main(String args[]) throws IOException
    {
        long t;

        t = System.currentTimeMillis();

        if (args.length != 1){
            System.out.println("Usage: java visitStarColumn sqlfile.sql");
            return;
        }
        File file=new File(args[0]);
        if (!file.exists()){
            System.out.println("File not exists:"+args[0]);
            return;
        }

        EDbVendor dbVendor = EDbVendor.dbvredshift;
        System.out.println("Selected SQL dialect: "+dbVendor.toString());

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
        sqlparser.sqlfilename  = args[0];

        int ret = sqlparser.parse();
        if (ret == 0){
            ResultColumnVisitor starColumnVisitor = new ResultColumnVisitor();
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                System.out.println(sqlStatement.sqlstatementtype);
                sqlStatement.acceptChildren(starColumnVisitor);
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class ResultColumnVisitor extends TParseTreeVisitor {
    private int stmtCount = 0;


    public void preVisit(TResultColumn node) {

        if (node.toString().equalsIgnoreCase("*")){
            TObjectName starColumn = node.getExpr().getObjectOperand();
            System.out.println("\nFound star column * in table:"+starColumn.getSourceTable().getName());
            for(String colum:starColumn.getColumnsLinkedToStarColumn()){
                System.out.println("\t"+colum);
            }

//            System.out.println("Expand star column:"+starColumn.getSourceTable().toString());
//            for(String column:starColumn.getColumnsLinkedToStarColumn() ){
//                System.out.println(column);
//            }
        }

    }

}
