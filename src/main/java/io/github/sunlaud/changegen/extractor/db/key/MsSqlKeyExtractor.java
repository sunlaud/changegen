package io.github.sunlaud.changegen.extractor.db.key;

import org.skife.jdbi.v2.DBI;

public class MsSqlKeyExtractor extends AbstractKeyExtractor implements KeyExtractor {

    public MsSqlKeyExtractor(DBI dbi) {
        super(dbi);
    }

    @Override
    protected String getFkQuerySql() {
        return "SELECT " +
        "    ccu.constraint_name  " +
        "    ,ccu.table_name " +
        "    ,ccu.column_name " +
        "FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu " +
        "    INNER JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc " +
        "        ON ccu.CONSTRAINT_NAME = rc.CONSTRAINT_NAME  " +
        "    INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu  " +
        "        ON kcu.CONSTRAINT_NAME = rc.UNIQUE_CONSTRAINT_NAME " +
        "WHERE kcu.table_name = :tableName AND kcu.column_name = :columnName;";
    }

    @Override
    protected String getPkQuerySql() {
        return "SELECT tc.constraint_name, column_name, tc.table_name " +
                "FROM information_schema.constraint_column_usage cu " +
                "    INNER JOIN information_schema.table_constraints tc ON cu.constraint_name = tc.constraint_name " +
                "WHERE tc.table_name = :tableName AND constraint_type = 'PRIMARY KEY';";
    }
}
