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

import android.os.Build;
import android.webkit.WebView;

import com.goforer.base.model.data.Clause;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.beatery.R;
import com.goforer.beatery.model.event.TermsEvent;
import com.goforer.beatery.model.event.action.TermsAgreeAction;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TermsActivity extends BaseActivity {
    private static final int TERMS_USE_TYPE = 0;
    private static final int TERMS_POLICY_TYPE = 1;

    @BindView(R.id.wv_agree_terms)
    WebView mAgreeTerms;
    @BindView(R.id.wv_agree_policy)
    WebView mAgreePolicy;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_terms);
    }

    @Override
    protected void setViews() {
        super.setViews();

        requestTerms(TERMS_USE_TYPE);
        requestTerms(TERMS_POLICY_TYPE);
    }

    private void requestTerms(int type) {
        TermsEvent event = new TermsEvent(true);
        Intermediary.INSTANCE.getClause(this, type, event);
    }

    public void onEvent(TermsEvent event) {
        if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
            Type listType = new TypeToken<List<Clause>>(){}.getType();
            List<Clause> clauses = new Gson().fromJson(event.getResponseClient().getResponseEntity(),
                    listType);

            for (Clause clause : clauses) {
                if (clause.getType() == 1) {
                    bindTerms(mAgreeTerms, clause.getClause());
                }
                if (clause.getType() == 2) {
                    bindTerms(mAgreePolicy, clause.getClause());
                }
            }
        }
    }

    private void bindTerms(WebView webView, String term) {
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.loadData(term, "text/html; charset=utf-8", null);
        } else {
            webView.loadData(term, "text/html", "utf-8");
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.btn_agree)
    void onAgree() {
        EventBus.getDefault().post(new TermsAgreeAction());
        finish();
    }
}
