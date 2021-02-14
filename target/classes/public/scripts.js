/*
scripts.js
The JavaScript used by the Twitterpolice homepage in order to integrate
the Twitter Police API and allow searching through the database for
relevant events to the specified query.
Version 2.1.0
Date: 2021-02-14
Authors: Tor Stenfeldt, Viktor Polak
*/

/*
Requests data from the API using the specified search query
then populates the list of events using the returned data.
*/
function searchEvents(search) {
  var searchTerm = search.searchTerm.value;
  var startDate = search.startDate.value;
  var endDate = search.endDate.value;

  if (searchTerm == '') {
    searchTerm = '*';
  }

  if (startDate == '') {
    startDate = '*';
  }

  if (endDate == '') {
    endDate = '*';
  }

  $.ajax({
    method: "get",
    url: "http://localhost:4000/events/" + searchTerm + "/" + startDate + "/" + endDate,
    headers: {"Accept": "application/json"},
  }).done(function(result) {
    $("#events").empty();
    list = $('#events');

    for (i=0; i<result.length; i++) {
      html = '<li id="police_post_' + i + '">' + result[i]['name'] + '</li>';
      list.append(html);
      $('#police_post_' + i).click(setEvent(result[i]));
    }
  });
}

/*
Sends a request to the database through the API using the
specified data then populates the list of tweets on the
homepage using the returned data.
*/
function setTweets(event) {
  var date = event.datetime;
  var coordinates = event.location.split(',');
  var x = coordinates[0];
  var y = coordinates[1];

  $.ajax({
    method: "get",
    url: "http://localhost:4000/tweets/" + x + "/" + y + "/" + date,
    headers: {"Accept": "application/json"},
  }).done(function(result) {
    $("#tweetList").empty();
    tweetsContained = $('#tweetList');

    for (tweet of result) {
      tweetContainer = '<p>';
      tweetContainer += tweet.user;
      tweetContainer += tweet.text;
      tweetContainer += '</p>';
      tweetsContained.append(tweetContainer);
    }
  });
}

/*
Triggered when an event is clicked on, populating
the middle bracket with data extracted from said event.
*/
function setEvent(event) {
  return function() {
    $("#tweets").empty();

    $("#eventTitle").empty();
    eventName = '<h3>' + event.name + '</h3>';
    $("#eventTitle").append(eventName);

    $("#eventDescription").empty();
    eventSummary = '<p>' + event.summary + '</p>';
    $("#eventTitle").append(eventSummary);

    $("#eventUrl").empty();
    eventUrl = '<a src="' + event.url + '">' + event.url + '</a>';
    $("#eventUrl").append(eventUrl);

    setTweets(event);
  }
}
