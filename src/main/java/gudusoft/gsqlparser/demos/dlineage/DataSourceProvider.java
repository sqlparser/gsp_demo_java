package gsp.demos.dlineage;

import gudusoft.dbadapter.*;
import gudusoft.gsqlparser.EDbVendor;

public class DataSourceProvider {
    public static TSQLDataSource createSQLDataSource(EDbVendor vendor, Class<?> driver, String jdbc, String account, String password, String schema) {
        try {
            if (vendor == EDbVendor.dbvoracle) {
                TOracleSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas(schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvmssql) {
                TMssqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas("*/" + schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvpostgresql) {
                TPostgreSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas("*/" + schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvgreenplum) {
                TGreenplumSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas("*/" + schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvredshift) {
                TRedshiftSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas("*/" + schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvmysql) {
                TMysqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas("*/" + schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvnetezza) {
                TNetezzaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas("*/" + schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvsnowflake) {
                TSnowflakeSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas("*/" + schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvteradata) {
                TTeradataSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                return datasource;
            }
            if (vendor == EDbVendor.dbvhive) {
                THiveMetadataDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                return datasource;
            }
            if (vendor == EDbVendor.dbvimpala) {
                TImpalaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
                        password);
                return datasource;
            }
        } catch (Exception e) {
            System.err.println("Connect datasource failed. " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static TSQLDataSource createSQLDataSource(EDbVendor vendor, String jdbc, String user, String password,
                                                     String schema) {
        try {
            if (vendor == EDbVendor.dbvoracle) {
                TOracleSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas(schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvmssql) {
                TMssqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvpostgresql) {
                TPostgreSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvgreenplum) {
                TGreenplumSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvredshift) {
                TRedshiftSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvmysql) {
                TMysqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvnetezza) {
                TNetezzaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvsnowflake) {
                TSnowflakeSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvteradata) {
                TTeradataSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    datasource.setExtractedDatabases(database);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvhive) {
                THiveMetadataDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    datasource.setExtractedDatabases(database);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvimpala) {
                TImpalaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
                String database = datasource.getDatabase();
                if (database != null) {
                    datasource.setExtractedDatabases(database);
                }
                return datasource;
            }
        } catch (Exception e) {
            System.err.println("Connect datasource failed. " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static TSQLDataSource createSQLDataSource(EDbVendor vendor, String host, String port, String user,
                                                     String password, String database, String schema) {
        try {
            if (vendor == EDbVendor.dbvoracle) {
                TOracleSQLDataSource datasource = new TOracleSQLDataSource(host, port, user, password, database);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas(schema);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvmssql) {
                TMssqlSQLDataSource datasource = new TMssqlSQLDataSource(host, port, user, password);
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvpostgresql) {
                TPostgreSQLDataSource datasource = new TPostgreSQLDataSource(host, port, user, password, database);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas(database + "/" + schema);
                } else {
                    datasource.setExtractedDbsSchemas(database);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvgreenplum) {
                TGreenplumSQLDataSource datasource = new TGreenplumSQLDataSource(host, port, user, password);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas(database + "/" + schema);
                } else {
                    datasource.setExtractedDbsSchemas(database);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvhive) {
                THiveMetadataDataSource datasource = new THiveMetadataDataSource(host, port, user, password, database);
                if (database != null) {
                    datasource.setExtractedDatabases(database);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvimpala) {
                TImpalaSQLDataSource datasource = new TImpalaSQLDataSource(host, port, user, password, database);
                if (database != null) {
                    datasource.setExtractedDatabases(database);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvredshift) {
                TRedshiftSQLDataSource datasource = new TRedshiftSQLDataSource(host, port, user, password, database);
                if (schema != null) {
                    datasource.setExtractedDbsSchemas(database + "/" + schema);
                } else {
                    datasource.setExtractedDbsSchemas(database);
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvmysql) {
                TMysqlSQLDataSource datasource = new TMysqlSQLDataSource(host, port, user, password);
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvnetezza) {
                TNetezzaSQLDataSource datasource = new TNetezzaSQLDataSource(host, port, user, password, database);
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvsnowflake) {
                TSnowflakeSQLDataSource datasource = new TSnowflakeSQLDataSource(host, port, user, password);
                if (database != null) {
                    if (schema != null) {
                        datasource.setExtractedDbsSchemas(database + "/" + schema);
                    } else {
                        datasource.setExtractedDbsSchemas(database);
                    }
                }
                return datasource;
            }
            if (vendor == EDbVendor.dbvteradata) {
                TTeradataSQLDataSource datasource = new TTeradataSQLDataSource(host, port, user, password, database);
                if (database != null) {
                    datasource.setExtractedDatabases(database);
                }
                return datasource;
            }
        } catch (Exception e) {
            System.err.println("Connect datasource failed. " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
