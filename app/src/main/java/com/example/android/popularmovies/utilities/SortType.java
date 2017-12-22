package com.example.android.popularmovies.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alfianlosari on 06/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public enum SortType {
    POPULAR(0), TOP_RATED(1), FAVORITES(2);

    private int value;
    private static Map map = new HashMap<>();

    private SortType(int value) {
        this.value = value;
    }

    static {
        for (SortType sortType: SortType.values()) {
            map.put(sortType.value, sortType);
        }
    }

    public static SortType valueOf(int pageType) {
        return (SortType) map.get(pageType);
    }

    public int getValue() {
        return value;
    }


};


