package edu.school21.cjettie.models;

import edu.school21.cjettie.annotations.OrmColumn;
import edu.school21.cjettie.annotations.OrmColumnId;
import edu.school21.cjettie.annotations.OrmEntity;

@OrmEntity(table = "simple_user")
public class User {
    @OrmColumnId
    private Long id;
    @OrmColumn(name = "first_name", length = 10)
    private String firstName;
    @OrmColumn(name = "first_name", length = 10)
    private String lastName;
    @OrmColumn(name = "age")
    private Integer age;
// setters /getters
}