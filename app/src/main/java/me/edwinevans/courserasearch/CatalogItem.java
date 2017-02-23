package me.edwinevans.courserasearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

// This can be used for a course or specialization and whether it is a specialation or
// not can be determined by if it has any courses contained in it.
// We could break into a class hierachy if helpful. This is simple for now.
public class CatalogItem {
    private String mName;
    private String mUniversityName;
    private int mNumCourses; // for now we just care about the number

    private  CatalogItem() {
    }

    public CatalogItem(JSONObject jsonObject, Map<Integer, String> partnerIdToName) {
        mName = jsonObject.optString("name");

        JSONArray partnerIds = jsonObject.optJSONArray("partnerIds");
        if (partnerIds != null && partnerIds.length() == 1) {
            // Assume there is only one item and it is the partner
            Integer id = partnerIds.optInt(0);
            if (id != null && partnerIdToName != null) {
                mUniversityName = partnerIdToName.get(id);
            }
        }

        JSONArray courses = jsonObject.optJSONArray("courseIds");
        if (courses != null) {
            mNumCourses = courses.length();
        }
    }

//    static CatalogItem fromJson(JSONObject jsonObject, Map<Integer, String> partnerIdToName) {
//        CatalogItem item = new CatalogItem();
//
//        item.mName = jsonObject.optString("name"));
//
//        JSONArray partnerIds = jsonObject.optJSONArray("partnerIds");
//        if (partnerIds != null && partnerIds.length() == 1) {
//            // Assume there is only one item and it is the partner
//            Integer id = partnerIds.optInt(0);
//            if (id != null) {
//                item.mUniversityName = partnerIdToName.get(id);
//            }
//        }
//
//        JSONArray courses = jsonObject.optJSONArray("courseIds");
//        if (courses != null) {
//            item.mNumCourses = courses.length();
//        }
//
//        return item;
//    }

    public String getName() { return mName; }
    public String getUniversityName() { return mUniversityName; }
    public int getNumCourses() { return mNumCourses; }
}
