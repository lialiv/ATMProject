package com.example.amirl2.atmfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ATMDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_details);

        TextView tvName = (TextView) findViewById(R.id.tv_name);
        TextView tvVicinity = (TextView) findViewById(R.id.tv_vicinity);
        TextView tvRating = (TextView) findViewById(R.id.tv_rating);
        TextView tvOpenNow = (TextView) findViewById(R.id.tv_open_now);

        String atmName = null;
        String atmVicinity = null;
        String atmRating = null;
        String atmOpeningHours = null;
        boolean atmOpenNow = true;

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            atmName = extras.getString(MainActivity.EXTRA_ATM_NAME);
            atmVicinity = extras.getString(MainActivity.EXTRA_ATM_VICINITY);
            atmRating = extras.getString(MainActivity.EXTRA_ATM_RATING);
            atmOpenNow = extras.getBoolean(MainActivity.EXTRA_ATM_OPEN_NOW);
        }
        
        tvName.setText(atmName);
        tvVicinity.setText(atmVicinity);
        tvRating.setText("Rating: "+ atmRating);
        if (atmOpenNow)
            tvOpenNow.setText("Open Now");
        else if (!atmOpenNow)
            tvOpenNow.setText("Closed!");
    }
}
