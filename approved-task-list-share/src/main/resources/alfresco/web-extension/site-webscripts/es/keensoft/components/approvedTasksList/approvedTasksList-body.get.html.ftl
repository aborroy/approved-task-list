<@markup id="js">
    <@script type="text/javascript" src="${url.context}/res/resources/es/keensoft/components/approvedTasksList/approvedTasksList.js" />
    <@script src="${url.context}/res/components/people-finder/people-finder.js"/>
</@>

<@markup id="css" >
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
   <@link href="${url.context}/res/components/profile/usertrashcan.css" group="profile"/>
</@>

<@markup id="widgets">
    <@createWidgets />
</@>

<@markup id="html">

	<#assign el = args.htmlid?html>

      <div id="${el}-body" class="trashcan profile">
	
         <div class="header-bar">${msg("page.approvedTasksList.title")}</div>
                     
         <div style="display: none;">
            <div id="${el}-reassignPanel" class="task-edit-header reassign-panel">
               <div class="hd">${msg("page.approvedTasksList.select.user")}</div>
               <div class="bd">
                  <div style="margin: auto 10px;">
                     <div id="${el}-peopleFinder"></div>
                  </div>
               </div>
            </div>
         </div>
         
         <div class="search-bar theme-bg-color-3">
            <div class="search-text toolbar-widget">
                <span>${msg("page.approvedTasksList.year")}</span>
                <input type="text" id="${el}-filterYear" value="${year}" size="4" maxlength="4" tabindex="0"/>
            </div>
            <#if member == "true">
	            <div class="search-button toolbar-widget">
	               <input type="hidden" id="${el}-username" value="${username}"/>
	               <span id="${el}-select-user-button" class="yui-button yui-push-button">
	                  <button id="${el}-selectUserButton">${msg("page.approvedTasksList.user")}</button>
	               </span>
	               <span id="${el}-usernameLabel">${username}</span>
	            </div>
            </#if>            
            <div class="search-button toolbar-widget">
               <span id="${el}-search-button" class="yui-button yui-push-button">
                  <button id="${el}-filterButton">${msg("page.approvedTasksList.filter")}</button>
               </span>
            </div>            
            <div class="align-right">
               <div class="empty-button">
                  <span class="yui-button yui-push-button" id="${el}-empty-button">
                     <button id="${el}-exportButton">${msg("page.approvedTasksList.export")}</button>
                  </span>
               </div>
            </div>            
         </div>
         
         <div class="results" style="padding-left: 16px;">
         
			<div class="datalist">
				<table width="100%">
					<thead>
						<tr class="yui-dt-first yui-dt-last">
							<th style="padding:10px" rowspan="1" colspan="1" class="yui-dt-th">${msg("page.approvedTasksList.id")}</td>
							<th style="padding:10px" rowspan="1" colspan="1" class="yui-dt-th">${msg("page.approvedTasksList.nameFile")}</td>
							<th style="padding:10px" rowspan="1" colspan="1" class="yui-dt-th">${msg("page.approvedTasksList.version")}</td>
							<th style="padding:10px" rowspan="1" colspan="1" class="yui-dt-th">${msg("page.approvedTasksList.folder")}</td>
							<th style="padding:10px" rowspan="1" colspan="1" class="yui-dt-th">${msg("page.approvedTasksList.owner")}</td>
							<th style="padding:10px" rowspan="1" colspan="1" class="yui-dt-th">${msg("page.approvedTasksList.approvedDate")}</td>
						</tr>
					</thead>		
					<tbody class="yui-dt-message" id="entriesTable">	
							<#list entries.entries as entry>
								<tr>
									<td style="padding:10px;width:1%;white-space:nowrap;"><a href="${entry.URLID}">${entry.ID}</a></td>
									<td style="padding:10px;width:1%;white-space:nowrap;"><a href="${entry.URLShareFile}">${entry.NameFile}</a> </td>
									<td style="padding:10px;width:1%;white-space:nowrap;">${entry.Version}</td>
									<td style="padding:10px;width:1%;white-space:nowrap;"><a href="${entry.URLShareFolder}">${entry.NameFolder}</a></td>
									<td style="padding:10px;width:1%;white-space:nowrap;">${entry.Owner}</td>
									<td style="padding:10px;width:100%;white-space:nowrap;">${entry.ApprovedDate}</td>
								</tr>
							</#list>
					</tbody>			
				</table>
			</div>
			
		</div>
			
	</div>
</@>

