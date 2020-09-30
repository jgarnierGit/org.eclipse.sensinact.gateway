package ${package}.app.component;

import org.eclipse.sensinact.gateway.core.message.AgentRelay;
import org.eclipse.sensinact.gateway.core.message.MidCallbackException;
import org.eclipse.sensinact.gateway.core.message.SnaMessage;
import org.eclipse.sensinact.gateway.core.message.SnaUpdateMessageImpl;
import org.eclipse.sensinact.gateway.core.message.whiteboard.AbstractAgentRelay;
import org.eclipse.sensinact.gateway.core.message.annotation.Filter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ${package}.app.service.WebAppRelay;

/**
 * {@link AgentRelay} implementation
 */
@Filter(handled = {SnaMessage.Type.UPDATE})
@Component(immediate=true, service=AgentRelay.class)
public class AgentRelayImpl extends AbstractAgentRelay {

	@Reference 
	private WebAppRelay webapp; 
	
	@Override
	public void doHandle(SnaUpdateMessageImpl message) throws MidCallbackException {
		webapp.relay(message);
	}
}
