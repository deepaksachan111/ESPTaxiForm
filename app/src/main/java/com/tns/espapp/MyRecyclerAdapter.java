package com.tns.espapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TNS on 04-Mar-17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.GridItemViewHolder> {
    private Context context;
    private ArrayList<String> mItemList;
    View itemView;

    private static AdapterView.OnItemClickListener mOnItemClickListener;

    public MyRecyclerAdapter(Activity activity, ArrayList<String> outfits) {
        this.mItemList = outfits;
        this.context = activity;

    }

    @Override
    public MyRecyclerAdapter.GridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(context).inflate(R.layout.single_cell_layout, parent, false);
        return new GridItemViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerAdapter.GridItemViewHolder holder, final int position) {
        try {
            holder.userNumber.setText(mItemList.get(position));
            holder.userName.setText(mItemList.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private static void onItemHolderClick(GridItemViewHolder versionViewHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, versionViewHolder.itemView,
                    versionViewHolder.getAdapterPosition(), 0);
        }
    }

    public class GridItemViewHolder extends RecyclerView.ViewHolder  {
        public TextView userNumber;
        public TextView userName;

        public MyRecyclerAdapter mAdapter;
        public ImageView add_icon;

        public GridItemViewHolder(final View itemView, final MyRecyclerAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            try {
                userNumber = (TextView) itemView.findViewById(R.id.event_id);
                userName = (TextView) itemView.findViewById(R.id.calendar_date_id);

                userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // MyRecyclerAdapter.onItemHolderClick(MyRecyclerAdapter.GridItemViewHolder.this);
                    }
                });
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     Toast.makeText(context,v.getParent().toString()+ "",Toast.LENGTH_LONG).show();
                      ViewGroup parentView = (ViewGroup) userName.getParent();
                     parentView.removeView(userName);
                      mItemList.remove(userName.getText().toString());
                      mAdapter.notifyDataSetChanged();
                  }
              });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
