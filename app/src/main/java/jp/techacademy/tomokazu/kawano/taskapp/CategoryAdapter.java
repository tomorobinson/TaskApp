package jp.techacademy.tomokazu.kawano.taskapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class CategoryAdapter extends ArrayAdapter<Category> {
    private List<Category> mCategoryList;

    public CategoryAdapter(Context context){
        super(context, android.R.layout.simple_spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public CategoryAdapter(Context context, ArrayList<Category> list) {
        super(context, android.R.layout.simple_spinner_item, list);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView)super.getView(position, convertView, parent);
        textView.setText(getItem(position).getCategory());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView)super.getDropDownView(position, convertView, parent);
        textView.setText(getItem(position).getCategory());
        return textView;
    }

    public void setCategoryList(List<Category> categoryList) {
        mCategoryList = categoryList;
    }



}