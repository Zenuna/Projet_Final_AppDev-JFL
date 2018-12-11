var stompClient = null;
var maxDelta = 0;
var minDelta = 999999;

var messages = new Array();
var avatar = new String("data:image/png;base64,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guD+GIX2PQaE4A8R0B9W/hiEr0PwRQS8/xYD93ncP4bhPWH9Jc2vcXCfRn6F4stAfM7a54Hdp2D8t1D8oRjcr6HI2speWPAStUPyIPXZWWcW9bcVvnAO0yGK8bI7dVYBegvul+xr1Z9WgvJIvBsKGBurhBsIqyMb+kE7r/pjMyaN88Wuv886te31sXl8aXmYzP0HLYBiuWzw2JsAABAQaVRYdFhNTDpjb20uYWRvYmUueG1wAAAAAAA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/Pgo8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJYTVAgQ29yZSA0LjQuMC1FeGl2MiI+CiA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICB4bWxuczppcHRjRXh0PSJodHRwOi8vaXB0Yy5vcmcvc3RkL0lwdGM0eG1wRXh0LzIwMDgtMDItMjkvIgogICAgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iCiAgICB4bWxuczpzdEV2dD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL3NUeXBlL1Jlc291cmNlRXZlbnQjIgogICAgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiCiAgICB4bWxuczpwbHVzPSJodHRwOi8vbnMudXNlcGx1cy5vcmcvbGRmL3htcC8xLjAvIgogICAgeG1sbnM6R0lNUD0iaHR0cDovL3d3dy5naW1wLm9yZy94bXAvIgogICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICAgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIgogICB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOkY2RENCQ0MyQ0Y2NDExRTc5MkRGQjFGRjczQUJBNjk2IgogICB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjQ1NDQ5YjMxLWUzYmMtNDNmMi04OWUxLTI0MWQ1YmQxYzkyYyIKICAgeG1wTU06T3JpZ2luYWxEb2N1bWVudElEPSJ4bXAuZGlkOjJlNzlmYTM1LTVlZTctNDExMC04NGJkLTQ0NTY3YzI4Y2ViZCIKICAgR0lNUDpBUEk9IjIuMCIKICAgR0lNUDpQbGF0Zm9ybT0iTGludXgiCiAgIEdJTVA6VGltZVN0YW1wPSIxNTM5NjE0ODg1MjAwOTMxIgogICBHSU1QOlZlcnNpb249IjIuMTAuNiIKICAgZGM6Rm9ybWF0PSJpbWFnZS9wbmciCiAgIHhtcDpDcmVhdG9yVG9vbD0iR0lNUCAyLjEwIj4KICAgPGlwdGNFeHQ6TG9jYXRpb25DcmVhdGVkPgogICAgPHJkZjpCYWcvPgogICA8L2lwdGNFeHQ6TG9jYXRpb25DcmVhdGVkPgogICA8aXB0Y0V4dDpMb2NhdGlvblNob3duPgogICAgPHJkZjpCYWcvPgogICA8L2lwdGNFeHQ6TG9jYXRpb25TaG93bj4KICAgPGlwdGNFeHQ6QXJ0d29ya09yT2JqZWN0PgogICAgPHJkZjpCYWcvPgogICA8L2lwdGNFeHQ6QXJ0d29ya09yT2JqZWN0PgogICA8aXB0Y0V4dDpSZWdpc3RyeUlkPgogICAgPHJkZjpCYWcvPgogICA8L2lwdGNFeHQ6UmVnaXN0cnlJZD4KICAgPHhtcE1NOkhpc3Rvcnk+CiAgICA8cmRmOlNlcT4KICAgICA8cmRmOmxpCiAgICAgIHN0RXZ0OmFjdGlvbj0ic2F2ZWQiCiAgICAgIHN0RXZ0OmNoYW5nZWQ9Ii8iCiAgICAgIHN0RXZ0Omluc3RhbmNlSUQ9InhtcC5paWQ6ZWE3OTY3MjgtOWM1Ny00MWY5LTliNmItN2MwMmU2MTZjZTc0IgogICAgICBzdEV2dDpzb2Z0d2FyZUFnZW50PSJHaW1wIDIuMTAgKExpbnV4KSIKICAgICAgc3RFdnQ6d2hlbj0iLTA0OjAwIi8+CiAgICA8L3JkZjpTZXE+CiAgIDwveG1wTU06SGlzdG9yeT4KICAgPHhtcE1NOkRlcml2ZWRGcm9tCiAgICBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkY2RENCQ0MwQ0Y2NDExRTc5MkRGQjFGRjczQUJBNjk2IgogICAgc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpGNkRDQkNCRkNGNjQxMUU3OTJERkIxRkY3M0FCQTY5NiIvPgogICA8cGx1czpJbWFnZVN1cHBsaWVyPgogICAgPHJkZjpTZXEvPgogICA8L3BsdXM6SW1hZ2VTdXBwbGllcj4KICAgPHBsdXM6SW1hZ2VDcmVhdG9yPgogICAgPHJkZjpTZXEvPgogICA8L3BsdXM6SW1hZ2VDcmVhdG9yPgogICA8cGx1czpDb3B5cmlnaHRPd25lcj4KICAgIDxyZGY6U2VxLz4KICAgPC9wbHVzOkNvcHlyaWdodE93bmVyPgogICA8cGx1czpMaWNlbnNvcj4KICAgIDxyZGY6U2VxLz4KICAgPC9wbHVzOkxpY2Vuc29yPgogIDwvcmRmOkRlc2NyaXB0aW9uPgogPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgIAo8P3hwYWNrZXQgZW5kPSJ3Ij8+UhDx8AAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAAuIwAALiMBeKU/dgAAAAd0SU1FB+IKDw4wBSA596gAABP1SURBVHja7d1betvGloDRveFMwnPJVDI6T8VzySSi2nkgKVGyJBIkLgVgrZe2+5zuLwFRrL8KF2ZVBQAA0xkcAgAAgQUAILAAAAQWAAACCwBAYAEACCwAAAQWAIDAAgAQWAAACCwAAIEFACCwAAAEFgAAAgsAQGABAAgsAAAEFgCAwAIAEFgAAAgsAACBBQAgsAAAePOXQwD0JDPr0f/b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");


