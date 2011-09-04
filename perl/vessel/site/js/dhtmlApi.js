/* ***********************************************************
DHTMLapi.js  (Ch. 4, pp. 88-94)
 Dynamic HTML:  The Definitive Reference, 2nd Edition
 Danny Goodman, O'Reilly & Associates, ISBN 1-56592-494-0
 http://www.oreilly.com, http://www.dannyg.com
 Copyright 2002 Danny Goodman.  All Rights Reserved.
************************************************************ */
// DHTMLapi.js custom API for cross-platform
// object positioning by Danny Goodman (http://www.dannyg.com).
// Release 2.0. Supports NN4, IE, and W3C DOMs.
//  Corrections from http://www.dannyg.com/support/update9.html
//  and Image Swapping code from Ch. 5, pp. 108-11 added by
//  Steve Handy, 06/03/2005.  Further corrections added 07/28/2005.
// !!! To use this script, you must add 'initDHTMLAPI();' to the onload handler for the <body> !!!

// Global variables
var isCSS, isW3C, isIE4, isNN4, isIE6CSS;

// Initialize upon load to let all browsers establish content objects
function initDHTMLAPI() {
  if (document.images) {
    isCSS = (document.body && document.body.style) ? true : false;
    isW3C = (isCSS && document.getElementById) ? true : false;
    isIE4 = (isCSS && document.all) ? true : false;
    isNN4 = (document.layers) ? true : false;
    isIE6CSS = (document.compatMode && document.compatMode.indexOf("CSS1") >= 0) ? true : false;
  }
}

// Seek nested NN4 layer from string name
function seekLayer(doc, name) {
  var theObj;
  for (var i = 0; i < doc.layers.length; i++) {
    if (doc.layers[i].name == name) {
      theObj = doc.layers[i];
      break;
    }
    // dive into nested layers if necessary
    if (doc.layers[i].document.layers.length > 0) {
      theObj = seekLayer(doc.layers[i].document, name);
    }
  }
  return theObj;
}

// Convert object name string or object reference
// into a valid element object reference
function getRawObject(obj) {
  var theObj;
  if (typeof obj == "string") {
    if (isW3C) {
      theObj = document.getElementById(obj);
    } else if (isIE4) {
      theObj = document.all(obj);
    } else if (isNN4) {
      theObj = seekLayer(document, obj);
    }
  } else {
    // pass through object reference
    theObj = obj;
  }
  return theObj;
}

// Convert object name string or object reference
// into a valid style (or NN4 layer) reference
function getObject(obj) {
  var theObj = getRawObject(obj);
  if (theObj && isCSS) {
    theObj = theObj.style;
  }
  return theObj;
}

// Position an object at a specific pixel coordinate
function shiftTo(obj, x, y) {
  var theObj = getObject(obj);
  if (theObj) {
    if (isCSS) {
      // equalize incorrect numeric value type
      var units = (typeof theObj.left == "string") ? "px" : 0;
      theObj.left = x + units;
      theObj.top = y + units;
    } else if (isNN4) {
      theObj.moveTo(x,y);
    }
  }
}

// Move an object by x and/or y pixels
function shiftBy(obj, deltaX, deltaY) {
  var theObj = getObject(obj);
  if (theObj) {
    if (isCSS) {
      // equalize incorrect numeric value type
      var units = (typeof theObj.left == "string") ? "px" : 0;
      theObj.left = getObjectLeft(obj) + deltaX + units;
      theObj.top = getObjectTop(obj) + deltaY + units;
    } else if (isNN4) {
      theObj.moveBy(deltaX, deltaY);
    }
  }
}

// Set the z-order of an object
function setZIndex(obj, zOrder) {
  var theObj = getObject(obj);
  if (theObj) {
    theObj.zIndex = zOrder;
  }
}

// Set the background color of an object
function setBgColor(obj, color) {
  var theObj = getObject(obj);
  if (theObj) {
    if (isNN4) {
      theObj.bgColor = color;
    } else if (isCSS) {
      theObj.backgroundColor = color;
    }
  }
}

// Set the visibility of an object to visible
function show(obj) {
  var theObj = getObject(obj);
  if (theObj) {
    theObj.visibility = "visible";
  }
}

// Set the visibility of an object to hidden
function hide(obj) {
  var theObj = getObject(obj);
  if (theObj) {
    theObj.visibility = "hidden";
  }
}

