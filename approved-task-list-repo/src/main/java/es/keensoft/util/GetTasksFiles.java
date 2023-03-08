package es.keensoft.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.UrlUtil;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.AbstractWebScript;

import com.ibm.icu.text.SimpleDateFormat;

public abstract class GetTasksFiles extends AbstractWebScript {
	
	private static final String reviewerGroup = "GROUP_CONTROL_CALIDAD";
	
	private SysAdminParams sysAdminParams;
	private ServiceRegistry serviceRegistry;

	public List<List<Object>> getData(String year, String userId) throws Exception {
		
		boolean runSystem = false;
		List<List<Object>> listAnswer = new ArrayList<>();
		
		try {
		
			// Current user does not include userId in the request 
			if (userId == null) {
				userId = AuthenticationUtil.getFullyAuthenticatedUser();
			} else {
				
				AuthenticationUtil.pushAuthentication();
				AuthenticationUtil.setRunAsUserSystem();
				runSystem = true;
				
				Set<String> authorities = serviceRegistry.getAuthorityService().getAuthoritiesForUser(AuthenticationUtil.getFullyAuthenticatedUser());
				boolean member = false;
				for (String authority : authorities) {
					if (authority.equals(reviewerGroup)) {
						member = true;
						break;
					}
				}
				
				if (!member) {
					throw new RuntimeException("Users must belong to group " + reviewerGroup + " in order to access other users approval list tasks!");
				}
				
			}
			
			// Local services
			WorkflowService workflowService = serviceRegistry.getWorkflowService();
			PersonService personService = serviceRegistry.getPersonService();
			NodeService nodeService = serviceRegistry.getNodeService();
			VersionService versionService = serviceRegistry.getVersionService();
	
			// Create qnames need for get information
			QName qnameCompletionDate = QName.createQName(NamespaceService.BPM_MODEL_1_0_URI, "completionDate");
			QName qnamePackage = QName.createQName(NamespaceService.BPM_MODEL_1_0_URI, "package");
			QName qnameFileName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "name");
			QName qnameOutcome = QName.createQName(NamespaceService.BPM_MODEL_1_0_URI, "outcome");
	
			List<Object> listData = new ArrayList<>();
	
			Date filterYearStart = null;
			Date filterYearEnd = null;
			Boolean filterDate = true;
	
			// Create filters for the year
			if (year == null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				year = String.valueOf(cal.get(Calendar.YEAR));
			}
	
			if (!year.equals("*")) {
				Calendar calendar = Calendar.getInstance();
				calendar.clear();
				calendar.set(Calendar.YEAR, Integer.valueOf(year));
				calendar.set(Calendar.DAY_OF_YEAR, 1);
				filterYearStart = calendar.getTime();
	
				calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, Integer.valueOf(year));
				calendar.set(Calendar.MONTH, 11);
				calendar.set(Calendar.DAY_OF_MONTH, 31);
				calendar.set(Calendar.HOUR_OF_DAY, 24);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
	
				filterYearEnd = calendar.getTime();
			}
	
			SimpleDateFormat dateFormatUser = new SimpleDateFormat("dd/MM/YY HH:mm:ss");
	
			// Query for all the task of the user that are completed and order by completion
			// date
			WorkflowTaskQuery workflowTaskQuery = new WorkflowTaskQuery();
			workflowTaskQuery.setActorId(userId);
			workflowTaskQuery.setActive(null);
			workflowTaskQuery.setTaskState(null);
			workflowTaskQuery.setOrderBy(new WorkflowTaskQuery.OrderBy[] { WorkflowTaskQuery.OrderBy.TaskCreated_Desc });
	
			List<WorkflowTask> taskList = workflowService.queryTasks(workflowTaskQuery, true);
	
