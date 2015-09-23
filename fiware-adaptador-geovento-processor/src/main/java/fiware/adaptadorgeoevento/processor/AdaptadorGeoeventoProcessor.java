package fiware.adaptadorgeoevento.processor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.StringUtils;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.Property;
import com.esri.ges.core.validation.ValidationException;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManagerException;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;

public class AdaptadorGeoeventoProcessor extends GeoEventProcessorBase
{
	private static final BundleLogger log = BundleLoggerFactory.getLogger(AdaptadorGeoeventoProcessor.class);
  
	private Messaging messaging;
	private GeoEventDefinitionManager manager;
	private ArrayList<String> camposOrigenList = null;
	private ArrayList<String> camposDestinoList = null;
	private String nombreEvento = null;
	
	protected AdaptadorGeoeventoProcessor(GeoEventProcessorDefinition definition) throws ComponentException
	{
		super(definition);
	}
	
	public void setMessaging(Messaging messaging)
	{
		this.messaging = messaging;
	}

	public void setManager(GeoEventDefinitionManager manager)
	{
		this.manager = manager;
	}

	public void afterPropertiesSet()
	{
//		Boolean limpiarCache = (Boolean)getProperty("limpiarCache").getValue();
//		
//		campoClave = (String)getProperty("campoClave").getValue();
//		
//		String campos = (String)getProperty("camposDetectar").getValue();
//		log.info(String.format("Campos a detectar cambios: %s", campos));
//		if (campos == null)
//		{
//			log.error("DetectCambiosInfoSIGOProcessor propiedad camposDetectar vacia.");
//		}
//		else
//		{
//			camposDetectarCambios = campos.trim().split(",");
//			if (camposDetectarCambios.length == 0)
//				log.error("DetectCambiosInfoSIGOProcessor propiedad camposDetectar vacia.");
//		}
//		
//		antiguedadEvento = (Converter.convertToInteger(getProperty("antiguedadEvento").getValueAsString(), Integer.valueOf(600)).intValue() * 1000);
//		mostrarMensajesLog = (Boolean)getProperty("mensajesLog").getValue();
		
		String camposOrigen = (String)getProperty("camposAdaptarOrigen").getValue();
		String[] camposOrigenArray = camposOrigen.split(",");
		camposOrigenList = new ArrayList<String>(Arrays.asList(camposOrigenArray));
		String camposDestino = (String)getProperty("camposAdaptarDestino").getValue();
		String[] camposDestinoArray = camposDestino.split(",");
		camposDestinoList = new ArrayList<String>(Arrays.asList(camposDestinoArray));
		
		nombreEvento = (String)getProperty("definicionGeoeventoSalida").getValue();
		
		super.afterPropertiesSet();
	}
	
	@Override
	public synchronized void validate() throws ValidationException {
		// Validation Phase ...
		super.validate();
		if(camposOrigenList.size() != camposDestinoList.size()){
			throw new ValidationException("El número de campos origen y destino debe ser el mismo");
		}
//		if (bufferEventFld == null) {
//			if (radius == null)
//				throw new ValidationException("Radius is not specified.");
//			if (radius <= 0)
//				throw new ValidationException("Radius must be greater than 0.");
//		} else if (bufferEventFld.trim().equals("")) {
//			if (radius == null)
//				throw new ValidationException("Radius is not specified.");
//			if (radius <= 0)
//				throw new ValidationException("Radius must be greater than 0.");
//		}
	}


