package demos.search;

import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * search sql files that include specified class name in a directory recursively.
 * Usage:
 * searchClause class_name directory
 *
 * You need to modify searchVisitor to add support to search other clause, it support THierarchical,TTable,TFunctionCall only.
*/
public class searchClause {

    public static void main(String args[])
    {
        long t;
        t = System.currentTimeMillis();

        if (args.length != 2){
            System.out.println("Usage: java searchClause class_name directory");
            return;
        }

        String parseTreeNodeName = args[0];
        String dir = args[1];
        int ret;

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        SqlFileList sqlfiles = new SqlFileList(dir);
        System.out.println("Found files:"+sqlfiles.sqlfiles.size());
        for(int k=0;k < sqlfiles.sqlfiles.size();k++){
            sqlparser.sqlfilename = sqlfiles.sqlfiles.get(k).toString();
            ret = sqlparser.parse();
            if (ret == 0){
                for(int i=0;i<sqlparser.sqlstatements.size();i++){
                searchVisitor sv = new searchVisitor(parseTreeNodeName);
                sqlparser.sqlstatements.get(i).acceptChildren(sv);
                if (sv.isFound()) {
                    System.out.println("Find "+parseTreeNodeName+" in "+sqlparser.sqlfilename);
                    break;
                }
                }
            }
        }
        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }

}

class searchVisitor extends TParseTreeVisitor {
    private boolean found = false;
    private String parseTreeNodeName = null;

    public boolean isFound() {
        return found;
    }

    public searchVisitor(String c){
        this.parseTreeNodeName = c;

    }


    public void preVisit(THierarchical node){

        if (node.getClass().getSimpleName().compareToIgnoreCase(this.parseTreeNodeName) == 0){
            found = true;
        }
    }

    public void preVisit(TSelectSqlStatement node)
    {
        //do something
       // System.out.println(node.getClass().getSimpleName());
    }

    public void preVisit(TTable node)
    {
        //do something
        //System.out.println(node.getClass().getSimpleName());
        if (node.getClass().getSimpleName().compareToIgnoreCase(this.parseTreeNodeName) == 0){
            found = true;
        }
    }

    public void preVisit(TFunctionCall node)
    {
        //do something
        // System.out.println(node.getClass().getSimpleName());
        if (node.getClass().getSimpleName().compareToIgnoreCase(this.parseTreeNodeName) == 0){
            found = true;
        }
    }

}

class SqlFileList {
    String dir;
    FilenameFilter ffobj;
    public ArrayList sqlfiles;
    public  SqlFileList(String dir){
       this.dir = dir;
       this.ffobj = ffobj;
        sqlfiles = new ArrayList();
        getfiles(this.dir);
    }

    void getfiles(String pdir){
        File f1 = new File(pdir);
        if(f1.isDirectory()){
          File[]  fs = f1.listFiles();
            for (int i=0;i<fs.length;i++){
                if(fs[i].isDirectory()){
                    getfiles(pdir+"/"+fs[i].getName());
                }else{
                    if (fs[i].getName().endsWith("sql"))
                      sqlfiles.add(pdir+"/"+fs[i].getName());
                }
            }
        }
    }
}