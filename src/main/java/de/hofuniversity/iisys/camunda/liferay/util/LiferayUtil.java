package de.hofuniversity.iisys.camunda.liferay.util;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class LiferayUtil
{
	private static final String PROPERTIES = "liferay-tasks";
	
	private static final String LIFERAY_URL = "camunda.liferay_url";
	private static final String LIFERAY_USER = "camunda.liferay_user";
	private static final String LIFERAY_PASS = "camunda.liferay_password";
	
	private static final String JSONWS_FRAG = "api/jsonws/";
	
	
	private static final String METHOD_PARAM = "method";
	private static final String PARAMS_PARAM = "params";
	private static final String ID_PARAM = "id";
	
	private static final String CONTEXT_PARAM = "context";
	
	private static final String JSONRPC_PARAM = "jsonrpc";
	private static final String JSONRPC_VALUE = "2.0";
	
	private static final String CAMUNDA_PATH = "camunda.camunda";
	
	private static final String COMMENTS_PATH = "comment.commentmanagerjsonws";
	private static final String ADD_COMMENT_METHOD = "add-comment";
	
	private static final String UPDATE_STATUS_METHOD = "update-status";
	private static final String STATUS_PARAM = "status";
	
	private static final String CONTEXT_PREFIX = "Liferay_";
	private static final String[] CONTEXT_PARAMS = {"userId", "entryType",
			"companyId", "entryClassPK", "entryClassName", "groupId",
			"taskComments", "command", "url"};
	
	private static LiferayUtil fInstance;
	
	private final String fLiferayUrl;
	private final String fLiferayUser;
	private final String fLiferayPassword;
	
	private final Random fRandom;
	
	private final Logger fLogger;
	
	public static LiferayUtil getInstance()
	{
		if(fInstance == null)
		{
			fInstance = new LiferayUtil();
		}
		
		return fInstance;
	}
	
	public LiferayUtil()
	{
		final ClassLoader loader = Thread.currentThread()
	            .getContextClassLoader();
        ResourceBundle rb = ResourceBundle.getBundle(PROPERTIES,
            Locale.getDefault(), loader);
        
        fLiferayUrl = rb.getString(LIFERAY_URL);
        fLiferayUser = rb.getString(LIFERAY_USER);
        fLiferayPassword = rb.getString(LIFERAY_PASS);
        
        fRandom = new Random();
        
        fLogger = Logger.getLogger(this.getClass().getName());
	}
	
	private void setContext(JSONObject parameters,
			Map<String, Object> variables)
	{
		JSONObject context = new JSONObject();
		
		Object value = null;
		for(String key : CONTEXT_PARAMS)
		{
			// liferay context parameters are prefixed in Camunda
			value = variables.get(CONTEXT_PREFIX + key);
			
			if(value != null)
			{
				context.put(key, value);
			}
		}
		
		parameters.put(CONTEXT_PARAM, context.toString());
	}
	
	// for workflow calls that require a context
	private JSONObject getBody(String method, JSONObject parameters,
			Map<String, Object> variables)
	{
		JSONObject body = new JSONObject();
		
		// build JSON RPC call
		body.put(METHOD_PARAM, method);
		
		// set workflow context for Liferay
		setContext(parameters, variables);
		
		body.put(PARAMS_PARAM, parameters);
		
		// TODO: what kind of ID is this? assuming request ID
		body.put(ID_PARAM, fRandom.nextInt());
		
		body.put(JSONRPC_PARAM, JSONRPC_VALUE);
		
		return body;
	}
	
	public void updateWorkflowStatus(String status, Map<String, Object> variables)
			throws Exception
	{
		JSONObject params = new JSONObject();
		
		// set specific parameters
		params.put(STATUS_PARAM, status);
		
		// create generic body
		JSONObject body = getBody(UPDATE_STATUS_METHOD, params, variables);
		
		// send to Liferay
		String url = fLiferayUrl + JSONWS_FRAG + CAMUNDA_PATH;
		
		fLogger.log(Level.INFO, "sending to " + url + " : " + body.toString());
		
		String response = HttpUtil.sendJson(new URL(url), "POST", body.toString(),
				fLiferayUser, fLiferayPassword);
		
		fLogger.log(Level.INFO, "got response: " + response);
		
		//TODO: evaluate repsonse?
	}
	
	public void addComment(String comment, Map<String, Object> variables)
			throws Exception
	{
		JSONObject params = new JSONObject();
		String groupId = variables.get("Liferay_groupId").toString();
		params.put("groupId", Long.valueOf(groupId));
		params.put("className", variables.get("Liferay_entryClassName"));
		String classPK = variables.get("Liferay_entryClassPK").toString();
		params.put("classPK", Long.valueOf(classPK));
		params.put("body", comment);
		
		// construct JSON RPC body
		JSONObject body = new JSONObject();
		body.put(METHOD_PARAM, ADD_COMMENT_METHOD);
		body.put(PARAMS_PARAM, params);
		// TODO: what kind of ID is this? assuming request ID
		body.put(ID_PARAM, fRandom.nextInt());
		body.put(JSONRPC_PARAM, JSONRPC_VALUE);
		
		
		// send to Liferay
		String url = fLiferayUrl + JSONWS_FRAG + COMMENTS_PATH;
		
		fLogger.log(Level.INFO, "sending to " + url + " : " + body.toString());
		
		String response = HttpUtil.sendJson(new URL(url), "POST", body.toString(),
				fLiferayUser, fLiferayPassword);
		
		fLogger.log(Level.INFO, "got response: " + response);
		
		//TODO: evaluate repsonse?
	}
}
