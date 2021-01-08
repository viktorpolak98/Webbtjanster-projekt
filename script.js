function logga(form) {
    var ort = form.inputbox.value;
    console.log(testvar);
    
    $.ajax({
        method: "get",
        url: "https://polisen.se/api/events?locationname=" + ort,        // Här ska den localhost länken ligga!
        headers: {"Accept": "application/json"}  // Det här berättar för webservern
                                                // att vi vill ha JSON tillbaka.
    })
 
    .done(function (data) { 
        list = $('#events'); // Här hämtar vi en referens till listan
        
        /*
        * data är en tvådimensionell vektor av enhörningsobjekt. Vi går igenom dem
        * en och en och använder oss av deras värde 'name'.
        */
        for (i = 0; i < data.length; i++) {
        // Vi lägger till rå HTML till listan, så vi skapar en variabel för det.
        html = '';
        
        /*
        * För att kunna lägga till ett skript till den senare döper vi
        * listobjektet till "unicorn_i", där "i" är ett enkelt index. Varje
        * listobjekt har enhörningens namn som text.
        * Så här ser det ut:
        * (Egentligen finns det vackrare och renare sätt att göra det här på,
        * men det här är lite enklare att förstå.)
        */
        html = '<h5 class="card-title"> id="event_' + i + '">' + data[i]['name'] + '</h5>';
        
        // Sist lägger vi till listobjektet i vår lista. Det gör man så här:
        list.append(html);
        }
    });
}






function polisen() {
    return function(){
        $(document).ready(function () {
            console.log("Polisens api!");  // Skriver ut en text till konsollen.
        });

        $(document).ready(function () {
        /*
        * JQuerys ajax-funktion kan hämta information från APIer. Den använder vi
        * oss av för att hämta listan.
        */
        $.ajax({
            method: "get",
            url: "https://polisen.se/api/events",        // Här ska den localhost länken ligga!
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
            list = $('#events'); // Här hämtar vi en referens till listan
            
            /*
            * data är en tvådimensionell vektor av enhörningsobjekt. Vi går igenom dem
            * en och en och använder oss av deras värde 'name'.
            */
            for (i = 0; i < data.length; i++) {
            // Vi lägger till rå HTML till listan, så vi skapar en variabel för det.
            html = '';
            
            /*
            * För att kunna lägga till ett skript till den senare döper vi
            * listobjektet till "unicorn_i", där "i" är ett enkelt index. Varje
            * listobjekt har enhörningens namn som text.
            * Så här ser det ut:
            * (Egentligen finns det vackrare och renare sätt att göra det här på,
            * men det här är lite enklare att förstå.)
            */
            html = '<h5 class="card-title"> id="event_' + i + '">' + data[i]['name'] + '</h5>';
            
            // Sist lägger vi till listobjektet i vår lista. Det gör man så här:
            list.append(html);
            }
        });
        });
    }
}        


