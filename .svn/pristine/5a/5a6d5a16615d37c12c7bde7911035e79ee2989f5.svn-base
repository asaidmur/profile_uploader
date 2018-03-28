package profile.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import processor.beans.dao.es.EsSkill;
//import processor.constants.ProcessorConstants;
import profile.constants.EsConstants;
import profile.utils.EsConnectionFactory;

public class ProfileDao {

	private static Logger logger = LogManager.getLogger(ProfileDao.class);

	private static String esIndexProfile;
	private static String esTypeProfile;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle(EsConstants.ES_PROPERTIES);
		esIndexProfile = bundle.getString(EsConstants.ES_INDEX_SKILL);
		esTypeProfile = bundle.getString(EsConstants.ES_INDEX_PROFILE);
	}

	public static String saveProfileDoc(Map<String, String> profileDoc) throws InterruptedException, ExecutionException {

		long FLOOR = System.currentTimeMillis() - 120000L;
		long CEILING = System.currentTimeMillis() + 120000L;
		Gson gson = null;

		try 
		{
			gson = new GsonBuilder().create(); // (GsonAdapterType.SERIALIZER).create();
		} 
		catch (Exception e) 
		{
			logger.error("Error in GsonTypeAdapter for SERIALIZATION: " + e.getMessage());
		}

		
		
		String json = gson.toJson(profileDoc);

		boolean created = false;

		Client client = EsConnectionFactory.getClient();

		SearchResponse profileSearchResponse = client
				.prepareSearch(esIndexProfile)
				.setTypes(esTypeProfile)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery(EsConstants.HASHED_KEY, profileDoc.get(EsConstants.HASHED_KEY))))
				.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery(profileDoc.get(EsConstants.MSG_SEQ)).gte(FLOOR).lte(CEILING)))
//				.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery(EsConstants.DELIMITTED_KEY , profileDoc.get("delimitted_key"))))
				.setFrom(0)
				.setSize(60)
				.setExplain(true).execute().actionGet();

		//		update existing doc 
		
		if (profileSearchResponse.getHits().getTotalHits() > 0) 
		{
			Map<String,Object> profileDocEs = findProfileDoc(profileSearchResponse);
			
			// merge maps, then convert to string
			profileDocEs.putAll(profileDoc);
			String jsonUpsert = gson.toJson(profileDocEs);
			String id = profileSearchResponse.getHits().getAt(0).getId();

			// savedoc
			
			logger.debug("Updating existing doc in " + esIndexProfile + " " + esTypeProfile + " new msg: " + jsonUpsert);
			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(esIndexProfile);
			updateRequest.type(esTypeProfile);
			updateRequest.id(id);
			updateRequest.doc(jsonUpsert, XContentType.JSON);

			client.update(updateRequest).get();
			
			
			
		} 
		else 
		{
			logger.debug("Inserting new doc in " + esIndexProfile + " " + esTypeProfile + ": " + json);
			IndexResponse response = client.prepareIndex(esIndexProfile, esTypeProfile).setSource(json, XContentType.JSON).get();
			created = response.getResult().equals(Result.CREATED);
		}

		return (created ? EsConstants.ES_INSERT : EsConstants.ES_UPDATE);
	}

	
	
	
	private static Map<String, Object> findProfileDoc(SearchResponse profileSearchResponse) 
	{
		Map<String, Object> esProfileMap = new HashMap<String,Object>();

		try 
		{
			
			
			if (profileSearchResponse.getHits().getAt(0).getSourceAsMap() != null) 
			{
				SearchHit profileValue = profileSearchResponse.getHits().getAt(0);
				esProfileMap = profileValue.getSourceAsMap();
				
				if (!esProfileMap.isEmpty()) 
				{
					logger.debug("value is: " + esProfileMap);
					return esProfileMap;
				} 
				else 
				{
					logger.debug("profile is empty. default blank.");
				}
				
			} else 
			{
				logger.debug("profile not found");
			}
				
			return esProfileMap;
		} 
		catch(Exception e) {
			logger.error("Can't find profile, default blank - Exception Message:" + e.getMessage());
			return esProfileMap;
		}
	}

}