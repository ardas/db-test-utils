package ua.ardas.db.checker;

import com.google.common.collect.Lists;

import java.util.List;

public class ExpectedData {
    public static final String NULL = "__NULL__";

    private final List<List<String>> data = Lists.newArrayList();

    public ExpectedData addRow(String... values) {
        data.add(Lists.newArrayList(values));
        return this;
    }

    public List<List<String>> getData() {
        return data;
    }
}
