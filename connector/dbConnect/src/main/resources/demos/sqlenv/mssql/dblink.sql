SELECT
   'unknown' as "owner",
   '['+ s.name + ']' as linkName,
   '['+ l.remote_name + ']' as userName,
   s.data_source as hostName
FROM
   sys.servers s
   left outer join
   sys.linked_logins l on s.server_id = l.server_id
WHERE
   s.is_linked = 1;
