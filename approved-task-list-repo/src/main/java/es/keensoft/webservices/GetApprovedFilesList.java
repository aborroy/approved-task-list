package es.keensoft.webservices;

import java.util.List;

import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import es.keensoft.util.GetTasksFiles;

public class GetApprovedFilesList extends GetTasksFiles {

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) {

			JSONObject jsonAnswer = new JSONObject();
			JSONObject jsonTaskInfo = new JSONObject();
			JSONArray jsonTask = new JSONArray();
			
			try {

				List<List<Object>> data = getData(req.getParameter("year"), req.getParameter("username"));
	
				for (List<Object> informationLine : data) {
					jsonTaskInfo = new JSONObject();
					jsonTaskInfo.put("ID",informationLine.get(0));
					jsonTaskInfo.put("URLID", informationLine.get(1));
					jsonTaskInfo.put("NameFile", informationLine.get(2));
					jsonTaskInfo.put("Version", informationLine.get(3));
					jsonTaskInfo.put("URLShareFile", informationLine.get(4));
					jsonTaskInfo.put("NodeRefFile", informationLine.get(5));
					jsonTaskInfo.put("NameFolder", informationLine.get(6));
					jsonTaskInfo.put("URLShareFolder", informationLine.get(7));
					jsonTaskInfo.put("NodeRefFolder", informationLine.get(8));
					jsonTaskInfo.put("ApprovedDate", informationLine.get(9));
					jsonTaskInfo.put("Owner", informationLine.get(10));
	
					jsonTask.put(jsonTaskInfo);
				}
	
				jsonAnswer.put("entries", jsonTask);
	
				res.setContentType(ContentType.APPLICATION_JSON.getMimeType());
				res.setContentEncoding("UTF-8");
				res.getWriter().write(jsonAnswer.toString());
				
			} catch (Exception re) {
				throw new RuntimeException(re);
			}

	}

}
