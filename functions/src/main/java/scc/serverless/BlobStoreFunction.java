package scc.serverless;

import com.microsoft.azure.functions.annotation.*;

import com.microsoft.azure.functions.*;

import scc.serverless.azure.blob.BlobStoreLayer;

/**
 * Azure Functions with Blob Trigger.
 */
public class BlobStoreFunction {
	@FunctionName("imagereplication")
	public void setLastBlobInfo(
			@BlobTrigger(name = "imagereplication", dataType = "binary", path = "images/{name}", connection = "BlobStoreConnection") byte[] content,
			@BindingName("name") String blobname,
			final ExecutionContext context) {
		BlobStoreLayer blob = BlobStoreLayer.getInstance();
		blob.upload(blobname, content);
	}

}
