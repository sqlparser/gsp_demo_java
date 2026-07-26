package demos.listGSPInfo;

/**
 * This demo illustrate how to list information of general sql parser library.
 * You can download more demos from official site: http://www.sqlparser.com 
 */

import gudusoft.gsqlparser.*;

import java.util.*;


public class listGSPInfo {

public static void main(String args[])
    {
        long t = System.currentTimeMillis();

        EDbVendor vendor = EDbVendor.dbvoracle;
        TGSqlParser sqlparser = null;
        int ret,success=0,failed=0;
        ArrayList <String>  dbList = new ArrayList<String>();

        List<EDbVendor> somethingList =
                new ArrayList<EDbVendor>(EnumSet.allOf(EDbVendor.class));
        for(int i=0;i<somethingList.size();i++){
            EDbVendor dbVendor = somethingList.get(i);
            //System.out.println(dbVendor);
            try {
                sqlparser = new TGSqlParser(dbVendor);
                sqlparser.sqltext = "select 2 from t";
                ret = sqlparser.parse();
                if (!((dbVendor == EDbVendor.dbvgeneric)||(dbVendor == EDbVendor.dbvaccess)
                        ||(dbVendor == EDbVendor.dbvfirebird)||(dbVendor == EDbVendor.dbvansi)||(dbVendor == EDbVendor.dbvodbc))){
                    success++;
                    dbList.add(dbVendor.toString());
                    //db = db + dbVendor.toString() + ",";
                }
            }catch (NullPointerException e){
                failed++;
               // System.out.println(e.toString());
            }catch (ExceptionInInitializerError e){
                failed++;
                //System.out.println(e.toString());
            }catch (Exception   e){
                failed++;
               // System.out.println(e.toString());
            }catch (Error   e){
                failed++;
                //System.out.println(e.toString());
            }
        }

       // System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
        Collections.sort(dbList, new SortIgnoreCase());
        System.out.println(String.format("Version: %s,Release date: %s, Full version:%s",TBaseType.versionid, TBaseType.releaseDate, String.valueOf(TBaseType.full_edition)));
        System.out.println(String.format("Supported DBs: %d/%d,%s",success,somethingList.size(), dbList.toString()));
    }


}

class SortIgnoreCase implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
}