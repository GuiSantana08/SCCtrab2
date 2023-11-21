package scc.srv;

import java.util.List;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import scc.azure.blob.BlobStoreLayer;
import scc.interfaces.MediaResourceInterface;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource implements MediaResourceInterface {

	BlobStoreLayer blob = BlobStoreLayer.getInstance();

	public Response upload(byte[] contents) {
		Double key = Math.random();
		blob.upload(key.toString(), contents);
		return Response.ok().entity(key).build();
	}

	public Response download(String id) {
		byte[] data = blob.download(id);
		return Response.ok().entity(data).build();
	}

	@Override
	public Response delete(String id) {
		boolean isDeleted = blob.delete(id);

		if (!isDeleted)
			return Response.status(Status.NOT_FOUND).build();

		return Response.ok().build();
	}

	@Override
	public Response listImages() {
		List<String> list = blob.list();
		return Response.ok().entity(list).build();
	}
}
