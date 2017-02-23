package me.edwinevans.courserasearch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseraApiClient {
    private static final String TAG = "CourseraApiClient";
    private static final String CATALOG_URL = "https://www.coursera.org/api/catalogResults.v2?";
    private static final String FIELDS =
            "courseId,onDemandSpecializationId,courses.v1(name,photoUrl,partnerIds)" +
            ",onDemandSpecializations.v1(name,logo,courseIds,partnerIds),partners.v1(name)&amp;";
    private static final String INCLUDES =
            "courseId,onDemandSpecializationId,courses.v1(partnerIds)";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getCourses(final Context context, String searchString,
                                  int start, int limit,
                                  final JsonHttpResponseHandler responseHandler) {
        if (Utility.isMockMode()) {
            String str = context.getString(R.string.mock_query_response2);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            responseHandler.onSuccess(200, null, jsonObject);
        }
        else {
            RequestParams params = new RequestParams();
            params.put("q", "search");
            params.put("query", searchString);
            params.put("start", start);
            params.put("limit", limit);
            params.add("fields", FIELDS);
            params.add("includes", INCLUDES);

            // Below line doesn't work. Perhaps start/limit needs to combined into query param
            // client.get(CATALOG_URL, params, responseHandler);
            String url = CATALOG_URL + params.toString(); // since above doesn't work
            Log.d(TAG, "GET " + url);
            client.get(url, responseHandler);
        }
    }
}
