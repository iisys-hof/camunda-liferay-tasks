package de.hofuniversity.iisys.camunda.liferay;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.liferay.util.LiferayUtil;

public class AddCommentDelegate implements JavaDelegate
{
	private final LiferayUtil fLiferay;
	
	public AddCommentDelegate()
	{
		fLiferay = LiferayUtil.getInstance();
	}

	public void execute(DelegateExecution exec) throws Exception
	{
		String comment = (String) exec.getVariable("Liferay_comment");
		
		fLiferay.addComment(comment, exec.getVariables());
	}
}
