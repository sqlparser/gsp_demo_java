package demos.dlineage;

import gudusoft.sqlflow.ingester.exporter.SqlflowExporter;
import gudusoft.sqlflow.ingester.library.domain.DataSource;
import gudusoft.sqlflow.ingester.library.domain.DbVendor;
import gudusoft.sqlflow.ingester.library.inf.SqlflowMetadataExporterInf;
import gudusoft.sqlflow.ingester.library.jdbc.ConnectionProperties;
import gudusoft.sqlflow.ingester.library.jdbc.JDBCExecutor;
import gudusoft.sqlflow.ingester.library.result.Result;
import gudusoft.sqlflow.ingester.library.tools.FileUtils;
import gudusoft.dbadapter.TSQLDataSourceFactory;
import gudusoft.dbadapter.TSQLDataSource;
import gudusoft.sqlflow.ingester.library.tools.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlflowIngester {
    public static boolean export(String dbVendor, String[] args, String savePath){
        if(args == null){
            System.out.println("error args");
            return false;
        }
        DataSource source = new DataSource();
        for (int i=0; i< args.length-1; i++){
            String arg = args[i];
            if("-host".equalsIgnoreCase(arg)){
                source.setHostname(args[i+1]);
            }
            else if("-port".equalsIgnoreCase(arg)){
                source.setPort(args[i+1]);
            }
            else if("-db".toLowerCase().equals(arg.toLowerCase())){
                source.setDatabase(args[i+1]);
            }
            else if("-user".equalsIgnoreCase(arg)){
                source.setAccount(args[i+1]);
            }
            else if("-pwd".equalsIgnoreCase(arg)){
                source.setPassword(args[i+1]);
            }
            else if("-extractedDbsSchemas".equalsIgnoreCase(arg)){
                if(i<args.length-1){
                    source.setExtractedDbsSchemas(args[i+1]);
                }
            }
            else if("-extractedViews".equalsIgnoreCase(arg)){
                if(i<args.length-1){
                    source.setExtractedViews(args[i+1]);
                }
            }
            else if("-extractedStoredProcedures".equalsIgnoreCase(arg)){
                if(i<args.length-1){
                    source.setExtractedStoredProcedures(args[i+1]);
                }
            }
            else if("-excludedDbsSchemas".equalsIgnoreCase(arg)){
                if(i<args.length-1){
                    source.setExcludedDbsSchemas(args[i+1]);
                }
            }
        }
        if(Objects.isNull(dbVendor)){
            System.out.println("-dbVendor not exits");
            return false;
        }
        String name = dbVendor;
        String version = "";
        if(dbVendor.indexOf(":")>0){
            name = dbVendor.split(":")[0];
            version = dbVendor.split(":")[1];
        }
        DbVendor vendor = new DbVendor(name, version);
        source.setDbVendor(vendor);
        if(Objects.isNull(source.getHostname())){
            System.out.println("-host not exits");
            return false;
        }
        if(Objects.isNull(source.getPort())){
            System.out.println("-port not exits");
            return false;
        }
        if(Objects.isNull(source.getAccount())){
            System.out.println("-user not exits");
            return false;
        }
        SqlflowExporter exec = new SqlflowExporter();
        Result<String> result = exec.exporterMetadataString(source);
        if(result.getCode() != 200){
            System.out.println(result.getMsg());
            return false;
        }
        else{
            if(Objects.isNull(savePath)){
                savePath = System.getProperty("java.class.path");
            }
            if(!savePath.endsWith(".json")){
                if(!savePath.endsWith("/")){
                    savePath = savePath + "/";
                }
                savePath = savePath + "metadata.json";
            }
            try {
                FileUtils.SaveFile(savePath, result.getData());
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}