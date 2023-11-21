package scc.serverless;

import scc.serverless.azure.db.RentalDBLayer;
import scc.serverless.data.RentalDAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.azure.cosmos.util.CosmosPagedIterable;

import redis.clients.jedis.Jedis;
import scc.serverless.azure.cache.RedisCache;

public class TimerFunction {
    private static final Logger logger = Logger.getLogger(TimerFunction.class.getName());

    @FunctionName("deleteRentalsByTime")
    public void cosmosFunction(@TimerTrigger(name = "periodicSetTime", schedule = "* */1 * * * *") String timerInfo,
            ExecutionContext context) throws ParseException {
        logger.info("Java Timer trigger function executed at: " + new Date());
        RentalDBLayer rentalDB = RentalDBLayer.getInstance();
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            jedis.incr("cnt:timer");
            jedis.set("serverless-time", new SimpleDateFormat("MMMM").format(new Date()));
        }

        CosmosPagedIterable<RentalDAO> rentals = rentalDB.getRentals();

        // Get the current month as an integer
        int currentMonth = LocalDate.now().getMonthValue();

        for (RentalDAO rental : rentals) {
            String rentalDate = rental.getRentalPeriod();
            String[] monthsList = rentalDate.split("-");
            String lastMonth = monthsList[monthsList.length - 1];

            Month monthEnum = Month.valueOf(lastMonth.toUpperCase(Locale.ENGLISH));

            // Get the month value as an integer
            int lastMonthValue = monthEnum.getValue();

            if (lastMonthValue < currentMonth)
                rentalDB.delRentalById(rental.getId());
        }

    }
}
