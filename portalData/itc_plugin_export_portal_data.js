// ==UserScript==
// @id             iitc-plugin-portals-export@marcosdiez
// @name           IITC plugin: Export Portals
// @category       Info
// @version        0.0.1
// @namespace      https://github.com/marcosdiez/ingress-intel-total-conversion
// @updateURL      none
// @downloadURL    none
// @description    [local-2013-12-11-234238] Exports portals
// @include        https://www.ingress.com/intel*
// @include        http://www.ingress.com/intel*
// @match          https://www.ingress.com/intel*
// @match          http://www.ingress.com/intel*
// @grant          none
// ==/UserScript==


function wrapper(plugin_info) {
// ensure plugin framework is there, even if iitc is not yet loaded
if(typeof window.plugin !== 'function') window.plugin = function() {};

//PLUGIN AUTHORS: writing a plugin outside of the IITC build environment? if so, delete these lines!!
//(leaving them in place might break the 'About IITC' page or break update checks)
plugin_info.buildName = 'local';
plugin_info.dateTimeVersion = '20131211.234238';
plugin_info.pluginId = 'portal-export';
//END PLUGIN AUTHORS NOTE



// PLUGIN START ////////////////////////////////////////////////////////

/* whatsnew
* 0.0.8 : use dialog() instead of alert()
* 0.0.6 : ignoring outside bounds portals (even if close to)
* 0.0.5 : changed table layout, added some colors
* 0.0.4 : reverse show order of portals, using MAX_PORTAL_LEVEL now for array, changed table layout to be more compact, cleaned up code
* 0.0.3 : fixed incorrect rounded portal levels, adjusted viewport
* 0.0.2 : fixed counts to be reset after scrolling
* 0.0.1 : initial release, show count of portals
* todo :
*/

/************************* INSPECT ***************/
function PrettyPlot(text) {
    var w = window.open("about:blank", "_blank");
    w.document.open();
    w.document.write("<pre>" + text + "</pre>");
    //w.document.write("<form><textarea rows=37 cols=100>" + text + "</textarea></form>");
    w.document.close();
    w.focus();

}


var tabber = ["", "\t", "\t\t", "\t\t\t", "\t\t\t\t", "\t\t\t\t\t", "\t\t\t\t\t\t"];

function Inspect(xxx) {
    return InspectHelper(xxx, 0);
}

function InspectHelper(xxx, i) {
    var saida = "";
    var typeObject = typeof({ "aa": "bb" });
    //    alert(typeObject)
    var theTab = tabber[i];
    for (var x in xxx) {
        var valueType = typeof(xxx[x]);
        if (valueType == typeObject) {
            saida += "\n" + theTab + "(" + typeof(x) + ") " + x + "=\t\t(" + valueType + ")\n " + InspectHelper(xxx[x], i + 1) + "\n"; ;
        } else {
            saida += theTab + "(" + typeof(x) + ") " + x + "=\t\t(" + valueType + ") " + xxx[x] + "\n"; ;
        }
    }
    return saida;
}



function InspectGui(theObject) {
    PrettyPlot(Inspect(theObject));
}

function makeSqlString(portal){

        var position = portal.getLatLng();
        var options = portal.options;
        var optionsData = options.data;

        var title = optionsData.title.replace("\"" , "").replace("'" , "");

        //var data = "{ guid: '"+guid+"', title: '"+ title + ", imageUrl: '"+ optionsData.image + "',  lat: " + position.lat ", lng: " + position.lng  + " }\n";

        //var sep = "' , '";


      // var theOutput = "INSERT OR REPLACE into PortalData ( guid , title, imageUrl , lat , lng ) VALUES ( '"
      //   + options.guid + sep + title + sep + optionsData.image + sep + position.lat + sep + position.lng + "' );   \n";

      var theOutput = "{ \"guid\": \"" + options.guid +"\", \"title\": \""+ title + "\", \"imageUrl\": \""+ optionsData.image + "\",  \"lat\": " + position.lat + ", \"lng\": " + position.lng  + " }, \n";
      window.plugin.export_portals += theOutput;
      //console.log(theOutput);

      return theOutput;
}

// use own namespace for plugin

window.plugin.export_portals = "";

window.plugin.portal_exporter = function() {};
function xinspect(o,i){
    if(typeof i=='undefined')i='';
    if(i.length>50)return '[MAX ITERATIONS]';
    var r=[];
    for(var p in o){
        var t=typeof o[p];
        r.push(i+'"'+p+'" ('+t+') => '+(t=='object' ? 'object:'+xinspect(o[p],i+'  ') : o[p]+''));
    }
    return r.join(i+'\n');
}

// example of use:

//count portals for each level available on the map
window.plugin.portal_exporter.getPortals = function(){
  //console.log('** getPortals');
  var retval=false;
  var displayBounds = map.getBounds();
  window.plugin.portal_exporter.enlP = 0;
  window.plugin.portal_exporter.resP = 0;
  window.plugin.portal_exporter.neuP = 0;

  window.plugin.portal_exporter.PortalsEnl = new Array();
  window.plugin.portal_exporter.PortalsRes = new Array();
  for(var level = window.MAX_PORTAL_LEVEL; level > 0; level--){
    window.plugin.portal_exporter.PortalsEnl[level] = 0;
    window.plugin.portal_exporter.PortalsRes[level] = 0;
  }

  var theOutput = "";


  $.each(window.portals, function(i, portal) {

    // INERT OR REPLACE INTO tabname (QID,ANID,value) VALUES ('axo',3,45)




    retval=true;
    var level = portal.options.level;
    var team = portal.options.team;

    theOutput += makeSqlString( portal );

      //alert(xinspect(portal));
      //console.log(
        //  "thePortalX " + portal.options.guid +  " name: " + optionsData.title +
        //  " lat: " + position.lat + " lng: " + position.lng +  " img: " + optionsData.image );
      // InspectGui(portal.options)
    // just count portals in viewport

    if(!displayBounds.contains(portal.getLatLng())) return true;
    switch (team){
      case 1 :
        window.plugin.portal_exporter.resP++;
        window.plugin.portal_exporter.PortalsRes[level]++;
        break;
      case 2 :
        window.plugin.portal_exporter.enlP++;
        window.plugin.portal_exporter.PortalsEnl[level]++;
        break;
      default:
        window.plugin.portal_exporter.neuP++;
        break;
    }
     // console.log("vv " + theOutput);
     // PrettyPlot(theOutput)
  });

  //get portals informations from IITC
  var minlvl = getMinPortalLevel();

  var counts = '<table>';
  if(retval) {
    counts += '<tr><th></th><th class="enl">AEnlightenedA</th><th class="res">BResistanceB</th></tr>';  //'+window.plugin.portal_exporter.enlP+' Portal(s)</th></tr>';
    for(var level = window.MAX_PORTAL_LEVEL; level > 0; level--){
      counts += '<tr><td class="L'+level+'">Level '+level+'</td>';
      if(minlvl > level)
        counts += '<td colspan="2">zoom in to see portals in this level</td>';
      else
        counts += '<td class="enl">'+window.plugin.portal_exporter.PortalsEnl[level]+'</td><td class="res">'+window.plugin.portal_exporter.PortalsRes[level]+'</td>';
      counts += '</tr>';
    }

    counts += '<tr><th>Total:</th><td class="enl">'+window.plugin.portal_exporter.enlP+'</td><td class="res">'+window.plugin.portal_exporter.resP+'</td></tr>';

    counts += '<tr><td>Neutral:</td><td colspan="2">';
    if(minlvl > 0)
      counts += 'zoom in to see unclaimed portals';
    else
      counts += window.plugin.portal_exporter.neuP;
    counts += '</td></tr>';

  } else
    counts += '<tr><td>No Portals in range!</td></tr>';
  counts += '</table>';


  var total = window.plugin.portal_exporter.enlP + window.plugin.portal_exporter.resP + window.plugin.portal_exporter.neuP;

  PrettyPlot( "[\n\n" + window.plugin.export_portals + "{}\n\n]");


  dialog({
    html: '<div id="portal_exporter">' + counts + '</div>',
    title: 'Portal counts: ' + total + ' ' + (total == 1 ? 'portal' : 'portals'),
  });
}

var setup =  function() {
  $('#toolbox').append(' <a onclick="window.plugin.portal_exporter.getPortals()" title="Exports Portal Data">Export Portal Data</a>');
  $('head').append('<style>' +
    '#portal_exporter table {margin-top:5px; border-collapse: collapse; empty-cells: show; width:100%; clear: both;}' +
    '#portal_exporter table td, #portal_exporter table th {border-bottom: 1px solid #0b314e; padding:3px; color:white; background-color:#1b415e}' +
    '#portal_exporter table tr.res th {  background-color: #005684; }' +
    '#portal_exporter table tr.enl th {  background-color: #017f01; }' +
    '#portal_exporter table th { text-align: center;}' +
    '#portal_exporter table td { text-align: center;}' +
    '#portal_exporter table td.L0 { background-color: #000000 !important;}' +
    '#portal_exporter table td.L1 { background-color: #FECE5A !important;}' +
    '#portal_exporter table td.L2 { background-color: #FFA630 !important;}' +
    '#portal_exporter table td.L3 { background-color: #FF7315 !important;}' +
    '#portal_exporter table td.L4 { background-color: #E40000 !important;}' +
    '#portal_exporter table td.L5 { background-color: #FD2992 !important;}' +
    '#portal_exporter table td.L6 { background-color: #EB26CD !important;}' +
    '#portal_exporter table td.L7 { background-color: #C124E0 !important;}' +
    '#portal_exporter table td.L8 { background-color: #9627F4 !important;}' +
    '#portal_exporter table td:nth-child(1) { text-align: left;}' +
    '#portal_exporter table th:nth-child(1) { text-align: left;}' +
    '</style>');
}

// PLUGIN END //////////////////////////////////////////////////////////


setup.info = plugin_info; //add the script info data to the function as a property
if(!window.bootPlugins) window.bootPlugins = [];
window.bootPlugins.push(setup);
// if IITC has already booted, immediately run the 'setup' function
if(window.iitcLoaded && typeof setup === 'function') setup();
} // wrapper end
// inject code into site context
var script = document.createElement('script');
var info = {};
if (typeof GM_info !== 'undefined' && GM_info && GM_info.script) info.script = { version: GM_info.script.version, name: GM_info.script.name, description: GM_info.script.description };
script.appendChild(document.createTextNode('('+ wrapper +')('+JSON.stringify(info)+');'));
(document.body || document.head || document.documentElement).appendChild(script);
