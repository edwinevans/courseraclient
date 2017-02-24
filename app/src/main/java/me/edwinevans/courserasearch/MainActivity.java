/**************

 NOTES:
 1. Uses EndlessRecyclerViewScrollListener (https://gist.github.com/nesquena/d09dc68ff07e845cc622)
    to implement infinite scrolling with additional calls to the catalog API to fetch more data.
 2. Some of the code is written in Kotlin. If I was doing this for real I would probably write it
    all in Java or all in Kotlin.
 3. I think everything is working except there are no images showing for courses because the
    catalog API is not returning them. Images work in list for specializations and in detail
    page for both.
 4. There are a few more things that we can discuss.

 **************/

package me.edwinevans.courserasearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Limit to request with each API call. The total number of courses plus
    // specializations may be more
    private static final int LIMIT_INITIAL = 15;
    private static final int LIMIT_INCREMENTAL = 10;

    private EndlessRecyclerViewScrollListener mScrollListener;
    private String mSearchString;
    private int mPagingNext = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private SearchListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreDataFromApi();
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView = (RecyclerView)findViewById(R.id.search_list);
        mRecyclerView.addOnScrollListener(mScrollListener);

        View searchButton = findViewById(R.id.search_button);
        final TextView searchEntry = (TextView) findViewById(R.id.search_entry);
        searchEntry.setText("machine learning"); // for testing
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchString = searchEntry.getText().toString();
                mScrollListener.resetState();
                if (mAdapter != null) {
                    mAdapter.clearData();
                }
                searchRequest(0);
            }
        });
    }

    private void loadMoreDataFromApi() {
        // Should really only do this if we know more data is available
        Log.d(TAG, "Request more data");
        searchRequest(mPagingNext);
    }

    private void searchRequest(final int pagingNext) {
        Log.d(TAG, "Request a search. Search string: " + mSearchString + ", Starting from: " + pagingNext);
        CourseraApiClient.getCourses(getApplicationContext(), mSearchString, pagingNext,
                (pagingNext == 0 ? LIMIT_INITIAL : LIMIT_INCREMENTAL),
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
                    mAdapter = new SearchListAdapter(
                            getApplicationContext(), response, mRecyclerView);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                }
                else {
                    mAdapter.appendNewResponse(response);
                    mAdapter.notifyDataSetChanged(); // would be good to make more granular
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  JSONObject response) {
                Log.i(TAG, "Catalog search call failed, statusCode: " + statusCode +
                        ", mResponse: " + response);
            }
        });
    }
}
