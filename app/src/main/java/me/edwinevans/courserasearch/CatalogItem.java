package me.edwinevans.courserasearch;

import android.content.Context;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

// This can be used for a course or specialization and whether it is a specialization or
// not can be determined by if it has any courses contained in it.
// We could break into a class hierarchy if helpful. This is simple for now.
class CatalogItem {
    static final String EXTRA_KEY = "CATALOG_ITEM";
    private static final String BUNDLE_KEY_ID = "ID";
    private static final String BUNDLE_KEY_LOGO = "LOGO";
    private static final String BUNDLE_KEY_NAME = "NAME";
    private static final String BUNDLE_KEY_UNIVERSITY_NAME = "UNIVERSITY_NAME";
    private static final String BUNDLE_KEY_NUM_COURSES = "NUM_COURSES";
    private String mId;
    private String mLogoUrl;
    private String mName;
    private String mUniversityName;
    private int mNumCourses; // for now we just care about the number

    private CatalogItem() {
    }

    CatalogItem(JSONObject jsonObject, Map<Integer, String> partnerIdToName) {
        mId = jsonObject.optString("id");
        mLogoUrl = jsonObject.optString("logo");
        mName = jsonObject.optString("name");

        JSONArray partnerIds = jsonObject.optJSONArray("partnerIds");
        if (partnerIds != null && partnerIds.length() == 1) {
            // Assume there is only one item and it is the partner
            Integer id = partnerIds.optInt(0);
            mUniversityName = partnerIdToName.get(id);
        }

        JSONArray courses = jsonObject.optJSONArray("courseIds");
        if (courses != null) {
            mNumCourses = courses.length();
        }
    }

    Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_ID, getId());
        bundle.putString(BUNDLE_KEY_LOGO, getLogoUrl());
        bundle.putString(BUNDLE_KEY_NAME, getName());
        bundle.putString(BUNDLE_KEY_UNIVERSITY_NAME, getUniversityName());
        bundle.putInt(BUNDLE_KEY_NUM_COURSES, getNumCourses());
        return bundle;
    }

    static CatalogItem fromBundle(Bundle bundle) {
        CatalogItem catalogItem = new CatalogItem();
        catalogItem.mId = bundle.getString(BUNDLE_KEY_ID);
        catalogItem.mLogoUrl = bundle.getString(BUNDLE_KEY_LOGO);
        catalogItem.mName = bundle.getString(BUNDLE_KEY_NAME);
        catalogItem.mUniversityName = bundle.getString(BUNDLE_KEY_UNIVERSITY_NAME);
        catalogItem.mNumCourses = bundle.getInt(BUNDLE_KEY_NUM_COURSES);
        return catalogItem;
    }

    String getId() { return mId; }
    String getName() { return mName; }
    String getUniversityName() { return mUniversityName; }
    int getNumCourses() { return mNumCourses; }
    String getLogoUrl() { return mLogoUrl; }

    String getNumCoursesDisplayString(Context context) {
        if (mNumCourses > 0) {
            return context.getString(R.string.num_courses_display_string, mNumCourses);
        }
        else {
            return null;
        }
    }
}
