package scc.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public interface MediaResourceInterface {

    /**
     * Post a new image.The id of the image is its hash.
     * 
     * @param contents - byte array containing the file uploaded
     * @return the key used in Storage
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public String upload(byte[] contents);

    /**
     * Return the contents of an image. Throw an appropriate error message if
     * id does not exist.
     * 
     * @param id - the id by which a file is identified in the azure storage
     * @return the file in question
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] download(@PathParam("id") String id);
}
