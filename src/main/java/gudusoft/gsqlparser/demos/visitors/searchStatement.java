package demos.visitors;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TForStmt;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class searchStatement {

    public static void main(String args[]) throws IOException
    {
        long t = System.currentTimeMillis( );

        if ( args.length < 1 )
        {
            System.out.println( "Usage: java SearchSelect sqlfile.sql [/t <database type>]" );
            System.out.println( "/t: Option, set the database type." );
            return;
        }
        File file = new File( args[0] );
        if ( !file.exists( ) )
        {
            System.out.println( "File not exists:" + args[0] );
            return;
        }

        EDbVendor dbVendor = EDbVendor.dbvdb2;

        List<String> argList = Arrays.asList( args );
        int index = argList.indexOf( "/t" );

        if ( index != -1 && args.length > index + 1 )
        {

            dbVendor = TGSqlParser.getDBVendorByName(args[index + 1]);

        }
        System.out.println( "Selected SQL dialect: " + dbVendor.toString( ) );

        TGSqlParser sqlparser = new TGSqlParser( dbVendor );
        sqlparser.sqlfilename = args[0];


        int ret = sqlparser.parse();
        if (ret == 0){
            stmtVisitor fv = new stmtVisitor();
            TCustomSqlStatement sqlStatement = null;
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                sqlStatement = sqlparser.sqlstatements.get(i);
                System.out.println(sqlStatement.sqlstatementtype);
//                if (sqlStatement.getCommentBeforeNode() != null){
//                    System.out.println("Found comment before statement:\n"+sqlStatement.getCommentBeforeNode());
//                }
//                if (sqlStatement.getCommentAfterNode() != null){
//                    System.out.println("Found comment after statement:\n"+sqlStatement.getCommentAfterNode());
//                }
                sqlStatement.acceptChildren(fv);
            }

//            System.out.println("\n\nvisit again:");
//            sqlStatement.acceptChildren(fv);

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class stmtVisitor extends TParseTreeVisitor {
    public void preVisit(TMssqlDeclare declare){
        System.out.println("\ndeclare type:"+declare.getDeclareType());
        if (declare.getDeclareType() == EDeclareType.variable){

        }
    }
    public void preVisit(TDeclareVariable declareVariable){
        System.out.print("\n\tvariable name:"+declareVariable.getVariableName().toString()+", data type:"+declareVariable.getDatatype().toString());
    }

    public void preVisit(TExpression expressionNode){
        if (expressionNode.getExpressionType() == EExpressionType.in_t){
            System.out.println("expr:"+expressionNode.getNodeStatus()+",\ttext:"+expressionNode.toString());
            if (expressionNode.getNotToken() != null){
                System.out.println(expressionNode.getNotToken().toString());
            }
        }

//        if (expressionNode.toString().equalsIgnoreCase("fx(2)")){
//            if (expressionNode.getNodeStatus() == ENodeStatus.nsNormal){
//                System.out.println("remove:"+expressionNode.toString());
//                expressionNode.removeTokens();
//            }
//        }
    }

    public void preVisit(TUpdateSqlStatement updateSqlStatement){
//        System.out.println("Update set clause:\t"+updateSqlStatement.getResultColumnList().getResultColumn(0).toString());
//        TResultColumn resultColumn = updateSqlStatement.getResultColumnList().getResultColumn(0);
//        System.out.println("Expression type:\t"+resultColumn.getExpr().getExpressionType());

    }
    public void preVisit(TInsertSqlStatement insertSqlStatement){
        System.out.println("\ninsert stmt:\n"+insertSqlStatement.toString());
    }

    public void preVisit(TForStmt forStmt){
        System.out.println("\nfor stmt:\n"+forStmt.getBodyStatements().size());
        //System.out.println("\nfor stmt:\n"+forStmt.toString());
    }
}
