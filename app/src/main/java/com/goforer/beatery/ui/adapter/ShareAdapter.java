/*
 * Copyright (C) 2015-2016 Lukoh Nam, goForer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goforer.beatery.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goforer.beatery.model.data.ListItem;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.ui.fragment.ShareDialogFragment;
import com.goforer.beatery.R;

import java.util.List;

public class ShareAdapter extends ArrayAdapter<ListItem> {
    private Context mContext;
    private ShareDialogFragment mFragment;
    private EateryInfo mEateryInfo;

    public ShareAdapter(ShareDialogFragment fragment, Context context, int resourceId,
                        List<ListItem> items, EateryInfo eateryInfo) {
        super(context, resourceId, items);
        mContext = context;
        mFragment = fragment;
        mEateryInfo = eateryInfo;
    }

    private class ViewHolder {
        ImageView mIcon;
        TextView mShareTo;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final ListItem item = getItem(position);

        LayoutInflater mInflater =
                (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_share_item, parent, false);
            holder = new ViewHolder();
            holder.mShareTo = (TextView) convertView.findViewById(R.id.title);
            holder.mIcon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mShareTo.setText(item.getShareTo());
        holder.mIcon.setImageResource(item.getImageId());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getShareTo().contains("Facebook")) {
                    // Will be implemented
                } else if (item.getShareTo().contains("Google+")) {
                    // Will be implemented
                } else if (item.getShareTo().contains("Twitter")) {
                    // Will be implemented
                } else {
                    // Will be implemented
                }

                mFragment.dismiss();
            }
        });

        return convertView;
    }
}
