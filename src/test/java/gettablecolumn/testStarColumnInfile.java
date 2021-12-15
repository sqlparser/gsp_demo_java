package gettablecolumn;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import junit.framework.TestCase;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class testStarColumnInfile  extends TestCase {

    public static void test1(){
        doCompare(EDbVendor.dbvredshift,starColumnsPrivateDir +"syngenta/create-table1.sql",starColumnsPrivateDir +"syngenta/create-table1.txt");
    }

    public static void testCTE(){
        doCompare(EDbVendor.dbvpostgresql,starColumnsPublicDir +"pg-cte-1.sql",starColumnsPublicDir +"pg-cte-1.txt");
    }

    public static String starColumnsPrivateDir =  gspCommon.BASE_SQL_DIR_PRIVATE+"starColumns/";
    public static String starColumnsPublicDir =  gspCommon.BASE_SQL_DIR_PUBLIC+"starColumns/";

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    static boolean comparyStringArray(String[] fromSQL, String[] fromText){
        boolean ret = true;
        if (fromSQL.length != fromText.length){
            System.out.println("Total lines in not equal, from file: "+ fromText.length+", from SQL: "+fromSQL.length);
            return false;
        }

        for(int i=0;i<fromText.length;i++){
            if (fromText[i].equalsIgnoreCase(fromSQL[i])){
                continue;
            }else{
                System.out.println("File text of Line:"+(i+1)+" is not equal");
                System.out.println("From  SQL:"+fromSQL[i]);
                System.out.println("From file:"+fromText[i]);
                ret = false;
                break;
            }
        }
        return ret;
    }

    public static void doCompare(EDbVendor dbVendor, String in, String out){

        String outStr = "";
        String[] a = null,b=null;

        try {
            outStr = readFile(out, Charset.defaultCharset());
            a = outStr.trim().split(TBaseType.windowsLinebreakEscape);

        } catch (IOException e) {
            e.printStackTrace();
        }

        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
        sqlparser.sqlfilename  = in;

        int ret = sqlparser.parse();
        if (ret == 0){
            ResultColumnVisitor starColumnVisitor = new ResultColumnVisitor();
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
                TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(i);
                sqlStatement.acceptChildren(starColumnVisitor);
                StringBuilder sbout = starColumnVisitor.getResultColumns();
                b = sbout.toString().trim().split(TBaseType.windowsLinebreakEscape);
            }
            assertTrue(comparyStringArray(b,a));
        }else{
            System.out.println(sqlparser.getErrormessage());
        }
    }



}

class ResultColumnVisitor extends TParseTreeVisitor {
    private int stmtCount = 0;

    public StringBuilder getResultColumns() {
        return resultColumns;
    }

    private StringBuilder resultColumns = new StringBuilder();


    public void preVisit(TResultColumn node) {

        if (node.toString().equalsIgnoreCase("*")){
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