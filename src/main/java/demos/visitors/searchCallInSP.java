package demos.visitors;

import demos.tracedatalineage.SqlFileList;
import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.TCallStatement;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import gudusoft.gsqlparser.stmt.oracle.*;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;


import java.io.IOException;

public class searchCallInSP {

    public static void main(String args[]) throws IOException
    {
        long t;
        int j, e = 0, m = 0;
        String sqlDir = "";


        EDbVendor dbVendor = EDbVendor.dbvoracle;
        System.out.println("Selected SQL dialect: "+dbVendor.toString());
        sqlDir = "c:\\prg\\gsp_sqlfiles\\TestCases\\private\\java\\oracle\\solidatus\\";
       // sqlDir = "c:\\prg\\gsp_sqlfiles\\TestCases\\public\\allversions\\oracle\\plsql\\";

        t = System.currentTimeMillis();
        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
        SqlFileList sqlfiles = new SqlFileList(sqlDir, true);

        for (int k = 0; k < sqlfiles.sqlfiles.size(); k++) {
            sqlparser.sqlfilename = sqlfiles.sqlfiles.get(k).toString();

            t = System.currentTimeMillis();
            j = sqlparser.parse();


            if (j == 0) {
                spVisitor sp = new spVisitor();
                TCustomSqlStatement sqlStatement = null;
                for(int i=0;i<sqlparser.sqlstatements.size();i++){
                    sqlStatement = sqlparser.sqlstatements.get(i);
                    sqlStatement.acceptChildren(sp);
                    if (sp.getCount() > 0){
                        System.out.println("Found in file: "+sqlfiles.sqlfiles.get(k)+"\n");
                    }
                }
            } else {
                e++;
                System.out.println("syntax error: " + sqlfiles.sqlfiles.get(k));
            }
        }


        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class spVisitor extends TParseTreeVisitor {
    private int nest = 0;
    private int count = 0;

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void preVisit(TPlsqlCreateFunction function){
        System.out.println(TBaseType.numberOfSpace(nest,'\t')+function.sqlstatementtype+", function name:"+function.getFunctionName().toString());
        count++;
        nest++;
    }
    public void postVisit(TPlsqlCreateFunction function){
        nest--;
    }

    public void preVisit(TPlsqlCreateProcedure procedure){
        System.out.println(TBaseType.numberOfSpace(nest,'\t')+procedure.sqlstatementtype+", procedure name:"+procedure.getProcedureName().toString());
        count++;
        nest++;
    }
    public void postVisit(TPlsqlCreateProcedure procedure){
        nest--;
    }

    public void preVisit(TPlsqlCreatePackage pkg){
        System.out.println(TBaseType.numberOfSpace(nest,'\t')+pkg.sqlstatementtype+", package name:"+pkg.getPackageName().toString());
        count++;
        nest++;
    }
    public void postVisit(TPlsqlCreatePackage pkg){
        nest--;
    }

    public void preVisit(TPlsqlCreateTrigger trigger){
        System.out.println(TBaseType.numberOfSpace(nest,'\t')+trigger.sqlstatementtype+", trigger name:"+trigger.getTriggerName().toString());
        count++;
        nest++;
    }
    public void postVisit(TPlsqlCreateTrigger trigger){
        nest--;
    }

    public void preVisit(TCreateFunctionStmt function){
        System.out.println(TBaseType.numberOfSpace(nest,'\t')+function.sqlstatementtype+", funtion name:"+function.getFunctionName().toString());
        count++;
        nest++;
    }
    public void postVisit(TCreateFunctionStmt function){
        nest--;
    }

    public void preVisit(TCreateProcedureStmt procedure){
        System.out.println(TBaseType.numberOfSpace(nest,'\t')+procedure.sqlstatementtype+", procedure name:"+procedure.getProcedureName().toString());
        count++;
        nest++;
    }

    public void postVisit(TCreateProcedureStmt procedure){
        nest--;
    }

    public void preVisit(TFunctionCall node){
        if (nest > 0)
            System.out.println(TBaseType.numberOfSpace(nest,'\t')+ "--> function: "+node.getFunctionName().toString()+", type:"+node.getFunctionType());
    }

    public void preVisit(TCallStatement statement){
        if (nest > 0)
            System.out.println(TBaseType.numberOfSpace(nest,'\t')+"--> call: " + statement.getRoutineName().toString());
    }

    public void preVisit(TMssqlExecute statement){
        if (nest > 0)
            if (statement.getExecType() == TBaseType.metExecSp){
                System.out.println(TBaseType.numberOfSpace(nest,'\t')+"--> execute: " + statement.getModuleName().toString());
            }
    }
}

class functionCallVisitor extends TParseTreeVisitor {

    private int nest;


}
