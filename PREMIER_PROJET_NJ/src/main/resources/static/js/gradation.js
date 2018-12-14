var stompClient = null;
$(document).ready(function(){
    var socket = new SockJS('/webSocket');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/position/ceinture',function(){

        });
        stompClient.subscribe('/position/ancien',function(){

        });
        stompClient.subscribe('/position/sensei',function(){

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

function devenirAncien(receveur){
    var unID = receveur.id;
    var id = unID.replace('ancien','');
    stompClient.send("/app/gradation/ancien", {}, JSON.stringify({'texte': id+'-'+$("#admin").text() , 'creation':0  , 'de' : "DEVENIRANCIEN", 'avatar': 'GR8SUCCESS', 'type':"publique" }));
    setTimeout(function(){
        location.reload(true)
    },1000);
}

function versSensei(receveur){
    var unId = receveur.id;
    var id = unId.substr(3,unId.length);
    if(document.getElementById('chSen'+id).checked){
        stompClient.send("/app/gradation/sensei", {}, JSON.stringify({'texte': id , 'creation': 0 , 'de' : "DEVENIRSENSEI", 'avatar': 'GR8SUCCESS', 'type':"publique" }));
    }
    else{
        stompClient.send("/app/gradation/sensei", {}, JSON.stringify({'texte': id , 'creation':0  , 'de' : "DEVENIRSENSEI", 'avatar': 'TRUMP', 'type':"publique" }));
    }
    setTimeout(function(){
        location.reload(true)
    },1000);
}