package demos.antiSQLInjection;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class antiSQLInjection  {

    public static String getFileContent( File file )
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
            byte[] tmp = new byte[4096];
            InputStream is = new BufferedInputStream( new FileInputStream( file ) );
            while ( true )
            {
                int r = is.read( tmp );
                if ( r == -1 )
                    break;
                out.write( tmp, 0, r );
            }
            byte[] bytes = out.toByteArray( );
            is.close( );
            out.close( );
            String content = new String( bytes );
            return content.trim( );
        }
        catch ( IOException e )
        {
            e.printStackTrace( );
        }
        return null;
    }

    public static void main(String args[])
     {
		if ( args.length == 0 )
		{
			System.out.println( "Usage: java antiSQLInjection [/f scriptfile] [/t <database type>]" );
			System.out.println( "/f: Option, specify the sql file path to analyze anti SQL injection." );
			System.out.println( "/t: Option, set the database type, the default type is mysql" );
			// Console.Read();
			return;
		}
		
         File sqlFile = null;
         List<String> argList = Arrays.asList(args);

         if ( argList.indexOf( "/f" ) != -1
                 && argList.size( ) > argList.indexOf( "/f" ) + 1 )
         {
             sqlFile = new File( args[argList.indexOf( "/f" ) + 1] );
             if ( !sqlFile.exists( ) || !sqlFile.isFile( ) )
             {
                 System.out.println( sqlFile + " is not a valid file." );
                 return;
             }
         }
         
		if ( sqlFile == null )
		{
			System.out.println( "Please specify a sql file path to analyze anti SQL injection." );
			return;
		}

         EDbVendor dbvendor = EDbVendor.dbvmysql;


         int index = argList.indexOf( "/t" );

         if ( index != -1 && args.length > index + 1 )
         {
             dbvendor = TGSqlParser.getDBVendorByName(args[index + 1]);
         }

         System.out.println("Selected SQL dialect: "+dbvendor.toString());

         TAntiSQLInjection anti = new TAntiSQLInjection(dbvendor);
         if (anti.isInjected(getFileContent(sqlFile))){
            System.out.println("SQL injected found:");
            for(int i=0;i<anti.getSqlInjections().size();i++){
                System.out.println("type: "+anti.getSqlInjections().get(i).getType()+", description: "+ anti.getSqlInjections().get(i).getDescription());
            }
         }else {
             System.out.println("Not injected");
         }

     }

}