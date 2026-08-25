-- hr_mini: a small HR warehouse, small enough for the trial parser.
--
-- The other schema dumps under samples/dlineageBasic/ are real vendor scripts
-- and every one of them is over the trial parser's 10,000-byte limit, so they
-- return a licence error instead of lineage. This file exists so that "column
-- lineage across a schema" is something you can actually run from a plain
-- clone: DDL, views, a CTE, joins, aggregates and CASE WHEN, all resolvable
-- without a metadata.json.
--
--   java -jar target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar \
--        /f samples/dlineageBasic/oracle/hr_mini/hr_mini.sql /t oracle /json

-- ---------------------------------------------------------------- staging --

CREATE TABLE stg_department (
    department_id   NUMBER(6),
    department_name VARCHAR2(60),
    location_city   VARCHAR2(40),
    country_code    CHAR(2)
);

CREATE TABLE stg_employee (
    employee_id   NUMBER(6),
    first_name    VARCHAR2(40),
    last_name     VARCHAR2(40),
    email         VARCHAR2(80),
    hire_date     DATE,
    job_id        VARCHAR2(12),
    salary        NUMBER(10, 2),
    commission    NUMBER(6, 2),
    manager_id    NUMBER(6),
    department_id NUMBER(6)
);

CREATE TABLE stg_job_grade (
    job_id        VARCHAR2(12),
    job_title     VARCHAR2(60),
    lowest_salary NUMBER(10, 2),
    grade_code    CHAR(1)
);

-- ------------------------------------------------------------- warehouse --

CREATE TABLE dim_department (
    department_key  NUMBER(6),
    department_name VARCHAR2(60),
    location_city   VARCHAR2(40),
    country_code    CHAR(2),
    headcount       NUMBER(6)
);

CREATE TABLE dim_employee (
    employee_key  NUMBER(6),
    full_name     VARCHAR2(90),
    email         VARCHAR2(80),
    job_title     VARCHAR2(60),
    grade_code    CHAR(1),
    hire_date     DATE,
    manager_key   NUMBER(6),
    department_key NUMBER(6)
);

CREATE TABLE fact_compensation (
    employee_key    NUMBER(6),
    department_key  NUMBER(6),
    base_salary     NUMBER(12, 2),
    total_pay       NUMBER(12, 2),
    pay_band        VARCHAR2(12),
    above_job_floor NUMBER(1)
);

-- ------------------------------------------------------------------ view --

CREATE VIEW v_employee_enriched AS
SELECT e.employee_id,
       e.first_name || ' ' || e.last_name AS full_name,
       LOWER(e.email)                     AS email,
       e.hire_date,
       e.salary,
       NVL(e.commission, 0)               AS commission,
       e.manager_id,
       e.department_id,
       g.job_title,
       g.grade_code,
       g.lowest_salary
  FROM stg_employee e
  LEFT JOIN stg_job_grade g
    ON e.job_id = g.job_id;

-- ------------------------------------------------------------------- ETL --

INSERT INTO dim_department (department_key,
                            department_name,
                            location_city,
                            country_code,
                            headcount)
SELECT d.department_id,
       UPPER(d.department_name),
       d.location_city,
       d.country_code,
       COUNT(e.employee_id)
  FROM stg_department d
  LEFT JOIN stg_employee e
    ON e.department_id = d.department_id
 GROUP BY d.department_id,
          d.department_name,
          d.location_city,
          d.country_code;

INSERT INTO dim_employee (employee_key,
                          full_name,
                          email,
                          job_title,
                          grade_code,
                          hire_date,
                          manager_key,
                          department_key)
SELECT v.employee_id,
       v.full_name,
       v.email,
       NVL(v.job_title, 'UNASSIGNED'),
       v.grade_code,
       v.hire_date,
       v.manager_id,
       v.department_id
  FROM v_employee_enriched v;

-- A CTE, an aggregate over it, and a CASE WHEN, so the lineage has both
-- direct (fdd) and indirect (fdr) relations to look at.
INSERT INTO fact_compensation (employee_key,
                               department_key,
                               base_salary,
                               total_pay,
                               pay_band,
                               above_job_floor)
WITH paid AS (
    SELECT v.employee_id,
           v.department_id,
           v.salary,
           v.salary + v.commission AS gross_pay,
           v.lowest_salary
      FROM v_employee_enriched v
     WHERE v.hire_date < SYSDATE
)
SELECT p.employee_id,
       p.department_id,
       p.salary,
       SUM(p.gross_pay),
       CASE
           WHEN SUM(p.gross_pay) >= 15000 THEN 'EXECUTIVE'
           WHEN SUM(p.gross_pay) >= 7000  THEN 'SENIOR'
           ELSE 'STANDARD'
       END,
       CASE WHEN p.salary >= p.lowest_salary THEN 1 ELSE 0 END
  FROM paid p
 GROUP BY p.employee_id,
          p.department_id,
          p.salary,
          p.lowest_salary;

-- Headcount is recomputed from the dimension it was loaded alongside, so the
-- lineage shows a target column depending on the same table it updates.
UPDATE dim_department dd
   SET dd.headcount = (SELECT COUNT(de.employee_key)
                         FROM dim_employee de
                        WHERE de.department_key = dd.department_key);