	@Override
	public GeoEvent process(GeoEvent geoEvent) throws Exception
	{
		
		int i = 0;
		String[] coordenadas = null;
		String strFecha = "";
		DateFormat formatoFechaUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
		Date fecha = null;
		String strFechaLocal = null;
		GeoEvent geoEventConvertido = null;
		double volumen = 0.0;
		
		ArrayList<AtributoGeoevento> atributosEvento = new ArrayList<AtributoGeoevento>();
		
		try{
			
			String id = (String)geoEvent.getField("id");
			log.debug("Leyendo los parámetros");
			List<FieldGroup> listAtributos = geoEvent.getFieldGroups("attributes");
			int indiceCampo;
			for (i = 0; i< listAtributos.size();i++){
				FieldGroup atributos = listAtributos.get(i);
				String name = (String)atributos.getField("name");
				String value = (String)atributos.getField("value");
				
				if (name.compareTo("position") == 0){
					coordenadas = value.split(",");
				}
				else if (name.compareTo("TimeInstant") == 0){
					strFecha = value;
					fecha = formatoFechaUTC.parse(strFecha);
					strFechaLocal = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(fecha);
				}
				/*else if (name.compareTo("sound") == 0){
					volumen = Double.parseDouble(value);
				}*/
				else if ((indiceCampo = camposOrigenList.indexOf(name)) != -1){
					AtributoGeoevento atrib = new AtributoGeoevento();
					atrib.setNombreCampo(camposDestinoList.get(indiceCampo));
					atrib.setValorCampo(value);
					
					atributosEvento.add(atrib);
				}
			}
						
			
			double latitud = Double.parseDouble(coordenadas[0]);
			double longitud = Double.parseDouble(coordenadas[1]);
			int wkid = 4326; //WGS84
			log.debug("Parseada la posición");
			geoEventConvertido = crearGeoeventoDestino();
			geoEventConvertido.setField("CreationTime", fecha);       	
			geoEventConvertido.setField("FechaCreacionLocal", strFechaLocal);       	
			geoEventConvertido.setField("Id", id);
			geoEventConvertido.setField("Longitud", longitud);      	
			geoEventConvertido.setField("Latitud", latitud);
			
			
			for(i=0;i<atributosEvento.size();i++){
				
				AtributoGeoevento atrib = atributosEvento.get(i);
				FieldDefinition campoDef = geoEventConvertido.getGeoEventDefinition().getFieldDefinition(atrib.getNombreCampo());
				if(campoDef != null){
					FieldType tipoCampo = campoDef.getType();
					
					if(tipoCampo == FieldType.String){
						geoEventConvertido.setField(atrib.getNombreCampo(), atrib.getValorCampo());
					}
					else if(tipoCampo == FieldType.Double){
						
						//if(org.apache.commons.lang3.StringUtils.isNumeric(atrib.getValorCampo())==true){
							double valorDoble = Double.parseDouble(atrib.getValorCampo());
							geoEventConvertido.setField(atrib.getNombreCampo(),valorDoble);
					//	}
						
					}
					else if(tipoCampo == FieldType.Integer){
					//	if(org.apache.commons.lang3.StringUtils.isNumeric(atrib.getValorCampo())==true){
							int valorInt = Integer.parseInt(atrib.getValorCampo());
							geoEventConvertido.setField(atrib.getNombreCampo(),valorInt);
						//}
					}
					else if(tipoCampo == FieldType.Date){
						Date fechaCampo = formatoFechaUTC.parse(atrib.getValorCampo());
						geoEventConvertido.setField(atrib.getNombreCampo(),fechaCampo);					
					}
				}
				
			}
			
			Point punto = new Point(longitud,latitud);
			SpatialReference sref = SpatialReference.create(wkid);
			MapGeometry mapGeo = new MapGeometry(punto,sref);
			geoEventConvertido.setGeometry(mapGeo);
			//geoEventConvertido.setField("Shape", punto);
			log.debug("Geoevento generado");
		}
		catch (Exception e){
			log.error("Error al crear el geoevento: " + e.getMessage());
		}
		return geoEventConvertido;
	}

	private GeoEvent crearGeoeventoDestino()
	{
		GeoEvent geoeventoDestino = null;
		
		try
		{
			
			//GeoEventDefinition geDef = manager.searchGeoEventDefinition(nombreEvento, owner);
			Collection<GeoEventDefinition> collGeDef = manager.searchInRepositoryGeoEventDefinitionByName(nombreEvento);
			Iterator<GeoEventDefinition> it = collGeDef.iterator();
			if(it.hasNext())
			{
				GeoEventDefinition geDef = it.next();
				geoeventoDestino = messaging.createGeoEventCreator().create(geDef.getGuid());
				log.debug("Creado geoevento");
			}
			else
			{
				log.error("Error: El GeoEvento " + nombreEvento + " no existe.");
				return null;
			}
			
		}
		catch (MessagingException ex)
		{
			log.error("Error al crear un mensaje GeoEvent", ex);
			return null;
		}
//		catch (GeoEventDefinitionManagerException ex)
//		{
//			log.error("Error al crear un mensaje GeoEvent.", ex);
//			return null;
//		}
		
		return geoeventoDestino;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(definition.getName());
		sb.append("/");
		sb.append(definition.getVersion());
		sb.append("[");
		for (Property p : getProperties())
		{
			sb.append(p.getDefinition().getPropertyName());
			sb.append(":");
			sb.append(p.getValue());
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
}