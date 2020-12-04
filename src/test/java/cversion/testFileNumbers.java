package cversion;
/*
 * Date: 12-2-20
 */

import common.SqlFileList;
import junit.framework.TestCase;

public class testFileNumbers extends TestCase {

    public void test0(){
        String oracledir = "c:\\prg\\gsqlparser\\Test\\TestCases\\oracle";
        SqlFileList sqlfiles = new SqlFileList(oracledir,true);
        //System.out.print(sqlfiles.sqlfiles.size());
        for(int i=0;i<sqlfiles.sqlfiles.size();i++){
          //  System.out.printf("%s\n",sqlfiles.sqlfiles.get(i).toString());
        }
    }

}
