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

package com.goforer.beatery.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.goforer.base.model.ListModel;
import com.goforer.base.model.data.Image;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.view.CustomDialog;
import com.goforer.base.ui.view.SquircleImageView;
import com.goforer.base.ui.view.TextRendererDrawable;
import com.goforer.beatery.R;
import com.goforer.beatery.helper.AccountHelper;
import com.goforer.beatery.helper.ImagePickerHelper;
import com.goforer.beatery.model.data.response.User;
import com.goforer.beatery.model.event.ProfileEvent;
import com.goforer.beatery.model.event.SignUpEvent;
import com.goforer.beatery.model.event.UserPictureEvent;
import com.goforer.beatery.model.event.action.CropImageAction;
import com.goforer.beatery.model.event.action.PictureEditAction;
import com.goforer.beatery.model.event.action.TermsAgreeAction;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.utillity.FileUtils;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.goforer.beatery.web.wire.connecter.reponse.ResponseClient;
import com.google.gson.JsonElement;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SignUpActivity extends BaseActivity {
    private static final String TRANSITION_PICTURE = "transition_picture";

    private static final int MAX_NICKNAME_LENGTH = 12;
    private static final int FROM_BIRTHDAY_SPINNER = 0;
    private static final int FROM_SEX_SPINNER = 1;

    public static final int CALL_NONE_TYPE = -1;
    public static final int CALL_PROFILE_TYPE = 0;
    public static final int CALL_SIGN_UP_TYPE = 1;

    private ImagePickerHelper mImagePickerHelper;

    private MenuItem mMenuNext;

    private User mMyProfile;

    private String mSnsId;
    private String mToken;
    private String mEmail;

    private int mCallType;
    private int mAccountType;

    private SpinDummyAdapter mGenderAdapter;

    private boolean mIsSelected;

    private TextView mHintCopyText;

    @BindView(R.id.tv_email)
    TextView mEmailText;
    @BindView(R.id.et_nickname)
    EditText mNickNameText;
    @BindView(R.id.iv_picture)
    SquircleImageView mPicture;
    @BindView(R.id.container_agree)
    LinearLayout mAgreeContainer;
    @BindView(R.id.cb_agree)
    CheckBox mAgreeCBox;
    @BindView(R.id.tv_agree)
    TextView mAgreeText;
    @BindView(R.id.spin_birth)
    Spinner mBirthSpinner;
    @BindView(R.id.spin_gender)
    Spinner mGenderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCallType = getIntent().getIntExtra(ActivityCaller.EXTRA_CALL_TYPE, CALL_NONE_TYPE);
        switch (mCallType) {
            case CALL_PROFILE_TYPE:
                mNickNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                ViewCompat.setTransitionName(mPicture, TRANSITION_PICTURE);

                String userString = getIntent().getStringExtra(User.class.getName());

                if (!TextUtils.isEmpty(userString)) {
                    mMyProfile = User.gson().fromJson(userString, User.class);
                }

                break;
            case CALL_SIGN_UP_TYPE:
                mSnsId = getIntent().getStringExtra(ActivityCaller.EXTRA_SNS_ID);
                mToken = getIntent().getStringExtra(ActivityCaller.EXTRA_TOKEN);
                mEmail = getIntent().getStringExtra(ActivityCaller.EXTRA_EMAIL);
                mAccountType = getIntent().getIntExtra(ActivityCaller.EXTRA_ACCOUNT_TYPE, -1);
                break;
            case CALL_NONE_TYPE:
                break;
            default:
                break;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_signup);
    }

    @Override
    protected void setViews() {
        super.setViews();
        InputFilter lengthFilter = new InputFilter.LengthFilter(MAX_NICKNAME_LENGTH);
        InputFilter charFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (c == ' ') {
                        return "";
                    }
                }
                return null;
            }
        };

        mNickNameText.setFilters(new InputFilter[]{lengthFilter, charFilter});
        mEmailText.setText(mEmail);

        switch (mCallType) {
            case CALL_PROFILE_TYPE:
                mAgreeContainer.setVisibility(View.GONE);
                if (mMyProfile != null) {
                    setMyProfile();
                } else {
                    long userIdx = fetchIdx();

                    if (userIdx < 0) {
                        supportFinishAfterTransition();
                        return;
                    }

                    mMyProfile = new User();
                    mMyProfile.setUserIdx(userIdx);
                }

                requestMyProfile();
                break;
            case CALL_SIGN_UP_TYPE:
                mAgreeContainer.setVisibility(View.VISIBLE);
                setLinkText();
                setItemBirth("");
                setItemSex("");

                mAgreeCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mAgreeCBox.isChecked()) {
                            updateMenuNext(true);
                        } else {
                            updateMenuNext(false);
                        }
                    }
                });
                break;
            case CALL_NONE_TYPE:
                break;
            default:
                break;
        }
    }

    @Override
    protected void setActionBar() {
        super.setActionBar();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            switch (mCallType) {
                case CALL_PROFILE_TYPE:
                    actionBar.setTitle(getResources().getString(R.string.title_profile));
                    break;
                case CALL_SIGN_UP_TYPE:
                    actionBar.setTitle(getResources().getString(R.string.title_signup));
                    break;
                case CALL_NONE_TYPE:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (mCallType) {
            case CALL_PROFILE_TYPE:
                inflater.inflate(R.menu.change_profile, menu);
                break;
            case CALL_SIGN_UP_TYPE:
                inflater.inflate(R.menu.next, menu);
                break;
            case CALL_NONE_TYPE:
                break;
            default:
                break;
        }

        mMenuNext = menu.getItem(0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mCallType) {
            case CALL_PROFILE_TYPE:
                menu.findItem(R.id.menu_change_profile).setVisible(true);
                break;
            case CALL_SIGN_UP_TYPE:
                menu.findItem(R.id.menu_next).setVisible(false);
                break;
            case CALL_NONE_TYPE:
                break;
            default:
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_next:
            case R.id.menu_change_profile:
                if (isValid()) {
                    requestSignUp();
                }

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mImagePickerHelper != null) {
            mImagePickerHelper.onActivityResult(requestCode, resultCode, data);
        } else {
            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setMessage(R.string.cutom_dialog_signup_message_camera_error);
            builder.setPositiveButton(R.string.custom_dialog_login_action_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            CustomDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void setEffectIn() {
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.scale_down_exit);
    }

    @Override
    protected void setEffectOut() {
        overridePendingTransition(R.anim.scale_up_enter, R.anim.slide_out_to_bottom);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private long fetchIdx() {
        long idx = -1;
        Uri uri = getIntent().getData();
        try {
            String path = uri.getLastPathSegment();
            idx = Long.parseLong(path);
        } catch (Exception ignored) {
        }
        return idx;
    }

    private void setMyProfile() {
        mNickNameText.setText(mMyProfile.getUserNickName());
        mPicture.setImage(mMyProfile.getPictureImage());
        setItemSex(mMyProfile.getGender());
        setItemBirth(mMyProfile.getBirth());
    }

    private void setItemBirth(String text) {
        ArrayList<String> items = new ArrayList<>();
        int from = 1900;
        int last = 2099;

        while (true) {
            items.add(String.valueOf(from));
            if (from >= last) break;
            from++;
        }

        mBirthSpinner.setAdapter(new SpinDummyAdapter(this, R.layout.spinner_signup, items, text,
                FROM_BIRTHDAY_SPINNER));
        mBirthSpinner.setSelection(items.size() - 1);
    }

    private void setItemSex(String text) {
        mIsSelected = false;
        final ArrayList<String> items = new ArrayList<>();

        items.add(getString(R.string.signup_spinner_sex_male));
        items.add(getString(R.string.signup_spinner_sex_female));
        items.add(getString(R.string.signup_spinner_sex_etc));
        mGenderAdapter = new SpinDummyAdapter(this, R.layout.spinner_signup, items, text,
                FROM_SEX_SPINNER);

        mGenderSpinner.setAdapter(mGenderAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (!mIsSelected) {
                    mHintCopyText.setText(getString(R.string.spiner_signup_hint_sex));
                } else {
                    mHintCopyText.setText(items.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mGenderSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mIsSelected = true;
                }

                return false;
            }
        });

        mGenderSpinner.setSelection(0);
    }

    private TextRendererDrawable getLinkImage(final Context context, float fontSize, String keyword,
                                              final boolean select) {
        final float density = context.getResources().getDisplayMetrics().density;
        float size = density * fontSize;

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(size);
        textPaint.setColor(0xffff0000);
        textPaint.setUnderlineText(true);

        return new TextRendererDrawable(context, textPaint, keyword) {
            @Override
            public void prepare() {
                Paint.FontMetrics fontMetrics = getTextPaint().getFontMetrics();

                int width = (int) (getTextPaint().measureText((String) getObejct(0)) +
                        (fontMetrics.bottom * 2));
                int height = (int) (Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom * 2));

                setBounds(0, 0, width, height);
            }

            @Override
            public void draw(Canvas canvas) {
                Paint.FontMetrics fontMetrics = getTextPaint().getFontMetrics();

                int width = (int) (getTextPaint().measureText((String) getObejct(0)) +
                        (fontMetrics.bottom * 2));
                int height = (int) (Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom));

                Paint bgPaint = new Paint();
                bgPaint.setAntiAlias(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    bgPaint.setColor(select ? context.getResources().getColor(
                            R.color.bg_signup_picture, null)
                            : context.getResources().getColor(R.color.bg_signup_no_picture, null));
                } else {
                    bgPaint.setColor(select ? context.getResources().getColor(R.color.bg_signup_picture)
                            : context.getResources().getColor(R.color.bg_signup_no_picture));
                }

                bgPaint.setStyle(Paint.Style.FILL);

                canvas.drawText((String) getObejct(0), (float) width, (float) height, getTextPaint());
            }
        };
    }

    private void requestMyProfile() {
        ProfileEvent event = new ProfileEvent();
        Intermediary.INSTANCE.getUserInfo(this, mMyProfile.getUserIdx(), event);
    }

    private boolean isValid() {
        String email = mEmailText.getText().toString().trim();
        String nickname = mNickNameText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.toast_signup_login_fail,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(getApplicationContext(), R.string.toast_signup_nickname_required,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void requestSignUp() {
        switch (mCallType) {
            case CALL_PROFILE_TYPE:
                showProgress(R.string.progress_change_profile);
                break;
            case CALL_SIGN_UP_TYPE:
                showProgress(R.string.progress_signup);
                break;
            case CALL_NONE_TYPE:
                break;
            default:
                break;
        }

        blockEdit(true);

        final String email = mEmailText.getText().toString();
        final String nickname = mNickNameText.getText().toString().trim();
        final String snsType = mAccountType == AccountHelper.ACCOUNT_TYPE_FACEBOOK ?
                "facebook" : "google+";
        final String birth = (String)mBirthSpinner.getSelectedItem();
        final String gender = String.valueOf(mGenderSpinner.getSelectedItemPosition() + 1);
        Image image = mPicture.getImage();

        final SignUpEvent event = new SignUpEvent();
        event.setAccountType(mAccountType);

        if (image != null) {
            Glide.with(this).load(image.getImageUrl()).asBitmap().into(new SimpleTarget<Bitmap>
                    (mPicture.getWidth(), mPicture.getHeight()) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                        glideAnimation) {
                    try {
                        File file = FileUtils.createImageFile();
                        FileUtils.saveBitmap(resource, file);
                        String mimeType = "image/jpeg";
                        RequestBody picture = RequestBody.create(MediaType.parse(mimeType), file);
                        Intermediary.INSTANCE.signUp(getApplicationContext(), snsType, mToken,
                                mSnsId, email, nickname, birth, gender, picture, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    Intermediary.INSTANCE.signUp(getApplicationContext(), snsType, mToken,
                            mSnsId, email, nickname, birth, gender, null, event);
                }
            });
        } else {
            Intermediary.INSTANCE.signUp(getApplicationContext(), snsType, mToken,
                    mSnsId, email, nickname, birth, gender, null, event);
        }
    }

    private void blockEdit(boolean lock) {
        mNickNameText.setEnabled(!lock);
    }

    private void signUpSuccess(ResponseClient responseClient){
        try {
            JsonElement jsonElement = responseClient.getResponseEntity().getAsJsonArray().get(0);
            User me = User.gson().fromJson(jsonElement, User.class);
            AccountHelper.putMe(this, me);
        } catch (Exception ignored) {
        } finally {
            ActivityCaller.INSTANCE.callEateryList(this);
            supportFinishAfterTransition();
        }
    }

    public void setLinkText() {
        mAgreeText.setMovementMethod(LinkMovementMethod.getInstance());

        String total = getString(R.string.signup_agree_desc);

        SpannableStringBuilder builder = new SpannableStringBuilder(total);

        List<String> keywords = new ArrayList<>();
        keywords.add(getString(R.string.signup_keyword_agree_terms));
        keywords.add(getString(R.string.signup_keyword_agree_policy));

        for (String keyword : keywords) {
            TextRendererDrawable nickNameDrawable = getLinkImage(this, 15.0f, keyword, true);

            int start = total.indexOf(keyword);
            int end  = start + keyword.length();

            builder.setSpan(new ImageSpan(nickNameDrawable, ImageSpan.ALIGN_BOTTOM), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    setLinkText();
                    Intent intent = new Intent(SignUpActivity.this, TermsActivity.class);
                    startActivity(intent);
                }
            };

            builder.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        mAgreeText.setText(builder.subSequence(0, builder.length()));
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(UserPictureEvent event) {
        if(event.getResponseClient() != null && event.getResponseClient().isSuccessful()){
            List<Image> userPicture = new ListModel<>(Image.class).fromJson(
                    event.getResponseClient().getResponseEntity());
            if(userPicture != null && userPicture.size() > 0){
                mPicture.setImage(userPicture.get(0));
            }
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(ProfileEvent event) {
        if (event.isMine(String.valueOf(mMyProfile.getUserIdx()))) {
            if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
                JsonElement jsonElement = event.getResponseClient().getResponseEntity().
                        getAsJsonArray().get(0);
                mMyProfile = User.gson().fromJson(jsonElement, User.class);
                if (mMyProfile != null) {
                    setMyProfile();
                    return;
                }
            }

            supportFinishAfterTransition();
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(SignUpEvent event) {
        if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
            signUpSuccess(event.getResponseClient());
        } else if (event.getResponseClient() != null &&
                (event.getResponseClient().getResponseCode() != ResponseClient.CODE_SUCCESS &&
                        !event.getResponseClient().getResponseMessage().equals("fail"))) {
            dismissProgress();
            blockEdit(false);

            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setTitle(R.string.custom_dialog_notification_title);
            if (event.getResponseClient().getResponseMessage() != null &&
                    event.getResponseClient().getResponseMessage().length() > 0) {
                builder.setMessage(event.getResponseClient().getResponseMessage());
            } else {
                builder.setMessage(R.string.cutom_dialog_signup_message_nickname_warning);
            }

            builder.setPositiveButton(R.string.custom_dialog_login_action_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            dismissProgress();
            blockEdit(false);

            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setTitle(R.string.custom_dialog_notification_title);
            builder.setMessage(R.string.cutom_dialog_signup_message_error);
            builder.setPositiveButton(R.string.custom_dialog_login_action_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(TermsAgreeAction action) {
        mAgreeCBox.setChecked(true);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(PictureEditAction action) {
        if(action.getImagePath() == null || action.getImagePath().isEmpty()) {
            return;
        }

        mPicture.setImageNewCache(new Image(action.getImagePath()));
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(CropImageAction action) {
        if(action.getPicturePath() == null || action.getPicturePath().isEmpty()) {
            return;
        }

        final String path = action.getPicturePath();

        Image image = new Image(path);
        mPicture.setImageNewCache(image);

    }

    @SuppressWarnings("")
    @OnClick(R.id.iv_picture)
    void onPictureEdit() {
        mImagePickerHelper = new ImagePickerHelper(this, new ImageChooserListener() {
            @Override
            public void onImageChosen(final ChosenImage chosenImage) {
                ActivityCaller.INSTANCE.callPictureEdit(SignUpActivity.this,
                        chosenImage.getFilePathOriginal());
            }

            @Override
            public void onError(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                R.string.toast_signup_image_pick_fail, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onImagesChosen(ChosenImages chosenImages) {

            }
        });

        mImagePickerHelper.show();
    }

    public void updateMenuNext(boolean enable) {
        mMenuNext.setVisible(enable);
    }

    public class SpinDummyAdapter extends ArrayAdapter<String> {
        private List<String> mItems;
        private final int mFromWhat;

        @BindView(R.id.tv_hint)
        TextView mHintText;
        @BindView(R.id.tv_spin)
        TextView mSpinText;
        @BindView(R.id.tv_selected)
        TextView mSelectedText;

        public SpinDummyAdapter(Context context, int txtViewResourceId, List<String> items,
                                String text, int fromWhat) {
            super(context, txtViewResourceId, items);

            mItems = items;
            mFromWhat = fromWhat;

            if (!text.isEmpty()) {
                mSpinText.setText(text);
            }
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, parent);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (mFromWhat == FROM_BIRTHDAY_SPINNER) {
                if(convertView == null || !convertView.getTag().equals("NONE_DROPDOWN")){
                    convertView = getLayoutInflater().inflate(R.layout.spinner_view_signup, parent,
                            false);
                    convertView.setTag("NONE_DROPDOWN");
                }

                mSelectedText.setText(mItems.get(pos));
            } else {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_view_hint, parent,
                            false);
                }

                mHintCopyText = mHintText;
                if (!mIsSelected) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mHintText.setTextColor(getResources().getColor(R.color.gray, null));
                    } else {
                        mHintText.setTextColor(getResources().getColor(R.color.gray));
                    }

                    mHintText.setText(getString(R.string.spiner_signup_hint_sex));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mHintText.setTextColor(getResources().getColor(R.color.black, null));
                    } else {
                        mHintText.setTextColor(getResources().getColor(R.color.black));
                    }

                    mHintText.setText(mItems.get(pos));
                    mGenderAdapter.notifyDataSetChanged();
                }
            }

            return convertView;
        }

        public View getCustomView(int position, ViewGroup parent) {
            View spinner = getLayoutInflater().inflate(R.layout.spinner_signup, parent, false);

            mSpinText.setText(mItems.get(position));

            return spinner;
        }
    }
}
