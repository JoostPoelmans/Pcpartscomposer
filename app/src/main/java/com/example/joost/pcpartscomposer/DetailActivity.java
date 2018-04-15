package com.example.joost.pcpartscomposer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.joost.pcpartscomposer.Data.PartDataContract;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private TextView mName;
    private TextView mPrice;
    private TextView mDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mName = (TextView) findViewById(R.id.tv_name);
        mPrice = (TextView) findViewById(R.id.tv_price);
        mDetails = (TextView) findViewById(R.id.tv_details);

        String temp = "dfsd";

        Intent intentThatStartedThisActivity = getIntent();
        final int defaultvalue = 0;
        if(intentThatStartedThisActivity.hasExtra("name") && intentThatStartedThisActivity.hasExtra("price")){
            String partName = intentThatStartedThisActivity.getStringExtra(PartDataContract.PartDataEntry.COLUMN_NAME);
            String partPrice = String.valueOf(intentThatStartedThisActivity.getIntExtra(PartDataContract.PartDataEntry.COLUMN_PRICE, defaultvalue));
            String partDetails = intentThatStartedThisActivity.getStringExtra(PartDataContract.PartDataEntry.COLUMN_DETAILS);

            mName.setText(partName);
            mPrice.setText("€" + partPrice);
            mDetails.setText(partDetails);

        }
    }
}
