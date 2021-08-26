
spider.nbModules++;

var put;
var SpiderLaunch;

var spiderLaunchExecuted = false;
var spiderAlertDisplayed = false;

function SpiderMain() {
  
  if (spider.nbLoadedModules == spider.nbModules && !spiderLaunchExecuted)
  { 
    if (SpiderLaunch)
    {
      // disable virtual desktop scrolling when dragging a window outside the screen
      //
      $(document).on('scroll', function() {
        $(document).scrollLeft(0);
        $(document).scrollTop(0);
      });
      
      spiderLaunchExecuted = true;
      SpiderLaunch();
    }
  }
}


require(['put.min', 'jquery.min'], function(PutSelectorPut) {
  
  put = PutSelectorPut;

  // We need to load these after jquery, as it depends on it  
  require(['jquery-ui.custom.min', 'jquery.injectCSS', 'jquery.blockUI.min'], function() {

    // This depends of jquery-ui    
    require(['jquery.ui.touch-punch.min'], function() {
      
       spider.nbLoadedModules++;
       SpiderMain(); // Try to launch the main procedure (will happen if all other libs modules are loaded)
    });
  });
  
  // Add a function to jquery dynamically
  (function($) {
   $.fn.cssValue = function(p) {
      var result;
      return isNaN(result = parseFloat(this.css(p))) ? 0 : result;
   };
  })(jQuery);
});


var PIXEL_RATIO = (function () {
    var ctx = document.createElement("canvas").getContext("2d"),
        dpr = window.devicePixelRatio || 1,
        bsr = ctx.webkitBackingStorePixelRatio ||
              ctx.mozBackingStorePixelRatio ||
              ctx.msBackingStorePixelRatio ||
              ctx.oBackingStorePixelRatio ||
              ctx.backingStorePixelRatio || 1;

    return dpr / bsr;
})();


setHiDPICanvas = function(w, h, ratio) {
    if (!ratio) { ratio = PIXEL_RATIO; }
    
    spider.canvas.width = w * ratio;
    spider.canvas.height = h * ratio;
    spider.canvas.style.width = w + "px";
    spider.canvas.style.height = h + "px";
    spider.context.setTransform(ratio, 0, 0, ratio, 0, 0);
    
    spider.canvas.mozOpaque = true;
    spider.context.fillStyle = 'white';
    spider.context.fillRect(0, 0, w, h);
}
