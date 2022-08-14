package edu.school21.cjettie.repositories;

import edu.school21.cjettie.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OrmManagerTest {

    private final String SCHEMA_FILE_NAME = "schema.sql";
    private final String ORIGINAL_SCHEMA_FILE_NAME = "original_schema.sql";
    private OrmManager ormManager;

    @BeforeEach
    @Test
    void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL)
                .generateUniqueName(true)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript(SCHEMA_FILE_NAME)
                .build();
        ormManager = new OrmManager(dataSource);
    }

    @Test
    void test_find_by_id( ) {
        User user = new User(null,
                "Boba",
                "Fett",
                36,
                false,
                25D,
                7L);
        ormManager.save(user);
        User userFromBase = ormManager.findById(0L, user.getClass());
        Assertions.assertEquals(user, userFromBase);
    }
    @Test
    void test_save( ) {
        User user1 = new User(null,
                "Boba",
                "Fett",
                36,
                false,
                25D,
                7L);
        User user2 = new User(null,
                "Boba",
                "Fett",
                36,
                false,
                25D,
                7L);
        ormManager.save(user1);
        ormManager.save(user2);
        Assertions.assertEquals(user1.getId(), 0);
        Assertions.assertEquals(user2.getId(), 1);
    }

    @Test
    void test_update( ) {
        User user = new User(null,
                "Boba",
                "Fett",
                36,
                false,
                25D,
                7L);
        ormManager.save(user);
        user.setFirstName("Vader");
        ormManager.update(user);
        User updatedUser = ormManager.findById(user.getId(), user.getClass());
        Assertions.assertEquals(user.getFirstName(), updatedUser.getFirstName());
    }

}
