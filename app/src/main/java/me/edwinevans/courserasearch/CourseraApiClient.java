package me.edwinevans.courserasearch;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

class CourseraApiClient {
    private static final String TAG = "CourseraApiClient";

    private static final String BASE_URL = "https://www.coursera.org/api/";
    private static final String CATALOG_URL = BASE_URL + "catalogResults.v2?";
    private static final String COURSE_URL = BASE_URL + "courses.v1/";
    private static final String SPECIALIZATION_URL = BASE_URL + "onDemandSpecializations.v1/";
    private static final String COURSE_FIELDS = "photoUrl,description";
    private static final String SPECIALIZATION_FIELDS = "logo,description";
    private static final String CATALOG_FIELDS =
            "courseId,onDemandSpecializationId,courses.v1(name,photoUrl,partnerIds)" +
            ",onDemandSpecializations.v1(name,logo,courseIds,partnerIds),partners.v1(name)&amp;";
    private static final String CATALOG_INCLUDES =
            "courseId,onDemandSpecializationId,courses.v1(partnerIds)";


    private static final AsyncHttpClient mClient = new AsyncHttpClient();

    public static void getCourses(final Context context, String searchString,
                                  int start, int limit,
                                  final JsonHttpResponseHandler responseHandler) {
        if (Utility.isMockMode()) {
            String str = context.getString(R.string.mock_query_response);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            responseHandler.onSuccess(200, null, jsonObject);
        }
        else {
            // May be good to first check if we have an internet connections
            RequestParams params = new RequestParams();
            params.put("q", "search");
            params.put("query", searchString);
            params.put("start", start);
            params.put("limit", limit);
            params.add("fields", CATALOG_FIELDS);
            params.add("includes", CATALOG_INCLUDES);
            String url = CATALOG_URL + params.toString();
            Log.d(TAG, "GET " + url);
            mClient.get(url, responseHandler);
        }
    }

    public static void getCourse(final Context context, String id,
                                 final JsonHttpResponseHandler responseHandler) {
        String url = COURSE_URL + id;
        RequestParams params = new RequestParams();
        params.add("fields", COURSE_FIELDS);
        mClient.get(url, params, responseHandler);
    }

    public static void getSpecialization(final Context context, String id,
                                 final JsonHttpResponseHandler responseHandler) {
        String url = SPECIALIZATION_URL + id;
        RequestParams params = new RequestParams();
        params.add("fields", SPECIALIZATION_FIELDS);
        mClient.get(url, params, responseHandler);
    }
}
