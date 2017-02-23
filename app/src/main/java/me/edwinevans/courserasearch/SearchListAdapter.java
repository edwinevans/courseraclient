package me.edwinevans.courserasearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class SearchListAdapter extends BaseAdapter {
    private JSONObject mResponse;
    private JSONArray mCourses;
    private JSONArray mSpecializations;
    private Map<Integer, String> mPartnerIdToName;
    private Context context;

    public SearchListAdapter(Context context, JSONObject response) {
        super();
        this.context=context;
        this.mResponse = response;
        try {
            JSONObject linked = response.getJSONObject("linked");
            mCourses = linked.getJSONArray("courses.v1");
            mSpecializations = linked.getJSONArray("onDemandSpecializations.v1");
            updatePartnersMap(linked);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updatePartnersMap(JSONObject linked) {
        mPartnerIdToName = new HashMap();
        try {
            JSONArray partners = linked.optJSONArray("partners.v1");
            if (partners != null) {
                for (int i = 0; i < partners.length(); i++) {
                    JSONObject partner = partners.getJSONObject(i);
                    String name = partner.getString("name");
                    Integer id = partner.getInt("id");
                    mPartnerIdToName.put(id, name);
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mCourses.length() + mSpecializations.length();
    }

    @Override
    public Object getItem(int arg0) {
        try {
            int numCourses = mCourses.length();
            if (arg0 < numCourses) {
                return mCourses.getJSONObject(arg0);
            }
            else {
                return mSpecializations.getJSONObject(arg0 - numCourses);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }

    @Override
    public boolean isEmpty(){
        return mCourses ==null || mCourses.length()==0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_list_item, parent, false);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView textViewName = (TextView) rowView.findViewById(R.id.name);
        TextView textViewUniversityName = (TextView) rowView.findViewById(R.id.university_name);
        TextView textViewNumCourses = (TextView) rowView.findViewById(R.id.number_of_courses);
        try {
            JSONObject jsonObject = (JSONObject)getItem(position);
            textViewName.setText(jsonObject.getString("name"));
            textViewUniversityName.setVisibility(View.GONE);
            try {
                JSONArray partnerIds = jsonObject.optJSONArray("partnerIds");
                if (partnerIds != null && partnerIds.length() == 1) {
                    // Assume there is only one item and it is the partner
                    Integer id = partnerIds.getInt(0);
                    String universityName = mPartnerIdToName.get(id);
                    textViewUniversityName.setVisibility(View.VISIBLE);
                    textViewUniversityName.setText(universityName);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            JSONArray courses = jsonObject.optJSONArray("courseIds");
            textViewNumCourses.setVisibility(View.GONE);
            if (courses != null && courses.length() > 0) {
                textViewNumCourses.setVisibility(View.VISIBLE);
                textViewNumCourses.setText(String.valueOf(courses.length()) + " Courses");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
