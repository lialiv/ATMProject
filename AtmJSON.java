package com.example.amirl2.atmfinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AmirL2 on 12/05/2017.
 */

public class AtmJSON {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                jPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> atmsList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> atm = null;

            for (int i = 0; i < placesCount; i++) {
                try {
                    atm = getAtm((JSONObject) jPlaces.get(i));
                    atmsList.add(atm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return atmsList;
        }

        private HashMap<String, String> getAtm(JSONObject jAtm) {

            HashMap<String, String> atm = new HashMap<String, String>();
            String atmName = "-NA-";
            String vicinity = "-NA-";
            String rating = "Rating not available";
            boolean open_now = false;

            try {
                if (!jAtm.isNull("name")) {
                    atmName = jAtm.getString("name");
                }

                if (!jAtm.isNull("vicinity")) {
                    vicinity = jAtm.getString("vicinity");
                }

                if (!jAtm.isNull("rating")) {
                    rating = jAtm.getString("rating");
                }

                if (!jAtm.isNull("open_now")) {
                    open_now = jAtm.getBoolean("open_now");
                }

                atm.put("atm_name", atmName);
                atm.put("vicinity", vicinity);
                atm.put("rating", rating);
                atm.put("open_now", "" + open_now);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return atm;
        }
    }


