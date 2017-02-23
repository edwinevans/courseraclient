package me.edwinevans.courserasearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private JSONObject mResponse;
    private JSONArray mCourses;
    private JSONArray mSpecializations;
    private Map<Integer, String> mPartnerIdToName;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewUniversityName;
        public TextView textViewNumCourses;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.name);
            textViewUniversityName = (TextView)itemView.findViewById(R.id.university_name);
            textViewNumCourses = (TextView) itemView.findViewById(R.id.number_of_courses);
        }
    }

    public SearchListAdapter(Context context, JSONObject response) {
        super();
        this.mContext = context;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    public JSONObject getItem(int arg0) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = (JSONObject)getItem(position);
            holder.textViewName.setText(jsonObject.getString("name"));
            holder.textViewUniversityName.setVisibility(View.GONE);
            try {
                JSONArray partnerIds = jsonObject.optJSONArray("partnerIds");
                if (partnerIds != null && partnerIds.length() == 1) {
                    // Assume there is only one item and it is the partner
                    Integer id = partnerIds.getInt(0);
                    String universityName = mPartnerIdToName.get(id);
                    holder.textViewUniversityName.setVisibility(View.VISIBLE);
                    holder.textViewUniversityName.setText(universityName);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            JSONArray courses = jsonObject.optJSONArray("courseIds");
            holder.textViewNumCourses.setVisibility(View.GONE);
            if (courses != null && courses.length() > 0) {
                holder.textViewNumCourses.setVisibility(View.VISIBLE);
                holder.textViewNumCourses.setText(String.valueOf(courses.length()) + " Courses");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        return mCourses.length() + mSpecializations.length();
    }

}
