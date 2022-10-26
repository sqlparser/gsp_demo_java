package singletonThread;

import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import junit.framework.TestCase;

class Thread1 extends Thread{

    public  void run(){
        TObjectName table1 = TObjectName.createObjectName (EDbVendor.dbvoracle, EDbObjectType.table);
        for(int i=0;i<100;i++){
            table1.setString("scott.emp"+i);
            //System.out.println (Thread.currentThread().getName()+": "+ i);
        }
    }
}

class Thread2 extends Thread{

    public  void run(){
        for(int i=0;i<100;i++){
            String condition = "scott.dept >"+i;
            TExpression expr = TGSqlParser.parseExpression(EDbVendor.dbvoracle,condition);
           // System.out.println (Thread.currentThread().getName()+": "+ expr.toString());
            if (i % 5 == 0){
                try{
                    Thread.currentThread().sleep(10);
                }catch (InterruptedException e){

                }
            }

        }
    }
}


public class testSingletonThread extends TestCase {

    public void test1(){
          Thread1 t1 = new Thread1();
          t1.setName("t1");
          t1.start();

        Thread2 t2 = new Thread2();
        t2.setName("t2");
        t2.start();

        for(int i=0;i<100;i++){
            TObjectName table1 = TObjectName.createObjectName (EDbVendor.dbvoracle, EDbObjectType.table);
            String newName = "scott.emp"+i;
            table1.setString(newName);
           // System.out.println (Thread.currentThread().getName()+": "+ i);
            assertTrue(table1.toString().equalsIgnoreCase(newName));
        }
    }

    public void test2(){
        Thread1 t1 = new Thread1();
        t1.setName("t1");
        t1.start();

        Thread2 t2 = new Thread2();
        t2.setName("t2");
        t2.start();

        for(int i=0;i<100;i++){
            String condition = "scott.emp >"+i;
            TExpression expr = TGSqlParser.parseExpression(EDbVendor.dbvoracle,condition);
            assertTrue(expr.toString().equalsIgnoreCase(condition));
           // System.out.println (Thread.currentThread().getName()+": "+ expr.toString());
            if (i % 5 == 0){
                try{
                    Thread.currentThread().sleep(10);
                }catch (InterruptedException e){

                }
            }
        }
    }


}

