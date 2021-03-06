// See MainActivity for notes

package me.edwinevans.courserasearch;

import android.content.Context;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

// This Activity can be used to display a course or specialization.
// I'm assuming an item is a specialization if and only if it contains courses.
// We could break this into a hierarchy of classes if helpful. This is simple for now.
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
            // I'm assuming there is only one item and it is the partner
            Integer id = partnerIds.optInt(0);
            mUniversityName = partnerIdToName.get(id);
        }

        JSONArray courses = jsonObject.optJSONArray("courseIds");
        if (courses != null) {
            mNumCourses = courses.length();
            mLogoUrl = jsonObject.optString("logo");
        }
        else {
            mLogoUrl = jsonObject.optString("photoUrl");
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
    boolean isSpecialization() { return mNumCourses > 0; }
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
