package fiware.adaptadorgeoevento.processor;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;

public class AdaptadorGeoeventoProcessorService extends GeoEventProcessorServiceBase
{
	private GeoEventDefinitionManager manager;
	private Messaging messaging;
	
	public AdaptadorGeoeventoProcessorService()
	{
		definition = new AdaptadorGeoeventoProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() throws ComponentException
	{
		AdaptadorGeoeventoProcessor processor = new AdaptadorGeoeventoProcessor(definition);
		processor.setMessaging(messaging);
		processor.setManager(manager);
		
		return processor;
	}

	public void setMessaging(Messaging messaging)
	{
		this.messaging = messaging;
	}

	public void setManager(GeoEventDefinitionManager manager)
	{
		this.manager = manager;
	}
}