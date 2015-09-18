package com.bookdose.confly.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookdose.confly.R;
import com.bookdose.confly.object.Category;

import java.util.ArrayList;

/**
 * Created by Teebio on 9/17/15 AD.
 */
public class CategorylistAdapter extends BaseAdapter{
    public interface CategoryListListener{
        void onSelectCategory(Category category);
    }

    private CategoryListListener categoryListListener;

    public void setCategoryListListener(CategoryListListener categoryListListener){
        this.categoryListListener = categoryListListener;
    }

    Context context;
    ArrayList<Category>categories;
    public CategorylistAdapter(Context context, ArrayList<Category>categories){
        this.context = context;
        this.categories = categories;
    }
    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            int layoutId = R.layout.category_list;
            convertView = ((Activity) context).getLayoutInflater().inflate(
                    layoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.categoryName = (TextView) convertView
                    .findViewById(R.id.categoryName);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null)
        {
            Category category = categories.get(position);
            viewHolder.categoryName.setText(category.category_name);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryListListener != null)
                    categoryListListener.onSelectCategory(categories.get(position));
                Log.e("", "Position item click ===> " + v.getTag());
            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView categoryName;
    }

//    @Override
//    public void onClick(View v)
//    {
//        if(v != null)
//        {
//            if(v.getTag() != null)
//            {
//                if (categoryListListener != null)
//                    categoryListListener.onSelectCategory(categories.get(Integer.parseInt(v.getTag().toString())));
//                Log.e("", "Position item click ===> " + v.getTag());
//            }
//        }
//    }
}
