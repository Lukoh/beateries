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

package com.goforer.beatery.model.data.response;

import com.goforer.base.model.BaseModel;
import com.goforer.base.model.data.Image;
import com.google.gson.annotations.SerializedName;

/**
 * The class for putting comment data after parsing JSON formatted data that got from BEatery server
 *
 * <p>
 * Please refer to below link if you'd like to get more details.
 * The link provides REST APIs.
 *
 * @see <a href="https://github.com/goforer/beatery/BEatery_REST_API.pdf">
 *     BEatery REST APIs</a>
 * </p>
 */
public class Comment extends BaseModel {
    @SerializedName("comment_id")
    private long mCommentId;
    @SerializedName("commenter_idx")
    private long mCommenterId;
    @SerializedName("eatery_id")
    private long mEateryId;
    @SerializedName("like_count")
    private long mLikeCount;
    @SerializedName("commenter_picture")
    private Image mPicture;
    @SerializedName("comment")
    private String mComment;
    @SerializedName("commenter_name")
    private String mCommenterName;
    @SerializedName("date")
    private String mDate;


    public long getCommentId() {
        return mCommentId;
    }

    public Image getPicture() {
        return mPicture;
    }

    public String getComment() {
        return mComment;
    }

    public long getCommenterId() {
        return mCommenterId;
    }

    public String getCommenterName() {
        return mCommenterName;
    }

    public String getDate() {
        return mDate;
    }

    public long getEateryId() {
        return mEateryId;
    }

    public long getLikeCount() {
        return mLikeCount;
    }

    public void setCommentId(long commendId) {
        mCommentId = commendId;
    }

    public void setPicture(Image picture) {
        mPicture = picture;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public void setCommenterId(long commenterId) {
        mCommenterId = commenterId;
    }

    public void setCommenterName(String commenterName) {
        mCommenterName = commenterName;
    }

    public void setEateryId(long eateryId) {
        mEateryId = eateryId;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setLikeCount(long likeCount) {
        mLikeCount = likeCount;
    }
}
