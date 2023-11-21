package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.User;
import scc.utils.Login;

public interface UserResourceInterface {

        @Path("/create")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response createUser(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive,
                        @CookieParam("scc:session") Cookie session, User user);

        @Path("/delete")
        @DELETE
        public Response deleteUser(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        @QueryParam("userId") String userId);

        @Path("/update")
        @PUT
        @Consumes(MediaType.APPLICATION_JSON)
        public Response updateUser(@HeaderParam("isCacheActive") boolean isCacheActive,
                        @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
                        User user);

        @Path("/listHouses")
        @GET
        public Response listHouses(@QueryParam("userId") String userId);

        @POST
        @Path("/auth")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response auth(Login user);
}
