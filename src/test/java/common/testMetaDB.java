package common;
/*
 * Date: 13-3-14
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

class NullPtrExc implements IMetaDatabase
{
  public void runSql ()
  {
    String sql = "SELECT DISTINCT\r\n" +
      "  NULL AS M_CUS_AMO\r\n" +
      "FROM\r\n" +
      "  (SELECT *  --m_udf_ref\r\n" +
      "  FROM\r\n" +
      "    (SELECT T22.* ,\r\n" +
      "      rank() over (partition BY T22.M_TRADE_REF order by T22.M_VERSION DESC) AS ordering\r\n" +
      "    FROM mxcuser.TRN_EXT_DBF T22\r\n" +
      "    ORDER BY T22.M_VERSION DESC\r\n" +
      "    ) T33\r\n" +
      "  WHERE T33.ordering = '1'\r\n" +
      "  ) ext,\r\n" +
      "  mxcuser.TABLE#DATA#DEALCURR_DBF t\r\n" +
      "  WHERE ext.m_udf_ref = T.M_NB\r\n" +
      "" ;
    TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle) ;
    parser.sqltext = sql ;
    //System.out.println(sql);
    parser.setMetaDatabase (this) ;                                                  // if you don't do this everything works fine
    parser.parse () ;
  }

//  @Override
  public boolean checkColumn (String server, String database,String schema, String table, String column)
  {
    System.out.println ("checkcolumn") ;
    return false ;
  }
}

public class testMetaDB extends TestCase {

    public  void test1()
    {
      new NullPtrExc().runSql () ;
    }

}
