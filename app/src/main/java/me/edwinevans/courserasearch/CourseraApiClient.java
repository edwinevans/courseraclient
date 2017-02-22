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
            "courseId,onDemandSpecializationId,mCourses.v1(name,photoUrl,partnerIds)" +
            ",onDemandSpecializations.v1(name,logo,courseIds,partnerIds),partners.v1(name)&amp;";
    private static final String INCLUDES =
            "courseId,onDemandSpecializationId,mCourses.v1(partnerIds)";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getCourses(final Context context, String searchString,
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
            params.put("start", 0); // TODO!
            params.put("limit", 5); // TODO!
            params.add("fields", FIELDS);
            params.add("includes", INCLUDES);

            // client.get(CATALOG_URL, params, responseHandler); // Doesn't work
            String url = CATALOG_URL + params.toString(); // since above doesn't work
            Log.d(TAG, "GET " + url);
            client.get(url, responseHandler);
        }
    }
}
