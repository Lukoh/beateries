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

package com.goforer.base.model.data;

import com.goforer.base.model.BaseModel;

public class Image extends BaseModel {
    private long id;
    private String url;
    private int width;
    private int height;
    private String mime_type;
    private String key;
    private String name;
    private String description;

    public Image(String url) { this(url, 0, 0); }

    public Image(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public long getImageId() { return this.id; }
    public String getImageUrl() { return this.url; }
    public int getImageWidth() { return this.width; }
    public int getImageHeight() { return this.height; }
    public String getMimeType() { return this.mime_type; }
    public String getImageKey() { return this.key; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Image)) return false;
        Image target = (Image) obj;
        return (this == obj || (this.url != null && this.url.equals(target.url)));
    }

    @Override
    public  int hashCode() { return String.valueOf(url).hashCode(); }
}
