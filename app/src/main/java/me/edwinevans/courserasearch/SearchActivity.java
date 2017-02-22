package me.edwinevans.courserasearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // TODO, remove!
        View testButton = findViewById(R.id.test_search);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearchRequested("ma");
            }
        });

        SearchView searchView = (SearchView)findViewById(R.id.search_view);
        searchView.setQueryHint("Search Catalog");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearchRequested(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void handleSearchRequested(String query) {
        Log.d(TAG, "Search for " + query);
        CourseraApiClient.getCourses(getApplicationContext(), query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "Search mResponse: " + response);

                final SearchListAdapter adapter = new SearchListAdapter(
                        getApplicationContext(), response);
                ListView lv = (ListView)findViewById(R.id.search_list);
                lv.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  JSONObject response) {
                Log.i(TAG, "Search call failed, statusCode: " + statusCode +
                        ", mResponse: " + response);
            }
        });
    }
}