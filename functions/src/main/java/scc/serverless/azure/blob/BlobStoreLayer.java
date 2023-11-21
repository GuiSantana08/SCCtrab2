package scc.serverless.azure.blob;

import java.util.List;
import java.util.stream.Collectors;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;

public class BlobStoreLayer {

    static String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=sccblob2324;AccountKey=hbNdZ5Nj7mQC+Hvhstl+ox3RNhz0lkJjwiCOrM0eFTiqjXUTfGS26u3XxNJHiSQSuYeO4eUzUbV3+AStm4UGFw==;EndpointSuffix=core.windows.net";

    private static BlobStoreLayer instance;

    public static synchronized BlobStoreLayer getInstance() {
        if (instance != null)
            return instance;

        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(storageConnectionString)
                .containerName("images")
                .buildClient();

        instance = new BlobStoreLayer(containerClient);
        return instance;
    }

    private BlobContainerClient client;

    public BlobStoreLayer(BlobContainerClient client) {
        this.client = client;
    }

    public void upload(String key, byte[] image) {
        BlobClient blob = client.getBlobClient(key);
        blob.upload(BinaryData.fromBytes(image));
    }

    public byte[] download(String key) {
        BlobClient blob = client.getBlobClient(key);
        BinaryData data = blob.downloadContent();
        return data.toBytes();
    }

    public boolean delete(String key) {
        BlobClient blob = client.getBlobClient(key);
        boolean isDeleted = blob.deleteIfExists();
        return isDeleted;
    }

    public List<String> list() {
        List<BlobItem> blobs = client.listBlobs().stream().collect(Collectors.toList());
        return blobs.stream().map(BlobItem::getName).collect(Collectors.toList());
    }
}
