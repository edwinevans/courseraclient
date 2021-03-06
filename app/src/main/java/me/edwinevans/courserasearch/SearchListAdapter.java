package me.edwinevans.courserasearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    // Contains list of items which are either Courses or Specializations
    private static List<CatalogItem> mCatalogItems = new ArrayList<>();

    private Map<Integer, String> mMapPartnerIdToName = new HashMap<>();
    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final RowOnClickListener mRowOnClickListener = new RowOnClickListener();

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mTextViewName;
        final TextView mTextViewUniversityName;
        final TextView mTextViewNumCourses;
        final ImageView mLogo;

        ViewHolder(View itemView) {
            super(itemView);
            mTextViewName = (TextView) itemView.findViewById(R.id.name);
            mTextViewUniversityName = (TextView)itemView.findViewById(R.id.university_name);
            mTextViewNumCourses = (TextView) itemView.findViewById(R.id.number_of_courses);
            mLogo = (ImageView) itemView.findViewById(R.id.logo);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            CatalogItem item = mCatalogItems.get(position);
            Bundle bundle = item.toBundle();
            Intent intent = new Intent();
            intent.setClass(v.getContext(), CatalogItemActivity.class);
            intent.putExtra(CatalogItem.EXTRA_KEY, bundle);
            v.getContext().startActivity(intent);
        }
    }

    SearchListAdapter(Context context, JSONObject response, RecyclerView recyclerView) {
        super();
        mContext = context;
        mRecyclerView = recyclerView;
        appendNewResponse(response);
    }

    void clearData() {
        mCatalogItems.clear();
    }

    private void addToCatalogItems(JSONArray items) {
        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJson = items.optJSONObject(i);
            CatalogItem item = new CatalogItem(itemJson, mMapPartnerIdToName);
            mCatalogItems.add(item);
        }
    }

    void appendNewResponse(JSONObject response) {
        try {
            JSONObject linked = response.getJSONObject("linked");
            updatePartnersMap(linked);
            addToCatalogItems(linked.getJSONArray("courses.v1"));
            addToCatalogItems(linked.getJSONArray("onDemandSpecializations.v1"));
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
        rowView.setOnClickListener(mRowOnClickListener);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CatalogItem catalogItem = mCatalogItems.get(position);

        holder.mTextViewName.setText(catalogItem.getName());
        holder.mTextViewUniversityName.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(catalogItem.getUniversityName())) {
            holder.mTextViewUniversityName.setVisibility(View.VISIBLE);
            holder.mTextViewUniversityName.setText(catalogItem.getUniversityName());
        }
        holder.mTextViewNumCourses.setVisibility(View.GONE);
        int numCourses = catalogItem.getNumCourses();
        if (numCourses > 0) {
            holder.mTextViewNumCourses.setVisibility(View.VISIBLE);
            holder.mTextViewNumCourses.setText(catalogItem.getNumCoursesDisplayString(mContext));
        }

        Uri uri = Uri.parse(catalogItem.getLogoUrl());
        Picasso.with(mContext).load(uri).into(holder.mLogo);
    }

    @Override
    public int getItemCount() {
        return mCatalogItems.size();
    }

    private class RowOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position =  mRecyclerView.getChildAdapterPosition(v);
            CatalogItem item = mCatalogItems.get(position);
            Bundle bundle = item.toBundle();
            Intent intent = new Intent(mContext, CatalogItemActivity.class);
            intent.putExtra(CatalogItem.EXTRA_KEY, bundle);
            mContext.startActivity(intent);
        }
    }
}
