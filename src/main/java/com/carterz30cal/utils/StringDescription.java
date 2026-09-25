package com.carterz30cal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class StringDescription {
    private final List<String> list;

    public StringDescription() {
        list = new ArrayList<>();
    }

    public StringDescription(String... strings) {
        list = Arrays.asList(strings);
    }

    public String random() {
        return RandomUtils.getChoice(list);
    }

    public List<String> list() {
        return list;
    }
}
