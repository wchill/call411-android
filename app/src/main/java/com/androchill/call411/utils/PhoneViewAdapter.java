package com.androchill.call411.utils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androchill.call411.R;
import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhoneViewAdapter extends RecyclerView.Adapter<PhoneViewAdapter.ViewHolder> {

    List<Phone> mDataset;
    Context mContext;

    public PhoneViewAdapter(Context context, List<Phone> phones) {
        mContext = context;
        mDataset = phones;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RoundedImageView mImageView;
        public TextView mTextView;
        public CardView mCardView;
        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card);
            mImageView = (RoundedImageView) mCardView.findViewById(R.id.photo);
            mTextView = (TextView) mCardView.findViewById(R.id.show_details);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_phone, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ImageView mImageView = viewHolder.mImageView;
        TextView mTextView = viewHolder.mTextView;
        Picasso.with(mContext).cancelRequest(mImageView);
        Picasso.with(mContext)
                .load(mDataset.get(i).getImageUrl())
                .placeholder(R.drawable.loading_placeholder)
                .error(android.R.drawable.stat_notify_error)
                .tag("PhoneViewAdapter")
                .into(mImageView);
        mTextView.setText(mDataset.get(i).getModelNumber());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addPhone(Phone phone) {
        mDataset.add(phone);
        notifyItemInserted(mDataset.size() - 1);
    }

    public void setDataset(List<Phone> phones) {
        int oldSize = mDataset.size();
        mDataset.clear();
        notifyItemRangeRemoved(0, oldSize);
        mDataset.addAll(phones);
        notifyItemRangeInserted(0, mDataset.size());
    }

    public Phone getPhoneAt(int index) {
        return mDataset.get(index);
    }
}
