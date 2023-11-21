package scc.azure.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;
import com.azure.search.documents.util.SearchPagedResponse;

public class CSLayer {

    public static final String SEARCH_PROP_FILE = "search-azurekeys-northeurope.props";

    public static final String PROP_SERVICE_NAME = "scc2324cs";
    public static final String PROP_SERVICE_URL = "https://scc2324cs.search.windows.net";
    public static final String PROP_INDEX_NAME = "cosmosdb-index";
    public static final String PROP_QUERY_KEY = "rXfxL3cqYMywdmxisOVy7n69eQ9zPkxi3Bgm3fM2rXAzSeA4POpC";

    public static final String PROPS_FILE = "azurekeys.props";

    private static CSLayer instance;
    private SearchClient searchClient;

    public static CSLayer getInstance() {
        if (instance != null)
            return instance;

        SearchClient searchClient = new SearchClientBuilder()
                .credential(new AzureKeyCredential(PROP_QUERY_KEY))
                .endpoint(PROP_SERVICE_URL).indexName(PROP_INDEX_NAME)
                .buildClient();

        instance = new CSLayer(searchClient);
        return instance;
    }

    public CSLayer(SearchClient searchClient) {
        this.searchClient = searchClient;
    }

    public List<List<Map.Entry<String, Object>>> csQuery(String query, String filter) {
        SearchOptions options = new SearchOptions()
                .setIncludeTotalCount(true)
                .setFilter(filter)
                .setSelect("id", "userId", "name", "location", "description", "basePrice", "availability")
                .setSearchFields("name", "description", "location")
                .setTop(5);

        SearchPagedIterable searchPagedIterable = searchClient.search(query, options, null);

        List<List<Map.Entry<String, Object>>> results = new ArrayList<>();
        for (SearchPagedResponse resultResponse : searchPagedIterable.iterableByPage()) {
            List<Map.Entry<String, Object>> houses = new ArrayList<>();
            resultResponse.getValue().forEach(searchResult -> {
                for (Map.Entry<String, Object> res : searchResult.getDocument(SearchDocument.class).entrySet()) {
                    houses.add(res);
                }
            });
            results.add(houses);
        }

        return results;
    }
}
