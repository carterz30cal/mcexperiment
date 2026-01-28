package com.carterz30cal.utils;

import java.util.ArrayList;
import java.util.List;

public class StringDescription {
    private final List<String> list;

    public StringDescription() {
        list = new ArrayList<>();
    }

    public StringDescription(String... strings) {
        list = new ArrayList<>();
        for (String string : strings) {
            list.add(StringUtils.colourString(string));
        }
    }

    public String GetRandomChoice() {
        return RandomUtils.getChoice(list);
    }

    public List<String> GetList() {
        return list;
    }
}
