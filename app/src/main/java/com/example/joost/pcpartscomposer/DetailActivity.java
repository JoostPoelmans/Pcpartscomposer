package com.example.joost.pcpartscomposer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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

        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            String dataOfPart = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            String partName = null;

            String partPrice = null;
            String partDetails = null;
            try {
                JSONObject dataOfPartInJSON = new JSONObject(dataOfPart);
                partName = dataOfPartInJSON.getString("name");
                partPrice = dataOfPartInJSON.getString("price");
                partDetails = dataOfPartInJSON.getString("details");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mName.setText(partName);
            mPrice.setText(partPrice);
            mDetails.setText(partDetails);

        }
    }
}
