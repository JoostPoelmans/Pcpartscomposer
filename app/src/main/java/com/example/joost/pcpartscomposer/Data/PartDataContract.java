package com.example.joost.pcpartscomposer.Data;

import android.provider.BaseColumns;

/**
 * Created by joost on 21/03/2018.
 */

public final class PartDataContract {

    private PartDataContract() {}

    public static final class PartDataEntry implements BaseColumns{
        public static final String TABLE_NAME = "Data";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DETAILS = "details";

    }
}
