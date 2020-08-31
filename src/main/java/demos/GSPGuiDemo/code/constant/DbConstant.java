package com.gudusoft.format.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * CemB
 */
public interface DbConstant {

    String MySQL = "mysql";
    String Oracle = "oracle";
    String DB2 = "db2";
    String PostgreSQL = "postgresql";
    String SQLServer = "mssql";
    String Snowflake = "snowflake";
    String Couchbase = "couchbase";
    String Dax = "dax";
    String Greenplum = "greenplum";
    String Hana = "hana";
    String Hive = "hive";
    String Impala = "impala";
    String Informix = "informix";
    String Mdx = "mdx";
    String Netezza = "netezza";
    String Openedge = "openedge";
    String Redshift = "redshift";
    String Sybase = "sybase";
    String Teradata = "teradata";
    String Vertica = "vertica";

    List<String> dbs = new ArrayList() {{
        add(Couchbase);
//        add(Dax);
        add(DB2);
        add(Greenplum);
        add(Hana);
        add(Hive);
        add(Impala);
        add(Informix);
        add(Mdx);
        add(MySQL);
        add(Netezza);
        add(Openedge);
        add(Oracle);
        add(PostgreSQL);
        add(Redshift);
        add(Snowflake);
        add(SQLServer);
        add(Sybase);
        add(Teradata);
        add(Vertica);
    }};

}
