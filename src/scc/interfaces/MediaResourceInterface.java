package scc.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public interface MediaResourceInterface {

    /**
     * Post a new image.The id of the image is its hash.
     * 
     * @param contents - byte array containing the file uploaded
     * @return the key used in Storage
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response upload(byte[] contents);

    /**
     * Return the contents of an image. Throw an appropriate error message if
     * id does not exist.
     * 
     * @param id - the id by which a file is identified in the azure storage
     * @return the file in question
     */
    @GET
    @Path("/download/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("id") String id);

    /**
     * 
     * @param id
     * @return
     */
    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") String id);

    /**
     * 
     * @return
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listImages();
}
