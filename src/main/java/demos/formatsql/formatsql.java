package demos.formatsql;
/*
 * Date: 2010-11-9
 * Time: 9:38:43
 */


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.styleenums.TCaseOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;

import java.io.File;


public class formatsql {

    public static void main(String args[])
     {

         if (args.length != 1){
             System.out.println("Usage: java formatsql sqlfile.sql");
             return;
         }
         File file=new File(args[0]);
         if (!file.exists()){
             System.out.println("File not exists:"+args[0]);
             return;
         }

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
         sqlparser.sqlfilename = args[0];

//        sqlparser.sqltext = "insert into emp(empno,empnm,deptnm,sal) select empno, empnm, dptnm, sal from emp where empno=:empno;\n" +
//                "\n" +
//                "select empno, empnm from (select empno, empnm from emp)";

//         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

//         sqlparser.sqltext ="WITH upd AS (\n" +
//                 "  UPDATE employees SET sales_count = sales_count + 1 WHERE id =\n" +
//                 "    (SELECT sales_person FROM accounts WHERE name = 'Acme Corporation')\n" +
//                 "    RETURNING *\n" +
//                 ")\n" +
//                 "INSERT INTO employees_log SELECT *, current_timestamp FROM upd;";



        int ret = sqlparser.parse();
        if (ret == 0){
            GFmtOpt option = GFmtOptFactory.newInstance();
            //option.wsPaddingParenthesesInExpression = false;
            //option.selectColumnlistComma =     TLinefeedsCommaOption.LfBeforeComma;
            // umcomment next line generate formatted sql in html
            //option.outputFmt =  GOutputFmt.ofhtml;
           // option.removeComment = true;
            //option.caseFuncname = TCaseOption.CoNoChange;
            String result = FormatterFactory.pp(sqlparser, option);
            System.out.println(result);
        }else{
            System.out.println(sqlparser.getErrormessage());
        }
     }

}