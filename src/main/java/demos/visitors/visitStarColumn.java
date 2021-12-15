package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
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

        EDbVendor dbVendor = EDbVendor.dbvpostgresql;
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
                System.out.println(starColumnVisitor.getResultColumns().toString());
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class ResultColumnVisitor extends TParseTreeVisitor {
    private int stmtCount = 0;

    public StringBuilder getResultColumns() {
        return resultColumns;
    }

    private StringBuilder resultColumns = new StringBuilder();


    public void preVisit(TResultColumn node) {

        if (node.toString().endsWith("*")){
            TObjectName starColumn = node.getExpr().getObjectOperand();
            //System.out.println("\nFound star column * in table:"+starColumn.getSourceTable().getName());
            resultColumns.append(TBaseType.windowsLinebreak+"Found star column * in table:"+starColumn.getSourceTable().getName()+TBaseType.windowsLinebreak);
            for(String colum:starColumn.getColumnsLinkedToStarColumn()){
                //System.out.println("\t"+colum);
                resultColumns.append("\t"+colum+TBaseType.windowsLinebreak);
            }
        }

    }

}
