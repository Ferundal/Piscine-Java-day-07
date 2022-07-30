package edu.school21.cjettie.repositories;

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



    public static boolean isFilesSame(String path1, String path2)  {
        try {
            BufferedReader bf1 = new BufferedReader(new InputStreamReader(OrmManagerTest.class.getResource(path1).openStream()));
            BufferedReader bf2 =new BufferedReader(new InputStreamReader(OrmManagerTest.class.getResource(path2).openStream()));

            String line1 = "", line2 = "";
            while ((line1 = bf1.readLine()) != null) {
                line2 = bf2.readLine();
                if (line2 == null || !line1.equals(line2)) {
                    return false;
                }
            }
            if (bf2.readLine() == null) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (IOException ioException) {
            return false;
        }
    }
}
