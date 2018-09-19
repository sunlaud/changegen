package io.github.sunlaud.changegen.generator.change;

import io.github.sunlaud.changegen.generator.Column;

public interface SingleColumnChange extends Change {
    Column getColumn();
}
