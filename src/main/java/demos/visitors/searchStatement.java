package demos.visitors;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TDeclareVariable;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
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

        EDbVendor dbVendor = EDbVendor.dbvmssql;

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

class stmtVisitor extends TParseTreeVisitor {
    public void preVisit(TMssqlDeclare declare){
        System.out.println("\ndeclare type:"+declare.getDeclareType());
        if (declare.getDeclareType() == EDeclareType.variable){

        }
    }
    public void preVisit(TDeclareVariable declareVariable){
        System.out.print("\tvariable name:"+declareVariable.getVariableName().toString()+", data type:"+declareVariable.getDatatype().toString());
    }

}
