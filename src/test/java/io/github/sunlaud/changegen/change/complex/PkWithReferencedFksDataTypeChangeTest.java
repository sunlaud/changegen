package io.github.sunlaud.changegen.change.complex;

import io.github.sunlaud.changegen.Column;
import io.github.sunlaud.changegen.Columns;
import io.github.sunlaud.changegen.Key;
import io.github.sunlaud.changegen.change.basic.DataTypeChange;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class PkWithReferencedFksDataTypeChangeTest {

    @Test
    void name() {
        //GIVEN
        Column changedeColumn = new Column("users", "id");
        DataTypeChange dataTypeChange = new DataTypeChange(changedeColumn, "varchar(10)");
        Key pk = new Key("pk_users", new Columns(changedeColumn));
        Key fk1 = new Key("fk_phones_users", new Column("phones", "user_id"));
        Key fk2 = new Key("fk_addresses_users", new Column("addresses", "user_id"));

        PkWithReferencedFksDataTypeChange change = new PkWithReferencedFksDataTypeChange(dataTypeChange, pk, Arrays.asList(fk1, fk2));

        //WHEN
        String generated = change.generate();

        //THEN
        System.out.println(generated);
    }
}