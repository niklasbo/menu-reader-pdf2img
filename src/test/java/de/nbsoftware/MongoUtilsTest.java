package de.nbsoftware;

import org.junit.Test;

public class MongoUtilsTest {
    private static final String MONGODB_CONNECTION_STRING = "mongodb+srv://...?retryWrites=true&w=majority";

    @Test
    public void testIsWeeknumAlreadyInMongoDB() {
        MongoUtils.isWeeknumAlreadyInMongoDB(MONGODB_CONNECTION_STRING, "menudb", "weeknum-and-image", 1);
    }

    @Test
    public void testWriteToMongoDB() {

    }
}
