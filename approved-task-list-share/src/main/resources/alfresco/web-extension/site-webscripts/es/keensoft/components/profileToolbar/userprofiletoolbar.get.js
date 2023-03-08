function main(){
   var userId = page.url.templateArgs["userid"];
   
   if (userId == null)
   {
      userId = user.name;
   }
   
   model.activeUserProfile = (userId == user.name);
   
   if(model.activeUserProfile){
	   addLink("approvedTasksList", "approvedTasksList", msg.get("link.approvedTasksList"))
   }
}

function addLink(id, href, msgId, msgArgs)
{
   model.links.push(
   {
      id: id,
      href: href,
      label: msg.get(msgId, msgArgs ? msgArgs : null)
   });
}

main();