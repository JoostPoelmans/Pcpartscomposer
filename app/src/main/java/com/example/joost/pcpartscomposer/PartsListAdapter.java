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

    private Cursor mCursor;
    private static final String TAG_HOLDER = "OnBindViewHolder";
    private static final String TAG_GET_ITEM_COUNT = "getItemCount";

    private final PartsListAdapterOnClickHandler mClickHandler;

    public interface PartsListAdapterOnClickHandler {
        void onListClick(int adapterPosition, Cursor mCursor);
    }

    public PartsListAdapter(PartsListAdapterOnClickHandler ClickHandler, Cursor cursor) {
        mClickHandler = ClickHandler;
        mCursor = cursor;
    }

    public class PartsListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Within PartListAdapterViewHolder ///////////////////////////////////////////////////////
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
            mClickHandler.onListClick(adapterPosition, mCursor);
        }
        // Within PartListAdapterViewHolder ///////////////////////////////////////////////////////
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new PartsListAdapterViewHolder that holds the View for each list item
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


        if(!mCursor.moveToPosition(position)) {
            Log.v(TAG_HOLDER, "no data or out of bounds of the cursor");
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_NAME));
        int price = mCursor.getInt(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_PRICE));
        //String details = mCursor.getString(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_DETAILS));
        partsListAdapterViewHolder.mPartTextView.setText(String.valueOf(name));
        partsListAdapterViewHolder.mPartPriceTextView.setText("€" + String.valueOf(price));
    }





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
    }

    public void setPartsData(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public void swapCursor(Cursor newCursor) {
        // Inside, check if the current cursor is not null, and close it if so
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        // Update the local mCursor to be equal to  newCursor
        mCursor = newCursor;
        // Check if the newCursor is not null, and call this.notifyDataSetChanged() if so
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
}
