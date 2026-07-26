## Description
Get join table,column and operator in the SQL where clause or join condition.

```sql
SELECT i_acctdt,
hscf.lncfno,
hscf.lncfbr AS lncfbr,
rcbl.loanbl AS overbl,
rcbl.matudt,
hscf.isdism AS inrvin,
hscf.osdism AS ovrvin
FROM dcam_cn_ln_hscf hscf, 
temp_dcag_am_cn_ln_rcbl_1 rcbl
WHERE 
 hscf.lncfno = rcbl.lncfno
AND hscf.matudt > rcbl.i_acctdt;
```

the output is
```
JoinTable1	JoinColumn1	JoinTable2	JoinColumn2	JoinType	JoinOperator
dcam_cn_ln_hscf	matudt		temp_dcag_am_cn_ln_rcbl_1	i_acctdt	where		>	
dcam_cn_ln_hscf	lncfno		temp_dcag_am_cn_ln_rcbl_1	lncfno		where		=	
```

## Usage
`joinRelationAnalyze <sql script file path> <output file path> [/t <database type>]`


