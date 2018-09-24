create table department(
  id numeric,
  name  varchar(14),
  location    varchar(13),
  constraint pk_department primary key ( id )
);

create table employee(
  id    numeric,
  name    varchar(10),
  job      varchar(9),
  hire_date date,
  department_id   numeric,
  constraint pk_employee primary key ( id ),
  constraint fk_employee_department foreign key ( department_id ) references department ( id )
);

insert into department values( 10, 'ACCOUNTING', 'NEW YORK' );
insert into department values( 20, 'RESEARCH', 'DALLAS' );
insert into department values( 30, 'SALES', 'CHICAGO' );
insert into department values( 40, 'OPERATIONS', 'BOSTON' );

insert into employee values(7839,'KING','PRESIDENT','1981-11-17',10);
insert into employee values(7698,'BLAKE','MANAGER','1981-05-01',20);
insert into employee values(7782,'CLARK','MANAGER','1981-09-09',30);
insert into employee values(7566,'JONES','MANAGER','1981-04-02',40);