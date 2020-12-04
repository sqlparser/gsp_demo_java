package common;
/*
 * Date: 11-8-11
 */

import gudusoft.gsqlparser.EDbVendor;
//import gudusoft.gsqlparser.crypto.*;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class testLicense extends TestCase {


    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT * from dual;";
       int i = sqlparser.parse();
        assertTrue(sqlparser.parse() == 0);

//        if (i == 0){
//          System.out.println("username: "+sqlparser.getUserName());
//          System.out.println("machineid: "+sqlparser.getMachineId());
//        }else{
//          System.out.println("error message:"+sqlparser.getErrormessage());
//        }
    }

    public void test2(){

      //HardwareBinder hb = new HardwareBinder();
    //  hb.setUseHwAddress();
     // System.out.print("license system not test for machine id:"+hb.getMachineIdString());
      //assertTrue(hb.getMachineIdString().equalsIgnoreCase("516c1e52-cc5b-37ef-81c8-08d8fc2dee9e"));
    }

    public void testMd5hash(){

        boolean ignoreMachineId = false;
        byte[] bytesOfMessage=null;
        String username = "james wang";
        String machineid = "516c1e52-cc5b-37ef-81c8-08d8fc2dee9e";
        String teststr = "I love sql pretty printer, yeah!"+username.toLowerCase();
        if(!ignoreMachineId){
            teststr += machineid.toLowerCase();
        }
        try {
            bytesOfMessage = teststr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        byte[] digest = md.digest(bytesOfMessage);

        //System.out.println(HardwareBinder.getHex(digest));

    }


}

