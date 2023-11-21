package scc.srv;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import jakarta.ws.rs.Path;
import scc.interfaces.MediaResourceInterface;
import scc.utils.Hash;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource implements MediaResourceInterface {

	String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=scc2223lab2;AccountKey=aKo5p8YMR6kaaXQdYpakkzC1KDS0rKibpazF4dyV0MpL2ezGcRjzOJEuzCVYZ2lPsgCMWi5L37c/+AStrnDibA==;EndpointSuffix=core.windows.net";
	BlobContainerClient containerClient = new BlobContainerClientBuilder()
			.connectionString(storageConnectionString)
			.containerName("images")
			.buildClient();

	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		BlobClient blob = containerClient.getBlobClient(key);
		blob.upload(BinaryData.fromBytes(contents));
		return key;
	}

	public byte[] download(String id) {
		BlobClient blob = containerClient.getBlobClient(id);
		BinaryData data = blob.downloadContent();
		return data.toBytes();
	}
}