			for (WorkflowTask workflowTask : taskList) {
				
				if (workflowTask.getProperties().get(qnameOutcome) != null && workflowTask.getProperties().get(qnameCompletionDate) != null) {
					
					Date dateTask = (Date) workflowTask.getProperties().get(qnameCompletionDate);
					String outcome = workflowTask.getProperties().get(qnameOutcome).toString();
					String idWorkflowInstance = workflowTask.getId();
		
					// Check if the completion date is in the desire date and if is for approved
					// documents
					if (!year.equals("*")) {
						if (filterYearStart.before(dateTask) && filterYearEnd.after(dateTask)) {
							filterDate = true;
						} else {
							filterDate = false;
						}
					} else {
						filterDate = true;
					}
					
					if (outcome.equals("Approve") && filterDate) {
		
						NodeRef nodeRefToDocuments = new NodeRef(workflowTask.getProperties().get(qnamePackage).toString());
						List<ChildAssociationRef> listNodeRefDocuments = nodeService.getChildAssocs(nodeRefToDocuments);
						
						String urlTask = UrlUtil.getShareUrl(sysAdminParams) + "/page/task-details?taskId=" + idWorkflowInstance;
						NodeRef packageNodeRef =
								(NodeRef) workflowTask.getProperties().get(QName.createQName("http://www.alfresco.org/model/bpm/1.0", "package"));
						String userOwner =
								nodeService.getProperties(packageNodeRef).get(QName.createQName("http://www.alfresco.org/model/content/1.0", "creator")).toString();
						PersonInfo personInfo = personService.getPerson(personService.getPerson(userOwner));
						String nameOwner = personInfo.getFirstName() + " " + personInfo.getLastName();
						
						// Check if the node of approved documents have any file inside
						if (!listNodeRefDocuments.isEmpty()) {
		
							for (ChildAssociationRef childAssociationRef : listNodeRefDocuments) {
		
								NodeRef fileOfTask = childAssociationRef.getChildRef();
		
								// Get the path of parent folder
								NodeRef nodeRefFolder = nodeService.getPrimaryParent(fileOfTask).getParentRef();
								String nameFolder = 
										nodeService.getPath(nodeRefFolder).toDisplayPath(nodeService, serviceRegistry.getPermissionService()) + "/" + 
								        nodeService.getProperty(nodeRefFolder, ContentModel.PROP_NAME);
								
								// Build the URL to access by share
								String urlFile = UrlUtil.getShareUrl(sysAdminParams) + "/page/document-details?nodeRef=" + fileOfTask.toString();
								// Skip first "Company Home" folder as it is not required for Share URL
								String urlFolder = UrlUtil.getShareUrl(sysAdminParams) + "/page/repository#filter=path" + 
								    URLEncoder.encodeUriComponent("|" + nameFolder.substring(nameFolder.indexOf("/", 1)));
								
								// Find version label by using task date as reference
								String versionLabel = "1.0";
								if (versionService.getVersionHistory(fileOfTask) != null) {
									Collection<Version> versions = versionService.getVersionHistory(fileOfTask).getAllVersions();
									Map<Date, String> labels = new HashMap<Date, String>();
									Set<Date> previousDates = new TreeSet<Date>();
									for (Version version : versions) {
										if (version.getFrozenModifiedDate().before(dateTask)) {
											previousDates.add(version.getFrozenModifiedDate());
											labels.put(version.getFrozenModifiedDate(), version.getVersionLabel());
										}
									}
									versionLabel = labels.get(getLastElement(previousDates));
								}
		
								// Build answer
								listData = new ArrayList<>();
								listData.add(idWorkflowInstance);
								listData.add(urlTask);
								listData.add(nodeService.getProperties(fileOfTask).get(qnameFileName).toString());
								listData.add(versionLabel);
								listData.add(urlFile);
								listData.add(fileOfTask.toString());
								listData.add(nameFolder.toString());
								listData.add(urlFolder);
								listData.add(nodeRefFolder);
								listData.add(dateFormatUser.format(dateTask));
								listData.add(nameOwner);
		
								listAnswer.add(listData);
		
							}
		
						} else {
							
							// Build answer
							listData = new ArrayList<>();
							listData.add(idWorkflowInstance);
							listData.add(urlTask);
							listData.add("-");
							listData.add("-");
							listData.add("-");
							listData.add("-");
							listData.add("-");
							listData.add("-");
							listData.add("-");
							listData.add(dateFormatUser.format(dateTask));
							listData.add(nameOwner);
	
							listAnswer.add(listData);
							
						}
					}
				}
			}
		} finally {
			if (runSystem) {
				AuthenticationUtil.popAuthentication();
			}
		}

		return listAnswer;
	}
	
    public static <T> T getLastElement(final Iterable<T> elements) {
        final Iterator<T> itr = elements.iterator();
        T lastElement = itr.next();

        while(itr.hasNext()) {
            lastElement=itr.next();
        }

        return lastElement;
    }	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setSysAdminParams(SysAdminParams sysAdminParams) {
		this.sysAdminParams = sysAdminParams;
	}

}
