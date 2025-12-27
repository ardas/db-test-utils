package ua.ardas.db.checker;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class ExpectedData {
    public static final String NULL = "__NULL__";

    @Getter
    private final List<List<String>> data = Lists.newArrayList();

    public ExpectedData addRow(String... values) {
        data.add(Lists.newArrayList(values));
        return this;
    }

}
