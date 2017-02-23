package me.edwinevans.courserasearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    // Limit to request with each call. The total number of courses plus
    // specializations may be more
    private static final int LIMIT = 10;

    private EndlessRecyclerViewScrollListener mScrollListener;
    private String mSearchString;
    private int mPagingNext = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLinearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        mScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView = (RecyclerView)findViewById(R.id.search_list);
        mRecyclerView.addOnScrollListener(mScrollListener);

        // TODO, remove!
        View testButton = findViewById(R.id.test_search);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchString = "machine learning";
                searchRequest(0);
            }
        });

        SearchView searchView = (SearchView)findViewById(R.id.search_view);
        searchView.setQueryHint(getString(R.string.search_catalog));
        searchView.setQuery("machine learning", false); // TESTING

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchString = query;
                mScrollListener.resetState();
                searchRequest(0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void loadNextDataFromApi(int page) {
        // TODO! if more data
        Log.d(TAG, "Request more data");
        searchRequest(mPagingNext);
    }

    private void searchRequest(final int pagingNext) {
        Log.d(TAG, "Request a search. Search string: " + mSearchString + ", Starting from: " + pagingNext);
        CourseraApiClient.getCourses(getApplicationContext(), mSearchString, pagingNext,
                (pagingNext == 0 ? LIMIT : 2), // testing
                new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "Search mResponse: " + response);
                JSONObject paging = response.optJSONObject("paging");
                if (paging != null) {
                    mPagingNext = paging.optInt("next", mPagingNext);
                    Log.d(TAG, "  Total: " + paging.optString("total") + ", Next: " + mPagingNext);
                }
                if (pagingNext == 0) {
                    final SearchListAdapter adapter = new SearchListAdapter(
                            getApplicationContext(), response);
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                }
                else {
                    final SearchListAdapter adapter = (SearchListAdapter)mRecyclerView.getAdapter();
                    adapter.appendNewResponse(response);
                    //mScrollListener.resetState();
                    adapter.notifyDataSetChanged();
                }
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
