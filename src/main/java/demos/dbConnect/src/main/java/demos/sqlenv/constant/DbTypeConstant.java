package demos.sqlenv.constant;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.constant.DbConstant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * CemB
 */
public class DbTypeConstant {

    public static final Map<String, EDbVendor> dbTypes = new HashMap<String, EDbVendor>();
    public static final Map<EDbVendor, String> vendorTypes = new HashMap<EDbVendor, String>();

    static {
        dbTypes.put(DbConstant.MySQL, EDbVendor.dbvmysql);
        dbTypes.put(DbConstant.Oracle, EDbVendor.dbvoracle);
        dbTypes.put(DbConstant.DB2, EDbVendor.dbvdb2);
        dbTypes.put(DbConstant.PostgreSQL, EDbVendor.dbvpostgresql);
        dbTypes.put(DbConstant.AzureSQL, EDbVendor.dbvazuresql);
        dbTypes.put(DbConstant.SQLServer, EDbVendor.dbvmssql);
        dbTypes.put(DbConstant.Snowflake, EDbVendor.dbvsnowflake);
        dbTypes.put(DbConstant.Couchbase, EDbVendor.dbvcouchbase);
        dbTypes.put(DbConstant.Dax, EDbVendor.dbvdax);
        dbTypes.put(DbConstant.Greenplum, EDbVendor.dbvgreenplum);
        dbTypes.put(DbConstant.Hive, EDbVendor.dbvhive);
        dbTypes.put(DbConstant.Impala, EDbVendor.dbvimpala);
        dbTypes.put(DbConstant.Informix, EDbVendor.dbvinformix);
        dbTypes.put(DbConstant.Mdx, EDbVendor.dbvmdx);
        dbTypes.put(DbConstant.Netezza, EDbVendor.dbvnetezza);
        dbTypes.put(DbConstant.Openedge, EDbVendor.dbvopenedge);
        dbTypes.put(DbConstant.Redshift, EDbVendor.dbvredshift);
        dbTypes.put(DbConstant.Sybase, EDbVendor.dbvsybase);
        dbTypes.put(DbConstant.Teradata, EDbVendor.dbvteradata);
        dbTypes.put(DbConstant.Vertica, EDbVendor.dbvvertica);
        dbTypes.put(DbConstant.Hana, EDbVendor.dbvhana);
        
        Iterator<String> dbTypeIter = dbTypes.keySet().iterator();
        while(dbTypeIter.hasNext()){
        	String dbType = dbTypeIter.next();
        	vendorTypes.put(dbTypes.get(dbType), dbType);
        }
    }
}
