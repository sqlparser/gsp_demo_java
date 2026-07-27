/* IMPORTANT: If you modify this file, rename it to cmd.sql (so it won't be overwritten when you upgrade lionfish).
Only cmd.sql will be used when both exist.*/
select
	'"'||dbl.owner||'"' as owner,
	'"'||dbl.db_link||'"' as linkName,
	'"'||dbl.username||'"' as userName,
	dbl.host as hostName
from ALL_DB_LINKS dbl