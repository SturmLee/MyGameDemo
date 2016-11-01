package com.example.gamedemo1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by stonegroup on 2016/10/31.
 */

public class MyAdapter extends BaseAdapter {

    private final static String TAG = "MainMyAdapter";

    private Context mContext ;
    private LayoutInflater mInflater ;
    private int[] mItemsImage;
    private int[] mCloneItems ;

    public MyAdapter(Context context ,int[] itemsImage){
        mContext = context ;
        mInflater = LayoutInflater.from(context);
        mItemsImage = itemsImage ;
        mCloneItems = itemsImage ;

    }

    @Override
    public int getCount() {
        return mItemsImage.length;
    }

    public int[] getCloneItemsSet(){
        return mCloneItems;
    }

    public void setCloneItemsSet(int position){
        mCloneItems[position] = 0 ;
    }


    @Override
    public Object getItem(int position) {
        return mItemsImage[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view ;
        ViewHolder holder ;
        if (convertView == null){
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.gridview_item,null);
            holder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        }else {
            view = convertView ;
            holder = (ViewHolder) view.getTag();
        }
        holder.image.setImageResource(mItemsImage[position]);
//
//        if (view.getVisibility() == View.GONE){
//            view.setVisibility(View.VISIBLE);
//        }

        return view;
    }

    private class ViewHolder{
        ImageView image ;
    }

}
