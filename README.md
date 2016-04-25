# BEatery

This is an Android application for finding the best eateries using the [BEatery REST APIs](https://github.com/Lukoh/beateries/blob/master/BEatery%20REST%20APIs.pdf). See the [BEatery REST APIs document](https://github.com/Lukoh/beateries/blob/master/BEatery%20REST%20APIs.pdf) on our cliffnotes for a step-by-step tutorial.

<img src="https://github.com/Lukoh/beateries/blob/master/BEatery.jpg" alt="Screen Demo" width="350" />
&nbsp;

## Installation

Quick note is that you must **provide your own API key** for RottenTomatoes in order to use this demo. To get an API key, you need to [register for an account](http://developer.rottentomatoes.com/member/register) (or [sign in](https://secure.mashery.com/login/developer.rottentomatoes.com/)). Once you have the key, put the key into the `API_KEY` constant in the `src/com/codepath/example/rottentomatoes/RottenTomatoesClient.java` file: 

```java
public class RottenTomatoesClient {
  private final String API_KEY = "ENTER-KEY-HERE";
  // ...
}
```

Once you've setup the key and imported the project into Eclipse, you should be all set.

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
 * [EventBus](http://greenrobot.org/eventbus/) - For asynchronous network requests
 * [Picasso](http://square.github.io/picasso/) - For remote image loading

## License
