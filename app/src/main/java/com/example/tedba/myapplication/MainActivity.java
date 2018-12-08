package com.example.tedba.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;


public class MainActivity extends AppCompatActivity {
    /* Default Logging Tag. */
    private static final String TAG = "Antonym Finder";
    /* Request Queue. */
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        /*
        Button handlers for antonym search
        */
        final Button search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "finding antonyms");
                findAntonyms();
            }
        });
    }

    /* Method that takes the returned JsonArray from the api and parses it to return a String containing the antonyms for a given word*/
    public String getAntonyms(final JSONArray input) throws JSONException{
        if (input.getJSONObject(0).getJSONObject("meta").getJSONArray("ants").length() == 0) {
            return "Word has no antonyms";
        }
        String toReturn = "";
        JSONArray antArray = input
                .getJSONObject(0)
                .getJSONArray("def")
                .getJSONObject(0)
                .getJSONArray("sseq")
                .getJSONArray(0)
                .getJSONArray(0)
                .getJSONObject(1)
                .getJSONArray("ant_list")
                .getJSONArray(0);
        for (int i = 0; i < antArray.length(); i++) {
            toReturn += antArray.getJSONObject(i).getString("wd");
            if (i < antArray.length() - 1) {
                toReturn += ", ";
            }
        }
        return toReturn;
    }

    /**
     * Method that calls the api and uses the String returned by getAntonyms to display it on the UI
     */
    public void findAntonyms() {
        final EditText enter_word = findViewById(R.id.enter_word);
        final TextView displayAntonym = findViewById(R.id.displayAntonym);
        String wordSearched = enter_word.getText().toString();
        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    "https://www.dictionaryapi.com/api/v3/references/thesaurus/json/"
                            + wordSearched + "?key=e267e542-c713-405e-9de9-916e1f134128",
                    null,
                    new Response.Listener<JSONArray>() {
                        public void onResponse(final JSONArray response) {
                            try {
                                Log.d(TAG, response.toString());
                                String antonyms = getAntonyms(response);
                                displayAntonym.setText(antonyms);
                            } catch (JSONException exception) {
                                displayAntonym.setText("The word you entered does not exist");
                            }
                        }
                    }, new Response.ErrorListener() {
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            });
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
