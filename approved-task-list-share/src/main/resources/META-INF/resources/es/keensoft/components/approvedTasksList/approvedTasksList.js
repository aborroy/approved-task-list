(function(){

	var Dom = YAHOO.util.Dom,
	Event = YAHOO.util.Event;

	var $html = Alfresco.util.encodeHTML;
	
	Alfresco.component.approvedTasksList = function ApprovedTasksList_constructor(htmlId) {
		return Alfresco.component.approvedTasksList.superclass.constructor.call(this, "Alfresco.component.approvedTasksList", htmlId);
	};

	YAHOO.extend(Alfresco.component.approvedTasksList, Alfresco.component.Base, {
		options: {},

		onReady: function ApprovedTasksList_onReady() {
			
		      Alfresco.util.ComponentManager.register(this);
			
			 this.widgets.filterButton = Alfresco.util.createYUIButton(this, "filterButton", this.onButtonFilterClick);
			 this.widgets.exportButton = Alfresco.util.createYUIButton(this, "exportButton", this.onButtonExportClick);
			 if (Dom.get(this.id + "-select-user-button")) {
				this.widgets.selectUserButton = Alfresco.util.createYUIButton(this, "selectUserButton", this.onSelectUserClick);
				Dom.removeClass(Selector.query(".actions .reassign", this.id), "hidden");
		     }
			
	         Alfresco.util.Ajax.request(
             {
                url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/people-finder",
                dataObj:
                {
                   htmlid: this.id + "-peopleFinder"
                },
                successCallback:
                {
                   fn: this.onPeopleFinderLoaded,
                   scope: this
                },
                failureMessage: "Could not load People Finder component",
                execScripts: true
             });
            
		},

		onButtonFilterClick: function FilterApprovedTasksList_onButtonClick(e) {
			
			var url = Alfresco.constants.PROXY_URI + "tasks/files/approved";
			
			if (document.getElementById(this.id + "-filterYear").value != ""){
				url = url + "?year=" + document.getElementById(this.id + "-filterYear").value;
			}

			if (document.getElementById(this.id + "-username")) {
				if (document.getElementById(this.id + "-username").value != ""){
					url = url + "&username=" + document.getElementById(this.id + "-username").value;
				}
			}
			
			var HttpClient = function() {
				this.get = function(aUrl, aCallback) {
					var anHttpRequest = new XMLHttpRequest();
					anHttpRequest.onreadystatechange = function() { 
						if (anHttpRequest.readyState == 4 && anHttpRequest.status == 200)
							aCallback(anHttpRequest.responseText);
					}

					anHttpRequest.open("GET", aUrl, true);            
					anHttpRequest.send(null);
				}
			}

			var client = new HttpClient();
			client.get(url, function(response) {			    
			tasks = eval('(' + response + ')');
			
			document.getElementById("entriesTable").innerHTML = "";
			for(j=0; j<tasks.entries.length; j++){
				
			    	    var row = document.createElement("TR");
			    	
			        var cell0 = row.insertCell(0);
			        var cell1 = row.insertCell(1);
			        var cell2 = row.insertCell(2);
			        var cell3 = row.insertCell(3);
			        var cell4 = row.insertCell(4);
			        var cell5 = row.insertCell(5);
			        
			        var linkId = document.createElement('a');
			        linkId.textContent = tasks.entries[j].ID.substring(9);
			        linkId.href = tasks.entries[j].URLID;
			        
			        var linkFile = document.createElement('a');
			        linkFile.textContent = tasks.entries[j].NameFile;
			        linkFile.href = tasks.entries[j].URLShare;
			        
			        var linkFolder = document.createElement('a');
			        linkFolder.textContent = tasks.entries[j].NameFolder;
			        linkFolder.href = tasks.entries[j].URLShareFolder;
			        
			        cell0.append(linkId);
			        cell1.append(linkFile);
			        cell2.innerHTML = tasks.entries[j].Version;
			        cell3.append(linkFolder);
			        cell4.innerHTML = tasks.entries[j].Owner;
			        cell5.innerHTML = tasks.entries[j].ApprovedDate;
			        
			        document.getElementById("entriesTable").append(row);
			    } 
			});
		},

		onButtonExportClick: function ExportApprovedTasksList_onButtonClick(e) {
			
			var url = Alfresco.constants.PROXY_URI + "tasks/files/approved/export";
			
			if (document.getElementById(this.id + "-username")) {
				if (document.getElementById(this.id + "-username").value != ""){
					url = url + "?username=" + document.getElementById(this.id + "-username").value;
				}
			}
			
			window.open(url, "_blank");

		},
		
	    onPeopleFinderLoaded: function TEH_onPeopleFinderLoaded(response)
	    {
	         var finderDiv = Dom.get(this.id + "-peopleFinder");
	         finderDiv.innerHTML = response.serverResponse.responseText;
	         this.widgets.reassignPanel = Alfresco.util.createYUIPanel(this.id + "-reassignPanel");
	         this.widgets.peopleFinder = Alfresco.util.ComponentManager.get(this.id + "-peopleFinder");
	         this.widgets.peopleFinder.setOptions(
	         {
	        	    singleSelectMode: true,
	            addButtonLabel: this.msg("button.select")
	         });
	         YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);
	    },
		
	    onPersonSelected: function TEH_onPersonSelected(e, args)
	    {
	    	   Dom.get(this.id + "-username").value = args[1].userName;
	    	   Dom.get(this.id + "-usernameLabel").innerHTML = args[1].userName;
	    	   this.widgets.reassignPanel.hide();
	    },
	    
	    onSelectUserClick: function ConsoleGroups_SearchPanelHandler_onSelectUserClick(columnInfo)
        {
	         this.widgets.peopleFinder.clearResults();
	         this.widgets.reassignPanel.show();
        }

	});

})();
