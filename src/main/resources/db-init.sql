create table department(
  id int,
  name  varchar(14),
  location    varchar(13),
  constraint pk_department primary key ( id )
);

create table employee(
  first_name   varchar(10),
  last_name    varchar(10),
  job      varchar(9),
  hire_date date,
  department_id   int,
  constraint pk_employee primary key ( first_name, last_name ),
  constraint fk_employee_department foreign key ( department_id ) references department ( id )
);


create table salary(
  id numeric,
  emp_first_name   varchar(10),
  emp_last_name    varchar(10),
  salary            numeric,
  constraint pk_salary primary key ( id ),
  constraint fk_salary_employee foreign key ( emp_first_name, emp_last_name ) references employee ( first_name, last_name )
);