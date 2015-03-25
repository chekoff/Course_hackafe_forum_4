package com.chekoff.hackafeforum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Plamen on 21.03.2015.
 */
public class TopicsAdapter extends BaseAdapter {
    LayoutInflater inflater;
    int rowCount;
    List<Topics> topicsList;
    Context mContext;
    String LOG = "Hackafe forum";

    public TopicsAdapter(LayoutInflater inflater, List<Topics> topicsList, Context mContext) {
        this.inflater = inflater;
        this.topicsList = topicsList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return rowCount;
    }


    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //final View listView;
        final Bitmap topicBitmap;
        final ViewHolder viewHolder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.topics_item, parent, false);
        }

        /*if ( position % 2 == 0)
            listView.setBackgroundResource(R.drawable.listview_selector_even);
        else
            listView.setBackgroundResource(R.drawable.listview_selector_odd);*/

        TextView txtPostTitle = (TextView) convertView.findViewById(R.id.txtPostTitle);
        TextView txtViewsCount = (TextView) convertView.findViewById(R.id.txtViewsCount);
        TextView txtRepliesCount = (TextView) convertView.findViewById(R.id.txtRepliesCount);
        TextView txtLastPostedAt = (TextView) convertView.findViewById(R.id.txtLastPostedAt);
        LinearLayout categoryColor = (LinearLayout) convertView.findViewById(R.id.category_color);

        txtPostTitle.setText(topicsList.get(position).getTopicTitle());

        try {
            categoryColor.setBackgroundColor(Color.parseColor(topicsList.get(position).getCategory().getText_color()));
        } catch (Throwable t) {
            Log.e(LOG, "Invalid color:" + topicsList.get(position).getCategory().getText_color());
        }

        txtViewsCount.setText(String.format("View: %s", topicsList.get(position).getViewsCount()));
        txtRepliesCount.setText(String.format("Replies: %s", topicsList.get(position).getRepliesCount()));
        txtLastPostedAt.setText(String.format("Last: %s", topicsList.get(position).getLastPostedAt()));

        ImageView imgTopicImage = (ImageView) convertView.findViewById(R.id.imgTopicImage);

        topicBitmap = topicsList.get(position).getTopicImageBitmap();
        if (topicBitmap != null) {
            imgTopicImage.setImageBitmap(topicBitmap);
            imgTopicImage.setVisibility(View.VISIBLE);
        } else {
            imgTopicImage.setVisibility(View.GONE);
        }

        viewHolder = new ViewHolder();

        viewHolder.imgButtUserOrigin = (ImageButton) convertView.findViewById(R.id.imgButtUserOrigin);
        viewHolder.imgButtUserLatest = (ImageButton) convertView.findViewById(R.id.imgButtUserLatest);

        viewHolder.imgButtUserOrigin.setImageBitmap(topicsList.get(position).getUserAvatar());
        viewHolder.imgButtUserLatest.setImageBitmap(topicsList.get(position).getUserLastPosterAvatar());

        //set OnClickListeners
        viewHolder.imgButtUserOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://frm.hackafe.org/users/%s/activity", topicsList.get(position).getUsername()))));
            }
        });

        viewHolder.imgButtUserLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://frm.hackafe.org/users/%s/activity", topicsList.get(position).getUserLastPoster()))));
            }
        });


        return convertView;
    }


}
