package scc.srv;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import com.azure.cosmos.CosmosException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import scc.azure.cache.RedisCache;
import scc.azure.db.HousesDBLayer;
import scc.azure.db.UsersDBLayer;
import scc.data.HouseDAO;
import scc.data.User;
import scc.data.UserDAO;
import scc.interfaces.UserResourceInterface;
import scc.utils.Constants;
import scc.utils.Hash;
import scc.utils.Login;
import scc.utils.Session;

@Path("/user")
public class UserResource implements UserResourceInterface {

    ObjectMapper mapper = new ObjectMapper();

    UsersDBLayer userDb = UsersDBLayer.getInstance();
    HousesDBLayer houseDb = HousesDBLayer.getInstance();

    static RedisCache cache = RedisCache.getInstance();

    @Override
    public Response createUser(boolean isCacheActive, boolean isAuthActive, Cookie session, User user) {
        try {
            if (isAuthActive) {
                checkCookieUser(session, user.getId());
            }

            UserDAO userDAO = new UserDAO(user);
            userDAO.setPwd(Hash.of(user.getPwd()));
            UserDAO u = userDb.putUser(userDAO);

            if (isCacheActive) {
                cache.setValue(userDAO.getId(), userDAO);
            }

            return Response.ok(u).build();
        } catch (

        NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getLocalizedMessage()).build();
        }
    }

    @Override
    public Response deleteUser(boolean isCacheActive, boolean isAuthActive, Cookie session, String userId) {
        try {
            if (isAuthActive) {
                checkCookieUser(session, userId);
            }

            for (HouseDAO h : houseDb.getHousesByUserId(userId)) {
                HouseDAO houseCosmos = houseDb.getHouseById(h.getId());
                if (houseCosmos != null) {
                    HouseDAO upHouse = houseCosmos;
                    upHouse.setUserId(Constants.deletedUser.getDbName());
                    houseDb.updateHouse(upHouse);

                    if (isCacheActive) {
                        cache.setValue(upHouse.getId(), upHouse);
                    }
                }
            }

            if (isCacheActive) {
                cache.delete(userId);
            }

            userDb.delUserById(userId);
            return Response.ok(userId).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateUser(boolean isCacheActive, boolean isAuthActive, Cookie session, User user) {
        try {
            if (isAuthActive) {
                checkCookieUser(session, user.getId());
            }

            UserDAO uDAO = new UserDAO(user);
            uDAO.setPwd(Hash.of(user.getPwd()));
            UserDAO u = userDb.updateUser(uDAO);

            if (isCacheActive) {
                cache.setValue(user.getId(), user);
            }

            return Response.ok(u).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listHouses(String id) {
        try {
            List<HouseDAO> houses = houseDb.getHousesByUserId(id);

            return Response.ok(houses.toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    public Response auth(Login user) {
        boolean pwdOk = true;

        String id = user.getId();
        String password = user.getPassword();

        if (id == null || password == null) {
            pwdOk = false;
        }

        var userIt = userDb.getUserById(id);

        if (userIt == null) {
            pwdOk = false;
        }

        if (pwdOk) {
            String uid = UUID.randomUUID().toString();
            NewCookie cookie = new NewCookie.Builder("scc:session")
                    .value(uid)
                    .path("/")
                    .comment("sessionid")
                    .maxAge(3600)
                    .secure(false)
                    .httpOnly(true)
                    .build();

            cache.putSession(new Session(uid, user.getId()));
            return Response.ok().cookie(cookie).build();
        } else
            throw new NotAuthorizedException("Incorrect login");
    }

    /**
     * Throws exception if not appropriate user for operation on Hopuse
     */
    public static Session checkCookieUser(Cookie session, String id)
            throws NotAuthorizedException {
        if (session == null || session.getValue() == null)
            throw new NotAuthorizedException("No session initialized");
        Session s;
        try {
            s = cache.getSession(session.getValue());
        } catch (CancellationException e) {
            throw new NotAuthorizedException("No valid session initialized");
        }
        if (s == null || s.getUsername() == null || s.getUsername().length() == 0)
            throw new NotAuthorizedException("No valid session initialized");
        if (!s.getUsername().equals(id))
            throw new NotAuthorizedException("Invalid user : " + s.getUsername());
        return s;
    }

}