function setConnexion(connexion) {
    $("#connexion").prop("disabled", connexion);
     $("#connexion").prop("disabled", connexion);
    $("#deConnexion").prop("disabled", !connexion);

    if (connexion) {
        $("#conversation").show();
        $("#envoyer").prop("disabled", false);
        $("#deConnexion").show();
        $("#connexion").hide();
    }
    else {
        $("#conversation").hide();
        $("#envoyer").prop("disabled", true);
        $("#deConnexion").hide();
        $("#connexion").show();
    }
    $("#reponses").html("");
}

function connexion() {
    var socket = new SockJS('/webSocket');
    stompClient = Stomp.over(socket);


    stompClient.connect({}, function (frame) {
        setConnexion(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/sujet/reponse', function (reponse) {
            var id = JSON.parse(reponse.body).id;
            var de = JSON.parse(reponse.body).de;
            var texte = JSON.parse(reponse.body).texte;
            var avatar = JSON.parse(reponse.body).avatar;
            var creation = JSON.parse(reponse.body).creation;

            var delta = calculerDelta(creation);
            calculerDeltaMinMax(delta);
            afficherMinMaxDelta();

            var message = {id : id, de: de, texte:texte, creation:creation, delta:delta, avatar:avatar};
            nb = messages.push(message);

            //On ne garde que les messages les plus récents (10 derniers)
            if (nb > 10)
                messages = messages.slice(1, 11);

            $("#reponses").empty();
            messages.forEach(function(message) {
                   afficherReponse( message  );
             });
        });
    });
}

//calculerDelta calcule la différence de temps en millisecondes entre la création du message sur le serveur
// et la réception par ce client. Typiquement, j'ai mesuré 10 millisecondes.
function calculerDelta(creation)
{
    var maintenant = Date.now();
    let delta = maintenant - creation;
    return delta;
}

//calculerDeltaMinMax permet de déterminer si la valeur courante de delta est plus petite que la valeur minimale et ou plus grande que la valeur maximale.
//Dans l'affirmative, la valeur de minDelta ou maxDelta est mise à jour.
function calculerDeltaMinMax(delta) {
    if (delta < minDelta)
    {
        minDelta = delta;
    }

    if (delta > maxDelta)
    {
        maxDelta = delta;
    }
}

function deConnexion() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnexion(false);

}

function envoyerMessage() {
    var creation = Date.now();
    var de = $("#de").val()

    stompClient.send("/app/message", {}, JSON.stringify({'texte': $("#texte").val() , 'creation': creation , 'de' : de, 'avatar': avatar }));
}

function afficherReponse(message) {

    $("#reponses").append("<tr>"    + "<td><img width=100 height=75 src='" +  message.avatar    + "'/></td>" +
                                     "<td>Id:" + message.id    + "</td>" +
                                     "<td>De:" + message.de    + "</td>" +
                                     "<td>Texte:" + message.texte + "</td>" +
                                     "<td>Delta en millisecondes:" + message.delta + "</td>" +
                                     "</tr>");
}

function afficherMinMaxDelta()
{
    $("#mindelta").val(minDelta);
    $("#maxdelta").val(maxDelta);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connexion" ).click(function() { connexion(); });
    $( "#deConnexion" ).click(function() { deConnexion(); });
    $( "#envoyer" ).click(function() { envoyerMessage(); });
});

$(document).ready(function(){
    $("#deConnexion").hide();

    $('#btnImage').hide();
    $('#uneVideo').hide();
    $('#uneImage').show();


    const btnVideo = document.querySelector('#btnVideo');
    const btnImage = document.querySelector('#btnImage');
    const uneImage = document.querySelector('#uneImage');
    const uneVideo = document.querySelector('#uneVideo');
    const unCanvas = document.querySelector('#unCanvas');

    uneImage.src =  avatar;

    btnVideo.onclick = function() {

          $('#btnImage').show();
          $('#btnVideo').hide();
          $('#uneVideo').show();
          $('#uneImage').hide();

         // Camera résolution la plus proche de 200x150.
         var constraints = { video: { width: 150, height: 200 } };

         navigator.mediaDevices.getUserMedia(constraints)
         .then(function(mediaStream) {

           uneVideo.srcObject = mediaStream;
           uneVideo.onloadedmetadata = function(e) {
             uneVideo.play();
           };
         })
         .catch(function(err) { console.log(err.name + ": " + err.message); }); // always check for errors at the end.
    }

    btnImage.onclick = uneVideo.onclick = function() {

      $('#btnImage').hide();
      $('#btnVideo').show();
      $('#uneVideo').hide();
      $('#uneImage').show();

      unCanvas.width =   100;
      unCanvas.height =  75;

      //https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/drawImage
      unCanvas.getContext('2d').drawImage(uneVideo, 0, 0,uneVideo.videoWidth,uneVideo.videoHeight ,0,0, 100,75 );
      uneVideo.srcObject.stop();

      image = unCanvas.toDataURL("image/jpeg");
      avatar =  image;
      uneImage.src =  avatar;


    };

    function gererReussite(stream) {
      uneVideo.srcObject = stream;
    }
    function gererErreur(erreur) {
      console.error('Erreur: ', erreur);
    }
});

