package com.example.joost.pcpartscomposer.Data;

import android.provider.BaseColumns;

/**
 * Created by joost on 21/03/2018.
 */

public final class PartDataContract {

    private FeedReaderContract() {}

    public static class FeedEntry implements BaseColumns{
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DETAILS = "details";

    }
}
