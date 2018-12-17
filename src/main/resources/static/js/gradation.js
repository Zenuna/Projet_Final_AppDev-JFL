var stompClient = null;
$(document).ready(function(){
    var socket = new SockJS('/webSocket');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/sujet/position/ceinture',function(){

        });
    });
});

function passerCeinture(receveur){
    var ceId = "ch"+receveur.id;
    if(document.getElementById(ceId).checked){
        stompClient.send("/app/gradation/ceinture", {}, JSON.stringify({'texte': receveur.id+'-'+$("#admin").text() , 'creation': 0 , 'de' : "PASSERCEINTURE", 'avatar': 'GR8SUCCESS', 'type':"publique" }));
    }
    else{
        stompClient.send("/app/gradation/ceinture", {}, JSON.stringify({'texte': receveur.id+'-'+$("#admin").text() , 'creation': 0 , 'de' : "PASSERCEINTURE", 'avatar': 'TRUMP', 'type':"publique" }));
    }
    setTimeout(function(){
        location.reload(true)
    },1000);
}



