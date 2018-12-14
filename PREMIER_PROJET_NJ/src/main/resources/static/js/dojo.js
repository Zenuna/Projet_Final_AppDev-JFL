var stompClient = null;
var messages = new Array();


function afficherReponse(message) {
    var theDate = new Date(message.creation);
    var Year = theDate.getFullYear();
    var Month = theDate.getMonth()+1;
    if(Month.toString().length === 1){
        Month = "0" + Month.toString();
    }
    var Jour = theDate.getDate();
    if(Jour.toString().length ===1){
        Jour = "0" + Jour.toString();
    }
    var Heure = theDate.getHours();
    if(Heure.toString().length === 1){
        Heure = "0" + Heure.toString();
    }
    var Minute = theDate.getMinutes();
    if(Minute.toString().length === 1){
        Minute = "0"+Minute.toString();
    }
    var Second = theDate.getSeconds();
    if(Second.toString().length === 1) {
        Second = "0"+Second.toString();
    }

    var chaineTemps = Heure+':'+Minute+':'+Second + 'le' + Year+'-'+Month+'-'+Jour;

    $("#reponses").append("<tr style='text-align: center'>"    + "<td><img width=100 height=75 src='" +  message.avatar    + "'/></td>" +
        "<td>Message " + message.type    + " de " + message.de    +  " à " + chaineTemps + "</td>" +
        "<td>Texte:" + message.texte + "</td>"+
        "</tr>");
}

function connexion() {
    var socket = new SockJS('/webSocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/position/reponse', function (reponse) {
            var type = "publique";
            var id = JSON.parse(reponse.body).id;
            var de = JSON.parse(reponse.body).de;
            var texte = JSON.parse(reponse.body).texte;
            var avatar = JSON.parse(reponse.body).avatar;
            var creation = JSON.parse(reponse.body).creation;

            var message = {id : id, de: de, texte:texte, creation:creation,  avatar:avatar, type:type};
            nb = messages.push(message);

            //On ne garde que les messages les plus récents (10 derniers)
            if (nb > 10)
                messages = messages.slice(1, 11);

            $("#reponses").empty();
            messages.forEach(function(message) {
                afficherReponse( message  );
            });
        });
        if($("#lblCompte").length === 0){
            stompClient.subscribe('/position/reponseprive', function (reponse) {
                var type = "privé"; //JSON.parse(reponse.body).type;
                var id = JSON.parse(reponse.body).id;
                var de = JSON.parse(reponse.body).de;
                var texte = JSON.parse(reponse.body).texte;
                var avatar = JSON.parse(reponse.body).avatar;
                var creation = JSON.parse(reponse.body).creation;

                var message = {id : id, de: de, texte:texte, creation:creation,  avatar:avatar, type:type};
                nb = messages.push(message);

                //On ne garde que les messages les plus récents (10 derniers)
                if (nb > 10)
                    messages = messages.slice(1, 11);

                $("#reponses").empty();
                messages.forEach(function(message) {
                    afficherReponse( message  );
                });
            });
        }

    });
}

function envoyerMessage() {
    var creation = Date.now();
    var de = $("#alias").text();
    var avatarConnecter = $("#uneImageSrc").text();
    stompClient.send('/app/message/publique', {}, JSON.stringify({'texte': $("#texte").val() , 'creation': creation , 'de' : de, 'avatar': avatarConnecter, 'type':'publique' }));
    $("#texte").val("");
}

function envoyerMessagePrive() {
    var creation = Date.now();
    var de = $("#alias").text();
    var avatarConnecter = $("#uneImageSrc").text();
    stompClient.send('/app/message/prive', {}, JSON.stringify({'texte': $("#texte").val() , 'creation': creation , 'de' : de, 'avatar': avatarConnecter, 'type':'privé'}));
    $("#texte").val("");
}

$(document).ready(function(){
    connexion();
    $( "#envoyerPublique" ).click(function() { envoyerMessage(); });
    $("#envoyerPrive").click(function() { envoyerMessagePrive(); });
});