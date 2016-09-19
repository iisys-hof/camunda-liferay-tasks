package de.hofuniversity.iisys.camunda.liferay;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.hofuniversity.iisys.camunda.liferay.util.LiferayUtil;

public class ApproveLiferayWorkflowDelegate implements JavaDelegate
{
	private final LiferayUtil fLiferay;
	
	public ApproveLiferayWorkflowDelegate()
	{
		fLiferay = LiferayUtil.getInstance();
	}

	public void execute(DelegateExecution exec) throws Exception
	{
		fLiferay.updateWorkflowStatus("approved", exec.getVariables());
	}

}
