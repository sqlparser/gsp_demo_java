package demos.scriptwriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class scriptwriter {
    public static void main(String args[]) {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle);
        sqlparser.sqltext = "             SELECT /*+ leading(dirs inpt ptbs dmpg) index(dirs IX_MNDHDIRS_03) use_nl(dmpg)  */ --(20140625 DBA 튜닝 반영)\n" +
                "                       nvl( (select /*+index_desc(icdr PK_pmihicdr ) */ icdr.ordtype  \n" +
                "                               from pam.pmihicdr icdr\n" +
                "                             where icdr.instcd= :1 \n" +
                "                                and icdr.pid=dirs.pid\n" +
                "                                and icdr.indd <=dirs.rsrvdd\n" +
                "                                and icdr.cretno >= dirs.cretno --당일 외래에서 입원한경우\n" +
                "                                and icdr.histstat='Y'\n" +
                "                                and icdr.mskind='M'\n" +
                "                                and icdr.indschacptstat not in ('D', 'T')\n" +
                "                                and dirs.rsrvdd between icdr.fromdd and icdr.todd\n" +
                "                                and rownum=1), 'O' )                                                                             AS histstat         -- 색상표시기준변경_20091001(ByJA)\n" +
                "                  , CASE WHEN ptbs.vipyn = 'Y'                 THEN 'V'     ELSE ''                             END AS vipyn             -- VIP여부\n" +
                "                  , CASE WHEN count(ptsp.pid) > 0              THEN '★'    ELSE ''                             END AS spcffactyn        -- 특이사항여부\n" +
                "                  , ptbs.pid                                                                                        AS pid               -- 등록번호\n" +
                "                  , ptbs.hngnm                                                                                      AS hngnm             -- 환자명\n" +
                "                  , ptbs.hngnm                                                                                      AS orgpatnm\n" +
                "                  , 'N'                                                                                                 AS dongchk\n" +
                "                  , ptbs.sex || '/' || com.fn_zz_getage ('', '', TO_CHAR(SYSDATE,'YYYYMMDD'), 'b', ptbs.brthdd)     AS sexage            -- 성별/나이\n" +
                "                  , CASE WHEN dirs.ioflag = 'I' THEN '입원'\n" +
                "                         WHEN dirs.ioflag = 'O' THEN '외래'\n" +
                "                         WHEN dirs.ioflag = 'E' THEN '응급' ELSE '' END                                             AS ioflag            -- 입원/외래구분\n" +
                "            \n" +
                "            \n" +
                "                  , SUBSTR(dirs.rsrvdd,1,4) || '-' || SUBSTR(dirs.rsrvdd,5,2) || '-' || SUBSTR(dirs.rsrvdd,7,2)  AS rsrvdd  -- 예약일자\n" +
                "                  , dirs.rsrvdd AS rsrvdd2\n" +
                "            \n" +
                "\t\t\t\t\t\t\n" +
                "                  , dirs.rsrvcnts                                                                                   AS rsrvcnts          -- 예약내용\n" +
                "                  , dirs.instcd                                                                                     AS instcd            -- 기관코드\n" +
                "                  , dirs.shiftflag                                                                                  AS shiftflag         -- shift구분\n" +
                "                  , dirs.dialseatflag                                                                               AS dialseatflag      -- 투석자리구분\n" +
                "                  , ( SELECT dcir.roomhospdialdd  \n" +
                "                       FROM emr.mnwmdcir dcir\n" +
                "                      WHERE dcir.pid     = dirs.pid\n" +
                "                          AND dcir.instcd = dirs.instcd\n" +
                "                          AND dcir.indd = (SELECT MAX(a.indd)\n" +
                "                                                     FROM emr.mnwmdcir a\n" +
                "                                                   WHERE a.pid     = dcir.pid\n" +
                "                                                       AND a.instcd = dcir.instcd                                                                \n" +
                "                                                        AND a.delflag = 'N'                                                            \n" +
                "                                                    )\n" +
                "                          AND rownum = 1\n" +
                "                    ) AS roomhospdialdd    -- 본원투석시작일자\n" +
                "                  , MAX((SELECT CASE WHEN count(distinct pid) > 0 THEN 'Y' ELSE '' END cnt\n" +
                "                              FROM emr.mndhihldi hldi\n" +
                "                            WHERE dirs.pid         = hldi.pid\n" +
                "                                AND dirs.rsrvdd    = hldi.rsrvdd\n" +
                "                                AND dirs.instcd     = hldi.instcd\n" +
                "                                AND dirs.hdcretno = hldi.hdcretno\n" +
                "                                AND dirs.dialflag   = hldi.dialflag\n" +
                "                                --임시저장일때 제외처리 2015.04.30 신장실 요청\n" +
                "                                AND hldi.signno    != 0 ))                                                     AS execyn            -- 시행여부\n" +
                "             \n" +
                "            \n" +
                "            \t\n" +
                "                  , (SELECT /*+ index_desc(hldi PK_MNDHIHLDI) */ SUBSTR(hldi.dialtodt,1,4) || '-' || SUBSTR(hldi.dialtodt,5,2) || '-' || SUBSTR(hldi.dialtodt,7,2) AS rectdialdd\n" +
                "                       FROM emr.mndhihldi hldi\n" +
                "                      WHERE dirs.pid      = hldi.pid\n" +
                "                        AND dirs.instcd   = hldi.instcd\n" +
                "                        AND dirs.dialflag = hldi.dialflag\n" +
                "                        AND dirs.hdcretno = hldi.hdcretno\n" +
                "                        AND hldi.dialtodt IS NOT NULL\n" +
                "                        AND hldi.rsrvdd  <= dirs.rsrvdd\n" +
                "                        AND ROWNUM = 1)\n" +
                "                   || ' (Hb:'\n" +
                "                   || (SELECT  /*+ index_desc(t IX_LLRHSPDO_12) */\n" +
                "                              SUBSTR(t.lastreptdt,1,4) || '-' ||\n" +
                "                              SUBSTR(t.lastreptdt,5,2) || '-' ||\n" +
                "                              SUBSTR(t.lastreptdt,7,2) || ' : ' || t.reptrslt\n" +
                "                         FROM lis.llrhspdo t\n" +
                "                        WHERE t.instcd = :2 \n" +
                "                          AND t.rsltflag = 'O'\n" +
                "                          AND t.rsltstat in ('4','5')\n" +
                "                          AND t.pid = dirs.pid\n" +
                "                          AND t.testcd = NVL( (SELECT K.CDID\n" +
                "                                                         FROM EMR.MNWMCODE K\n" +
                "                                                        WHERE K.INSTCD = :3 \n" +
                "                                                            AND K.CDGRUPID = 'T20'),'LHR102')\n" +
                "                          AND ROWNUM = 1)\n" +
                "                   || ' g/㎗)'\n" +
                "                   AS rectdialdd        -- 최근투석일\n" +
                "                   \n" +
                "            \n" +
                "\t\t\t\n" +
                "                  , dirs.ioflag                                                                                     AS ioflagvalue\n" +
                "                  , MAX(\n" +
                "                    (SELECT usernm\n" +
                "                      FROM com.zsumusrb usrb\n" +
                "                     WHERE dirs.rsrvdd BETWEEN usrb.userfromdd AND usrb.usertodd\n" +
                "                         AND inpt.instcd = usrb.dutinstcd\n" +
                "                         AND inpt.medispclid = usrb.userid ))               AS drnm\n" +
                "                  , MAX(\n" +
                "                    (SELECT code.cdnm\n" +
                "                       FROM com.zbcmcode code\n" +
                "                      WHERE code.cdgrupid   = 'P0008'\n" +
                "                        AND inpt.indd BETWEEN code.valifromdd AND code.valitodd\n" +
                "                        AND inpt.insukind = code.cdid))  AS insunm\n" +
                "                  , MAX(CASE WHEN dirs.ioflag in ( 'I','E')\n" +
                "\t\t\t\t\t\t                 THEN (CASE WHEN inpt.insukind = '11'\n" +
                "\t\t\t\t\t\t                            THEN (CASE WHEN inpt.SUPPKIND IN ('07', '45', '50', '51') THEN 'Y' ELSE '' END)\n" +
                "\t\t\t\t\t\t                            WHEN inpt.insukind = '21'\n" +
                "\t\t\t\t\t\t                            THEN (CASE WHEN inpt.SUPPKIND IN ('50', '51') THEN 'Y' ELSE '' END)\n" +
                "\t\t\t\t\t\t                            WHEN inpt.insukind = '22'\n" +
                "\t\t\t\t\t\t                            THEN (CASE WHEN inpt.SUPPKIND = '07' THEN 'Y' ELSE '' END)\n" +
                "\t\t\t\t\t\t                            ELSE ''\n" +
                "\t\t\t\t\t\t                            END)\n" +
                "\t\t\t\t\t\t                  END )   ||\n" +
                "                   MAX(\n" +
                "                    (SELECT case when (rooa.rareobstno = '-' and rooa.anohosprgstflag = 'N' and rooa.SIGNYN = 'N') then '' -- 신청서작성\n" +
                "                                 when (rooa.rareobstno = '-' and rooa.anohosprgstflag = 'N' and rooa.SIGNYN = 'Y') then 'Y' -- 신청서작성후 서명\n" +
                "                                 when (rooa.rareobstno != '-' and (to_char(sysdate,'YYYYMMDD') between rooa.rareobstfromdd and rooa.rareobsttodd)) then 'Y' -- 희귀난치 적용환자\n" +
                "                                 else ''\n" +
                "                                  end\n" +
                "                       FROM emr.mmohrooa rooa\n" +
                "                      WHERE rooa.pid         = inpt.pid\n" +
                "                        AND rooa.instcd       = inpt.instcd\n" +
                "                        --가접수로 발생되는 보조유형 문제로 조건 제거함 2015.11.18 sks\n" +
                "                        --AND rooa.insukind    = inpt.insukind\n" +
                "                        AND rooa.histcd       = 'O'\n" +
                "                        AND rooa.rareobstno <> '-'\n" +
                "                        AND rooa.rareobsttodd = (  SELECT max(rooa.rareobsttodd)\n" +
                "\t\t\t                                                        FROM emr.mmohrooa rooa\n" +
                "\t\t\t                                                       WHERE rooa.pid        = inpt.pid\n" +
                "\t\t\t                                                           AND rooa.instcd    = inpt.instcd\n" +
                "\t\t\t                                                          --가접수로 발생되는 보조유형 문제로 조건 제거함 2015.11.18 sks\n" +
                "\t\t\t                                                          --AND rooa.insukind = inpt.insukind\n" +
                "\t\t\t                                                           AND rooa.histcd    = 'O'\n" +
                "\t\t\t                                                           AND rooa.rareobstno <> '-' )\n" +
                "\t\t\t            AND rownum = 1\n" +
                "                    )) AS rooasignyn         -- 희귀등록사인여부\n" +
                "                    , MAX(CASE WHEN dirs.ioflag in ( 'I','E')\n" +
                "                              THEN CASE WHEN iprc.pid = inpt.pid and iprc.prcpcd in ( SELECT code.cdid\n" +
                "                                                                                                                    FROM emr.mnwmcode code\n" +
                "                                                                                                                   WHERE code.instcd   = :4 \n" +
                "                                                                                                                       AND code.cdgrupid = 'Z02'\n" +
                "                                                                                                                     \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t             --   AND code.grupdetldesc  = 'H'   -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t              AND code.grupdetldesc  in ( 'H','F')   -- 혈액투석,혈액관류 환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                                                                                                                                                                                                                                                                                                                                                                                \n" +
                "                                                                                                                ) THEN 'Y' ELSE '' END\n" +
                "                               END )                                                                               AS prcpflagdr           -- 투석처방여부(의사)                                           \n" +
                "                    , MAX(CASE WHEN dirs.ioflag in ( 'I','E')\n" +
                "                              THEN CASE WHEN iprc.pid = inpt.pid and iprc.prcpcd in ( SELECT code.cdid\n" +
                "                                                                                                                     FROM emr.mnwmcode code\n" +
                "                                                                                                                    WHERE code.instcd   = :5 \n" +
                "                                                                                                                        AND code.cdgrupid = 'Z02'\n" +
                "                                                                                                                     \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                AND code.supcdid  = '1'        -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                                                                                                                                                                                                                                                             \n" +
                "                                                                                                                   ) THEN 'Y' ELSE '' END\n" +
                "                               END )                                                                               AS prcpflag           -- 투석처방여부(간호)\n" +
                "                    , COUNT(CASE WHEN dirs.ioflag in ( 'I','E')\n" +
                "                              THEN CASE WHEN iprc.pid = inpt.pid and iprc.prcpcd in ( SELECT code.cdid\n" +
                "                                                                                                                    FROM emr.mnwmcode code\n" +
                "                                                                                                                   WHERE code.instcd   = :6 \n" +
                "                                                                                                                       AND code.cdgrupid = 'Z02'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t            \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t               AND code.supcdid  = '1'        -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                                                                                                                                                                                                                      \n" +
                "                                                                                                                  ) THEN 'Y' ELSE '' END\n" +
                "                               END )                                                                              AS prcpcdcnt           -- 투석재료대처방개수\n" +
                "                  \n" +
                "                  --2015.05.09 수납여부체크변경 \n" +
                "                   , MAX(CASE WHEN dirs.ioflag = 'E' THEN CASE WHEN COALESCE(inpt.dschdd, '99991231') = '99991231' THEN '' ELSE 'Y' END\n" +
                "                                     ELSE  '' END )  AS rcptflag           -- 수납여부\n" +
                "                   , MAX((\n" +
                "                        SELECT dept.depthngnm\n" +
                "                          FROM com.zsdddept dept\n" +
                "                         WHERE dept.deptcd = inpt.orddeptcd\n" +
                "                           AND inpt.indd BETWEEN dept.valifromdd AND dept.valitodd\n" +
                "                           AND dept.instcd = inpt.instcd ))                       AS orddeptnm      -- 진료과명\n" +
                "                   , null                                                                      AS prcsflag           -- 진행구분\n" +
                "                   , 'false'                                                                   AS acptyn             -- 외래간호접수여부\n" +
                "                   , null                                                                      AS orddd\n" +
                "                   , inpt.indd\n" +
                "                   , dirs.cretno                                                                                   AS cretno             -- 생성번호\n" +
                "                   , inpt.orddeptcd               \t\t\t\t \t\t\t\t\t\t\t\t\t\t\t   AS orddeptcd          -- 진료과코드\n" +
                "                   , 0                                                                                                AS acptseqno          -- 접수번호\n" +
                "                   , NVL(inpt.seqno,0)                                AS seqno              -- 일련번호\n" +
                "                   , CASE WHEN dirs.ioflag in ( 'I','E') THEN \n" +
                "                     (select dept.depthngnm\n" +
                "                          from com.zsdddept dept\n" +
                "                         where dept.deptcd = inpt.wardcd\n" +
                "                           and dept.instcd = inpt.instcd\n" +
                "                           and dept.instcd = :7 \n" +
                "                           AND inpt.indd   BETWEEN dept.valifromdd AND dept.valitodd\n" +
                "                          ) || '/' || inpt.roomcd ELSE '' END\t\t\t\t\t\t\tAS wardcd\t--병실코드추가\n" +
                "                   , inpt.roomcd \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAS roomcd\t--병실코드추가-kys-20091009\n" +
                "                   , CASE dirs.shiftflag WHEN '5' THEN '응급'\n" +
                "                                         \t\t  ELSE dirs.shiftflag END                                                      AS shiftnm\t\t--shift명 추가-kys-20091221\n" +
                "                   , dmpg.matnflag                                                                                   AS matnflag             -- 유지구분\n" +
                "                   , CASE WHEN dmpg.maintepatrgstdd = dirs.rsrvdd\n" +
                "                          THEN SUBSTR(dmpg.maintepatrgstdd,1,4) || '-' || SUBSTR(dmpg.maintepatrgstdd,5,2) || '-' || SUBSTR(dmpg.maintepatrgstdd,7,2) || '(당일)'\n" +
                "    \t\t\t\t\t\t          ELSE SUBSTR(dmpg.maintepatrgstdd,1,4) || '-' || SUBSTR(dmpg.maintepatrgstdd,5,2) || '-' || SUBSTR(dmpg.maintepatrgstdd,7,2)\n" +
                "               \t\t\t\t\t\t END                                                                                       AS maintepatrgstdd    -- 유지환자등록일자 (당일의뢰여부 건 표시 20120814_SCHProject_이정욱(lju485))\n" +
                "                   , CASE WHEN dmpg.maintepatrgstdd = to_char(SYSDATE,'YYYYMMDD') THEN '신환' ELSE ''  END                AS fnexamyn          -- 신환여부\n" +
                "                   , MAX(CASE WHEN dirs.ioflag = 'I'\n" +
                "                              THEN  (SELECT CASE WHEN count(distinct exip.pid) > 0 THEN 'Y'  ELSE '' END cnt\n" +
                "                                          FROM emr.mmodexip exip\n" +
                "                                         WHERE dirs.instcd     = exip.instcd\n" +
                "                                             AND dirs.pid        = exip.pid\n" +
                "                                             AND dirs.rsrvdd     = exip.prcpdd\n" +
                "                                             AND exip.execdd     = '00000000'\n" +
                "                                             AND exip.execprcphistcd = 'O'\n" +
                "                                             AND exip.prcpclscd IN ('A2', 'A4', 'A6'))\n" +
                "                               END ) AS execdrugyn   --20121113_jsk 약주사미시행여부 추가  */\n" +
                "                      ,MAX(CASE WHEN dirs.ioflag = 'I' AND inpt.suppkind IS NOT NULL --AND inpt.suppkind != '00'\n" +
                "                                        THEN COM.FN_ZB_GETCDNM('P0010',inpt.suppkind, inpt.indd)\n" +
                "                                        ELSE '-'\n" +
                "                               END )      AS suppkindnm --보조유형  2013.04.18 by ynh 컬럼 추가\n" +
                "                      , CASE WHEN inpt.dschdclrtyn = 'Y' THEN 'ⓓ' ELSE '' END AS indschstat\n" +
                "                      , CASE WHEN inpt.dschdclrtyn = 'Y' THEN '퇴원예정일:'||SUBSTR(inpt.dschdclrdt,0,4)||'/'||SUBSTR(inpt.dschdclrdt,5,2)||'/'||SUBSTR(inpt.dschdclrdt,7,2) ELSE '' END AS dschdclrdd  -- 퇴원예고일자\n" +
                "                      , ptbs.rrgstno1 AS rgstno\n" +
                "           FROM emr.mndhdirs dirs\n" +
                "                   , pam.pmcmptbs ptbs\n" +
                "                   , (SELECT a.* \n" +
                "                       FROM pam.pmcmptsp a\n" +
                "                           ,com.zsumusrb b\n" +
                "                      WHERE a.fstrgstrid = b.userid\n" +
                "                        AND a.histstat  = 'Y'\n" +
                "                        AND a.instcd    = :8 \n" +
                "                        AND b.userfromdd <= a.todd\n" +
                "                        AND b.usertodd   >= a.todd\n" +
                "                        AND ( a.opengrde = '2' OR ( a.opengrde = '3' AND b.dutunitcd = :9 ))\n" +
                "                    ) ptsp\n" +
                "                   ,pam.pmihinpt inpt\n" +
                "                   , (SELECT  /*+ index(a IX_mmohiprc_01) push_pred  leading (a b c) */ --(20140625 DBA 튜닝 반영)\n" +
                "                                  a.*, b.rcptdd                              \n" +
                "                       FROM emr.mmohiprc a\n" +
                "                               , emr.mmodexip b\n" +
                "                               , emr.mmbtprcd c\n" +
                "                     WHERE a.prcpdd    BETWEEN :10  AND :11  \n" +
                "                         AND a.prcpdd = b.prcpdd\n" +
                "                         AND a.prcpno = b.prcpno\n" +
                "                         AND a.prcphistno = b.prcphistno\n" +
                "                         AND a.instcd = b.instcd\n" +
                "                         AND a.pid     = b.pid   \n" +
                "                         AND a.prcpcd  = c.prcpcd\n" +
                "                         AND a.prcpdd >= c.fromdd\n" +
                "                         AND a.prcpdd <= c.todd\n" +
                "                         AND a.instcd  = c.instcd) iprc\n" +
                "\t\t\t      , emr.mndhdmpg dmpg\n" +
                "              WHERE dirs.instcd       = :12 \n" +
                "                 \n" +
                "\t            \n" +
                "\t                AND dirs.dialflag = 'H'          -- 혈액투석환자\n" +
                "\t            \n" +
                "                  AND dirs.rsrvdd BETWEEN :13  AND :14 \n" +
                "                  AND dirs.rsrvstatflag = 'Y'          -- 예약상태 정상\n" +
                "                  \n" +
                "                -- ptbs\n" +
                "                AND dirs.pid = ptbs.pid\n" +
                "                AND dirs.instcd = ptbs.instcd\n" +
                "                \n" +
                "                -- ptsp\n" +
                "                AND dirs.pid    = ptsp.pid(+)\n" +
                "                AND dirs.instcd = ptsp.instcd(+)\n" +
                "                AND dirs.rsrvdd BETWEEN ptsp.fromdd(+) AND ptsp.todd(+)\n" +
                "                \n" +
                "                -- inpt\n" +
                "                AND dirs.pid    = inpt.pid\n" +
                "                AND dirs.indd   = inpt.indd\n" +
                "                AND dirs.cretno = inpt.cretno\n" +
                "                AND dirs.instcd = inpt.instcd\n" +
                "                AND inpt.histstat = 'Y'\n" +
                "                \n" +
                "                -- iprc\n" +
                "                AND dirs.pid    = iprc.pid(+)\n" +
                "                AND dirs.indd   = iprc.orddd(+)\n" +
                "                AND dirs.cretno = iprc.cretno(+)\n" +
                "                AND dirs.instcd = iprc.instcd(+)\n" +
                "                AND dirs.rsrvdd = iprc.prcpdd(+)\n" +
                "                AND iprc.prnprcpflag(+)     != 'Y'\n" +
                "                AND iprc.tempprcpflag(+)     != 'Y'\n" +
                "                AND iprc.prepprcpflag(+)     != 'Y'\n" +
                "                AND iprc.hscttempprcpflag(+)  != 'Y'\n" +
                "                AND iprc.prcpsignflag(+)     != '3'\n" +
                "                AND iprc.selfdrugflag(+)      = 'N'\n" +
                "                AND iprc.prcphistcd(+)        = 'O'                                                        \n" +
                "                \n" +
                "                --dmpg    \n" +
                "               AND dirs.pid      = dmpg.pid\n" +
                "               AND dirs.instcd   = dmpg.instcd\n" +
                "               AND dirs.dialflag = dmpg.dialflag\n" +
                "               AND dmpg.instcd   = :15 \n" +
                "               AND dmpg.matnflag != 'X'\n" +
                "               AND dmpg.maintepatrgstdd = (SELECT /*++ index_desc( dmpg1 pk_mndhdmpg )*/\n" +
                "\t\t\t\t\t\t                                                 MAX(dmpg1.maintepatrgstdd)\n" +
                "\t\t\t \t\t\t\t\t\t\t                       FROM emr.mndhdmpg dmpg1\n" +
                "\t\t\t \t\t\t\t\t\t\t                      WHERE dmpg1.instcd       = :16 \n" +
                "\t\t\t \t\t\t\t\t\t\t                          AND dmpg1.pid          = dmpg.pid\n" +
                "\t\t\t \t\t\t\t\t\t\t                          AND dmpg1.instcd       = dmpg.instcd\n" +
                "\t\t\t \t\t\t\t\t\t\t                          AND dmpg1.dialflag     = dmpg.dialflag\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t   \t\t \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t            \t  AND dmpg1.dialflag     = 'H'          -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t           \t\t \n" +
                "\t\t\t \t\t\t\t\t\t\t                          AND dmpg1.matnflag != 'X'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t    \t  AND rownum=1\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t     )\n" +
                "            \n" +
                "                AND dirs.shiftflag    = :17 \n" +
                "             \n" +
                "        GROUP BY ptbs.vipyn\n" +
                "                  , ptbs.pid\n" +
                "                  , ptbs.hngnm\n" +
                "                  , ptbs.sex\n" +
                "                  , ptbs.brthdd\n" +
                "                  , dirs.ioflag\n" +
                "                  , dirs.rsrvdd\n" +
                "                  , dirs.rsrvcnts\n" +
                "                  , dirs.instcd\n" +
                "                  , dirs.shiftflag\n" +
                "                  , dirs.dialseatflag\n" +
                "                  , dirs.cretno\n" +
                "                  , dirs.pid      -- 색상표시기준변경_20091001(ByJA)\n" +
                "                  , dirs.orddd\n" +
                "                  , dirs.dialflag\n" +
                "                  , dirs.hdcretno\n" +
                "                  , inpt.indd\n" +
                "                  , inpt.seqno\n" +
                "                  , inpt.orddeptcd\n" +
                "                  , inpt.ordtype\n" +
                "                  , inpt.wardcd\n" +
                "                  , inpt.roomcd    --라벨출력을위해추가-kys-20091009\n" +
                "                  , inpt.instcd\n" +
                "                  , dmpg.pid\n" +
                "                  , dmpg.matnflag\n" +
                "                  , dmpg.maintepatrgstdd\n" +
                "                  , dmpg.instcd\n" +
                "                  , dmpg.dialflag\n" +
                "                  , inpt.instcd\n" +
                "                  , inpt.dschdclrtyn\n" +
                "                  , inpt.dschdclrdt\n" +
                "                  , ptbs.rrgstno1\n" +
                "          \n" +
                "      \n" +
                "      UNION     \n" +
                "             SELECT /*+ leading(dirs otpt ptbs dmpg) index(dirs IX_MNDHDIRS_03) use_nl(dmpg)  */ --(20140625 DBA 튜닝 반영)\n" +
                "                        nvl( (select /*+index_desc(icdr PK_pmihicdr ) */ icdr.ordtype\n" +
                "                                from pam.pmihicdr icdr\n" +
                "                              where icdr.instcd= :18 \n" +
                "                                 and icdr.pid=dirs.pid\n" +
                "                                 and icdr.indd <=dirs.rsrvdd\n" +
                "                                 and icdr.cretno >= dirs.cretno --당일 외래에서 입원한경우\n" +
                "                                 and icdr.histstat='Y'\n" +
                "                                 and icdr.mskind='M'\n" +
                "                                 and icdr.indschacptstat not in ('D', 'T')\n" +
                "                                 and dirs.rsrvdd between icdr.fromdd and icdr.todd\n" +
                "                                 and rownum=1\n" +
                "                              ) , otpt.ordtype)                                                                             AS histstat         -- 색상표시기준변경_20091001(ByJA)\n" +
                "                  , CASE WHEN ptbs.vipyn = 'Y'                 THEN 'V'     ELSE ''                             END AS vipyn             -- VIP여부\n" +
                "                  , CASE WHEN count(ptsp.pid) > 0           THEN '★'    ELSE ''                             END AS spcffactyn        -- 특이사항여부\n" +
                "                  , ptbs.pid                                                                                        AS pid               -- 등록번호\n" +
                "                  , ptbs.hngnm                                                                                      AS hngnm             -- 환자명\n" +
                "                  , ptbs.hngnm                                                                                      AS orgpatnm\n" +
                "                  , 'N'                                                                                                 AS dongchk\n" +
                "                  , ptbs.sex || '/' || com.fn_zz_getage ('', '', TO_CHAR(SYSDATE,'YYYYMMDD'), 'b', ptbs.brthdd)     AS sexage            -- 성별/나이\n" +
                "                  , CASE WHEN dirs.ioflag = 'I' THEN '입원'\n" +
                "                         WHEN dirs.ioflag = 'O' THEN '외래'\n" +
                "                         WHEN dirs.ioflag = 'E' THEN '응급' ELSE '' END                                             AS ioflag            -- 입원/외래구분\n" +
                "            \n" +
                "            \n" +
                "                  , SUBSTR(dirs.rsrvdd,1,4) || '-' || SUBSTR(dirs.rsrvdd,5,2) || '-' || SUBSTR(dirs.rsrvdd,7,2) AS rsrvdd  -- 예약일자\n" +
                "                  , dirs.rsrvdd AS rsrvdd2\n" +
                "            \n" +
                "\t\t\t\t\t\t\n" +
                "                  , dirs.rsrvcnts                                                                                   AS rsrvcnts          -- 예약내용\n" +
                "                  , dirs.instcd                                                                                     AS instcd            -- 기관코드\n" +
                "                  , dirs.shiftflag                                                                                  AS shiftflag         -- shift구분\n" +
                "                  , dirs.dialseatflag                                                                             AS dialseatflag      -- 투석자리구분\n" +
                "                  , ( SELECT dcir.roomhospdialdd  \n" +
                "                       FROM emr.mnwmdcir dcir\n" +
                "                      WHERE dcir.pid     = dirs.pid\n" +
                "                          AND dcir.instcd = dirs.instcd\n" +
                "                          AND dcir.indd = (SELECT MAX(a.indd)\n" +
                "                                                     FROM emr.mnwmdcir a\n" +
                "                                                   WHERE a.pid     = dcir.pid\n" +
                "                                                       AND a.instcd = dcir.instcd                                                                \n" +
                "                                                       AND a.delflag = 'N'                                                            \n" +
                "                                                    )\n" +
                "                          AND rownum = 1\n" +
                "                    ) AS roomhospdialdd    -- 본원투석시작일자\n" +
                "                  , MAX((SELECT CASE WHEN count(distinct pid) > 0 THEN 'Y' ELSE '' END cnt\n" +
                "                              FROM emr.mndhihldi hldi\n" +
                "                            WHERE dirs.pid         = hldi.pid\n" +
                "                                AND dirs.rsrvdd    = hldi.rsrvdd\n" +
                "                                AND dirs.instcd     = hldi.instcd\n" +
                "                                AND dirs.hdcretno = hldi.hdcretno\n" +
                "                                AND dirs.dialflag = hldi.dialflag\n" +
                "                                --임시저장일때 제외처리 2015.04.30 신장실 요청\n" +
                "                                AND hldi.signno   != 0 ))                                                     AS execyn            -- 시행여부\n" +
                "             \n" +
                "            \n" +
                "            \n" +
                "                 , (SELECT /*+ index_desc(hldi PK_MNDHIHLDI) */ SUBSTR(hldi.dialtodt,1,4) || '-' || SUBSTR(hldi.dialtodt,5,2) || '-' || SUBSTR(hldi.dialtodt,7,2) AS rectdialdd\n" +
                "                       FROM emr.mndhihldi hldi\n" +
                "                      WHERE dirs.pid      = hldi.pid\n" +
                "                        AND dirs.instcd   = hldi.instcd\n" +
                "                        AND dirs.dialflag = hldi.dialflag\n" +
                "                        AND dirs.hdcretno = hldi.hdcretno\n" +
                "                        AND hldi.dialtodt IS NOT NULL\n" +
                "                        AND hldi.rsrvdd  <= dirs.rsrvdd\n" +
                "                        AND ROWNUM = 1)\n" +
                "                   || ' (Hb:'\n" +
                "                   || (SELECT  /*+ index_desc(t IX_LLRHSPDO_12) */\n" +
                "                              SUBSTR(t.lastreptdt,1,4) || '-' ||\n" +
                "                              SUBSTR(t.lastreptdt,5,2) || '-' ||\n" +
                "                              SUBSTR(t.lastreptdt,7,2) || ' : ' || t.reptrslt\n" +
                "                         FROM lis.llrhspdo t\n" +
                "                        WHERE t.instcd = :19 \n" +
                "                          AND t.rsltflag = 'O'\n" +
                "                          AND t.rsltstat in ('4','5')\n" +
                "                          AND t.pid = dirs.pid\n" +
                "                          AND t.testcd = NVL( (SELECT K.CDID\n" +
                "                                                         FROM EMR.MNWMCODE K\n" +
                "                                                        WHERE K.INSTCD = :20 \n" +
                "                                                            AND K.CDGRUPID = 'T20'),'LHR102')\n" +
                "                          AND ROWNUM = 1)\n" +
                "                   || ' g/㎗)'\n" +
                "                   AS rectdialdd        -- 최근투석일\n" +
                "                   \n" +
                "            \n" +
                "\t\t\t\n" +
                "                  , dirs.ioflag                                                                                     AS ioflagvalue\n" +
                "                  , MAX(\n" +
                "                    (SELECT usernm\n" +
                "                      FROM com.zsumusrb usrb\n" +
                "                     WHERE otpt.orddd BETWEEN usrb.userfromdd AND usrb.usertodd\n" +
                "                         AND otpt.instcd = usrb.dutinstcd\n" +
                "                         AND otpt.orddrid = usrb.userid ))               AS drnm\n" +
                "                  , MAX(\n" +
                "                    (SELECT code.cdnm\n" +
                "                       FROM com.zbcmcode code\n" +
                "                      WHERE code.cdgrupid   = 'P0008'\n" +
                "                          AND otpt.orddd BETWEEN code.valifromdd AND code.valitodd\n" +
                "                          AND otpt.insukind = code.cdid))  AS insunm\n" +
                "                  , MAX(CASE WHEN dirs.ioflag = 'O'\n" +
                "                                         THEN (CASE WHEN otpt.insukind = '11'\n" +
                "                                                    THEN (CASE WHEN otpt.SUPPKIND IN ('07', '45', '50', '51') THEN 'Y' ELSE '' END)\n" +
                "                                                    WHEN otpt.insukind = '21'\n" +
                "                                                    THEN (CASE WHEN otpt.SUPPKIND IN ('50', '51') THEN 'Y' ELSE '' END)\n" +
                "                                                    WHEN otpt.insukind = '22'\n" +
                "                                                    THEN (CASE WHEN otpt.SUPPKIND = '07' THEN 'Y' ELSE '' END)\n" +
                "                                                    ELSE ''\n" +
                "                                                    END)                                      \n" +
                "                                          END )   ||\n" +
                "                   MAX(\n" +
                "                    (SELECT case when (rooa.rareobstno = '-' and rooa.anohosprgstflag = 'N' and rooa.SIGNYN = 'N') then '' -- 신청서작성\n" +
                "                                 when (rooa.rareobstno = '-' and rooa.anohosprgstflag = 'N' and rooa.SIGNYN = 'Y') then 'Y' -- 신청서작성후 서명\n" +
                "                                 when (rooa.rareobstno != '-' and (to_char(sysdate,'YYYYMMDD') between rooa.rareobstfromdd and rooa.rareobsttodd)) then 'Y' -- 희귀난치 적용환자\n" +
                "                                 else ''\n" +
                "                                  end\n" +
                "                       FROM emr.mmohrooa rooa\n" +
                "                      WHERE rooa.pid =  otpt.pid\n" +
                "                          AND rooa.instcd =  otpt.instcd\n" +
                "                          --가접수로 발생되는 보조유형 문제로 조건 제거함 2015.11.18 sks\n" +
                "                          --AND rooa.insukind = otpt.insukind\n" +
                "                          AND rooa.histcd       = 'O'\n" +
                "                          AND rooa.rareobstno <> '-'\n" +
                "                          AND rooa.rareobsttodd = (  SELECT max(rooa.rareobsttodd)\n" +
                "\t\t\t                                                          FROM emr.mmohrooa rooa\n" +
                "                                                                     WHERE rooa.pid =otpt.pid\n" +
                "                                                                         AND rooa.instcd =  otpt.instcd\n" +
                "                                                                         --가접수로 발생되는 보조유형 문제로 조건 제거함 2015.11.18 sks\n" +
                "                                                                         --AND rooa.insukind = otpt.insukind\n" +
                "\t\t\t                                                            AND rooa.histcd    = 'O' \n" +
                "\t\t\t                                                            AND rooa.rareobstno <> '-' )\n" +
                "\t\t\t              AND rownum = 1\n" +
                "                    )) AS rooasignyn         -- 희귀등록사인여부\n" +
                "                    , MAX(CASE WHEN dirs.ioflag = 'O'\n" +
                "                              THEN CASE WHEN oprc.pid = otpt.pid AND oprc.prcpcd in ( SELECT code.cdid\n" +
                "                                                                                                                       FROM emr.mnwmcode code\n" +
                "                                                                                                                      WHERE code.instcd   = :21 \n" +
                "                                                                                                                          AND code.cdgrupid = 'Z02'\n" +
                "\t\t\t\t\t\t\t\t                                                                                          \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                        --  AND code.grupdetldesc  = 'H'  -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                        AND code.grupdetldesc  in ( 'H','F')   -- 혈액투석,혈액관류 환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                     \n" +
                "                                                                                                                     ) THEN 'Y' ELSE '' END\n" +
                "                               END )                                                                                    AS prcpflagdr           -- 투석처방여부(의사)                                          \n" +
                "                    , MAX(CASE WHEN dirs.ioflag = 'O'\n" +
                "                              THEN CASE WHEN oprc.pid = otpt.pid AND oprc.prcpcd in (SELECT code.cdid\n" +
                "                                                                                                                        FROM emr.mnwmcode code\n" +
                "                                                                                                                       WHERE code.instcd  = :22 \n" +
                "                                                                                                                           AND code.cdgrupid = 'Z02'\n" +
                "                                                                                                                           \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                                                           AND code.supcdid  = '1'        -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                                                                                                                                                                                                                                                                                                  \n" +
                "                                                                                                                       ) THEN 'Y' ELSE '' END                             \n" +
                "                               END )                                                                               AS prcpflag           -- 투석처방여부(간호)\n" +
                "                    , COUNT(CASE WHEN dirs.ioflag = 'O'\n" +
                "                              THEN CASE WHEN oprc.pid = otpt.pid AND oprc.prcpcd in (SELECT code.cdid\n" +
                "                                                                                                                        FROM emr.mnwmcode code\n" +
                "                                                                                                                       WHERE code.instcd   = :23 \n" +
                "                                                                                                                           AND code.cdgrupid = 'Z02'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t           \t                                       \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                                                   AND code.supcdid  = '1'        -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                                                      \n" +
                "                                                                                                                         ) THEN 'Y' ELSE '' END                            \n" +
                "                               END )                                                                               AS prcpcdcnt           -- 투석재료대처방개수 \n" +
                "                   \n" +
                "                  --2015.05.09 수납여부체크변경 \n" +
                "                   , MAX(CASE WHEN dirs.ioflag = 'O' THEN CASE WHEN COALESCE(oprc.rcptdd, '00000000') != '00000000' and oprc.prcpcd in (SELECT code.cdid\n" +
                "                                                                                       FROM emr.mnwmcode code\n" +
                "                                                                                      WHERE code.instcd   =:24 \n" +
                "                                                                                          AND code.cdgrupid = 'Z02'\n" +
                "                                                                                          AND code.grupdetldesc not in ('H')) THEN 'Y' ELSE '' END                                                               \n" +
                "                                      END )                                                                               AS rcptflag           -- 수납여부   \n" +
                "                   , MAX((\n" +
                "                        SELECT dept.depthngnm\n" +
                "                          FROM com.zsdddept dept\n" +
                "                         WHERE dept.deptcd = otpt.orddeptcd\n" +
                "                             AND otpt.orddd BETWEEN dept.valifromdd AND dept.valitodd\n" +
                "                             AND dept.instcd = otpt.instcd ))                     AS orddeptnm      -- 진료과명\n" +
                "                   , MAX(CASE WHEN otpt.elbulbodstat = '3' THEN '보류'\n" +
                "                                     WHEN otpt.elbulbodstat = '2' THEN '완료'\n" +
                "                                     WHEN otpt.elbulbodstat = '1' THEN '대기'\n" +
                "                                     ELSE '' END)                                                                         AS prcsflag           -- 진행구분\n" +
                "                   , CASE WHEN otpt.elbulbodstat = '3' THEN 'true'\n" +
                "                             WHEN otpt.elbulbodstat = '2' THEN 'true'\n" +
                "                             WHEN otpt.elbulbodstat = '1' THEN 'true'\n" +
                "                             ELSE 'false' END                                                                         AS acptyn             -- 외래간호접수여부\n" +
                "                   , otpt.orddd                                                                      AS orddd\n" +
                "                   , null                                                                                 AS indd\n" +
                "                   , dirs.cretno                                                                      AS cretno             -- 생성번호\n" +
                "                   , otpt.orddeptcd               \t\t\t\t \t\t\t\t\t\t\t\t  AS orddeptcd          -- 진료과코드\n" +
                "                   , otpt.acptseqno                                                               AS acptseqno          -- 접수번호\n" +
                "                   , 0                                                                                    AS seqno              -- 일련번호\n" +
                "                   , null                                                                                AS wardcd\t--병실코드추가\n" +
                "                   , null\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t AS roomcd\t--병실코드추가-kys-20091009\n" +
                "                   , CASE dirs.shiftflag WHEN '5' THEN '응급'\n" +
                "                                         \t\t  ELSE dirs.shiftflag END                                           AS shiftnm\t\t--shift명 추가-kys-20091221\n" +
                "                   , dmpg.matnflag                                                                                   AS matnflag             -- 유지구분\n" +
                "                   , CASE WHEN dmpg.maintepatrgstdd = dirs.rsrvdd\n" +
                "                          THEN SUBSTR(dmpg.maintepatrgstdd,1,4) || '-' || SUBSTR(dmpg.maintepatrgstdd,5,2) || '-' || SUBSTR(dmpg.maintepatrgstdd,7,2) || '(당일)'\n" +
                "    \t\t\t\t\t\t          ELSE SUBSTR(dmpg.maintepatrgstdd,1,4) || '-' || SUBSTR(dmpg.maintepatrgstdd,5,2) || '-' || SUBSTR(dmpg.maintepatrgstdd,7,2)\n" +
                "               \t\t\t\t\t\t END                                                                                       AS maintepatrgstdd    -- 유지환자등록일자 (당일의뢰여부 건 표시 20120814_SCHProject_이정욱(lju485))\n" +
                "                   , CASE WHEN dmpg.maintepatrgstdd = to_char(SYSDATE,'YYYYMMDD') THEN '신환' ELSE ''  END                AS fnexamyn          -- 신환여부\n" +
                "                   , MAX(CASE WHEN dirs.ioflag = 'O'\n" +
                "                              THEN  (SELECT CASE WHEN count(distinct exop.pid) > 0 THEN 'Y' ELSE '' END cnt\n" +
                "                                       FROM emr.mmodexop exop\n" +
                "                                      WHERE dirs.instcd     = exop.instcd\n" +
                "                                        AND dirs.pid        = exop.pid\n" +
                "                                        AND dirs.rsrvdd     = exop.prcpdd\n" +
                "                                        AND exop.execdd     = '00000000'\n" +
                "                                        AND exop.execprcphistcd = 'O'\n" +
                "                                        AND exop.prcpclscd IN ('A2', 'A4', 'A6'))                            \n" +
                "                               END ) AS execdrugyn   --20121113_jsk 약주사미시행여부 추가  */\n" +
                "                      ,MAX(CASE WHEN dirs.ioflag = 'O' AND otpt.suppkind IS NOT NULL --AND otpt.suppkind != '00' \n" +
                "                                        THEN COM.FN_ZB_GETCDNM('P0010',otpt.suppkind, otpt.orddd)                         \n" +
                "                                        ELSE '-'\n" +
                "                               END )      AS suppkindnm --보조유형  2013.04.18 by ynh 컬럼 추가\n" +
                "                      , '' AS indschstat\n" +
                "                      , '' AS dschdclrdd\n" +
                "                      , ptbs.rrgstno1 AS rgstno\n" +
                "           FROM emr.mndhdirs dirs\n" +
                "                   , pam.pmcmptbs ptbs\n" +
                "                   , (SELECT a.* \n" +
                "                       FROM pam.pmcmptsp a\n" +
                "                                ,com.zsumusrb b\n" +
                "                      WHERE a.fstrgstrid = b.userid\n" +
                "                          AND a.histstat  = 'Y'\n" +
                "                          AND a.instcd    = :25 \n" +
                "                          AND b.userfromdd <= a.todd\n" +
                "                          AND b.usertodd   >= a.todd\n" +
                "                          AND ( a.opengrde = '2' OR ( a.opengrde = '3' AND b.dutunitcd = :26 ))\n" +
                "                    ) ptsp\n" +
                "                   ,pam.pmohotpt otpt   \n" +
                "                    ,(SELECT /*+ push_pred  leading(e f g) index(e  IX_MMOHOPRC_T2) */\n" +
                "                                  e.* , f.rcptdd\n" +
                "                       FROM emr.mmohoprc e\n" +
                "                               , emr.mmodexop f\n" +
                "                               , emr.mmbtprcd g\n" +
                "                     WHERE e.prcpdd    BETWEEN :27  AND :28   \n" +
                "                         AND e.prcpdd     = f.prcpdd\n" +
                "                         AND e.prcpno     = f.prcpno\n" +
                "                         AND e.prcphistno = f.prcphistno\n" +
                "                         AND e.instcd       = f.instcd\n" +
                "                         AND e.pid           = f.pid        \n" +
                "                         AND e.prcpcd     = g.prcpcd\n" +
                "                         AND e.prcpdd    >= g.fromdd\n" +
                "                         AND e.prcpdd   <= g.todd\n" +
                "                         AND e.instcd    = g.instcd\n" +
                "\t                     AND e.instcd =  :29 ) oprc\n" +
                "\t\t\t      , emr.mndhdmpg dmpg\n" +
                "              WHERE dirs.instcd       = :30 \n" +
                "                 \n" +
                "\t            \n" +
                "\t                AND dirs.dialflag = 'H'          -- 혈액투석환자\n" +
                "\t            \n" +
                "                  AND dirs.rsrvdd BETWEEN :31  AND :32 \n" +
                "                  AND dirs.rsrvstatflag = 'Y'          -- 예약상태 정상\n" +
                "                                \n" +
                "                -- ptbs\n" +
                "                AND dirs.pid = ptbs.pid\n" +
                "                AND dirs.instcd = ptbs.instcd\n" +
                "                \n" +
                "                -- ptsp\n" +
                "                AND dirs.pid    = ptsp.pid(+)\n" +
                "                AND dirs.instcd = ptsp.instcd(+)\n" +
                "                AND dirs.rsrvdd BETWEEN ptsp.fromdd(+) AND ptsp.todd(+)                \n" +
                "                \n" +
                "                -- otpt\n" +
                "                AND dirs.pid      = otpt.pid\n" +
                "                AND dirs.rsrvdd = otpt.orddd\n" +
                "                AND dirs.cretno = otpt.cretno\n" +
                "                AND dirs.instcd = otpt.instcd\n" +
                "                AND DECODE(otpt.histstat, 'R', 'Y', 'T', 'Y', 'N') = 'Y'\n" +
                "\n" +
                "                -- oprc\n" +
                "                AND dirs.pid    = oprc.pid(+)\n" +
                "                AND dirs.rsrvdd = oprc.orddd(+)\n" +
                "                AND dirs.cretno = oprc.cretno(+)\n" +
                "                AND dirs.instcd = oprc.instcd(+)\n" +
                "                AND oprc.prnprcpflag(+)      != 'Y'\n" +
                "                AND oprc.tempprcpflag(+)     != 'Y'\n" +
                "                AND oprc.prepprcpflag(+)    != 'Y'\n" +
                "                AND oprc.hscttempprcpflag(+)  != 'Y'\n" +
                "                AND oprc.prcpsignflag(+)     != '3'\n" +
                "                AND oprc.selfdrugflag(+)      = 'N'\n" +
                "                AND oprc.prcphistcd(+)        = 'O'                                        \n" +
                "             \n" +
                "              --dmpg\n" +
                "               AND dirs.ioflag       = 'O'               \n" +
                "               AND dirs.pid          = dmpg.pid\n" +
                "               AND dirs.instcd      = dmpg.instcd\n" +
                "               AND dirs.dialflag    = dmpg.dialflag\n" +
                "               AND dmpg.instcd   = :33 \n" +
                "               AND dmpg.matnflag != 'X'\n" +
                "               AND dmpg.maintepatrgstdd = (SELECT /*++ index_desc( dmpg1 pk_mndhdmpg )*/\n" +
                "\t\t\t\t\t\t                                                 MAX(dmpg1.maintepatrgstdd)\n" +
                "\t\t\t \t\t\t\t\t\t\t                       FROM emr.mndhdmpg dmpg1\n" +
                "\t\t\t \t\t\t\t\t\t\t                      WHERE dmpg1.instcd       = :34 \n" +
                "\t\t\t \t\t\t\t\t\t\t                          AND dmpg1.pid          = dmpg.pid\n" +
                "\t\t\t \t\t\t\t\t\t\t                          AND dmpg1.instcd       = dmpg.instcd\n" +
                "\t\t\t \t\t\t\t\t\t\t                          AND dmpg1.dialflag     = dmpg.dialflag\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t   \t\t \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t            \t  AND dmpg1.dialflag     = 'H'          -- 혈액투석환자\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t           \t\t \n" +
                "\t\t\t \t\t\t\t\t\t\t                         AND dmpg1.matnflag != 'X'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t    \tAND rownum=1\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t     )\n" +
                "\t\t\t   \n" +
                "                AND dirs.shiftflag    = :35 \n" +
                "             \n" +
                "        GROUP BY ptbs.vipyn\n" +
                "                  , ptbs.pid\n" +
                "                  , ptbs.hngnm\n" +
                "                  , ptbs.sex\n" +
                "                  , ptbs.brthdd\n" +
                "                  , dirs.ioflag\n" +
                "                  , dirs.rsrvdd\n" +
                "                  , dirs.rsrvcnts\n" +
                "                  , dirs.instcd\n" +
                "                  , dirs.shiftflag\n" +
                "                  , dirs.dialseatflag\n" +
                "                  , dirs.cretno\n" +
                "                  , dirs.pid      -- 색상표시기준변경_20091001(ByJA)\n" +
                "                  , dirs.orddd\n" +
                "                  , dirs.dialflag\n" +
                "\t\t\t\t  , dirs.hdcretno\n" +
                "                  , otpt.orddd\n" +
                "                  , otpt.cretno\n" +
                "                  , otpt.orddeptcd\n" +
                "                  , otpt.acptseqno\n" +
                "                  , otpt.elbulbodstat\n" +
                "                  , otpt.nursacptyn\n" +
                "                  , otpt.ordtype\n" +
                "                  , dmpg.pid\n" +
                "                  , dmpg.matnflag\n" +
                "                  , dmpg.maintepatrgstdd\n" +
                "                  , dmpg.dialflag\n" +
                "                  , dmpg.instcd\n" +
                "                  , ptbs.rrgstno1\n" +
                "       ORDER BY hngnm, pid ,rsrvdd\n" +
                "            \n" +
                "   /* himed/his/emr/dialmgr/dialpatmngtmgt/dao/sqls/dialpatmngtdao_sqls.xml getScheInfo */";

        sqlparser.parse( );

         System.out.println(sqlparser.sqlstatements.get(0).toScript());
        // assertTrue(verifyScript(EDbVendor.dbvoracle,sqlparser.sqlstatements.get(0).toString(),sqlparser.sqlstatements.get(0).toScript()));

    }
}
