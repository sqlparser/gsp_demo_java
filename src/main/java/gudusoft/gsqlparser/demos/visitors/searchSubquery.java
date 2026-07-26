package demos.visitors;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class searchSubquery {

    public static void main(String args[]) throws IOException
    {
        long t = System.currentTimeMillis( );

        if ( args.length < 1 )
        {
            System.out.println( "Usage: java searchSubquery sqlfile.sql [/t <database type>]" );
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
            TSubqueryVisitor fv = new TSubqueryVisitor(dbVendor);
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                sqlStatement.acceptChildren(fv);
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class TSubqueryVisitor extends TParseTreeVisitor {

    public TSubqueryVisitor(EDbVendor dbVendor){
        this.dbVendor = dbVendor;
    }

    private EDbVendor dbVendor;

    private int counter = 1;
    private int nestedQuery = 0;
    private int skippedQuery = 0;


    public void preVisit(TTable node){
        switch (node.getTableType()){
            case subquery:
                System.out.println("\nColumns for subquery: "+node.getDisplayName());
                for(TObjectName objectName:node.getLinkedColumns()){
                    System.out.println( String.format(objectName.toString()));
                }
                break;
        }

    }

}
