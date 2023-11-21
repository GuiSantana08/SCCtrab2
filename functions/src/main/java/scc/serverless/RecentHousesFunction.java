package scc.serverless;

import java.util.*;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import redis.clients.jedis.Jedis;
import scc.serverless.azure.cache.RedisCache;

import java.util.logging.Logger;

public class RecentHousesFunction {
	private static final Logger logger = Logger.getLogger(RecentHousesFunction.class.getName());

	@FunctionName("RecentHouses")
	public void Func(
			@CosmosDBTrigger(name = "database", databaseName = "scc2324", collectionName = "houses", connectionStringSetting = "AzureCosmosDBConnection", createLeaseCollectionIfNotExists = true, leaseCollectionName = "leases") String house,
			final ExecutionContext context) {

		logger.info("Java CosmosDB trigger function executed at: " + new Date());

		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			String key = "mostRecentHouses";
			int maxSize = 5;

			long currentSize = jedis.llen(key);
			if (currentSize >= maxSize) {
				jedis.lpop(key);
			}
			jedis.lpush(key, house);
		}
	}
}
