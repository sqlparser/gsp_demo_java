select * from emp;

select ename, dname
from emp, dept
where emp.deptid = dept.id;
