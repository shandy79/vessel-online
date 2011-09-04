<!--
// JavaScript: left.js
// Code to enable DHTML features in the Nav frame.
// Steven Handy (created: 02/18/2002, last mod: 08/16/2002)

var currMenu = "homMenu";
var onImgAry = new Array();
var offImgAry = new Array();

function preloadImg() {
  // Actual img name should be divId plus their index for these arrays
  if (document.images) {
    var onImg = new Array('img/nav/bioNavY.gif','img/nav/intNavY.gif','img/nav/proNavY.gif','img/nav/homNavY.gif');
    var offImg = new Array('img/nav/bioNav.gif','img/nav/intNav.gif','img/nav/proNav.gif','img/nav/homNav.gif');

    for (var j = 0; j < onImg.length; j++) {
      onImgAry[j] = new Image();
      onImgAry[j].src = onImg[j];
      offImgAry[j] = new Image();
      offImgAry[j].src = offImg[j];
    }
  }
  return true;
}

// Calls functions to create changes during onMouseover in navigator
function rollNavOver(lyr, i, msg) {
  imgOn(lyr, i);
  statusMsg(msg);
  return true;
}

// Calls functions to reverse changes when onMouseout in navigator
function rollNavOut(lyr, i) {
  imgOff(lyr, i);
  statusMsg('Welcome to Vessel Online!');
  return true;
}

// Displays proper menu based on which nav graphic is clicked
function changeMenu(menuName) {
  hide(currMenu);
  show(menuName);
  currMenu = menuName;
  return true;
}
// -->
