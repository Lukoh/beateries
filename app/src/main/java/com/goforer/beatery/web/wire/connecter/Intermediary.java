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

package com.goforer.beatery.web.wire.connecter;

import android.content.Context;

import com.goforer.base.model.event.ResponseEvent;
import com.goforer.base.model.data.GPSData;
import com.goforer.beatery.BEatery;
import com.goforer.beatery.helper.AccountHelper;
import com.goforer.beatery.helper.DeviceHelper;
import com.goforer.beatery.model.data.request.Device;
import com.goforer.beatery.web.wire.connecter.reponse.ResponseClient;
import com.goforer.beatery.web.wire.connecter.request.RequestClient;
import com.goforer.beatery.web.wire.connecter.request.RequestClient.*;

import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * The intermediary class for sending a request to BEatery Server and returns a response.

 * <p>
 * BEatery App adapts a Retrofit to communicate with BEatery Server(Retrofit Version 2).
 * Retrofit provides great way to HTTP calls by using annotations on the declared methods to
 * define how requests are made. (Retrofit is a great HTTP client for Android (and Java))
 *
 * A call that is busy writing its request or reading its response may receive a {@link IOException};
 * this is working as designed.
 *
 * ResponseClient: Successful response body type.
 * </p>
 *
 */
public enum  Intermediary {
    INSTANCE;

    private static final int PAGE_ROW_COUNT = 20;

    private static final String SEARCH_BY_NAME = "name";
    private static final String SEARCH_BY_ADDRESS = "address";

    public void setDeviceInfo(Context context, Device device, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context).setDeviceInfo(
                AccountHelper.getMyIdx(context), device);
        callEnqueue(context, call, event);
    }

    public void login(Context context, String sns_id, String token, String email,
                             String snsType, ResponseEvent event) {
        Call<ResponseClient> call =
                RequestClient.INSTANCE.getRequestMethod(context).login(sns_id, token, email,
                        snsType);
        callEnqueue(context, call, event);
    }

    public void logout(Context context, ResponseEvent event) {
        Call<ResponseClient> call =
                RequestClient.INSTANCE.getRequestMethod(context).logout(
                        AccountHelper.getMyIdx(context), AccountHelper.getMyId(context),
                        DeviceHelper.INSTANCE.getToken(BEatery.mContext));
        callEnqueue(context, call, event);
    }

    public void getClause(Context context, int type, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getClause(type);
        callEnqueue(context, call, event);
    }

    public void getUserInfo(Context context, long userIdx, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getUserInfo(AccountHelper.getMyIdx(context),
                        userIdx);
        callEnqueue(context, call, event);
    }

    public void signUp(Context context, String snsType, String token, String sndId,
                              String email, String nickname, String birth, String gender,
                              RequestBody picture, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .signUp(snsType, token, sndId, email, nickname,
                        birth, gender, "Y", picture);
        callEnqueue(context, call, event);
    }

    public void getAllEateryListOrderByAddress(Context context, int page, int pageRows,
                                                      ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getAllEateryListOrderByAddress(
                        AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                        GPSData.INSTANCE.getAdminArea(), GPSData.INSTANCE.getCity(),
                        GPSData.INSTANCE.getThoroughfare(), GPSData.INSTANCE.getSubThoroughfare(),
                        page, pageRows);
        callEnqueue(context, call, event);
    }

    public void getAllEateryListOrderByCoordinates(Context context, int page, int pageRows,
                                                          ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getAllEateryListOrderByCoordinates(
                        AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                        GPSData.INSTANCE.getLatitude(), GPSData.INSTANCE.getLongitude(),
                        page, pageRows);
        callEnqueue(context, call, event);
    }

    public void getBestEateryList(Context context, int page, int pageRows, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getBestEateryList(AccountHelper.getMyIdx(context),
                        GPSData.INSTANCE.getCountryCode(), page, pageRows);
        callEnqueue(context, call, event);
    }

    public void getMyHangoutsList(Context context, int page,
                                             int pageRows, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getMyHangoutsList(AccountHelper.getMyIdx(context),
                        GPSData.INSTANCE.getCountryCode(), AccountHelper.getMyId(context),
                        page, pageRows);
        callEnqueue(context, call, event);
    }

    public void getOptimalEateryListByAddress(Context context, int page, int pageRows,
                                            ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getOptimalEateryListByAddress(
                        AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                        GPSData.INSTANCE.getAdminArea(), GPSData.INSTANCE.getCity(),
                        GPSData.INSTANCE.getThoroughfare(), GPSData.INSTANCE.getSubThoroughfare(),
                        page, pageRows);
        callEnqueue(context, call, event);
    }

    public void getOptimalEateryListByCoordinates(Context context, int page, int pageRows,
                                              ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getOptimalEateryListByCoordinates(AccountHelper.getMyIdx(context),
                        GPSData.INSTANCE.getCountryCode(), GPSData.INSTANCE.getLatitude(),
                        GPSData.INSTANCE.getLongitude(),
                        page, pageRows);
        callEnqueue(context, call, event);
    }

    public void getEateryInfo(Context context, long eateryId, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getEateryInfo(AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                        eateryId);
        callEnqueue(context, call, event);
    }

    public void selectHangout(Context context, long eateryId, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context).selectHangout(
                AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(), eateryId);
        callEnqueue(context, call, event);
    }

    public void postLike(Context context, long eateryId, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .postLike(AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                        eateryId);
        callEnqueue(context, call, event);
    }

    public void postComment(Context context, long eateryId, String comment, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .postComment(AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                        eateryId, comment);
        callEnqueue(context, call, event);
    }

    public void getComments(Context context, long eateryId, long commentId, String sort,
                                   int commentCount, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context).getComments(
                AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(), eateryId,
                commentId, sort, commentCount);
        callEnqueue(context, call, event);
    }

    public void postLikeComment(Context context, long eateryId, long commenterId,
                                       long commentId, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .postLikeComment(AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                        eateryId, commenterId, commentId);
        callEnqueue(context, call, event);
    }

    public void getEventContents(Context context, long eateryId, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getEventContents(AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode()
                        , eateryId);
        callEnqueue(context, call, event);
    }

    public void getGalleryContents(Context context, long eateryId, int page, int pageRows,
                                          ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context)
                .getGalleryContents(AccountHelper.getMyIdx(context),
                        GPSData.INSTANCE.getCountryCode(), eateryId, page, pageRows);
        callEnqueue(context, call, event);
    }

    public void searchByName(Context context, String keyword, int page, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context).search(
                AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(), SEARCH_BY_NAME,
                keyword, page, PAGE_ROW_COUNT);
        callEnqueue(context, call, event);
    }

    public void searchByAddress(Context context, String keyword, int page, ResponseEvent event) {
        Call<ResponseClient> call = RequestClient.INSTANCE.getRequestMethod(context).search(
                AccountHelper.getMyIdx(context), GPSData.INSTANCE.getCountryCode(),
                SEARCH_BY_ADDRESS, keyword, page, PAGE_ROW_COUNT);
        callEnqueue(context, call, event);
    }

    private void callEnqueue(Context context, Call<ResponseClient> call, ResponseEvent event) {
        call.enqueue(new RequestCallback(event, context) {
            @Override
            public void onResponse(Call<ResponseClient> call, Response<ResponseClient> response) {
                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseClient> call, Throwable t) {
                super.onFailure(call, t);
            }

        });
    }


}
