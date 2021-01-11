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
    var searchTerm = form.inputbox.value;

    $.ajax({
        method: "get",
        url: "https://polisen.se/api/events?type=" + searchTerm,
        headers: {"Accept": "application/json"},
    })

    .done(function (data) { 
        list = $('#events');
        for (i = 0; i < data.length; i++) {
            html = '';
            html = '<li id=event_' + i + '"' + '>' + data[i]['name'] + '</li>';
            list.append(html);
            $('#event_' + i).click(function() {
                console.log('hej');
                genereateDetailedView();
            });

            function genereateDetailedView() {
                return function () {
                  console.log('oh herro');
                }
            };
        }
    });
}

/*

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