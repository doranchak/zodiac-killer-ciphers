// We use a different file as main, as it can have parsing errors if the browser is too old in main.js for require() calls
//

// Filter the locale, so we won't get a load error if it's not supported
//
function spiderGetBrowserLocale()
{
  var language = window.navigator.userLanguage || window.navigator.language; 
  
  language = language.toLowerCase();
  
  switch (language)
  {
    // Direct mapping
    case 'ar':
    case 'ca':
    case 'cs':
    case 'da':
    case 'de':
    case 'el':
    case 'en-gb':
    case 'en-us':
    case 'es-es':
    case 'fr-fr':
    case 'fi-fi':
    case 'he-il':
    case 'hu':
    case 'it-it':
    case 'ja-jp':
    case 'ko-kr':
    case 'nb':
    case 'nl-nl':
    case 'pl':
    case 'pt-br':
    case 'pt-pt':
    case 'ru':
    case 'sk':
    case 'sl':
    case 'sv':
    case 'th':
    case 'tr':
    case 'zh-cn':
    case 'zh-tw':
      return language;
      
    // Special mapping cases
    case 'es':
      return 'es-es';

    case 'fr':
      return 'fr-fr';

    case 'fi':
      return 'fi-fi';

    case 'it':
      return 'it-it';

    case 'ja':
    case 'jp':
      return 'ja-jp';

    case 'ko':
    case 'kr':
      return 'ko-kr';

    case 'nl':
      return 'nl-nl';

    case 'pt':
      return 'pt-pt';

    case 'zh':
      return 'zh-cn';
    
    default:
      return 'en-us';
  }
}


var dojoConfig = { 
  // baseUrl is set in the HTML
  selectorEngine : 'lite',
  tlmSiblingOfDojo : false,
  locale : spiderGetBrowserLocale(),
  packages : [
    { name: 'dojo', location : 'dojo' },
    { name: 'dijit', location : 'dojo' },
    { name: 'dgrid', location : 'dojo' },
    { name: 'cbtree', location : 'dojo/cbtree' }
  ]
};


function spiderCheckBrowser() 
{
  if (window.platform)
  {
    // platform.name + '<br>' + platform.version
  
    var browserName = window.platform.name;
    var majorVersion = parseInt(window.platform.version, 10);
  
    // All older IE aren't supported
    //
    if (browserName == "IE" && majorVersion < 10)
    {
      // Use an alert() instead of fancy div, so it will work on really old browsers
      alert(browserName + ' ' + majorVersion + 'is too old to run SpiderBasic programs.\nPlease a more recent browser (Edge, FireFox, Chrome or Safari for example).\n\nYou can change the default browser to use in IDE -> Preference -> Compilers');
    }
  }
  else // IE8/9 or older
  {
    alert('You browser is too old to run SpiderBasic programs.\nPlease a more recent browser (Edge, FireFox, Chrome or Safari for example).\n\nYou can change the default browser to use in IDE -> Preference -> Compilers');
  }
}
