var stompClient = null;
var combattantRouge = "";
var combattantBlanc = "";
var arbitre = "";
var avatarRouge;
var avatarBlanc;
var avatarArbitre;
$(document).ready(function(){
    connexion();
    refreshListeSpectateur();
    refreshListeAttente();
});

function connexion() {
    var socket = new SockJS('/webSocket');
    stompClient = Stomp.over(socket);


    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/sujet/radiogroup', function (reponse) {
            refreshListeSpectateur();
            refreshListeAttente();
        });
        stompClient.subscribe('/sujet/debutCombat', function (reponse) {
            refreshListeSpectateur();
            refreshListeAttente();
            var creation = JSON.parse(reponse.body).texte.split("-A-");
            for (var i = 0; i < creation.length; i++) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "TrouverAvatarSupreme/" + JSON.parse(creation[i]).avatar,
                    success: [function (repGet) {
                        if (i === 0) {
                            $("#imgTatami10").attr('src', repGet);
                            combattantRouge = JSON.parse(creation[i]).courriel;
                            avatarRouge = repGet;
                        }
                        else if (i === 1) {
                            $("#imgTatami1").attr('src', repGet);
                            combattantBlanc = JSON.parse(creation[i]).courriel;
                            avatarBlanc = repGet;
                        }
                        else {
                            $("#imgTatamiArbitre").attr('src', repGet);
                            arbitre = JSON.parse(creation[i]).courriel;
                            avatarArbitre = repGet;
                        }
                    }]

                });
            }
        });
        stompClient.subscribe('/sujet/envoyerChiffre', function (reponse) {
            var chainon = JSON.parse(reponse.body).texte.split("-A-");
            for (var i = 0; i < chainon.length; i++) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "TrouverAvatarSupreme/" + JSON.parse(chainon[i]).avatar,
                    success: [function (repGet) {
                        if (i === 0) {
                            combattantRouge = JSON.parse(chainon[i]).courriel;
                            avatarRouge = repGet;
                        }
                        else if (i === 1) {
                            combattantBlanc = JSON.parse(chainon[i]).courriel;
                            avatarBlanc = repGet;
                        }
                        else {
                            arbitre = JSON.parse(chainon[i]).courriel;
                            avatarArbitre = repGet;
                        }
                    }]
                })
            }

            var creation = JSON.parse(reponse.body).avatar.split("|");
            var millisecondsToWait = 1000;
            $("#paroleArbitre").text("Rei!");
            setTimeout(function(){

            $("#imgTatami1").attr('src', 'images/tatami.jpg');
            $("#imgTatami10").attr('src', 'images/tatami.jpg');
            $("#imgTatami3").attr('src', avatarBlanc);
            $("#imgTatami8").attr('src', avatarRouge);
            $("#imgTatami3").css('transform', "rotate(45deg)");
            $("#imgTatami8").css('transform', "rotate(-45deg)");
            setTimeout(function(){
                $("#imgTatami3").css('transform', "rotate(0deg)");
                $("#imgTatami8").css('transform', "rotate(0deg)")
                $("#paroleArbitre").text("Hajime!");
                var randomBlanc = creation[0];
                var randomRouge = creation[1];
                setTimeout(function () {
                    $("#paroleBlanc").text(MainJouee(randomBlanc));
                    $("#paroleRouge").text(MainJouee(randomRouge));
                    $("#paroleArbitre").text("");
                    setTimeout(function () {
                        var quiAGagner = quiGagne(randomBlanc, randomRouge);
                        if (quiAGagner === "NULLE") {
                            $("#paroleArbitre").text("PATATE!");
                            $("#imgVictoireRouge").attr('hidden', '');
                            $("#imgVictoireBlanc").attr('hidden', '')
                        }
                        else {
                            if (quiAGagner === "ROUGE") {
                                $("#imgVictoireRouge").attr('hidden', '')
                            }
                            else {
                                $("#imgVictoireBlanc").attr('hidden', '')
                            }
                            $("#paroleArbitre").text("IPPON!");
                        }
                        $("#imgTatami3").css('transform', "rotate(45deg)");
                        $("#imgTatami8").css('transform', "rotate(-45deg)");

                    },millisecondsToWait)
                },millisecondsToWait);

            },millisecondsToWait)
            }, millisecondsToWait)
        });
        stompClient.subscribe('/sujet/finCombat', function (reponse) {
            var millisecondsToWait = 1000;
            setTimeout(function(){
                $("#imgTatami3").css('transform', "rotate(0deg)");
                $("#imgTatami8").css('transform', "rotate(0deg)");

                $("#imgTatami1").attr('src', avatarBlanc);
                $("#imgTatami10").attr('src', avatarRouge);
                $("#imgTatami3").attr('src', 'images/tatami.jpg');
                $("#imgTatami8").attr('src', 'images/tatami.jpg');


                setTimeout(function(){
                    $("#imgTatamiArbitre").attr('src', 'images/tatami.jpg');
                    for (var i = 0; i <= 10; i++) {
                        $("#imgTatami" + i).attr('src', 'images/tatami.jpg');
                    }
                    refreshListeAttente()
                    refreshListeSpectateur()
                    $("#imgVictoireBlanc").attr('hidden', 'hidden');
                    $("#imgVictoireRouge").attr('hidden', 'hidden');
                    $("#paroleRouge").text("");
                    $("#paroleBlanc").text("");
                    $("#paroleArbitre").text("");
                    /*stompClient.send("/app/finCombat", {}, JSON.stringify({
                        'texte': "FINCOMBAT",
                        'creation': "",
                        'de': "",
                        'avatar': "",
                        'type': ""
                    }));*/
                    combattantRouge = "";
                    combattantBlanc = "";
                    arbitre = "";
                    avatarRouge = "";
                    avatarBlanc = "";
                    avatarArbitre = "";
                    $("#positionCombat").text("");

                }, millisecondsToWait)


            },millisecondsToWait)
        });
    });
}
function refreshListeSpectateur(){
    var inc = 0
    $.get("/ListeSpectateur", function (data) {
        async : false;
        inc = 0;
        $.each(JSON.parse(data.substr(3,data.length)), function (index, value){
            async : false;
            $.get("/TrouverAvatarSupreme/" + value.avatar, function (data) {
                async : false;
                inc++;
                $("#" + "imgSiegeSpec" + inc).attr('src', data);
            });
        });
        for(var i = inc+1; i <= 12; i++){
            $("#" + "imgSiegeSpec" + i).attr('src', 'images/siege.jpg');
        }
    });
}
function refreshListeAttente(){
    var inc = 0
    $.get("/ListeAttente", function (data) {
        async : false;
        inc = 0;
        $.each(JSON.parse(data.substr(3,data.length)), function (index, value){
            async : false;
            $.get("/TrouverAvatarSupreme/" + value.avatar, function (data) {
                async : false;
                inc++;
                $("#" + "imgSiegeCombattant" + inc).attr('src', data);
            });
        });
        for(var i = inc+1; i <= 12; i++){
            $("#" + "imgSiegeCombattant" + i).attr('src', 'images/siege.jpg');
        }
    });
}
function afficherReponse(message) {
    $("#reponses").append("<tr style='text-align: center'>"    + "<td><img width=100 height=75 src='" +  message.avatar    + "'/></td>" +
        "<td>Message " + message.type    + " de " + message.de    +  " à " + chaineTemps + "</td>" +
        "<td>Texte:" + message.texte + "</td>"+
        "</tr>");
}

function MainJouee(chiffre){
    switch(chiffre){
        case "1":
            return "ROCHE"
        case "2":
            return "PAPIER"
        case "3":
            return "CISEAUX"
    }
}

function quiGagne(chiffreBlanc, chiffreRouge){
    if(chiffreBlanc === chiffreRouge){
        return "NULLE";
    }
    else{
        switch(chiffreBlanc){
            case "1":
                if(chiffreRouge === "2") return "ROUGE";
                else return "BLANC";
            case "2":
                if(chiffreRouge === "3") return "ROUGE";
                else return "BLANC";
            case "3":
                if(chiffreRouge === "1") return "ROUGE";
                else return "BLANC";
        }
    }
}



