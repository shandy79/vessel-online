<!--
// File: js/yuiLib.js
// Library to extend DHTML features of YUI APIs provided by Yahoo.
// Steven Handy (created: 05/23/2006, last mod: 05/24/2006)

// Calculate the element's height as an integer
function yuiGetHeight(el) {
  return (YAHOO.util.Dom.getRegion(el).bottom - YAHOO.util.Dom.getRegion(el).top);
}

// Calculate the element's width as an integer
function yuiGetWidth(el) {
  return (YAHOO.util.Dom.getRegion(el).right - YAHOO.util.Dom.getRegion(el).left);
}

// Get the element's x-coordinate relative to the container (for 'position:relative')
function yuiGetDirectX(el) {
  var x = YAHOO.util.Dom.getStyle(el, 'left');

  if (x == 'auto') return 0;
  else return parseInt(x, 10);
}

// Get the element's y-coordinate relative to the container (for 'position:relative')
function yuiGetDirectY(el) {
  var y = YAHOO.util.Dom.getStyle(el, 'top');

  if (y == 'auto') return 0;
  else return parseInt(y, 10);
}

// Set the element's x-coordinate relative to the container (for 'position:relative')
function yuiSetDirectX(el, x) {
  YAHOO.util.Dom.setStyle(el, 'left', x + 'px');
  return true;
}

// Set the element's y-coordinate relative to the container (for 'position:relative')
function yuiSetDirectY(el, y) {
  YAHOO.util.Dom.setStyle(el, 'top', y + 'px');
  return true;
}

// Set the element's x,y-coordinates relative to the container (for 'position:relative')
function yuiSetDirectXY(el, x, y) {
  yuiSetDirectX(el, x);
  yuiSetDirectY(el, y);
  return true;
}

// Set the element's z-index
function yuiSetZIndex(el, z) {
  YAHOO.util.Dom.setStyle(el, 'zIndex', z);
  return true;
}

// Hide the element
function yuiHide(el) {
  YAHOO.util.Dom.setStyle(el, 'visibility', 'hidden');
  return true;
}

// Show the element
function yuiShow(el) {
  YAHOO.util.Dom.setStyle(el, 'visibility', 'visible');
  return true;
}

// "Turns off" an image on mouseOut (for an image in a div)
function setImgOff(div, i) {
  if (document.images) document.images[div + i].src = offImgAry[i].src;
  return true;
}

// "Lights up" an image on mouseOver (for an image in a div)
function setImgOn(div, i) {
  if (document.images) document.images[div + i].src = onImgAry[i].src;
  return true;
}

// Change the status bar text of the browser
function setWindowStatus(msg) {
  window.status = msg;
  return true;
}
//-->
