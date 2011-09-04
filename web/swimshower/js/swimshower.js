<!--
// File: js/swimshower.js
// Code to provide various features to the swimshower.com web site.
// Steven Handy (created: 12/22/2006, last mod: 05/02/2007)

// Global variables
var SERVER_PREFIX = "http://localhost:8080/";

// fixAction: replace a malformed form action with the correct one
function fixAction(formObj, action) {
  formObj.action = SERVER_PREFIX + action;
  return true;
}

// setOverlay: establishes position and opacity for a div overlay
function setOverlay(divObj) {
  YAHOO.util.Dom.setStyle(divObj, 'opacity', 0.9);
  var x = (YAHOO.util.Dom.getViewportWidth() - yuiGetWidth(divObj)) / 2;
  var y = (YAHOO.util.Dom.getViewportHeight() - yuiGetHeight(divObj)) / 2;
  YAHOO.util.Dom.setX(divObj, x);
  YAHOO.util.Dom.setY(divObj, y);
  return true;
}
//-->
