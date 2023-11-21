package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.House;

public interface HouseResourceInterface {
        @Path("/create")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response createHouse(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        House house);

        @Path("/delete")
        @DELETE
        public Response deleteHouse(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        @QueryParam("id") String id);

        @Path("/get")
        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response getHouse(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @QueryParam("id") String id);

        @Path("/update")
        @PUT
        @Consumes(MediaType.APPLICATION_JSON)
        public Response updateHouse(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        House house);

        @Path("/getHouseByLocation")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response listAvailableHouses(@QueryParam("location") String location);

        @Path("/searchAvailableHouses")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response searchAvailableHouses(@QueryParam("period") String period,
                        @QueryParam("location") String location);

        @GET
        @Path("/trysearch")
        @Produces(MediaType.APPLICATION_JSON)
        public Response trySearch(@QueryParam("query") String query, @QueryParam("filterType") String filterType,
                        @QueryParam("filter") String filter);

}
