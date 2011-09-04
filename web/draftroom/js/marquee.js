<!--
// File: js/marquee.js
// Code to provide scrolling marquee.
// Steven Handy (created: 10/10/2008, last mod: 10/13/2008)

// http://www.dynamicdrive.com/dynamicindex2/cmarquee2.htm
// Modified to support multiple scrollers per page and improve formatting.
// A scrolling marquee is defined in HTML per the following:
/*
 * <div id="marqueecontainer<MARQUEE ID HERE (e.g. 1)>" class="marqueecontainer" onmouseover="copySpeed=pauseSpeed;" onmouseout="copySpeed=marqueeSpeed;">
 *   <div id="vmarquee<MARQUEE ID HERE (e.g. 1)>" class="vmarquee">
 *     MARQUEE CONTENT HERE (any valid HTML is acceptable)
 *   </div>
 * </div>
 */
// You must include the file css/marquee.css when using this widget!

/***********************************************
* Cross browser Marquee II- © Dynamic Drive (www.dynamicdrive.com)
* This notice MUST stay intact for legal use
* Visit http://www.dynamicdrive.com/ for this script and 100s more.
***********************************************/

// Global variables
var scrollDelay = 2000;  // Specify initial delay before marquee starts to scroll on page (msec)
var marqueeSpeed = 3;  // Specify marquee scroll speed (larger is faster 1-10)
var mouseoverPause = 1;  // Pause marquee onmouseover?  (0=no, 1=yes)
var scrollCount = 2;

// NO NEED TO EDIT BELOW THIS LINE //

var copySpeed = marqueeSpeed;
var pauseSpeed = (mouseoverPause == 0) ? copySpeed : 0;
var actualHeight = '';

// scrollMarquee:  Moves marquee contents progressively upward
function scrollMarquee(marqueeID, marqueeHeight) {
  var crossMarquee = document.getElementById("vmarquee" + marqueeID);

  if (parseInt(crossMarquee.style.top) > ((actualHeight * -1) + 8)) {
    crossMarquee.style.top = parseInt(crossMarquee.style.top) - copySpeed + "px";
  } else {
    crossMarquee.style.top = parseInt(marqueeHeight) + 8 + "px";
  }
}

// initMarquee:  Initializes up to 'scrollCount' marquees found on the page
function initMarquee() {
  var crossMarquee, marqueeHeight;

  for (var i = 1; i <= scrollCount; i++) {
    crossMarquee = document.getElementById("vmarquee" + i);
    crossMarquee.style.top = 0;
    marqueeHeight = document.getElementById("marqueecontainer" + i).offsetHeight;
    actualHeight = crossMarquee.offsetHeight;

    // if Opera or Netscape 7x, add scrollbars to scroll and exit
    if (window.opera || navigator.userAgent.indexOf("Netscape/7") != -1) {
      crossMarquee.style.height = marqueeHeight + "px";
      crossMarquee.style.overflow = "scroll";
      return;
    }

    setTimeout("setInterval('scrollMarquee(" + i + ", " + marqueeHeight + ")', 100)", scrollDelay);
  }
}

if (window.addEventListener) {
  window.addEventListener("load", initMarquee, false);
} else if (window.attachEvent) {
  window.attachEvent("onload", initMarquee);
} else if (document.getElementById) {
  window.onload = initMarquee;
}
//-->
