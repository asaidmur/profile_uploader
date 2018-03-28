package profile.utils;

import java.net.InetAddress;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import profile.constants.EsConstants;




public class EsConnectionFactory
{
     private static Logger logger = LogManager.getLogger(EsConnectionFactory.class);


     private static TransportClient client;

     

 	
     
	public static Client getClient() 
	{
		if (client == null) 
		{
			synchronized (EsConnectionFactory.class) 
			{
				try 
				{
				 	String esHost;
				 	int esPort;
				 
				 	ResourceBundle bundle = ResourceBundle.getBundle(EsConstants.ES_PROPERTIES);
				 	esHost = bundle.getString(EsConstants.ES_HOST);
				 	esPort = Integer.parseInt(bundle.getString(EsConstants.ES_PORT));
				 

				 	
					Settings settings = 
							Settings
							.builder()
							.put(EsConstants.CLUSTER_NAME_CONFIG, (bundle.getString(EsConstants.CLUSTER_NAME)))
							.put(EsConstants.CLIENT_TRANSPORT_SNIFF_CONFIG, (bundle.getString(EsConstants.CLIENT_TRANSPORT_SNIFF)))
							.build();
					
					client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
				} 
				catch (Exception e) 
				{
					logger.error(e.getMessage());
				}
			}
		}
		return client;

	}




     
     public synchronized void shutdown() 
     {
    	 	if (client != null) 
    	 	{
    	 		logger.debug("shutdown started");
    	 		client.close();
    	 		client.threadPool().shutdown();
    	 		client = null;
    	 		logger.debug("shutdown complete");
    	 	}
     }

     
     
}