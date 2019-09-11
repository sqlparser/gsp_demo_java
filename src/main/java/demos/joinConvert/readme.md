## Description
Convert Oracle/SQL Server proprietary joins to ANSI SQL compliant joins.
 
Oracle 
```sql
select *
from  ods_trf_pnb_stuf_lijst_adrsrt2 lst
		, ods_stg_pnb_stuf_pers_adr pas
		, ods_stg_pnb_stuf_pers_nat nat
		, ods_stg_pnb_stuf_adr adr
		, ods_stg_pnb_stuf_np prs
where 
	pas.soort_adres = lst.soort_adres
	and prs.id(+) = nat.prs_id
	and adr.id = pas.adr_id
	and prs.id = pas.prs_id
  and lst.persoonssoort = 'PERSOON'
   and pas.einddatumrelatie is null
``` 

Converted join:
```sql
select *    
  from ods_trf_pnb_stuf_lijst_adrsrt2 lst    
  join ods_stg_pnb_stuf_pers_adr pas    
    on (pas.soort_adres = lst.soort_adres)    
 right outer join ods_stg_pnb_stuf_pers_nat nat    
    on (prs.id = nat.prs_id)       
      join ods_stg_pnb_stuf_adr adr
        on (adr.id = pas.adr_id)
      join ods_stg_pnb_stuf_np prs 
        on (prs.id = pas.prs_id)
where lst.persoonssoort = 'PERSOON'
   and pas.einddatumrelatie is null
```

SQL Server
```sql
SELECT t1.*
FROM   table1 t1,
       table2 t2
WHERE  t1.f1 *= t2.f1
       AND t1.f2 > 10 
```   

Converted join:
```sql
SELECT t1.*
FROM   table1 t1
       LEFT JOIN table2 t2
         ON t1.f1 = t2.f1
WHERE  t1.f2 > 10 
```

## Usage
`java JoinConverter scriptfile [/t <database type>]`


