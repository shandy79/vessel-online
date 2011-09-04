<!--
// JavaScript: tools.js
// Code to enable animation of Launch bar and write HTML to page.
// Steven Handy (created: 04/21/2001, last mod: 08/10/2008)

// Var and function to control sliding display of Launch panel
// Is not expanded by default, then slides onto screen
var expand = 0;
var onImgAry = new Array();
var offImgAry = new Array();

function preloadImg() {
  var htmlRoot = 'http://sjhandy1.tripod.com/cgi-bin/';

  // Actual img name should be divId plus their index for these arrays
  if (document.images) {
    var onImg = new Array(htmlRoot + 'img/launch/cityY.gif',htmlRoot + 'img/launch/dictionaryY.gif',htmlRoot + 'img/launch/encyclopediaY.gif',
                          htmlRoot + 'img/launch/mapsY.gif',htmlRoot + 'img/launch/newsY.gif',htmlRoot + 'img/launch/searchY.gif',
                          htmlRoot + 'img/launch/sportsY.gif',htmlRoot + 'img/launch/weatherY.gif',htmlRoot + 'img/launch/gmailY.gif',
                          htmlRoot + 'img/launch/yahooY.gif');
    var offImg = new Array(htmlRoot + 'img/launch/city.gif',htmlRoot + 'img/launch/dictionary.gif',htmlRoot + 'img/launch/encyclopedia.gif',
                           htmlRoot + 'img/launch/maps.gif',htmlRoot + 'img/launch/news.gif',htmlRoot + 'img/launch/search.gif',
                           htmlRoot + 'img/launch/sports.gif',htmlRoot + 'img/launch/weather.gif',htmlRoot + 'img/launch/gmail.gif',
                           htmlRoot + 'img/launch/yahoo.gif');

    for (var j = 0; j < onImg.length; j++) {
      onImgAry[j] = new Image();
      onImgAry[j].src = onImg[j];
      offImgAry[j] = new Image();
      offImgAry[j].src = offImg[j];
    }
  }
  return true;
}

function moveLaunch() {
  if (expand == 0) {
    for (i = 0; i < 10; i++) {
      setTimeout("shiftBy('launch', 0, 10)", i * 100);
    }
    expand = 1;
  } else {
    for (i = 0; i < 10; i++) {
      setTimeout("shiftBy('launch', 0, -10)", i * 100);
    }
    expand = 0;
  }
  return true;
}
//-->
