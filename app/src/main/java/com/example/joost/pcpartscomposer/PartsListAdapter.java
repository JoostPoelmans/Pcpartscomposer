package com.example.joost.pcpartscomposer;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.joost.pcpartscomposer.Data.PartDataContract;
import com.example.joost.pcpartscomposer.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joost on 28/02/2018.
 */

public class PartsListAdapter extends RecyclerView.Adapter<PartsListAdapter.PartsListAdapterViewHolder> {
    // COMPLETED (23) Create a private string array called mWeatherData
    private String[] mPartsData;
    private String partResponse;
    private JSONArray partArray;
    private Cursor mCursor;
    private static final String TAG_HOLDER = "OnBindViewHolder";
    private static final String TAG_GET_ITEM_COUNT = "getItemCount";

    private final PartsListAdapterOnClickHandler mClickHandler;

    public interface PartsListAdapterOnClickHandler {
        void onListClick(String partData, String dataOfPart);
    }
    // COMPLETED (47) Create the default constructor (we will pass in parameters in a later lesson)
    public PartsListAdapter(PartsListAdapterOnClickHandler ClickHandler, Cursor cursor) {
        mClickHandler = ClickHandler;
        mCursor = cursor;
    }

    // COMPLETED (16) Create a class within ForecastAdapter called ForecastAdapterViewHolder
    // COMPLETED (17) Extend RecyclerView.ViewHolder
    /**
     * Cache of the children views for a forecast list item.
     */
    public class PartsListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Within ForecastAdapterViewHolder ///////////////////////////////////////////////////////
        // COMPLETED (18) Create a public final TextView variable called mWeatherTextView
        public final TextView mPartTextView;
        public final TextView mPartPriceTextView;


        public PartsListAdapterViewHolder(View view) {
            super(view);
            mPartTextView = (TextView) view.findViewById(R.id.tv_item_part);
            mPartPriceTextView = (TextView) view.findViewById(R.id.tv_item_price);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String partData = mPartsData[adapterPosition];
            String dataOfPart = null;
            try {
                JSONObject part = partArray.getJSONObject(adapterPosition);
                dataOfPart = part.toString();
                mClickHandler.onListClick(partData, dataOfPart);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        // Within PartListAdapterViewHolder ///////////////////////////////////////////////////////
    }

    // COMPLETED (24) Override onCreateViewHolder
    // COMPLETED (25) Within onCreateViewHolder, inflate the list item xml into a view
    // COMPLETED (26) Within onCreateViewHolder, return a new ForecastAdapterViewHolder with the above view passed in as a parameter
    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public PartsListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.part_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new PartsListAdapterViewHolder(view);
    }

    // COMPLETED (27) Override onBindViewHolder
    // COMPLETED (28) Set the text of the TextView to the weather for this list item's position
    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param partsListAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(PartsListAdapterViewHolder partsListAdapterViewHolder, int position) {


//        try {
//            String result = partResponse.replace("\r", "").replace("\n)", "");
//
//            partArray = new JSONArray(result);
//            //String[] parsedPartsData = new String[partArray.length()];
//            String[] mPartNameData = new String[partArray.length()];
//            String[] mPartsPrices = new String[partArray.length()];
//            for(int i = 0; i<partArray.length();i++ ){
//
//                JSONObject part = partArray.getJSONObject(i);
//                String partName = part.getString("name");
//                mPartNameData[i] = partName;
//                mPartsPrices[i] = part.getString("price");
//
//            }
//            mPartsData = mPartNameData;
//            String partData = mPartNameData[position];
//            partsListAdapterViewHolder.mPartTextView.setText(partData);
//            String partPrice = mPartsPrices[position];
//            partsListAdapterViewHolder.mPartPriceTextView.setText("€" + partPrice);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if(!mCursor.moveToPosition(position)) {
            Log.v(TAG_HOLDER, "no data or out of bounds of the cursor");
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_NAME));
        int price = mCursor.getInt(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_PRICE));
        //String details = mCursor.getString(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_DETAILS));
        partsListAdapterViewHolder.mPartTextView.setText(String.valueOf(name));
        partsListAdapterViewHolder.mPartPriceTextView.setText(String.valueOf(price));
    }





    // COMPLETED (29) Override getItemCount
    // COMPLETED (30) Return 0 if mWeatherData is null, or the size of mWeatherData if it is not null
    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        Log.v(TAG_GET_ITEM_COUNT, String.valueOf(mCursor.getCount()));
        return mCursor.getCount();

        /*if (null == partResponse) return 0;
        JSONArray partArray;
        try {
            partArray = new JSONArray(partResponse);
            return partArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
        */
    }

    // COMPLETED (31) Create a setWeatherData method that saves the weatherData to mWeatherData
    // COMPLETED (32) After you save mWeatherData, call notifyDataSetChanged
    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param PartsData The new weather data to be displayed.
     */
    public void setPartsData(String PartsData) {
        partResponse = PartsData;
        notifyDataSetChanged();
    }
}
