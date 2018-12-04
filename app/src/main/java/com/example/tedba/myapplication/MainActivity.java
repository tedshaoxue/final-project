package com.example.tedba.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;


public class MainActivity extends AppCompatActivity {
    /**
     * Add try-catch block to api call
     * Do something with requestQueue
     * Hope it works
     */

    /* Default Logging Tag */
    private static final String TAG = "Antonym Finder";
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        /*
        Button handlers for antonym search
        */
        final Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "finding antonyms");
                findAntonyms();
            }
        });
    }

    /* Temporary Method*/
    public String getAntonyms(final JSONObject input) throws JSONException{
        if (input.getJSONObject("meta").getJSONArray("ants").length() == 0) {
            return "Word has no antonyms";
        }
        String toReturn = "";
        JSONArray antArray = input
                .getJSONObject("meta")
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
     * method to use api and find the antonyms.
     */
    public void findAntonyms() {
        final EditText enter_word = (EditText) findViewById(R.id.enter_word);
        final TextView displayAntonym = (TextView) findViewById(R.id.displayAntonym);
        String wordSearched = enter_word.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://www.dictionaryapi.com/api/v3/references/thesaurus/json/" + wordSearched +
                        "?key=e267e542-c713-405e-9de9-916e1f134128",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        String antonyms;
                        try {
                            antonyms = getAntonyms(response);
                        } catch (JSONException exception) {
                            antonyms = "Invalid Argument";
                        }
                        Log.d(TAG, response.toString());
                        displayAntonym.setText(antonyms);
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(final VolleyError error) {
                Log.w(TAG, error.toString());
            }
        });
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
