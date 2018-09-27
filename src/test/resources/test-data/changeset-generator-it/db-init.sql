create table not_referenced(
    id int,
    code bigint,
    constraint pk_not_referenced primary key (id)
);

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
  department_id   int not null,
  temp_department_id int,
  constraint pk_employee primary key ( first_name, last_name ),
  constraint fk_employee_department foreign key ( department_id ) references department ( id ),
  constraint fk_employee_temp_department foreign key ( temp_department_id ) references department ( id )
);


create table salary(
  count_num int,
  emp_first_name   varchar(10),
  emp_last_name    varchar(10),
  salary           numeric,
  constraint pk_salary primary key ( count_num, emp_first_name, emp_last_name ),
  constraint fk_salary_employee foreign key ( emp_first_name, emp_last_name ) references employee ( first_name, last_name )
);

create table indexed_constrained(
    id int primary key,
    uni1 int,
    uni2 varchar(2),
    idx1 int,
    idx2 varchar(2),
    constraint uk_indexed_constrained unique (uni1, uni2),
);
create index idx_indexed_constrained on indexed_constrained (idx1, idx2);