// Retrieve the x coordinate of a positionable object
function getObjectLeft(obj)  {
  var elem = getRawObject(obj);
  var result = 0;
  if (document.defaultView && document.defaultView.getComputedStyle) {
    var style = document.defaultView;
    var cssDecl = style.getComputedStyle(elem, "");
    result = cssDecl.getPropertyValue("left");
  } else if (elem.currentStyle) {
    result = elem.currentStyle.left;
  } else if (elem.style) {
    result = elem.style.left;
  } else if (isNN4) {
    result = elem.left;
  }
  return parseInt(result);
}

// Retrieve the y coordinate of a positionable object
function getObjectTop(obj)  {
  var elem = getRawObject(obj);
  var result = 0;
  if (document.defaultView && document.defaultView.getComputedStyle) {
    var style = document.defaultView;
    var cssDecl = style.getComputedStyle(elem, "");
    result = cssDecl.getPropertyValue("top");
  } else if (elem.currentStyle) {
    result = elem.currentStyle.top;
  } else if (elem.style) {
    result = elem.style.top;
  } else if (isNN4) {
    result = elem.top;
  }
  return parseInt(result);
}

// Retrieve the rendered width of an element
function getObjectWidth(obj)  {
  var elem = getRawObject(obj);
  var result = 0;
  if (elem.offsetWidth) {
    result = elem.offsetWidth;
  } else if (elem.clip && elem.clip.width) {
    result = elem.clip.width;
  } else if (elem.style && elem.style.pixelWidth) {
    result = elem.style.pixelWidth;
  }
  return parseInt(result);
}

// Retrieve the rendered height of an element
function getObjectHeight(obj)  {
  var elem = getRawObject(obj);
  var result = 0;
  if (elem.offsetHeight) {
    result = elem.offsetHeight;
  } else if (elem.clip && elem.clip.height) {
    result = elem.clip.height;
  } else if (elem.style && elem.style.pixelHeight) {
    result = elem.style.pixelHeight;
  }
  return parseInt(result);
}

// Return the available content width space in browser window
function getInsideWindowWidth() {
  if (window.innerWidth) {
    return window.innerWidth;
  } else if (isIE6CSS) {
    // measure the html element's clientWidth
    return document.body.parentElement.clientWidth;
  } else if (document.body && document.body.clientWidth) {
    return document.body.clientWidth;
  }
  return 0;
}

// Return the available content height space in browser window
function getInsideWindowHeight() {
  if (window.innerHeight) {
    return window.innerHeight;
  } else if (isIE6CSS) {
    // measure the html element's clientHeight
    return document.body.parentElement.clientHeight;
  } else if (document.body && document.body.clientHeight) {
    return document.body.clientHeight;
  }
  return 0;
}

// Change the status bar text of the browser
function statusMsg(msg) {
  window.status = msg;
  return true;
}

// "Lights up" an image on mouseOver (for an image in a div)
function imgOn(lyr, i) {
  if (document.images) {
    if (isNN4) {
      document[lyr].document[lyr + i].src = onImgAry[i].src;
    } else {
      document.images[lyr + i].src = onImgAry[i].src;
    }
  }
  return true;
}

// "Turns off" an image on mouseOut (for an image in a div)
function imgOff(lyr, i) {
  if (document.images) {
    if (isNN4) {
      document[lyr].document[lyr + i].src = offImgAry[i].src;
    } else {
      document.images[lyr + i].src = offImgAry[i].src;
    }
  }
  return true;
}

// Reloads the window if Nav4 resized (from Macromedia Dreamweaver)
function MM_reloadPage(init) {
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}

// Returns the pixel measure of the amount of the page's content
// that has been scrolled upward
/* function getScrollTop() {
  if (isIE4) {
    return document.body.scrollTop;
  } else {
    return window.pageYOffset;
  }
} */

/* Example precaching code for related images
if (document.images) {
  // create "on" array and populate with Image objects
  var onImgArray = new Array();
  onImgArray["play"] = new Image(75,35);
  // set URLs for the "on" images
  onImgArray["play"].src = "images/playon.gif";

  // create "off" array and populate with Image objects
  var offImgArray = new Array();
  offImgArray["play"] = new Image(75,35);
  // set URLs for the "off" images
  offImgArray["play"].src = "images/playoff.gif";
}

<a href="http://www.web.com" onmouseover="imageOn('play'); return setMsg('Play');" onmouseout="imageOff('play'); return setMsg('');">
  <img src="images/playoff.gif" name="play" id="play" height="35" width="75"></a>
*/
