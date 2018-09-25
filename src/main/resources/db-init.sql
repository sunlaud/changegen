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



insert into department values( 10, 'ACCOUNTING', 'NEW YORK' );
insert into department values( 20, 'RESEARCH', 'DALLAS' );
insert into department values( 30, 'SALES', 'CHICAGO' );
insert into department values( 40, 'OPERATIONS', 'BOSTON' );

insert into employee values('John','KING','PRESIDENT','1981-11-17',10);
insert into employee values('Andy','BLAKE','MANAGER','1981-05-01',20);
insert into employee values('Kevin','CLARK','MANAGER','1981-09-09',30);
insert into employee values('Levi','JONES','MANAGER','1981-04-02',40);