<!--
// JavaScript: splash.js
// Code to enable DHTML features of splash page.
// Steven Handy (created: 02/18/2002, last mod: 07/28/2005)

// Preloading images for image swapping
var onImgAry = new Array();
var offImgAry = new Array();

function preloadImg() {
  // Actual img name should be divId plus their index for these arrays
  if (document.images) {
    var onImg = new Array('img/splash/innovRed.gif','img/splash/dedicRed.gif','img/splash/intelRed.gif','img/splash/styleRed.gif','img/splash/enterRed.gif');
    var offImg = new Array('img/splash/innov.gif','img/splash/dedic.gif','img/splash/intel.gif','img/splash/style.gif','img/splash/enter.gif');
 
    for (var j = 0; j < onImg.length; j++) {
      onImgAry[j] = new Image();
      onImgAry[j].src = onImg[j];
      offImgAry[j] = new Image();
      offImgAry[j].src = offImg[j];
    }
  }
  return true;
}


// Runs image swap and status bar during mouseover on vessel
function rollVesselOver() {
  show("rays");
  statusMsg("To the four corners of the Earth...");
  return true;
}

// Runs image swap and resets status bar after mouseout on vessel
function rollVesselOut() {
  hide("rays");
  statusMsg("");
  return true;
}


// Runs image swap and status bar for mouseover for words
function rollWordOver(lyr, i) {
  var def = lyr + "Def";
  show(def);
  imgOn(lyr, i);
  statusMsg("Feed your head, kid!  Vessel Online, holla.");
  return true;
}

// Runs image swap and resets status bar for mouseout for words
function rollWordOut(lyr, i) {
  var def = lyr + "Def";
  hide(def);
  imgOff(lyr,i);
  statusMsg("");
  return true;
}


// Lights up all words and sets status bar for enter
function rollEnterOver() {
  imgOn("enter", 4);
  imgOn("style", 3);
  imgOn("innov", 0);
  imgOn("dedic", 1);
  imgOn("intel", 2);
  statusMsg("Click here to enter...");
  return true;
}

// Turns off all words and resets status bar for enter
function rollEnterOut() {
  imgOff("enter", 4);
  imgOff("style", 3);
  imgOff("innov", 0);
  imgOff("dedic", 1);
  imgOff("intel", 2);
  statusMsg("");
  return true;
}


// Resizes image by passed value in NN or resizes obj in IE
/* function resizeImgBy(obj, amt) {
  var space = Math.round(amt/2);
  if (isNN4) {
    document[obj].clip.bottom += space;
    document[obj].clip.top -= space;
    document[obj].clip.right += space;
    document[obj].clip.left -= space;
  } else {
    var img = obj.concat("Pic");
    document[img].height += amt;
    document[img].width += amt;
    document[img].hspace -= space;
    document[img].vspace -= space;
  }
} */

// vars for splashDance to control animation speed
var lng = 150;
var count = 0;

// Animation for splash
function splashDance() {
  count = 8;
  hide("enter");
  setTimeout("hide('dedic');hide('intel');hide('innov');imgOn('style',3);show('styleDef')", count*lng);
  count += 4;
  setTimeout("hide('style');hide('styleDef');show('innov');imgOn('innov',0);show('innovDef')", count*lng);
  count+=4;
  setTimeout("hide('innov');hide('innovDef');show('dedic');imgOn('dedic',1);show('dedicDef')", count*lng);
  count+=4;
  setTimeout("hide('dedic');hide('dedicDef');show('intel');imgOn('intel',2);show('intelDef')", count*lng);
  count+=4;
  setTimeout("hide('intelDef');show('dedic')", count*lng);
  count+=4;
  setTimeout("show('innov')", count*lng);
  count+=4;
  setTimeout("show('style')", count*lng);
  count+=4;
  setTimeout("show('rays')", count*lng);
  setTimeout("imgOff('intel',2);imgOff('style',3);imgOff('innov',0);imgOff('dedic',1)", count*lng);
  count+=8;
/*
  for (i = 0; i < 17; i++) {
    setTimeout("resizeImgBy('vessel', -10)", count*lng);
    count++;
  }
  for (i = 0; i < 17; i++) {
    setTimeout("resizeImgBy('vessel', 10)", count*lng);
    count++;
  }
*/
  setTimeout("hide('rays');show('enter')", count*lng);
  return true;
}

function loadDance() {
  if (preloadImg() && splashDance()) {
    return true;
  } else {
    alert('Load failure!!');
    return false;
  }
}
//-->
