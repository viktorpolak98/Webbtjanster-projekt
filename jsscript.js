$(document).ready(function () {
    /*
     * JQuerys ajax-funktion kan hämta information från APIer. Den använder vi
     * oss av för att hämta listan.
     */
    $.ajax({
      url: "http://unicorns.idioti.se",        // Här finns listan.
      headers: {"Accept": "application/json"}  // Det här berättar för webservern
                                               // att vi vill ha JSON tillbaka.
    })
    /*
     * Den här funktionen anropas när informationen hämtats. Inladdad information
     * går att nå via variabeln "data". När den är framme vill vi skriva ut den i
     * konsollen. Då kan vi titta på den. Klicka gärna runt i svaret när det har
     * skrivits ut.
     */
    .done(function (data) { 
      console.log(data);
    });
  });