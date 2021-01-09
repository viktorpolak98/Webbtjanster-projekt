function logclick(id) {
    console.log(id);
}


function fetchAndUpdateInfo(details) {
    return function() {
      $.ajax({
        url: details,
        headers: {"Accept": "application/json"}
      })
      .done(function (data) {
        console.log(data);
      });
    }
  }

function logga(form) {
    var ort = form.inputbox.value;
    console.log(ort);
    
    $.ajax({
        method: "get",
        url: "https://polisen.se/api/events?type=" + ort,
        headers: {"Accept": "application/json"}
    })
 
    .done(function (data) { 
        list = $('#events');
        for (i = 0; i < data.length; i++) {
            html = '';
            html = '<h5 id=event_' + i + '"' + 'class="card-title">' + data[i]['name'] + '</h5>';
            list.append(html);
            $('#event_' + i).click(logclick('#event_' + i));
        }
    });
}


function twitter(){
    $.ajax({ 
        method:"get",
        url:"localhost",
        headers: {"Accept": "application/json"}
    })
}


