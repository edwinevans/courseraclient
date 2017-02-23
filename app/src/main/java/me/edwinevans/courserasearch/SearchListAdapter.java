package me.edwinevans.courserasearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private JSONObject mResponse;
    private List<CatalogItem> mCatalogItems = new ArrayList<>();
    private Map<Integer, String> mMapPartnerIdToName = new HashMap<>();
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
            updatePartnersMap(linked);
            JSONArray courses = linked.getJSONArray("courses.v1");
            addToCatalogItems(courses);
            JSONArray specializations = linked.getJSONArray("onDemandSpecializations.v1");
            addToCatalogItems(specializations);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addToCatalogItems(JSONArray items) {
        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJson = items.optJSONObject(i);
            CatalogItem item = new CatalogItem(itemJson, mMapPartnerIdToName);
            mCatalogItems.add(item);
        }
    }

//    private JSONArray appendArray(JSONArray arr1, JSONArray arr2)
//            throws JSONException {
//        JSONArray result = new JSONArray();
//        for (int i = 0; i < arr1.length(); i++) {
//            result.put(arr1.get(i));
//        }
//        for (int i = 0; i < arr2.length(); i++) {
//            result.put(arr2.get(i));
//        }
//        return result;
//    }

    public void appendNewResponse(JSONObject response) {
        try {
            JSONObject linked = response.getJSONObject("linked");
            updatePartnersMap(linked);
            JSONArray courses = linked.getJSONArray("courses.v1");
            addToCatalogItems(courses);
            JSONArray specializations = linked.getJSONArray("onDemandSpecializations.v1");
            addToCatalogItems(specializations);


//            mCourses = appendArray(mCourses, courses);
//            mSpecializations = appendArray(mSpecializations, specializations);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updatePartnersMap(JSONObject linked) {
        try {
            JSONArray partners = linked.optJSONArray("partners.v1");
            if (partners != null) {
                for (int i = 0; i < partners.length(); i++) {
                    JSONObject partner = partners.getJSONObject(i);
                    String name = partner.getString("name");
                    Integer id = partner.getInt("id");
                    mMapPartnerIdToName.put(id, name);
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CatalogItem catalogItem = mCatalogItems.get(position);
        holder.textViewName.setText(catalogItem.getName());
        holder.textViewUniversityName.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(catalogItem.getUniversityName())) {
            holder.textViewUniversityName.setVisibility(View.VISIBLE);
            holder.textViewUniversityName.setText(catalogItem.getUniversityName());
        }
        holder.textViewNumCourses.setVisibility(View.GONE);
        int numCourses = catalogItem.getNumCourses();
        if (numCourses > 0) {
            holder.textViewNumCourses.setVisibility(View.VISIBLE);
            holder.textViewNumCourses.setText(String.valueOf(numCourses) + " Courses"); // TODO: string
        }
    }

    @Override
    public int getItemCount() {
        return mCatalogItems.size();
    }

}
