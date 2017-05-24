package com.full.firebasedatabase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.full.firebasedatabase.R;
import com.full.firebasedatabase.jdo.MessageJDO;
import com.full.firebasedatabase.util.CommonUtil;

import java.util.List;

/**
 * Created by Shangeeth Sivan on 23/05/17.
 */

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = "CustomRecyclerViewAdapt";
    private List<MessageJDO> mMessageJDOList;
    private String mCurrentUser;
    public String mLastUser;
    private Context mContext;

    public CustomRecyclerViewAdapter(Context pContext, List<MessageJDO> pMessageJDOList, String pCurrentUser) {
        mContext = pContext;
        mMessageJDOList = pMessageJDOList;
        mCurrentUser = pCurrentUser;
        mLastUser = "";
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int pViewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        LayoutInflater lInflater = LayoutInflater.from(viewGroup.getContext());
        View lView = lInflater.inflate(R.layout.rec_view_item_others, viewGroup, false);
        return new MyViewHolder(lView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: " + mMessageJDOList.get(position).getUsername() + " " + mLastUser + "pos:" + position);

//        if (mMessageJDOList.get(position).getUsername().equalsIgnoreCase(mCurrentUser))
//            myViewHolder.ContainerLayout.setBackgroundColor(mContext.getResources().getColor(R.color.chat_bg));
//        else
//            myViewHolder.ContainerLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        if (mMessageJDOList.get(position).getUsername().equalsIgnoreCase(mLastUser)) {
            myViewHolder.UserNameTV.setVisibility(View.GONE);
        } else {
            myViewHolder.UserNameTV.setVisibility(View.VISIBLE);
            myViewHolder.UserNameTV.setText(mMessageJDOList.get(position).getUsername());
        }
        myViewHolder.MessageTV.setText(mMessageJDOList.get(position).getMessage());
        myViewHolder.TimeTV.setText(CommonUtil.convertToFormattedTime(mMessageJDOList.get(position).getTime()));
        mLastUser = mMessageJDOList.get(position).getUsername();
    }

    @Override
    public int getItemCount() {
        return mMessageJDOList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView UserNameTV;
        TextView MessageTV;
        TextView TimeTV;
        LinearLayout ContainerLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            UserNameTV = (TextView) itemView.findViewById(R.id.tv_username);
            MessageTV = (TextView) itemView.findViewById(R.id.tv_message);
            TimeTV= (TextView) itemView.findViewById(R.id.tv_time);
            ContainerLayout = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }


    public int getNewSize() {
        return mMessageJDOList.size();
    }
}
