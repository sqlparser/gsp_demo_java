package test;

import demos.joinConvert.JoinConverter;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testJoinConverter extends TestCase
{

	public void testCrossJoin( )
	{
		String sqltext = "SELECT e.employee_id,\n"
				+ "       e.last_name,\n"
				+ "       e.department_id\n"
				+ "FROM   employees e,\n"
				+ "       departments d";

		JoinConverter converter = new JoinConverter(sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 0 );
		//System.out.println(converter.getQuery( ).trim());
		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( "SELECT e.employee_id,\n"
						+ "       e.last_name,\n"
						+ "       e.department_id\n"
						+ "FROM   employees e\n"
						+ "cross join departments d" ) );
	}

	public void testInnerJoin( )
	{
		String sqltext = "SELECT e.employee_id,\n"
				+ "       e.last_name,\n"
				+ "       e.department_id\n"
				+ "FROM   employees e,\n"
				+ "       departments d\n"
				+ "WHERE  e.department_id = d.department_id";

		JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 0 );

		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( "SELECT e.employee_id,\n"
						+ "       e.last_name,\n"
						+ "       e.department_id\n"
						+ "FROM   employees e\n"
						+ "inner join departments d on e.department_id = d.department_id" ) );
		// System.out.println(converter.getQuery());

	}

	public void testTableNotJoined( )
	{
		String sqltext = "SELECT * \n"
				+ "FROM   summit.mstr m, \n"
				+ "       summit.alt_name altname, \n"
				+ "       smmtccon.ccn_user ccu, \n"
				+ "       uhelp.deg_coll deg \n"
				+ "WHERE  m.id = ? \n"
				+ "       AND m.id = altname.id(+) \n"
				+ "       AND m.id = ccu.id(+) \n"
				+ "       AND 'N' = ccu.admin(+) \n"
				+ "       AND altname.grad_name_ind(+) = '*'";

		JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 1 );
	}

	public void testOutterJoin1( )
	{
		String sqltext = "SELECT m.*,\n"
				+ "       altname.last_name  last_name_student,\n"
				+ "       altname.first_name first_name_student,\n"
				+ "       ccu.date_joined,\n"
				+ "       ccu.last_login,\n"
				+ "       ccu.photo_id,\n"
				+ "       ccu.last_updated\n"
				+ "FROM   summit.mstr m,\n"
				+ "       summit.alt_name altname,\n"
				+ "       smmtccon.ccn_user ccu\n"
				+ "WHERE  m.id =?\n"
				+ "       AND m.id = altname.id(+)\n"
				+ "       AND m.id = ccu.id(+)\n"
				+ "       AND altname.grad_name_ind(+) = '*'";

		JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 0 );
		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( "SELECT m.*,\n"
						+ "       altname.last_name  last_name_student,\n"
						+ "       altname.first_name first_name_student,\n"
						+ "       ccu.date_joined,\n"
						+ "       ccu.last_login,\n"
						+ "       ccu.photo_id,\n"
						+ "       ccu.last_updated\n"
						+ "FROM   summit.mstr m\n"
						+ "left outer join summit.alt_name altname on m.id = altname.id and altname.grad_name_ind = '*'\n"
						+ "left outer join smmtccon.ccn_user ccu on m.id = ccu.id\n"
						+ "WHERE  m.id =?" ) );
	}

	public void testOutterJoin2( )
	{
		String sqltext = "SELECT *\n"
				+ "FROM   smmtccon.ccn_menu menu,\n"
				+ "       smmtccon.ccn_page paget\n"
				+ "WHERE  ( menu.page_id = paget.page_id(+) )\n"
				+ "       AND ( NOT enabled = 'N' )\n"
				+ "       AND ( ( :parent_menu_id IS NULL\n"
				+ "               AND menu.parent_menu_id IS NULL )\n"
				+ "              OR ( menu.parent_menu_id = :parent_menu_id ) )\n"
				+ "ORDER  BY item_seq";

		JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 0 );

		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( "SELECT *\n"
						+ "FROM   smmtccon.ccn_menu menu\n"
						+ "left outer join smmtccon.ccn_page paget on menu.page_id = paget.page_id\n"
						+ "WHERE  ( NOT enabled = 'N' )\n"
						+ "       AND ( ( :parent_menu_id IS NULL\n"
						+ "               AND menu.parent_menu_id IS NULL )\n"
						+ "              OR ( menu.parent_menu_id = :parent_menu_id ) )\n"
						+ "ORDER  BY item_seq" ) );
	}

	public void testOutterJoin3( )
	{
		String sqltext = "SELECT ppp.project_name proj_name,\n"
				+ "       pr.role_title    user_role\n"
				+ "FROM   jboss_admin.portal_application_location pal,\n"
				+ "       jboss_admin.portal_user_app_location_role pualr,\n"
				+ "       jboss_admin.portal_user pu\n"
				+ "WHERE  ( pal.application_location_id = pualr.application_location_id\n"
				+ "         AND pu.jbp_uid = pualr.jbp_uid\n"
				+ "          )";

		JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 0 );

		// System.out.println(converter.getQuery().trim());
		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( "SELECT ppp.project_name proj_name,\n"
						+ "       pr.role_title    user_role\n"
						+ "FROM   jboss_admin.portal_application_location pal\n"
						+ "inner join jboss_admin.portal_user_app_location_role pualr on pal.application_location_id = pualr.application_location_id\n"
						+ "inner join jboss_admin.portal_user pu on pu.jbp_uid = pualr.jbp_uid" ) );
	}

	public void testOutterJoin4( )
	{
		String sqltext = "SELECT ppp.project_name proj_name,\n"
				+ "       pr.role_title    user_role\n"
				+ "FROM   jboss_admin.portal_application_location pal,\n"
				+ "       jboss_admin.portal_application pa,\n"
				+ "       jboss_admin.portal_user_app_location_role pualr,\n"
				+ "       jboss_admin.portal_location pl,\n"
				+ "       jboss_admin.portal_role pr,\n"
				+ "       jboss_admin.portal_pep_project ppp,\n"
				+ "       jboss_admin.portal_user pu\n"
				+ "WHERE  (pal.application_location_id = pualr.application_location_id\n"
				+ "         AND pu.jbp_uid = pualr.jbp_uid\n"
				+ "         AND pu.username = 'USERID')\n"
				+ "       AND pal.uidr_uid = pl.uidr_uid\n"
				+ "       AND pal.application_id = pa.application_id\n"
				+ "       AND pal.application_id = pr.application_id\n"
				+ "       AND pualr.role_id = pr.role_id\n"
				+ "       AND pualr.project_id = ppp.project_id\n"
				+ "       AND pa.application_id = 'APPID'";

		JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 0 );

		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( "SELECT ppp.project_name proj_name,\n"
						+ "       pr.role_title    user_role\n"
						+ "FROM   jboss_admin.portal_application_location pal\n"
						+ "inner join jboss_admin.portal_application pa on pal.application_id = pa.application_id\n"
						+ "inner join jboss_admin.portal_user_app_location_role pualr on pal.application_location_id = pualr.application_location_id\n"
						+ "inner join jboss_admin.portal_location pl on pal.uidr_uid = pl.uidr_uid\n"
						+ "inner join jboss_admin.portal_role pr on pal.application_id = pr.application_id and pualr.role_id = pr.role_id\n"
						+ "inner join jboss_admin.portal_pep_project ppp on pualr.project_id = ppp.project_id\n"
						+ "inner join jboss_admin.portal_user pu on pu.jbp_uid = pualr.jbp_uid\n"
						+ "WHERE  ( pu.username = 'USERID')\n"
						+ "       AND pa.application_id = 'APPID'" ) );

		// assertTrue(converter.getQuery().trim().equalsIgnoreCase(""));
	}

	public void testOutterJoin5( )
	{
		String sqltext = "select *\n"
				+ "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst,\n"
				+ "       ods_stg_pnb_stuf_pers_adr pas,\n"
				+ "       ods_stg_pnb_stuf_pers_nat nat,\n"
				+ "       ods_stg_pnb_stuf_adr adr,\n"
				+ "       ods_stg_pnb_stuf_np prs\n"
				+ "where  pas.soort_adres = lst.soort_adres\n"
				+ "       and prs.id(+) = nat.prs_id\n"
				+ "       and adr.id = pas.adr_id\n"
				+ "       and prs.id = pas.prs_id\n"
				+ "       and lst.persoonssoort = 'PERSOON'\n"
				+ "       and pas.einddatumrelatie is null";

		JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
		assertTrue( converter.convert( ) == 0 );

		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( ( "SELECT *\n"
						+ "FROM  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n"
						+ "inner join ods_stg_pnb_stuf_pers_adr pas ON pas.soort_adres = lst.soort_adres\n"
						+ "right outer join ods_stg_pnb_stuf_pers_nat nat ON prs.id = nat.prs_id\n"
						+ "inner join ods_stg_pnb_stuf_adr adr ON adr.id = pas.adr_id\n"
						+ "inner join ods_stg_pnb_stuf_np prs ON prs.id = pas.prs_id\n"
						+ "WHERE  lst.persoonssoort = 'PERSOON'\n"
						+ "       AND pas.einddatumrelatie IS NULL" ).toLowerCase( ) ) );

		// assertTrue(converter.getQuery().trim().equalsIgnoreCase(""));
	}

	public void testOutterJoin6( )
	{
		String sqltext = "select *\n"
				+ "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst,\n"
				+ "       ods_stg_pnb_stuf_np prs,\n"
				+ "       ods_stg_pnb_stuf_pers_adr pas,\n"
				+ "       ods_stg_pnb_stuf_pers_nat nat,\n"
				+ "       ods_stg_pnb_stuf_adr adr\n"
				+ "where  pas.soort_adres = lst.soort_adres\n"
				+ "       and prs.id(+) = nat.prs_id\n"
				+ "       and adr.id = pas.adr_id\n"
				+ "       and prs.id = pas.prs_id\n"
				+ "       and lst.persoonssoort = 'PERSOON'\n"
				+ "       and pas.einddatumrelatie is null\n";

		JoinConverter converter = new JoinConverter( sqltext ,EDbVendor.dbvoracle);
		assertTrue( converter.convert( ) == 0 );
//		System.out.println(converter.getQuery( ).trim());
//
//		System.out.println("SELECT *\n"
//				+ "FROM  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n"
//				+ "inner join ods_stg_pnb_stuf_pers_adr pas ON pas.soort_adres = lst.soort_adres\n"
//				+ "inner join ods_stg_pnb_stuf_np prs ON prs.id = pas.prs_id\n"
//				+ "right outer join ods_stg_pnb_stuf_pers_nat nat ON prs.id = nat.prs_id\n"
//				+ "inner join ods_stg_pnb_stuf_adr adr ON adr.id = pas.adr_id\n"
//				+ "WHERE   lst.persoonssoort = 'PERSOON'\n"
//				+ "       AND pas.einddatumrelatie IS NULL");

		assertTrue( converter.getQuery( )
				.trim( )
				.equalsIgnoreCase( "SELECT *\n"
						+ "FROM  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n"
						+ "inner join ods_stg_pnb_stuf_pers_adr pas ON pas.soort_adres = lst.soort_adres\n"
						+ "inner join ods_stg_pnb_stuf_np prs ON prs.id = pas.prs_id\n"
						+ "right outer join ods_stg_pnb_stuf_pers_nat nat ON prs.id = nat.prs_id\n"
						+ "inner join ods_stg_pnb_stuf_adr adr ON adr.id = pas.adr_id\n"
						+ "WHERE  lst.persoonssoort = 'PERSOON'\n"
						+ "       AND pas.einddatumrelatie IS NULL" ) );

		// assertTrue(converter.getQuery().trim().equalsIgnoreCase(""));

	}

    public void testLeftOutterJoin( )
    {

        String sqltext = "SELECT e.employee_id,\n" +
             "       e.last_name,\n" +
             "       e.department_id\n" +
             "FROM   employees e,\n" +
             "       departments d\n" +
             "WHERE  e.department_id = d.department_id(+)";

        JoinConverter converter = new JoinConverter( sqltext,EDbVendor.dbvoracle );
        assertTrue( converter.convert( ) == 0 );

       // System.out.println(converter.getQuery( ).trim());

        assertTrue( converter.getQuery( )
                .trim( )
                .equalsIgnoreCase("SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "left outer join departments d on e.department_id = d.department_id"));


    }

    public void testRightOutterJoin( )
    {

        String sqltext = "SELECT e.employee_id,\n" +
             "       e.last_name,\n" +
             "       e.department_id\n" +
             "FROM   employees e,\n" +
             "       departments d\n" +
             "WHERE  e.department_id(+) = d.department_id";

        JoinConverter converter = new JoinConverter( sqltext ,EDbVendor.dbvoracle);
        assertTrue( converter.convert( ) == 0 );

        //System.out.println(converter.getQuery( ).trim());

        assertTrue( converter.getQuery( )
                .trim( )
                .equalsIgnoreCase("SELECT e.employee_id,\n" +
                        "       e.last_name,\n" +
                        "       e.department_id\n" +
                        "FROM   employees e\n" +
                        "right outer join departments d on e.department_id = d.department_id"));


    }



}
