## Description
This tool take 2 parameters
- source sql directory
- out sql directory

scan all sql files under source sql directory recursively to find out
the sql file that includes Oracle plsql, and then save this sql file 
to the out sql directory with the same filename, if the file with 
the same filename already exists, then check the file size, if the 
file size is same, then skip to copy this file, otherwise, append
a number to the filename and save it.
 
use dbvOracle when create the TGSqlParser instance, and reuse this instance
for every input sql files to improve the performance.

if order to find the Oracle plsql correctly, you first need to understand
what java class are used to represent plsql such as create function, create procedure,
create trigger, create package and etc.




