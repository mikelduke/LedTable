/**
 * LedTable.js 
 */

var ledTableCmd = "ledtable/LedTable?cmd=";
var currentEffect = "";
var effectInfo = "";

function downloadUrl(url, callback) {
  //prompt("",url);
  var request = window.ActiveXObject ?
    new ActiveXObject('Microsoft.XMLHTTP') :
    new XMLHttpRequest;

  request.onreadystatechange = function() {
    if (request.readyState == 4) {
      request.onreadystatechange = doNothing;
      callback(request, request.status);
    }
  };

  request.open('GET', url, true);
  request.send(null);
}

function doNothing() {}

function load() {
	var url = "ledtable/LedTable?cmd=EFFECT_LIST";
	downloadUrl(url, function(data) {
		loadEffectList(data);
	});
	
	var updatePlaylistStatus = function() {
		var url = ledTableCmd + "PLAYLIST_INFO";
		downloadUrl(url, function(data, status) {
			if (status == 200) {
				document.getElementById("playlist_play").innerHTML = data.responseText;
			}
		});
		
		//loadCurrentEffect()(); //called here to so that effect in browser can be changed
		//load removed, it was changing settings back and forth, maybe use a reload button instead
	}
	
	//Poll to see if playlist is playing, and update the current effect shown
	//TODO Change to button that can control play/stop
	window.setInterval(updatePlaylistStatus, 2000);
}

function loadEffectList(data) {
	var xml = data.responseXML;
	
	var effects = xml.documentElement.getElementsByTagName("EffectMode");
	
	var html = "";
	for (var i = 0; i < effects.length; i++) {
		var command = effects[i].getElementsByTagName("ModeName")[0].childNodes[0].nodeValue;
		//prompt('',command[0].childNodes[0].nodeValue);
		
		var name = "";
		
		name = effects[i].getElementsByTagName("ModeDescName");
		
		if (name != null && typeof(name) != undefined && name.length > 0) {
			name = name[0].childNodes[0];
			
			if (name != undefined) {
				name = name.nodeValue;
				//html += "<a onclick='changeMode(\"" + command + "\")' href='javascript:void(0);'>" + name + "</a><br />";
				
				document.getElementById("modes").innerHTML += "<option value='" + command + "'>" + name + "</option>";
			}
		}
	}
	
	loadCurrentEffect();
}

function changeMode() {
	//prompt('', document.getElementById("modes").value)
	
	var newMode = document.getElementById("modes").value;
	var url = ledTableCmd + "MODE&mode=" + newMode;
	
	downloadUrl(url, function(data) {
		var resp = "";
		
		if (data.responseText == "OK") {
			resp = "Mode Changed";
			loadCurrentEffect();
		} else resp = data.responseText;
		
		document.getElementById("status").innerHTML = resp;
	});
}

function loadCurrentEffect() {
	var url = ledTableCmd + "CURRENT_SELECTION_INFO";
	
	downloadUrl(url, function(data) {
		if (data.responseXml != currentEffect) {
			currentEffect = data.responseXML;
			var selectionName = currentEffect.getElementsByTagName("mode")[0].childNodes[0].nodeValue;
			
			document.getElementById("modes").value = selectionName;
			
			loadEffectInfo();
		}
	});
}

