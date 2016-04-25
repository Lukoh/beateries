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

package com.goforer.beatery.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.goforer.beatery.R;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.api.utils.ImageChooserBuilder;

public class ImagePickerHelper implements DialogInterface.OnClickListener {
    private Activity mActivity;
    private Fragment mFragment;

    private ImageChooserManager mManager;
    private AlertDialog mTypeChooser;
    private ImageChooserListener mListener;

    public ImagePickerHelper(Activity activity, ImageChooserListener chooserListener) {
        mActivity = activity;
        init(chooserListener);
    }

    public ImagePickerHelper(Fragment fragment, ImageChooserListener chooserListener) {
        this(fragment.getActivity(), chooserListener);
        mFragment = fragment;
    }

    private void init(ImageChooserListener listener) {
        mListener = listener;
    }

    public void show() {
        if (mTypeChooser == null) {
            ImageChooserBuilder builder = new ImageChooserBuilder(mActivity, this);
            builder.setDialogTitle(R.string.title_image_chooser);
            builder.setTitleGalleryOption(R.string.label_image_from_gallery);
            builder.setTitleTakePictureOption(R.string.label_image_from_camera);
            mTypeChooser = builder.create();
        }

        if (!mTypeChooser.isShowing()) {
            mTypeChooser.show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mFragment != null) {
            mManager = new ImageChooserManager(mFragment, which, false);
        } else if (mActivity != null) {
            mManager = new ImageChooserManager(mActivity, which, false);
        }
        if (mManager != null && mListener != null) {
            try {
                mManager.setImageChooserListener(mListener);
                mManager.choose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            mManager.submit(requestCode, resultData);
        }
    }
}
