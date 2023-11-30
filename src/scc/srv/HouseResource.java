package scc.srv;

import com.azure.cosmos.CosmosException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import scc.azure.cache.RedisCache;
import scc.azure.db.HousesDBLayer;
import scc.azure.db.RentalsDBLayer;
import scc.azure.db.UsersDBLayer;
import scc.azure.search.CSLayer;
import scc.data.House;
import scc.data.HouseDAO;
import scc.data.RentalDAO;
import scc.interfaces.HouseResourceInterface;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/house")
public class HouseResource implements HouseResourceInterface {

    ObjectMapper mapper = new ObjectMapper();

    HousesDBLayer houseDb = HousesDBLayer.getInstance();
    RentalsDBLayer rentDb = RentalsDBLayer.getInstance();
    UsersDBLayer userDb = UsersDBLayer.getInstance();

    static RedisCache cache = RedisCache.getInstance();

    @Override
    public Response createHouse(boolean isCacheActive, boolean isAuthActive, Cookie session, House house) {
        try {
            if (isAuthActive) {
                UserResource.checkCookieUser(session, house.getUserId());
            }

            HouseDAO hDAO = new HouseDAO(house);
            HouseDAO h = houseDb.putHouse(hDAO);

            if (isCacheActive) {
                cache.setValue(hDAO.getId(), hDAO);
            }

            return Response.ok(h).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteHouse(boolean isCacheActive, boolean isAuthActive, Cookie session, String id) {
        try {
            if (isAuthActive) {
                var houseIt = houseDb.getHouseById(id);

                UserResource.checkCookieUser(session, houseIt.getUserId());
            }

            houseDb.delHouseById(id);

            if (isCacheActive) {
                cache.delete(id);
            }

            return Response.ok().build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getHouse(boolean isCacheActive, boolean isAuthActive, String id) {
        try {
            HouseDAO h = null;
            if (isCacheActive) {
                h = cache.getValue(id, HouseDAO.class);
            }

            if (h == null) {
                var newH = houseDb.getHouseById(id);

                if (isCacheActive) {
                    cache.setValue(id, newH);
                }

                return Response.ok(newH).build();
            }

            return Response.ok(h).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateHouse(boolean isCacheActive, boolean isAuthActive, Cookie session, House house) {
        try {
            if (isAuthActive) {
                UserResource.checkCookieUser(session, house.getUserId());
            }

            HouseDAO hDAO = new HouseDAO(house);
            HouseDAO h = houseDb.updateHouse(hDAO);

            if (isCacheActive) {
                cache.setValue(house.getId(), house);
            }

            return Response.ok(h.toString()).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listAvailableHouses(String location) {
        List<HouseDAO> housesList = new ArrayList<>();
        try {
            List<HouseDAO> houseCosmos = houseDb.getHousesByLocation(location);
            String currentMonth = LocalDate.now().getMonth().toString().toLowerCase();

            for (HouseDAO h : houseCosmos) {
                List<RentalDAO> rentals = rentDb.getRentalsByHouseId(h.getId());
                boolean isOn = true;
                for (RentalDAO r : rentals) {
                    if (r.getRentalPeriod().contains(currentMonth))
                        isOn = false;
                }
                if (isOn)
                    housesList.add(h);
            }

            return Response.ok(housesList).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response searchAvailableHouses(String period, String location) {
        List<HouseDAO> availableHouses = new ArrayList<>();
        try {
            List<HouseDAO> houseCosmos = houseDb.getHousesByLocation(location);
            String[] months = period.split("-");

            for (HouseDAO h : filterAvailableHouses(houseCosmos, months)) {
                List<RentalDAO> rentals = rentDb.getRentalsByHouseId(h.getId());
                boolean isOn = true;
                for (RentalDAO r : rentals) {
                    for (String month : months) {
                        if (r.getRentalPeriod().contains(month))
                            isOn = false;
                    }
                }
                if (isOn)
                    availableHouses.add(h);
            }

            return Response.ok(availableHouses).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response trySearch(String query, String filterType, String filter) {
        CSLayer search = CSLayer.getInstance();

        String CSFilter = "";

        switch (filterType) {
            case "name":
                CSFilter = "name eq '" + filter + "'";
                break;
            case "location":
                CSFilter = "location eq '" + filter + "'";
                break;
            case "userId":
                CSFilter = "userId eq '" + filter + "'";
                break;
            default:
                break;
        }

        List<List<Map.Entry<String, Object>>> result = search.csQuery(query, CSFilter);

        return Response.ok(result).build();
    }

    private List<HouseDAO> filterAvailableHouses(List<HouseDAO> houseCosmos, String[] months) {
        List<HouseDAO> availableHouses = new ArrayList<>();

        for (HouseDAO h : houseCosmos) {
            for (String m : months) {
                if (h.getAvailability().contains(m)) {
                    availableHouses.add(h);
                    break;
                }
            }
        }

        return availableHouses;
    }

}
