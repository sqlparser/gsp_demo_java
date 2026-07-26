package demos.visitors;

import demos.joinConvert.JoinConverter;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SearchSelect {

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
            selectVisitor fv = new selectVisitor(dbVendor);
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
               // System.out.println(sqlStatement.sqlstatementtype);
                fv.init();
                sqlStatement.acceptChildren(fv);
                if (fv.getSkippedQuery() > 0){
                    System.out.println("Warning:"+fv.getSkippedQuery()+" query is skipped!");
                }

                System.out.println(sqlStatement.toString());
            }

        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
}

class selectVisitor extends TParseTreeVisitor {

    public selectVisitor(){
        this.dbVendor = EDbVendor.dbvoracle;
    }

    public selectVisitor(EDbVendor dbVendor){
        this.dbVendor = dbVendor;
    }

    private EDbVendor dbVendor;

    private int counter = 1;
    private int nestedQuery = 0;
    private int skippedQuery = 0;

    public int getSkippedQuery() {
        return skippedQuery;
    }

    public void init(){
        skippedQuery = 0;
        nestedQuery = 0;
    }

    public void preVisit(TSelectSqlStatement select){
        if (nestedQuery > 0){ // tokens of subquery is override, so, just skip it
            skippedQuery++;
            return;
        }
        nestedQuery++;
        if (select.isCombinedQuery()){
            System.out.println("\n\nfind set operator:"+select.getSetOperatorType());
            System.out.println("\n\nfind left");
            System.out.println(select.getLeftStmt().toString());
            System.out.println("\n\nfind right");
            System.out.println(select.getRightStmt().toString());
        }
        //System.out.println("\n\n"+counter++);
        //System.out.println(select.toString());

//        JoinConverter converter = new JoinConverter( select.toString(), dbVendor );
//        if ( converter.convert( ) != 0 )
//        {
//            System.out.println( converter.getErrorMessage( ) );
//        }
//        else
//        {
//            //System.out.println( "\nSQL in ANSI joins" );
//            //System.out.println( converter.getQuery( ) );
//            select.setText(converter.getQuery());
//        }
    }

    public void postVisit(TSelectSqlStatement select){
        nestedQuery--;
    }

    public void preVisit(TResultColumn resultColumn){
        if (resultColumn.getAliasClause() != null){
         //   System.out.println("alias:"+resultColumn.getAliasClause().toString()+",filed:"+resultColumn.toString());
        }
    }

}
