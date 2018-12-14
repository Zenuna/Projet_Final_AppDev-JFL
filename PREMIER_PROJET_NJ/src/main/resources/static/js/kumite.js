var stompClient = null;
var combattantRouge = "";
var combattantBlanc = "";
var arbitre = "";
var avatarRouge;
var avatarBlanc;
var avatarArbitre;
$(document).ready(function(){
    connexion();
});

function connexion() {
    var socket = new SockJS('/webSocket');
    stompClient = Stomp.over(socket);


    stompClient.connect({}, function (frame) {
        // SPECTATEUR
        stompClient.subscribe('/position/quiestspectateur', function (reponse) {
            if(JSON.parse(reponse.body).de === "SERVEUR"){
                var creation = JSON.parse(reponse.body).creation;
                if ($("#spec").attr("checked") === true) {
                    var courriel = $("#compteCourriel").text();
                    if(courriel !== combattantBlanc && courriel !== combattantRouge) {
                        stompClient.send("/app/position_client/spectateur", {}, JSON.stringify({
                            'texte': courriel,
                            'creation': creation,
                            'de': "POSITION",
                            'avatar': "",
                            'type': "spectateur"
                        }));
                    }
                }
            }
        });
        stompClient.subscribe('/position/spectateur', function (reponse) {
            var texte = JSON.parse(reponse.body).texte;
            var img = "imgSiegeSpec";
            var inc = 0;
            var test;
            $.each(JSON.parse(texte), function (index, value) {
                    $.get("/TrouverAvatar/" + value.avatar, function (data) {
                        inc++;
                        $("#" + img + inc).attr('src', data);
                        test = inc;
                    });
            });
            for (var i = JSON.parse(texte).length; i < 13; i++) {
                $("#" + img + i).attr('src', "images/siege.jpg");
            }

        });
        // COMBATTANT
        stompClient.subscribe('/position/quiestcombattant', function (reponse) {
            if(JSON.parse(reponse.body).de === "SERVEUR") {
                var creation = JSON.parse(reponse.body).creation;
                if ($("#comp").attr("checked") === true) {
                    var courriel = $("#compteCourriel").text();
                    if(courriel !== combattantRouge && courriel !== combattantBlanc){
                        stompClient.send("/app/position_client/combattant", {}, JSON.stringify({
                            'texte': courriel,
                            'creation': creation,
                            'de': "POSITION",
                            'avatar': "",
                            'type': "combatant"
                        }));
                    }
                }
            }
        });
        stompClient.subscribe('/position/combattant', function (reponse) {
            var texte = JSON.parse(reponse.body).texte;
            var img = "imgSiegeCombattant";
            var inc = 0;
            var test;
            $.each(JSON.parse(texte), function (index, value) {
                    $.get("/TrouverAvatar/" + value.avatar, function (data) {
                        inc++;
                        $("#" + img + inc).attr('src', data);
                        test = inc;
                    });
            });
            for (var i = JSON.parse(texte).length; i < 13; i++) {
                $("#" + img + i).attr('src', "images/siege.jpg");
            }
        });
        stompClient.subscribe('/position/quiestarbitre', function (reponse) {
            if(JSON.parse(reponse.body).de === "SERVEUR") {
                var creation = JSON.parse(reponse.body).creation;
                if ($("#arbitre").attr("checked") === true) {
                    var courriel = $("#compteCourriel").text();
                    stompClient.send("/app/position_client/arbitre", {}, JSON.stringify({
                        'texte': courriel,
                        'creation': creation,
                        'de': "POSITION",
                        'avatar': "",
                        'type': "arbitre"
                    }));
                }
            }
        });
        stompClient.subscribe('/position/debutCombat', function(reponse){
            var DE = JSON.parse(reponse.body).de;
            var creation = JSON.parse(reponse.body).texte.split("|");
            var millisecondsToWait = 1500;
            if(DE === "DEBUTCOMBAT"){
                for(var i = 0; i<creation.length;i++){
                    $.ajax({
                        async: false,
                        type: "GET",
                        url:"TrouverAvatar/" + JSON.parse(creation[i]).avatar,
                        success: [function (repGet) {
                                if(i === 0){
                                    if($("#compteCourriel").text() === JSON.parse(creation[i]).courriel){
                                        $("#positionCombat").text("COMBATTANT ROUGE");
                                    }
                                    $("#imgTatami10").attr('src', repGet);
                                    combattantRouge = JSON.parse(creation[i]).courriel;
                                    avatarRouge = repGet;
                                }
                                else if(i === 1){
                                    if($("#compteCourriel").text() === JSON.parse(creation[i]).courriel){
                                        $("#positionCombat").text("COMBATTANT BLANC");
                                    }
                                    $("#imgTatami1").attr('src', repGet);
                                    combattantBlanc = JSON.parse(creation[i]).courriel;
                                    avatarBlanc = repGet;
                                }
                                else{
                                    if($("#compteCourriel").text() === JSON.parse(creation[i]).courriel){
                                        $("#positionCombat").text("ARBITRE");
                                    }
                                    $("#imgTatamiArbitre").attr('src',repGet);
                                    arbitre = JSON.parse(creation[i]).courriel;
                                    avatarArbitre = repGet;
                                }
                        }]

                    });
                }
                $("#paroleArbitre").text("Rei!");
                setTimeout(function() {
                    $("#imgTatami1").attr('src','images/tatami.jpg');
                    $("#imgTatami10").attr('src','images/tatami.jpg');
                    $("#imgTatami3").attr('src',avatarBlanc);
                    $("#imgTatami8").attr('src',avatarRouge);
                    //$("#imgTatami3").css('transition-property','transform');
                    //$("#imgTatami3").css('transition-duration''0.5s');
                    $("#imgTatami3").css('transform',"rotate(45deg)");
                    $("#imgTatami8").css('transform',"rotate(-45deg)");
                    setTimeout(function(){
                        $("#imgTatami3").css('transform',"rotate(0deg)");
                        $("#imgTatami8").css('transform',"rotate(0deg)");
                        $("#paroleArbitre").text("Hajime!");
                        setTimeout(function(){
                            stompClient.send("/app/debutCombat", {}, JSON.stringify({
                                'texte': "ONVEUTDESCHIFFRES",
                                'creation': "",
                                'de': "",
                                'avatar': "",
                                'type': ""
                            }));
                        },millisecondsToWait)
                    },millisecondsToWait)
                }, millisecondsToWait);
            }
            else if(DE === "RECEVOIRCHIFFRE"){
                var randomBlanc = creation[0];
                var randomRouge = creation[1];
                setTimeout(function(){
                    $("#paroleBlanc").text(MainJouee(randomBlanc));
                    $("#paroleRouge").text(MainJouee(randomRouge));
                    $("#paroleArbitre").text("");
                    setTimeout(function(){
                        var quiAGagner = quiGagne(randomBlanc,randomRouge);
                        if(quiAGagner === "NULLE"){
                            $("#paroleArbitre").text("PATATE!");
                            $("#imgVictoireRouge").attr('hidden','')
                            $("#imgVictoireBlanc").attr('hidden','')
                        }
                        else{
                            if(quiAGagner === "ROUGE"){
                                $("#imgVictoireRouge").attr('hidden','')
                            }
                            else{
                                $("#imgVictoireBlanc").attr('hidden','')
                            }
                            $("#paroleArbitre").text("IPPON!");
                        }
                        $("#imgTatami3").css('transform',"rotate(45deg)");
                        $("#imgTatami8").css('transform',"rotate(-45deg)");
                        setTimeout(function(){
                            $("#imgTatami1").attr('src',avatarBlanc);
                            $("#imgTatami10").attr('src',avatarRouge);
                            $("#imgTatami3").attr('src','images/tatami.jpg');
                            $("#imgTatami8").attr('src','images/tatami.jpg');
                            $("#imgTatami3").css('transform',"rotate(0deg)");
                            $("#imgTatami8").css('transform',"rotate(0deg)");
                            $("#imgVictoireBlanc").attr('hidden','hidden');
                            $("#imgVictoireRouge").attr('hidden','hidden');
                            $("#paroleRouge").text("");
                            $("#paroleBlanc").text("");
                            $("#paroleArbitre").text("");
                            stompClient.send("/app/finCombat", {}, JSON.stringify({
                                'texte': "FINCOMBAT",
                                'creation': "",
                                'de': "",
                                'avatar': "",
                                'type': ""
                            }));
                            combattantRouge = "";
                            combattantBlanc = "";
                            arbitre = "";
                            avatarRouge = "";
                            avatarBlanc = "";
                            avatarArbitre = "";
                            $("#positionCombat").text("");
                            $("#imgTatamiArbitre").attr('src','images/tatami.jpg');
                            for(var i = 0; i <=10;i++){
                                $("#imgTatami"+i).attr('src','images/tatami.jpg');
                            }
                        },millisecondsToWait)

                    },millisecondsToWait)
                },millisecondsToWait)
            }

        })
    });
}
function cliqueRadioButton(element) {
    var creation = Date.now();
    var de = $("#alias").text();
    var avatarConnecter = $("#uneImageSrc").text();
    stompClient.send("/app/position_client", {}, JSON.stringify({'texte': element.value , 'creation': creation , 'de' : "position", 'avatar': avatarConnecter, 'type':"publique" }));
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



