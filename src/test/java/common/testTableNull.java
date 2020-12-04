package common;

import junit.framework.TestCase;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;

public class testTableNull extends TestCase {

    public static void testTableNull()
    {

        String dir = "c:\\prg\\gsqlparser\\Test\\TestCases\\oracle";
        int ret;

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        SqlFileList sqlfiles = new SqlFileList(dir);
        for(int k=0;k < sqlfiles.sqlfiles.size()-1;k++){
            sqlparser.sqlfilename = sqlfiles.sqlfiles.get(k).toString();
            ret = sqlparser.parse();
            if (ret == 0){
                searchVisitor sv = new searchVisitor();
                sqlparser.sqlstatements.accept(sv);
                assertTrue(!sv.isFound());
                if (sv.isFound()) {
                    System.out.println(sqlparser.sqlfilename);
                }
            }
        }
    }

}

class searchVisitor extends TParseTreeVisitor {
    private boolean found = false;

    public boolean isFound() {
        return found;
    }

    public searchVisitor(){
    }

    public void preVisit(TTable node){
        if (node.toString() == null){
            found = true;
        }
    }
}