function loadEffectInfo() {
	var url = ledTableCmd + "EFFECT_INFO";
	
	downloadUrl(url, function(data) {
		//prompt('', data.responseText);
		effectInfo = data.responseXML;
		
		var html = "";
		var effectName = effectInfo.getElementsByTagName("EffectName")[0].childNodes[0].nodeValue;
		var effectDesc = effectInfo.getElementsByTagName("EffectDesc")[0].childNodes[0].nodeValue;
		var effectParams = effectInfo.getElementsByTagName("EffectInfoParameter");
		
		html += "<h2>" + effectName + "</h2>";
		html += "<h4>" + effectDesc + "</h4><br />";
		
		document.getElementById("effectDetails").innerHTML = html;
		document.getElementById("effectSliders").innerHTML = "";
		document.getElementById("effectSelects").innerHTML = "";
		document.getElementById("effectSwitches").innerHTML = "";
		
		for (var i = 0; i < effectParams.length; i++) {
			var paramName = effectParams[i].getElementsByTagName("Name")[0].childNodes[0].nodeValue;
			var paramDesc = effectParams[i].getElementsByTagName("Desc")[0].childNodes[0].nodeValue;
			var paramType = effectParams[i].getElementsByTagName("DataType")[0].childNodes[0].nodeValue;
			
			var paramValuesAr = effectParams[i].getElementsByTagName("Value");
			
			var curValueFound = false;
			var curValue = currentEffect.getElementsByTagName(paramName);
			if (curValue != null && curValue.length > 0) {
				curValue = curValue[0].childNodes[0].nodeValue;
				curValueFound = true;
			}
			
			if (!curValueFound) curValue = ""; //TODO Add default EffectInfoParameter Amount
			
			//Add a slider control and amount box for the slider
			if (paramType == "INT") {
				var sliderPanel = document.createElement("div");
				sliderPanel.setAttribute("id", paramName + "Panel");
				
				var pName = document.createElement("p");
				pName.innerHTML = "<b>" + paramName + "</b>";
				sliderPanel.appendChild(pName);
				
				var pDesc = document.createElement("p");
				pDesc.innerHTML = paramDesc;
				sliderPanel.appendChild(pDesc);
				
				var pVal = document.createElement("p");
				pVal.setAttribute("id", paramName);
				pVal.innerHTML = curValue; 
				sliderPanel.appendChild(pVal);
				
				var paramMin = effectParams[i].getElementsByTagName("MinValue");
				if (paramMin != null && paramMin.length > 0) paramMin = paramMin[0].childNodes[0].nodeValue;
				
				var paramMax = effectParams[i].getElementsByTagName("MaxValue");
				if (paramMax != null && paramMax.length > 0) paramMax = paramMax[0].childNodes[0].nodeValue;
				
				var pSlider = document.createElement("INPUT");
				pSlider.setAttribute("type", "range");
				pSlider.setAttribute("id", paramName + "slider");
				pSlider.setAttribute("min", paramMin);
				pSlider.setAttribute("max", paramMax);
				if (curValueFound) pSlider.setAttribute("value", curValue);
				pSlider.setAttribute("onmouseup", "sliderMove(id,value, true)");
				pSlider.setAttribute("onmousemove", "sliderMove(id, value)");
				sliderPanel.appendChild(pSlider);
				
				//TODO instead of P, make into text box to allow typing in amounts
				var br = document.createElement("BR");
				sliderPanel.appendChild(br);
				
				document.getElementById("effectSliders").appendChild(sliderPanel);
			} else if (paramType == "SELECT") {
				var selectPanel = document.createElement("div");
				selectPanel.setAttribute("id", paramName + "Panel");
				
				var pName = document.createElement("p");
				pName.innerHTML = "<b>" + paramName + "</b>";
				selectPanel.appendChild(pName);
				
				var pDesc = document.createElement("p");
				pDesc.innerHTML = paramDesc;
				selectPanel.appendChild(pDesc);
				
				//<option value='" + command + "'>" + name + "</option>";
				var pSelect = document.createElement("SELECT");
				pSelect.setAttribute("id", paramName);
				pSelect.setAttribute("onchange", "sendEffectChange()");
				
				for (var j = 0; j < paramValuesAr.length; j++) {
					var pValue = paramValuesAr[j].childNodes[0].nodeValue;
					var pOption = document.createElement("OPTION");
					pOption.setAttribute("value", pValue);
					pOption.innerHTML = pValue;
					pSelect.appendChild(pOption);
				}
				
				selectPanel.appendChild(pSelect);
				
				var br = document.createElement("BR");
				selectPanel.appendChild(br);
				
				document.getElementById("effectSelects").appendChild(selectPanel);
			} else if (paramType == "BOOL") {
				var switchPanel = document.createElement("div");
				switchPanel.setAttribute("id", paramName + "Panel");
				
				var pName = document.createElement("p");
				pName.innerHTML = "<b>" + paramName + "</b>";
				switchPanel.appendChild(pName);
				
				/*var pDesc = document.createElement("p");
				pDesc.innerHTML = paramDesc;
				switchPanel.appendChild(pDesc);*/
				
				//<option value='" + command + "'>" + name + "</option>";
				var pCheckbox = document.createElement("INPUT");
				pCheckbox.setAttribute("id", paramName);
				pCheckbox.setAttribute("type", "checkbox");
				pCheckbox.setAttribute("name", paramName);
				if (curValueFound) pCheckbox.setAttribute("checked", curValue);
				pCheckbox.setAttribute("onchange", "sendEffectChange()");
				switchPanel.appendChild(pCheckbox);
				switchPanel.innerHTML += paramDesc;
				
				var br = document.createElement("BR");
				switchPanel.appendChild(br);
				
				document.getElementById("effectSwitches").appendChild(switchPanel);
			} else if (paramType == "STRING") {
				//document.getElementById("effectDetails").innerHTML += html;
			}
			//TODO Add more controls for other types
		}
	});
}

function sliderMove(id, value, connect) {
	//prompt('',id + " " + value);
	id = id.substring(0, id.length - "slider".length);
	document.getElementById(id).innerHTML = value;
	
	if (connect) sendEffectChange();
}

function sendEffectChange() {
	var newMode = document.getElementById("modes").value;
	var url = ledTableCmd + "MODE&mode=" + newMode;
	
	//load effect params to url string
	var effectParams = effectInfo.getElementsByTagName("EffectInfoParameter");
	for (var i = 0; i < effectParams.length; i++) {
		var paramName = effectParams[i].getElementsByTagName("Name")[0].childNodes[0].nodeValue;
		var paramType = effectParams[i].getElementsByTagName("DataType")[0].childNodes[0].nodeValue;
		var paramValue = "";
		
		if (paramType == "INT")
			paramValue = document.getElementById(paramName).innerHTML;
		else if (paramType == "SELECT")
			paramValue = document.getElementById(paramName).value;
		else if (paramType == "BOOL") {
			paramValue = document.getElementById(paramName).checked;
			if (paramValue == "") paramValue = "false";
		}
		
		if (paramValue != "")
			url += "&" + paramName + "=" + paramValue;
	}
	
	//prompt('', url);
	downloadUrl(url, function(data) {
		//success
		//TODO Add message ok thing
	});
}