package fiware.adaptadorgeoevento.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class AdaptadorGeoeventoProcessorDefinition extends GeoEventProcessorDefinitionBase
{
	private static final Log log = LogFactory.getLog(AdaptadorGeoeventoProcessorDefinition.class);
	
	public AdaptadorGeoeventoProcessorDefinition()
	{
		try
		{
		 	// this.propertyDefinitions.put("campoClave", new PropertyDefinition("campoClave", PropertyType.String, "", "Campo clave", "Campo clave usado en la cache de este processor", Boolean.valueOf(true), Boolean.valueOf(false)));			
		 	this.propertyDefinitions.put("camposAdaptarOrigen", new PropertyDefinition("camposAdaptarOrigen", PropertyType.String, "", "Campos origen a adaptar", "Campos del GeoEvento de Fiware a adaptar separados por coma", Boolean.valueOf(true), Boolean.valueOf(false)));
		 	this.propertyDefinitions.put("camposAdaptarDestino", new PropertyDefinition("camposAdaptarDestino", PropertyType.String, "", "Campos destino a adaptar", "Campos del GeoEvento de salida a adaptar separados por coma. Deben correponder con los campos origen", Boolean.valueOf(true), Boolean.valueOf(false)));
		 	this.propertyDefinitions.put("definicionGeoeventoSalida",new PropertyDefinition("definicionGeoeventoSalida",PropertyType.GeoEventDefinition, "", "Geoevento para convertir json de fiware","Nombre de la definición de geoevento para convertir el json proveniente de Fiware", Boolean.valueOf(true),Boolean.valueOf(false)));
		 	// this.propertyDefinitions.put("antiguedadEvento", new PropertyDefinition("antiguedadEvento", PropertyType.Long, Integer.valueOf(600), "Antigüedad del GeoEvento (segundos)", "Antigüedad del GeoEvento (segundos), tras estos segundos en caché se borra el GeoEvento de la caché", Boolean.valueOf(true), Boolean.valueOf(false)));
		 	// this.propertyDefinitions.put("limpiarCache", new PropertyDefinition("limpiarCache", PropertyType.Boolean, true, "Limpiar Cache", "Establezca esta propiedad para borrar la caché. Por ejemplo, si algo ha ido mal en GEP, como outputs que no lleguen a su destino(s), este procesador puede impedir que esos outputs sean reprocesados inmediatamente a menos que la caché sea borrada.", false, false ));
		 	// this.propertyDefinitions.put("mensajesLog", new PropertyDefinition("mensajesLog", PropertyType.Boolean, false, "Mostrar mensajes de log", "Mostrar mensajes de log", false, false ));
		}
	    catch (Exception e)
	    {
	      log.error("Error al inicializar el adaptador geoeventos de fiware Processor Definition.", e);
	    }		
	}

	@Override
	public String getName()
	{
		return "ConversorFiwareGeoevento";
	}

	@Override
	public String getDomain()
	{
		return "fiware.adaptadorgeoevento";
	}

	@Override
	public String getVersion()
	{
		return "10.3.1";
	}

	@Override
	public String getLabel()
	{
	  /**
     * Note: by using the ${myBundle-symbolic-name.myProperty} notation, the
     * framework will attempt to replace the string with a localized string in
     * your properties file.
     */
		return "Conversor Fiware Geoevento";
	}

	@Override
	public String getDescription()
	{
	  /**
     * Note: by using the ${myBundle-symbolic-name.myProperty} notation, the
     * framework will attempt to replace the string with a localized string in
     * your properties file.
     */
		return "Este procesador convierte el JSON recibido de Fiware a un geoevento";
	}

	@Override
	public String getContactInfo()
	{
		return "soluciones@esri.es";
	}
}