/*
scripts.js
The JavaScript used by the Twitterpolice project used for
implementing the API and allowing the site to interact with it.
Version 2.0.2
Date: 2021-02-06
Authors: Tor Stenfeldt, Viktor Polak, et al.
*/

/*
Populates the list of police events using the search term.
*/
function searchEvents(search) {
  console.log("Yarr");
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

  console.log(searchTerm);
  console.log(startDate);
  console.log(endDate);

  $.ajax({
    method: "get",
    //url: "https://polisen.se/api/events?type=" + searchTerm,
    url: "http://localhost:4000/" + searchTerm + "/" + startDate + "/" + endDate,
    headers: {"Accept": "application/json"},
  }).done(function(result) {
    console.log(result);

    $("#events").empty();
    list = $('#events');
    console.log(result[1]);

    for (i=0; i<result.length; i++) {
      html = '<li id="police_post_' + i + '">' + result[i]['name'] + '</li>';
      list.append(html);
      $('#police_post_' + i).click(setEvent(result[i]));
    }
  });
}

/*
Gets the tweets relevant to the selected police event.
*/
function getTweets (gps) {
  const twitterStuff = [
    '@Lenamalmstrom Eller hur 😀',
    '@MsMoneypenny11 @msgrp Nä, han sa ju no filter 🤣',
    'Carl har bryggt kaffe.. smakar sump.. men lär ju dricka upp de, som den snälla flickvännen man är 🥲',
    '(SWE/ENG) Grind med packs o annat skoj! #ad !cantona !messi !grannen #FuckCorona https://t.co/kUd1t3NhDT',
    '@svtnyheter Är man förvånad, knappast! Sen ett nytt begrepp "Iskyla" Ha Ha',
    '@Natursidan Bedrövligt!', '@Newbergskan Eller hur 😀',
    '@thereseverdun Min mamma jobbade i Saltsjöqvarn när jag var liten och som på den tiden var kungsörnen.',
    '@AryaStark_got_ För mycket snö är precis lagom',
    'Alla älskar snö https://t.co/PG7Z5q1Flr'
  ]

  /*
  $.ajax({
    url: "JAVAROUTEN TILL TWITTER API + gps",
    headers: {"Accept": "application/json"}
  }).done(function (data) {
    //return data.body ANTAGLIGEN DATA.BODY,
    //logga data om det inte är det för att titta;
  });
  */
  return twitterStuff;
}

/*
Updates the tweets related to the selected police event.
*/
function updateTweets(tweets) {
  var name = tweets[0];
  var info = tweets[1];
  var geocode = tweets[2];
  var dateTime = tweets[3]

  console.log(tweets[0]);
  console.log(tweets[1]);
  console.log(tweets[2]);

  $("#tweetList").empty();
  tweetsContained = $('#tweetList');
  for (tweet of tweets) {
    tweetContainer = '<p>' + tweet + '</p>';
    tweetsContained.append(tweetContainer);
  }
  // Loopa ut alla kommentarerna i en LI eller liknande beroende på hur du vill
  // visa dom och appenda till diven till höger som är gjort för twitter
}

/*
Updates the information about the selected police event.
*/
function setEvent(data) {
  return function() {
    $("#tweets").empty();
    var gps = data.location.gps;
    var locationName = data.name;
    var name = data.name;
    var summary = data.summary;
    var url = data.url;
    var type = data.type;
    var dateTime = data.datetime;

    $("#eventTitle").empty();
    something = '<h3>' + data.name + '</h3>';
    $("#eventTitle").append(something);

    $("#eventDescription").empty();
    something2 = '<p>' + data.summary + '</p>';
    $("#eventTitle").append(something2);

    $("#eventUrl").empty();
    something3 = '<a src="' + data.url + '">' + data.url + '</a>';
    $("#eventUrl").append(something3);

    var tweets = getTweets(data.location.gps);
    updateTweets(tweets);
  }
}


// HERE BE DRAGONS!
// Also, shit code.

