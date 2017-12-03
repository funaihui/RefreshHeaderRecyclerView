package com.wizardev.headerrefreshforrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wizardev.headerrefreshforrecyclerview.R;
import com.wizardev.headerrefreshforrecyclerview.interfaces.IRefreshHeader;
import com.wizardev.headerrefreshforrecyclerview.view.ArrowRefreshHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizardev on 17-12-2.
 */

public class RefreshHeaderAdapter extends RecyclerView.Adapter<RefreshHeaderAdapter.RefreshViewHolder>{
    private List<String> mLists;
    private Context mContext;
    private LayoutInflater mInflater;
    private static final int TYPE_REFRESH_HEADER = 100000;
//    private List<View> headers = new ArrayList<>();
    private static final int TYPE_NORMAL = 10001;
    private IRefreshHeader mRefreshHeader;

    public RefreshHeaderAdapter(List<String> lists, Context context) {
        mLists = lists;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RefreshViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_REFRESH_HEADER) {
//            return new RefreshViewHolder(mInflater.inflate(R.layout.refresh_header_item, parent, false));
            return new RefreshViewHolder(mRefreshHeader.getHeaderView());
        }

        return new RefreshViewHolder(mInflater.inflate(R.layout.normal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RefreshViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_REFRESH_HEADER;
        }
        return TYPE_NORMAL;
    }

    class RefreshViewHolder extends RecyclerView.ViewHolder {
        public RefreshViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setRefreshHeader(IRefreshHeader header) {
        if (header != null) {
            mRefreshHeader = header;
        }
    }
}
