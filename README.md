# BEatery(It means Best+Eatery)

This is an Android application for finding the best eateries around the user using the [BEatery REST APIs](https://github.com/Lukoh/beateries/blob/master/BEatery%20REST%20APIs.pdf). An Android App developer can see how to make some SNS or O20 service on Android vai the BEatery source code. The BEatery App does not currently work because the BEatery Server does not set up yet. But I bet the BEatery App will runs well if the BEatery Server would be developed. (I have repeated the tests with some json data several times.)

<img src="https://github.com/Lukoh/beateries/blob/master/BEatery_Login.jpg" alt="Log-in Demo" width="350" />
&nbsp;
<img src="https://github.com/Lukoh/beateries/blob/master/BEatery.jpg" alt="Screen Demo" width="350" />

## Installation

Quick note is that you must provide **Facebook & Google+ access token** for the BEatery App in order to use this App. To get an token, you need to refer below link :

[Google+](https://developers.google.com/+/mobile/android/sign-in?hl=en) 
[Google Sign-in](https://developers.google.com/identity/sign-in/android/start)
[Facebook](https://developers.facebook.com/docs/facebook-login/android) 


## Overview

The app does the following:

1. Fetch all eateries around a user from the [EateryInfo List API](https://github.com/Lukoh/beateries/blob/master/EateryList%20API.pdf) in JSON format
2. Deserialize the JSON data for each of the eateries into `EateryInfo` objects
3. Build an array of `EateryInfo` objects and create an `EateryInfoAdapter` for those eateries
4. Define `getView` to define how to inflate a layout for each eatery row and display each eatery's data.
5. Attach the adapter for the eateries to a RecyclerView to display the data on screen

To achieve this, there are four different components in this app:

1. `RequestClient & ResponseClient` - Responsible for executing the API requests and retrieving the JSON
2. `EateryInfo` - Model object responsible for encapsulating the attributes for each individual eatery
3. `EateryListAdapter` - Responsible for mapping each `EateryInfo` to a particular view layout
4. `EateryListActivity` - Responsible for fetching and deserializing the data and configuring the adapter

The app leverages the [EateryInfo List API](https://github.com/Lukoh/beateries/blob/master/BEatery%20REST%20APIs.pdf) which returns the following JSON response:

```json
{
  "entity": [
    {
	"id": 4324343,
    	"country_code": "KR",
    	"name": "RiverSide Honey",
    	"best_menu": "Fish Soup, BBQ, Pork stew",
    	"address": "Riverside 11th road, Hansin 3 dong, Seocho-Gu, Seoul",
    	"telephone": +82 02 521 4432,
    	"website": "www.riverside.com",
    	"type": "Korean Traditional Restaurant",
    	"logo": {
      		"id": 156043543,
      		"url": "http://www.riverside.com/logo/156043543_logo.png",
      		"width": 64,
      		"height": 64,
      		"mime_type": "image/png",
      		"key": "eatery_logo",
      		"name": "riverside_logo",
      		"description": "riverside's main logo-the 50-year-old tradition of making…"
    	},
    	"preference": 4,
   	    "information": "A Brooklyn Heights favorite, this sleepy American tavern is a…",
    	"detail_information": "Exposed brick walls covered with old-fashioned clocks…",
    	"has_event": true,
    	"has_gallery": true,
    	"comment_count": 304,
    	"like_count": 107

    },
    {
      "id": "7706879",
      ...
    },
  ...
  ]
}
```

See the [BEatery REST APIs](http://guides.thecodepath.com/android/Rotten-Tomatoes-Networking-Tutorial) on our cliffnotes for a step-by-step tutorial.

## Libraries

This app leverages third-party libraries:

 * [Retrofit](http://square.github.io/retrofit/) - For asynchronous network requests
 * [EventBus](http://greenrobot.org/eventbus/) - For communication between Activiteis, Fragments, Servcie, etc
 * [ButterKnife](http://jakewharton.github.io/butterknife/) - For field and method binding for Android views
 * [Glide](https://github.com/bumptech/glide) - For an image loading and caching library for Android focused on smooth scrolling
 * [image-chooser-library](https://github.com/coomar2841/image-chooser-library) - For choosing a imgage from gallery
 * [PhotoView](https://github.com/chrisbanes/PhotoView) - For viewing a image
 * [SwipyRefreshLayout](https://github.com/OrangeGangsters/SwipyRefreshLayout) - For swiping in both direction
 * [FloatingActionButton](https://github.com/Clans/FloatingActionButton) - For using floating action button

## To-Do Functionalities

A couple of functionalities were not currently implmented into the BEatery App.

So Some functionalities as listed below have to be implemented:

 * [Sharing] - For sharing the eatery's information to Facebook, Google+, Gmail, Twitter
 * [Request the review] - For requesting a review of the eatery

# License
```
Copyright 2015-2016 Lukoh Nam

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
