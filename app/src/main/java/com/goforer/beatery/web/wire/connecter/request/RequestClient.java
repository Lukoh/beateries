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

package com.goforer.beatery.web.wire.connecter.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.goforer.base.model.event.ResponseEvent;
import com.goforer.beatery.BEatery;
import com.goforer.beatery.BuildConfig;
import com.goforer.beatery.helper.AccountHelper;
import com.goforer.beatery.helper.model.data.UpdateInfo;
import com.goforer.beatery.model.data.request.Device;
import com.goforer.beatery.model.data.response.ServerUpdateInfo;
import com.goforer.beatery.model.event.action.LogoutAction;
import com.goforer.beatery.web.Cookie;
import com.goforer.beatery.web.storage.PreferenceStorage;
import com.goforer.beatery.web.wire.connecter.reponse.ResponseClient;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The class for sending an HTTP request to BEatery Server and returns a response.
 * Each call yields its own HTTP request and response pair. Use {@link #clone} to make multiple
 * calls with the same parameters to the same BEatery Server; this may be used to implement polling
 * or to retry a failed call.
 *
 * <p>
 * BEatery App adapts a Retrofit to communicate with BEatery Server(Retrofit Version 2).
 * Retrofit provides great way to HTTP calls by using annotations on the declared methods to
 * define how requests are made. (Retrofit is a great HTTP client for Android (and Java))
 *
 * Please refer to below link if you'd like to get more details.
 * The link provides various useful topics.
 *
 * @see <a href="https://futurestud.io/blog/retrofit-2-upgrade-guide-from-1-9">
 *     Retrofit 2 — Upgrade Guide from 1.9</a>
 * </p>
 *
 */
public enum RequestClient {
    INSTANCE;

    private static final String URL_STORAGE = "beatery:url";
    private static final String KEY_URL = "beatery:url";

    private static final String HTTP_HEADER_NAME_SET_COOKIE = "Set-Cookie";
    private static final String HTTP_HEADER_NAME_RESPONSE_DESCRIPTION = "Response-Description";
    private static final String HTTP_HEADER_NAME_FORCED_UPDATE = "Forced-Update";
    private static final String HTTP_HEADER_NAME_MARKET_URL = "Market-Url";
    private static final String HTTP_HEADER_NAME_URL = "Url";

    public static final String FORCED_UPDATE_NO = "No";
    public static final String FORCED_UPDATE_YES = "Yes";

    public static final String SERVER_ADDRESS = "http://api.beatery.com/public/v1.0/";

    public static final String GET_TYPE_LATEST = "latest";
    public static final String GET_TYPE_PREVIOUS = "previous";


    private Context mContext;
    private RequestMethod mRequestor;

    public RequestMethod getRequestMethod(Context context) {
        mContext = context;

        if (mRequestor == null) {
            OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String session = AccountHelper.getSessionId(mContext);

                    if (!TextUtils.isEmpty(session)) {
                        Request request = original.newBuilder()
                                .header("Cookie", session)
                                .header("version", BuildConfig.VERSION_NAME)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);

                    }

                    return chain.proceed(chain.request());
                }
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RequestClient.INSTANCE.getURL())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            mRequestor = retrofit.create(RequestMethod.class);
        }

        return mRequestor;
    }

    private List<Cookie> getCookieList(String rawString) {
        List<Cookie> cookieList = new ArrayList<>();

        if (!TextUtils.isEmpty(rawString)) {
            String[] splits = rawString.split(";");
            for (String cookie : splits) {
                String[] key_value = cookie.split("=");
                if (key_value.length == 2) {
                    Cookie data = new Cookie(key_value[0].trim(), key_value[1].trim());
                    data.setCookieData(key_value[0].trim() + "=" + key_value[1].trim());
                    cookieList.add(data);
                    break;
                }
            }
        }

        return cookieList;
    }

    private void setCookieInfo(String cookieString, Context context) {
        StringBuilder returnCookie = new StringBuilder();

        List<Cookie> cookieList = getCookieList(cookieString);
        String session = AccountHelper.getSessionId(context);
        if (!TextUtils.isEmpty(session)) {
            HashMap<String, Cookie> hashMap = new HashMap<>();

            List<Cookie> oldCookieList = getCookieList(session);
            for (Cookie oldCookie : oldCookieList) {
                hashMap.put(oldCookie.getCookieKey(), oldCookie);
            }

            for (Cookie cookie : cookieList) {
                hashMap.put(cookie.getCookieKey(), cookie);
            }

            for (String key : hashMap.keySet()) {
                Cookie cookie = hashMap.get(key);
                if (!TextUtils.isEmpty(cookie.getCookieValue()) && !cookie.getCookieValue().equals("/")) {
                    returnCookie.append(cookie.getCookieData());
                    returnCookie.append(";");
                }
            }

        } else {
            for (Cookie cookie : cookieList) {
                if (!TextUtils.isEmpty(cookie.getCookieValue()) && !cookie.getCookieValue().equals("/")) {
                    returnCookie.append(cookie.getCookieData());
                    returnCookie.append(";");
                }
            }
        }

        AccountHelper.putSessionId(returnCookie.toString(), context);
    }

    /**
     * Communicates responses from BEatery Server or offline requests.
     * One and only one method will be invoked in response to a given request.
     */
    static public class RequestCallback implements Callback<ResponseClient> {
        private ResponseEvent mEvent;
        private Context mContext;

        public RequestCallback(ResponseEvent event, Context context) {
            mEvent = event;
            mContext = context;
        }

        @Override
        public void onResponse(Call<ResponseClient> call,
                               retrofit2.Response<ResponseClient> response) {
            String description = "";
            String forcedUpdate = "";
            String marketUrl = "";
            String url = "";

            if (!response.isSuccessful()) {
                try {
                    System.out.println(response.errorBody().string());
                } catch (IOException e) {
                    // do nothing
                }

                return;
            }

            if (response.headers() != null) {
                if (response.headers().get(HTTP_HEADER_NAME_SET_COOKIE) != null) {
                    RequestClient.INSTANCE.setCookieInfo(response.headers()
                                    .get(HTTP_HEADER_NAME_SET_COOKIE), mContext);
                } else if (response.headers().get(HTTP_HEADER_NAME_RESPONSE_DESCRIPTION) != null) {
                    try {
                        description = URLDecoder.decode(response.headers()
                                .get(HTTP_HEADER_NAME_RESPONSE_DESCRIPTION), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else if (response.headers().get(HTTP_HEADER_NAME_FORCED_UPDATE) != null) {
                    forcedUpdate = response.headers().get(HTTP_HEADER_NAME_FORCED_UPDATE);
                } else if (response.headers().get(HTTP_HEADER_NAME_MARKET_URL) != null) {
                    marketUrl = response.headers().get(HTTP_HEADER_NAME_MARKET_URL);
                } else if (response.headers().get(HTTP_HEADER_NAME_URL) != null) {
                    url = response.headers().get(HTTP_HEADER_NAME_URL);
                }
            }

            if (description.length() > 0) {
                if (!(forcedUpdate.equalsIgnoreCase(FORCED_UPDATE_NO) && BEatery.isPreferenceUpdateSkip())) {
                    BEatery.updateNotify(new UpdateInfo(description, forcedUpdate, marketUrl, url,
                            response.body(), mEvent));

                    return;
                }
            }

            if (ResponseClient.CODE_AUTH_FAIL_UNKNOWN_USER == response.body().getResponseCode() ||
                    ResponseClient.CODE_AUTH_FAIL == response.body().getResponseCode() ||
                    ResponseClient.CODE_BLOCKED_USER == response.body().getResponseCode()) {
                logout(response);
            } else if (mEvent != null) {
                if (response.body().isServerUpdated()) {
                    ServerUpdateInfo serverUpdateInfo = ServerUpdateInfo.gson().fromJson(
                            response.body().getResponseEntity(), ServerUpdateInfo.class);
                    BEatery.showServerUpdate(serverUpdateInfo);
                } else {
                    mEvent.setResponseClient(response.body());
                    mEvent.parseInResponse();
                    EventBus.getDefault().post(mEvent);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseClient> call, Throwable t) {
            boolean isDeviceEnabled = true;

            if (!RequestClient.INSTANCE.isOnline()) {
                isDeviceEnabled = false;
            }

            if (mEvent != null) {
                mEvent.setResponseClient(new ResponseClient());
                if (!isDeviceEnabled) {
                    mEvent.getResponseClient().setResponseStatus(ResponseClient.CODE_NETWORK_ERROR);
                } else {
                    mEvent.getResponseClient().setResponseStatus(ResponseClient.CODE_GENERAL_ERROR);
                }

                EventBus.getDefault().post(mEvent);
            }
        }

        private void logout(retrofit2.Response<ResponseClient> response) {
            LogoutAction logoutAction = new LogoutAction();
            logoutAction.setCode(response.body().getResponseCode());
            logoutAction.setMessage(response.message());
            AccountHelper.removeAccount(mContext, logoutAction);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                @SuppressWarnings("deprecation")
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setURL(String url) {
        PreferenceStorage.getInstance().put(mContext, URL_STORAGE, KEY_URL, url);
    }

    public String getURL() {
        return PreferenceStorage.getInstance().get(mContext, URL_STORAGE, KEY_URL, SERVER_ADDRESS);
    }

    public interface RequestMethod {
        @FormUrlEncoded
        @POST("user/login")
        Call<ResponseClient> login(
                @Field("id") String sns_id,
                @Field("access_token") String access_token,
                @Field("email") String email,
                @Field("sns_type") String sns_type
        );

        @FormUrlEncoded
        @POST("user/logout")
        Call<ResponseClient> logout(
                @Field("my_idx") long my_idx,
                @Field("id") String my_id,
                @Field("device_token") String token
        );

        @GET("user/clause")
        Call<ResponseClient> getClause(
                @Query("type") int type
        );

        @GET("user/info")
        Call<ResponseClient> getUserInfo(
                @Path("my_idx") long my_idx,
                @Path("profile_idx") long profile_idx
        );

        @Multipart
        @POST("user/join")
        Call<ResponseClient> signUp(
                @Part("sns_provider") RequestBody sns_type,
                @Part("access_token") RequestBody access_token,
                @Part("sns_id") RequestBody sns_id,
                @Part("email") RequestBody email,
                @Part("nickname") RequestBody nickname,
                @Part("birth_year") RequestBody birth_year,
                @Part("gender") RequestBody gender,
                @Part("clause_agree_yn") RequestBody clause_agree_yn,
                @Part("picture") RequestBody picture
        );

        @GET("eatery/list/address/{my_idx}/{country_code}/get")
        Call<ResponseClient> getAllEateryListOrderByAddress(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("admin_area") String admin_area,
                @Query("city") String city,
                @Query("thoroughfare") String thoroughfare,
                @Query("sub_thoroughfare") String sub_thoroughfare,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/address/{my_idx}/{country_code}/update")
        Call<ResponseClient> updateEateryListOrderByAddress(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("admin_area") String admin_area,
                @Query("city") String city,
                @Query("thoroughfare") String thoroughfare,
                @Query("sub_thoroughfare") String sub_thoroughfare,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/coordinates/{my_idx}/{country_code}/get")
        Call<ResponseClient> getAllEateryListOrderByCoordinates(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("latitude") double latitude,
                @Query("longitude") double longitude,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/coordinates/{my_idx}/{country_code}/update")
        Call<ResponseClient> updateEateryListOrderByCoordinates(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("latitude") double latitude,
                @Query("longitude") double longitude,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/best/{my_idx}/{country_code}/get")
        Call<ResponseClient> getBestEateryList(
                        @Path("my_idx") long my_idx,
                        @Path("country_code") String country_code,
                        @Query("page") int page,
                        @Query("page_rows") int page_rows
        );

        @GET("eatery/list/best/{my_idx}/{country_code}/update")
        Call<ResponseClient> udateBestEateryList(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/hangouts/{my_idx}/{country_code}/get")
        Call<ResponseClient> getMyHangoutsList(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("snd_id") String sns_id,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/hangouts/{my_idx}/{country_code}/update")
        Call<ResponseClient> updateMyHangoutsList(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("snd_id") String sns_id,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/optimal/address/{my_idx}/{country_code}/get")
        Call<ResponseClient> getOptimalEateryListByAddress(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("admin_area") String admin_area,
                @Query("city") String city,
                @Query("thoroughfare") String thoroughfare,
                @Query("sub_thoroughfare") String sub_thoroughfare,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/optimal/address/{my_idx}/{country_code}/update")
        Call<ResponseClient> updateOptimalEateryListByAddress(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("admin_area") String admin_area,
                @Query("city") String city,
                @Query("thoroughfare") String thoroughfare,
                @Query("sub_thoroughfare") String sub_thoroughfare,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/optimal/coordinates/{my_idx}/{country_code}/get")
        Call<ResponseClient> getOptimalEateryListByCoordinates(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("latitude") double latitude,
                @Query("longitude") double longitude,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/list/optimal/coordinates/{my_idx}/{country_code}/update")
        Call<ResponseClient> updateOptimalEateryListByCoordinates(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("latitude") double latitude,
                @Query("longitude") double longitude,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/info")
        Call<ResponseClient> getEateryInfo(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Path("id") long id
        );

        @GET("eatery/events")
        Call<ResponseClient> getEventContents(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Path("id") long id
        );

        @GET("eatery/gallery/contents/{my_idx}/{country_code}/{id}/get")
        Call<ResponseClient> getGalleryContents(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Path("id") long id,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );


        @GET("eatery/gallery/contents/{my_idx}/{country_code}/{id}/update")
        Call<ResponseClient> updateGalleryContents(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Path("id") long id,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );

        @GET("eatery/comments/{my_idx}/{country_code}/{id}/get")
        Call<ResponseClient> getComments(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Path("id") long id,
                @Query("comment_id") long comment_id,
                @Query("sort") String sort,
                @Query("comment_count") int comment_count
        );

        @FormUrlEncoded
        @POST("device/{my_idx}/register")
        Call<ResponseClient> setDeviceInfo(
                @Path("my_idx") long my_idx,
                @Body Device device
        );

        @FormUrlEncoded
        @POST("eatery/hangout/select")
        Call<ResponseClient> selectHangout(
                @Field("my_idx") long my_idx,
                @Field("country_code") String country_code,
                @Field("id") long id
        );

        @FormUrlEncoded
        @POST("eatery/like")
        Call<ResponseClient> postLike(
                @Field("my_idx") long my_idx,
                @Field("country_code") String country_code,
                @Field("id") long id
        );

        @FormUrlEncoded
        @POST("eatery/post/comment")
        Call<ResponseClient> postComment(
                @Field("my_idx") long my_idx,
                @Field("country_code") String country_code,
                @Field("id") long id,
                @Field("comment") String comment
        );

        @FormUrlEncoded
        @POST("eatery/comment/like")
        Call<ResponseClient> postLikeComment(
                @Field("my_idx") long my_idx,
                @Field("country_code") String country_code,
                @Field("eatery_id") long eatery_id,
                @Field("commenter_idx") long commenter_idx,
                @Field("comment_id") long comment_id
        );

        @GET("eatery/{my_idx}/{country_code}/search")
        Call<ResponseClient> search(
                @Path("my_idx") long my_idx,
                @Path("country_code") String country_code,
                @Query("search_way") String search_way,
                @Query("search_keyword") String search_keyword,
                @Query("page") int page,
                @Query("page_rows") int page_rows
        );
    }
}
