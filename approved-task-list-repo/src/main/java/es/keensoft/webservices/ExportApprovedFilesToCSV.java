package es.keensoft.webservices;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import es.keensoft.util.GetTasksFiles;

public class ExportApprovedFilesToCSV extends GetTasksFiles {
	
	private static String CSV_SEPARATOR = ";";

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		try {

			List<List<Object>> data = getData("*", req.getParameter("username"));

			String csvContent = "";
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.workflowId") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.urlWorkflowId") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.nameFile") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.version") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.urlShareFile") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.noderefFile") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.nameFolder") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.urlShareFolder") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.noderefFolder") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.approvedDate") + CSV_SEPARATOR;
			csvContent = csvContent + I18NUtil.getMessage("csv.approvedTasksList.owner") + CSV_SEPARATOR + "\n";

			for (List<Object> listResults : data) {
				for (Object object : listResults) {
					csvContent = csvContent + object.toString() + CSV_SEPARATOR;
				}
				csvContent = csvContent + "\n";
			}


			res.addHeader("Content-Disposition", "attachment; filename=" + getFileName(req.getParameter("username")));
			res.setContentType(MimetypeMap.MIMETYPE_TEXT_CSV);
			res.getOutputStream().write(csvContent.getBytes());

		} catch (Throwable e) {
			throw new IOException(e);
		}

	}
	
	private String getFileName(String paramUserName) {
		
		String filename = "tasks_files";
		
		if (paramUserName != null && !paramUserName.equals("")) {
			filename = filename + "-" + paramUserName;
		} else {
			filename = filename + "-" + AuthenticationUtil.getFullyAuthenticatedUser();
		}
		
		DateFormat df = new SimpleDateFormat("yyyyddMM");
		filename = filename + "-" + df.format(new Date());
		
		return filename + ".csv";
		
	}

}
