package demos.visitors;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TWindowDef;
import gudusoft.gsqlparser.stmt.TCallStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;

import java.io.File;
import java.io.IOException;

public class searchFunction {

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
            functionVisitor fv = new functionVisitor();
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                System.out.println(sqlStatement.sqlstatementtype);
                sqlStatement.acceptChildren(fv);
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }

}

class functionVisitor extends TParseTreeVisitor {
    public void preVisit(TFunctionCall node){
        System.out.print("--> function: "+node.getFunctionName().toString()+", type:"+node.getFunctionType());
        if (node.getArgs() != null){
            for(int i=0;i<node.getArgs().size();i++){
                System.out.print(",\targ"+i+": "+node.getArgs().getExpression(i).toString());
                if (node.getArgs().getExpression(i).getObjectOperand() != null){
                    TObjectName objectName = node.getArgs().getExpression(i).getObjectOperand();
                    System.out.println("ObjectType:"+objectName.getDbObjectType());
                }
            }
        }
        if (node.getAggregateType() != EAggregateType.none){
            System.out.print(",\taggregate type:"+node.getAggregateType());
        }
        switch (node.getFunctionType()){
            case cast_t:
                System.out.print("\n\tcast: "+node.getExpr1()+", datatype:"+node.getTypename().toString());
                break;
        }

        System.out.println("");
    }

    public void preVisit(TWindowDef windowDef){
        System.out.println("\twindow_specification");
        if (windowDef.getPartitionClause() != null)
        {
            System.out.print("\t\tParition value: ");
            for(int i=0;i<windowDef.getPartitionClause().getExpressionList().size();i++){
                System.out.print(windowDef.getPartitionClause().getExpressionList().getExpression(i).toString());
            }

        }
        if(windowDef.getOrderBy() != null){
            System.out.print("\n\t\tOrder by clause: ");
            for(int i=0;i<windowDef.getOrderBy().getItems().size();i++){
                System.out.print(windowDef.getOrderBy().getItems().getOrderByItem(i).toString());
            }
        }

        System.out.println("");

    }
//    public void preVisit(TCallStatement statement){
//        System.out.println("--> call: " + statement.getRoutineName().toString());
//    }
//
//    public void preVisit(TMssqlExecute statement){
//        if (statement.getExecType() == TBaseType.metExecSp){
//            System.out.println("--> execute: " + statement.getModuleName().toString());
//        }
//    }
}
