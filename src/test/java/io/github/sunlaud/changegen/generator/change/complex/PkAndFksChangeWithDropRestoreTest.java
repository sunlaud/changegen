package io.github.sunlaud.changegen.generator.change.complex;

import io.github.sunlaud.changegen.generator.Column;
import io.github.sunlaud.changegen.generator.Columns;
import io.github.sunlaud.changegen.generator.Key;
import io.github.sunlaud.changegen.generator.change.basic.DataTypeChange;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class PkAndFksChangeWithDropRestoreTest {

    @Test
    void name() {
        //GIVEN
        Column changedeColumn = new Column("users", "id");
        DataTypeChange dataTypeChange = new DataTypeChange(changedeColumn, "varchar(10)");
        Key pk = new Key("pk_users", new Columns(changedeColumn));
        Key fk1 = new Key("fk_phones_users", new Column("phones", "user_id"));
        Key fk2 = new Key("fk_addresses_users", new Column("addresses", "user_id"));

        PkChangeWithDropRestore pkDataTypeChange = new PkChangeWithDropRestore(dataTypeChange, pk);
        PkAndFksChangeWithDropRestore change = new PkAndFksChangeWithDropRestore(pkDataTypeChange, pk, Arrays.asList(fk1, fk2));

        //WHEN
        String generated = change.generateXml();

        //THEN
        System.out.println(generated);
    }
}