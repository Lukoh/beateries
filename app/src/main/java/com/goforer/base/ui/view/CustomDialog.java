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

package com.goforer.base.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goforer.beatery.R;

public class CustomDialog extends Dialog {
    public static final int STYLE_RECOMMEND = 0;

    public CustomDialog(Context context) {
        super(context);
        init();
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private int mStyle = STYLE_RECOMMEND;

        private Button mPositiveBtn;
        private Button mNegativeBtn;

        private OnClickListener
                mPositiveButtonClickListener,
                mNegativeButtonClickListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setStyle(int style) {
            mStyle = style;
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(int message) {
            mMessage = (String) mContext.getText(message);
            return this;
        }

        public Builder setTitle(int title) {
            mTitle = (String) mContext.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            mPositiveButtonText = (String) mContext
                    .getText(positiveButtonText);
            mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            mPositiveButtonText = positiveButtonText;
            mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            mNegativeButtonText = (String) mContext
                    .getText(negativeButtonText);
            mNegativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            mNegativeButtonText = negativeButtonText;
            mNegativeButtonClickListener = listener;
            return this;
        }

        public Button getPositiveButton() {
            return mPositiveBtn;
        }

        public Button getNegatiButton() {
            return mNegativeBtn;
        }

        public CustomDialog create() {
            final CustomDialog dialog =
                    new CustomDialog(mContext, R.style.Theme_BorderlessDialog);

            mPositiveBtn = (Button) dialog.findViewById(R.id.positiveButton);
            if (mPositiveButtonText == null || mPositiveButtonText.equals("")) {
                mPositiveBtn.setVisibility(View.GONE);
            } else {
                mPositiveBtn.setVisibility(View.VISIBLE);
                mPositiveBtn.setText(mPositiveButtonText);

                if (mPositiveButtonClickListener != null) {
                    mPositiveBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mPositiveButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            }

            mNegativeBtn = (Button) dialog.findViewById(R.id.negativeButton);
            if (mNegativeButtonText == null || mNegativeButtonText.equals("")) {
                mNegativeBtn.setVisibility(View.GONE);
            } else {
                mNegativeBtn.setVisibility(View.VISIBLE);
                mNegativeBtn.setText(mNegativeButtonText);
                if (mNegativeButtonClickListener != null) {
                    mNegativeBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mNegativeButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            }

            if (mNegativeBtn.getVisibility() == View.GONE &&
                    mPositiveBtn.getVisibility() == View.GONE) {
                RelativeLayout buttonLayout = (RelativeLayout) dialog.findViewById(
                        R.id.buttonLayout);
                buttonLayout.setVisibility(View.GONE);
            }

            if (mTitle != null) {
                TextView titleTextView = (TextView) dialog.findViewById(R.id.tv_title);
                if (titleTextView != null) {
                    titleTextView.setText(mTitle);
                    titleTextView.setVisibility(View.VISIBLE);
                }
            }

            if (mMessage != null) {
                TextView message = ((TextView) dialog.findViewById(R.id.tv_message));
                if (message != null) {
                    message.setText(mMessage);
                    message.setVisibility(View.VISIBLE);
                }
            }

            dialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN &&
                            keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });

            dialog.setCanceledOnTouchOutside(true);
            return dialog;
        }
    }
}
