package scc.utils;

public enum Constants {
    guerraConst("https://cosmos-scc.documents.azure.com:443/",
            "3yJ9ApcLNiJpbJJEPHDS04cbSBZygR3mmcUfeCGnJeJNP1opXQ4Nsofyec5WUtIGxN9NlxszDSZ4ACDb38RUqg==", "scc-58201",
            null, null),
    santanaConst("https://scc232460182.documents.azure.com:443/",
            "S7xFx9UhYoBVAQ4wTEBz9W7KtRGGDiSdcrckwFKlGql3LfUHZ3CPBtrzvy3924hQbRopSU0DcV9gACDbL2FjNw==", "scc2324", null,
            null),
    camposConst("https://scc2324db60353.documents.azure.com:443/",
            "Y2TBwQZcPSyzjtt3DerktHMEVWr0MZfrcriJ8w7gffqCcQMtAdoSyL3XHgCxU2HLmhGo6fxm3EMbACDbLpWZdg==", "scc2324",
            "scc2324ache60353.redis.cache.windows.net", "ncBK7vPcdT1hQ5iCEiYOYLUwSdw50xkLRAzCaKP2fkU="),
    deletedUser(null, null, "DeletedUser", null, null);

    private final String dbUrl;
    private final String dbKey;
    private final String dbName;

    private final String redisHostname;
    private final String redisKey;

    Constants(String dbUrl, String dbKey, String dbName, String redisHostname, String redisKey) {
        this.dbUrl = dbUrl;
        this.dbKey = dbKey;
        this.dbName = dbName;
        this.redisHostname = redisHostname;
        this.redisKey = redisKey;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbKey() {
        return dbKey;
    }

    public String getDbName() {
        return dbName;
    }

    public String getRedisHostname() {
        return redisHostname;
    }

    public String getredisKey() {
        return redisKey;
    }
}
