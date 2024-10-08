package scc.srv;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

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
import scc.data.HouseDAO;
import scc.data.Rental;
import scc.data.RentalDAO;
import scc.interfaces.RentalResourceInterface;

@Path("/house/{id}/rental")
public class RentalResource implements RentalResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    RentalsDBLayer rentalDB = RentalsDBLayer.getInstance();
    HousesDBLayer houseDB = HousesDBLayer.getInstance();

    static RedisCache cache = RedisCache.getInstance();

    @Override
    public Response createRental(boolean isCacheActive, boolean isAuthActive, Cookie session, Rental rental,
            String id) {
        try {
            if (isAuthActive) {
                UserResource.checkCookieUser(session, rental.getUserId());
            }

            RentalDAO rDAO = new RentalDAO(rental, id);

            if (!isValidRental(rDAO))
                return Response.status(Status.BAD_REQUEST).build();

            RentalDAO rentalObj = rentalDB.putRental(rDAO);

            if (rentalObj == null) {
                return Response.status(Status.CONFLICT).build();
            }

            if (isCacheActive) {
                cache.setValue(rental.getId(), rental);
            }

            return Response.ok(rDAO).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateRental(boolean isCacheActive, boolean isAuthActive, Cookie session, Rental rental,
            String id) {
        try {
            if (isAuthActive) {
                UserResource.checkCookieUser(session, rental.getUserId());
            }

            RentalDAO rDAO = new RentalDAO(rental, id);
            RentalDAO r = rentalDB.updateRental(rDAO);

            if (r == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            if (isCacheActive) {
                cache.setValue(rental.getId(), rental);
            }

            return Response.ok(r.toString()).build();

        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getRentalInfo(boolean isCacheActive, String id) {
        try {
            RentalDAO r = null;
            if (isCacheActive) {
                r = cache.getValue(id, RentalDAO.class);
            }

            if (r == null) {
                RentalDAO rental = rentalDB.getRentalById(id);

                if (rental != null) {

                    if (isCacheActive) {
                        cache.setValue(id, rental);
                    }

                    return Response.ok(rental).build();
                }

                return Response.status(Status.NOT_FOUND).build();
            }

            return Response.ok(r).build();

        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteRental(boolean isCacheActive, boolean isAuthActive, Cookie session, String id) {
        try {
            if (isAuthActive) {
                var rentalIt = rentalDB.getRentalById(id);
                if (rentalIt != null)
                    UserResource.checkCookieUser(session, rentalIt.getUserId());
            }

            String newId = rentalDB.delRentalById(id);

            if (newId == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            if (isCacheActive) {
                cache.delete(id);
            }

            return Response.ok(id).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listDiscountedRentals(String id) {
        List<RentalDAO> discountedRentals = new ArrayList<>();
        try {
            HouseDAO house = houseDB.getHouseById(id);
            int currentMonth = LocalDate.now().getMonth().getValue();

            List<RentalDAO> rentals = rentalDB.getRentalsByHouseId(house.getId());
            for (RentalDAO r : rentals) {
                for (String month : r.getRentalPeriod().split("-")) {
                    if (r.getPrice() < house.getBasePrice()
                            && Month.valueOf(month.toUpperCase()).getValue() >= currentMonth)
                        discountedRentals.add(r);
                }
            }

            return Response.ok(discountedRentals).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    private boolean isValidRental(RentalDAO rental) {
        String[] args = rental.getRentalPeriod().split("-");
        String first = args[0];
        String last = args[args.length - 1];

        List<RentalDAO> rentalsIt = rentalDB.getRentalsByHouseId(rental.getHouseId());

        for (RentalDAO rentalObj : rentalsIt) {
            for (String month : rental.getRentalPeriod().split("-")) {
                if (rentalObj.getRentalPeriod().contains(month)) {
                    return false;
                }
            }
        }

        HouseDAO house = houseDB.getHouseById(rental.getHouseId());

        if (house == null)
            return false;

        if (rental.getPrice() > house.getBasePrice())
            return false;

        if (!house.getAvailability().contains(first) || !house.getAvailability().contains(last)) {
            return false;
        }

        return true;
    }
}