/*
function fetchAndUpdateInfo(details) {
  return function() {
    $.ajax({
      url: details,
      headers: {"Accept": "application/json"}
    })
    .done(function (data) {
      //spottedWhen tolkas av Javascript som en sträng, inte som ett
      //Date-objekt. Däremot kan vi använda  strängen för att skapa ett
      //Date-objekt.
      date = new Date(data['spottedWhen']);

      //Det här blir texten som vi visar längst ner på sidan.
      sighting = 'Av: ' + data['reportedBy'] + ', '
      + date.toLocaleDateString() // Den här funktionen gör om datum-
      // objektet till en sträng på det
      // format (åååå-mm-dd) som vi vill ha.
      + ' i ' + data['spottedWhere']['name'];

      //Med funktionen $().text() kan man ändra ett specifikt elements text.
      //Vi börjar med att ändra namnet på enhörningen.
      $('#unicornName').text(data['name']);

      //Eftersom vi lägger in HTML fungerar inte text. Här måste vi använda
      //oss av jQuerys $().html() istället.
      $('#unicornImage').html('<img src="' + data['image'] + '">');
      $('#unicornInfo').text(data['description']);
      $('#unicornSighting').text(sighting);
    });
  }
}

$(document).ready(function() {
  const twitterStuff = [
    '@Lenamalmstrom Eller hur 😀',
    '@MsMoneypenny11 @msgrp Nä, han sa ju no filter 🤣',
    'Carl har bryggt kaffe.. smakar sump.. men lär ju dricka upp de, som den snälla flickvännen man är 🥲',
    '(SWE/ENG) Grind med packs o annat skoj! #ad !cantona !messi !grannen #FuckCorona https://t.co/kUd1t3NhDT',
    '@svtnyheter Är man förvånad, knappast! Sen ett nytt begrepp "Iskyla" Ha Ha',
    '@Natursidan Bedrövligt!', '@Newbergskan Eller hur 😀',
    '@thereseverdun Min mamma jobbade i Saltsjöqvarn när jag var liten och som på den tiden var kungsörnen.',
    '@AryaStark_got_ För mycket snö är precis lagom',
    'Alla älskar snö https://t.co/PG7Z5q1Flr'
  ]
  $.ajax({
    method: "get",
    url: "https://polisen.se/api/events",
    headers: {"Accept": "application/json"},
  })

  .done(function (data) {
    list = $('#events');
    console.log(data[1]);
    for (i = 0; i < data.length; i++) {
      html = '<li id="police_post_' + i + '">' + data[i]['name'] + '</li>';
      list.append(html);
      $('#police_post_' + i).click(updatePoliceDiv(data[i]));
    }
  });


  function getTwitterPosts (gps) {
    // $.ajax({
    //   url: "JAVAROUTEN TILL TWITTER API + gps",
    //   headers: {"Accept": "application/json"}
    // })
    // .done(function (data) {
    //   return data.body ANTAGLIGEN DATA.BODY,
    //   logga data om det inte är det för att titta;
    // });
    return twitterStuff;
  }

  function updateTwitterDiv(twitterPosts) {
    console.log(twitterPosts[0]);
    console.log(twitterPosts[1]);
    console.log(twitterPosts[2]);
    // Loopa ut alla kommentarerna i en LI eller liknande beroende på hur du
    // vill visa dom och appenda till diven till höger som är gjort för twitter
  }

  function updatePoliceDiv(data) {
    return function () {
      var gps = data.location.gps;
      var locationName = data.name;
      var name = data.name;
      var summary = data.summary;
      var url = data.url;
      var type = data.type;
      var dateTime = data.datetime;
      // appenda informationen till diven som du gjort till höger för polisen
      // ta bort allt innan
      $("#eventName").empty();
      something = '<h3>' + data.name + '</h3>';
      $("#eventName").append(something);

      $("#eventInfo").empty();
      something2 = '<p>' + data.summary + '</p>';
      $("#eventName").append(something2);

      $("#eventUrl").empty();
      something3 = '<a src="' + data.url + '">' + data.url + '</a>';
      $("#eventUrl").append(something3);

      // lägg till grejer
      // https://www.w3schools.com/jquery/jquery_dom_add.asp

      var twitterPosts = getTwitterPosts(data.location.gps);
      updateTwitterDiv(twitterPosts);
    }
  }
})

function fetchAndUpdateInfo(url) {
  return function() {
    $.ajax({
      url: url,
      headers: {"Accept": "application/json"}
    })
    .done(function (data) {
      $('#eventName').text(data['name']);
      $('#eventInfo').text(data['summary']);
    });
  }
}

function genereateDetailedView() {
  console.log('oh herro');
};

function logga(form) {
  const twitterStuff = [
    '@Lenamalmstrom Eller hur 😀',
    '@MsMoneypenny11 @msgrp Nä, han sa ju no filter 🤣',
    'Carl har bryggt kaffe.. smakar sump.. men lär ju dricka upp de, som den snälla flickvännen man är 🥲',
    '(SWE/ENG) Grind med packs o annat skoj! #ad !cantona !messi !grannen #FuckCorona https://t.co/kUd1t3NhDT',
    '@svtnyheter Är man förvånad, knappast! Sen ett nytt begrepp "Iskyla" Ha Ha',
    '@Natursidan Bedrövligt!', '@Newbergskan Eller hur 😀',
    '@thereseverdun Min mamma jobbade i Saltsjöqvarn när jag var liten och som på den tiden var kungsörnen.',
    '@AryaStark_got_ För mycket snö är precis lagom',
    'Alla älskar snö https://t.co/PG7Z5q1Flr'
  ]
  var searchTerm = form.inputbox.value;

  $.ajax({
    method: "get",
    url: "https://polisen.se/api/events?type=" + searchTerm,
    headers: {"Accept": "application/json"},
  })

  .done(function (data) {
    list = $('#events');
    console.log(data[1]);
    for (i = 0; i < data.length; i++) {
      html = '<li id="police_post_' + i + '">' + data[i]['name'] + '</li>';
      list.append(html);
      $('#police_post_' + i).click(updatePoliceDiv(data[i]));
    }
  });
}

function fetchAndUpdateInfo(url) {
  return function() {
    $.ajax({
      url: url,
      headers: {"Accept": "application/json"}
    })
    .done(function (data) {

      $('#eventName').text(data['name']);
      $('#eventInfo').text(data['summary']);
    });
  }
}

function logga(form) {
  var ort = form.inputbox.value;

  $.ajax({
    method: "get",
    url: "https://polisen.se/api/events?type=" + ort,
    headers: {"Accept": "application/json"},
  })

  .done(function (data) {
    list = $('#events');
    for (i = 0; i < data.length; i++) {
      html = '';
      html = '<li id=event_' + i + '"' + '>' + data[i]['name'] + '</li>';
      list.append(html);
      $('#event_' + i).click(fetchAndUpdateInfo(data[i]['url']));
    }
  });
}

function twitter() {
  $.ajax({
    method:"get",
    url:"localhost",
    headers: {"Accept": "application/json"}
  })
}
*/
