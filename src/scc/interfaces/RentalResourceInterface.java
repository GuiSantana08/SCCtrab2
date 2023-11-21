package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.Rental;

public interface RentalResourceInterface {

        @Path("/create")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response createRental(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        Rental rental, @PathParam("id") String id);

        @Path("/update")
        @PUT
        @Consumes(MediaType.APPLICATION_JSON)
        public Response updateRental(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        Rental rental, @PathParam("id") String id);

        @Path("/delete")
        @DELETE
        public Response deleteRental(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        @QueryParam("id") String id);

        @Path("/getInfo")
        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response getRentalInfo(@HeaderParam("isCacheActive") boolean isCacheActive, @QueryParam("id") String id);

        @Path("/listDiscontedRentals")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response listDiscountedRentals(@PathParam("id") String id);
}
