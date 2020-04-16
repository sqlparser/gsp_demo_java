package demos.checksyntax;

/**
 * This demo illustrate how to use general sql parser to check syntax of SQL script.
 * You can download more demos from official site: http://www.sqlparser.com 
 */

import gudusoft.gsqlparser.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class checksyntax {

    private static  File[] listFiles( File sqlFiles )
    {
        List<File> children = new ArrayList<File>( );
        if ( sqlFiles != null )
            listFiles( sqlFiles, children );
        return children.toArray( new File[0] );
    }

    private static void listFiles( File rootFile, List<File> children )
    {
        if ( rootFile.isFile( ) )
            children.add( rootFile );
        else
        {
            File[] files = rootFile.listFiles( );
            for ( int i = 0; i < files.length; i++ )
            {
                listFiles( files[i], children );
            }
        }
    }


     public static void main(String args[])
    {
        long t = System.currentTimeMillis();

        if (args.length < 1){
            System.out.println("Usage: java checksyntax [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>]");
            return;
        }


        File sqlFiles = null;

        List<String> argList = Arrays.asList(args);

        if ( argList.indexOf( "/f" ) != -1
                && argList.size( ) > argList.indexOf( "/f" ) + 1 )
        {
            sqlFiles = new File( args[argList.indexOf( "/f" ) + 1] );
            if ( !sqlFiles.exists( ) || !sqlFiles.isFile( ) )
            {
                System.out.println( sqlFiles + " is not a valid file." );
                return;
            }
        }
        else if ( argList.indexOf( "/d" ) != -1
                && argList.size( ) > argList.indexOf( "/d" ) + 1 )
        {
            sqlFiles = new File( args[argList.indexOf( "/d" ) + 1] );
            if ( !sqlFiles.exists( ) || !sqlFiles.isDirectory( ) )
            {
                System.out.println( sqlFiles + " is not a valid directory." );
                return;
            }
        }
        else
        {
            System.out.println( "Please specify a sql file path or directory path." );
            return;
        }

        EDbVendor vendor = EDbVendor.dbvoracle;

        int index = argList.indexOf( "/t" );

        if ( index != -1 && args.length > index + 1 )
        {

            vendor = TGSqlParser.getDBVendorByName(args[index + 1]);

        }


        File[] children = listFiles( sqlFiles );

        int total_sql_files = 0, error_sql_flies=0;

        TGSqlParser sqlparser = new TGSqlParser(vendor);

        for ( int i = 0; i < children.length; i++ )
        {
            File child = children[i];
            if ( child.isDirectory( ) )
                continue;
            //String content = SQLUtil.getFileContent(child);
            if (child.getAbsolutePath().endsWith(".sql")){
                total_sql_files++;
                sqlparser.sqlfilename  = child.getAbsolutePath();

                int ret = sqlparser.parse();
                if (ret != 0){
                    error_sql_flies++;
                    System.out.println(child.getAbsolutePath()+ System.getProperty("line.separator")+sqlparser.getErrormessage());
                    System.out.println("");
                }
            }
        }


        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) +",file processed: "+total_sql_files+",syntax errors:"+error_sql_flies );
        if (error_sql_flies > 0){
            System.out.println("Selected SQL dialect: "+vendor.toString());
        }
    }
}