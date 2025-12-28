package ua.ardas.db.checker;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class CheckerExpectedData {
    public static final String NULL = "__NULL__";

    @Getter
    private final List<List<String>> data = Lists.newArrayList();

    public CheckerExpectedData addRow(String... values) {
        data.add(Lists.newArrayList(values));
        return this;
    }

}
