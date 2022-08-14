package edu.school21.cjettie.models;

import edu.school21.cjettie.annotations.OrmColumn;
import edu.school21.cjettie.annotations.OrmColumnId;
import edu.school21.cjettie.annotations.OrmEntity;

import java.util.Objects;

@OrmEntity(table = "simple_user")
public class User {
    @OrmColumnId
    private Long id;
    @OrmColumn(name = "first_name", length = 10)
    private String firstName;
    @OrmColumn(name = "last_name", length = 10)
    private String lastName;
    @OrmColumn(name = "age")
    private Integer age;
    @OrmColumn(name = "is_admin")
    private boolean isAdmin;
    @OrmColumn(name = "rating")
    private Double rating;
    @OrmColumn(name = "operations")
    private Long operations;
    public User() {}

    public User (Long id,
                 String firstName,
                 String lastName,
                 Integer age,
                 boolean isAdmin,
                 Double rating,
                 Long operations) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.isAdmin = isAdmin;
        this.rating = rating;
        this.operations = operations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getOperations() {
        return operations;
    }

    public void setOperations(Long operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", isAdmin=" + isAdmin +
                ", rating=" + rating +
                ", operations=" + operations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isAdmin == user.isAdmin && Objects.equals(id, user.id) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(age, user.age) && Objects.equals(rating, user.rating) && Objects.equals(operations, user.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, isAdmin, rating, operations);
    }
}