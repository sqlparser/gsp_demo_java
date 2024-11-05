## Description
Get join columns in the SQL where clause.

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
<matrixResults>
  <matrixResult leftColumn="lncfno" leftTable="dcam_cn_ln_hscf" rightColumn="lncfno" rightTable="temp_dcag_am_cn_ln_rcbl_1"/>
  <matrixResult leftColumn="matudt" leftTable="dcam_cn_ln_hscf" rightColumn="i_acctdt" rightTable="temp_dcag_am_cn_ln_rcbl_1"/>
</matrixResults>
```

## Usage
`java ColumnMatrix scriptfile [/o <output file path>]`


## See also
* [joinRelationAnalyze](../joinRelationAnalyze)
