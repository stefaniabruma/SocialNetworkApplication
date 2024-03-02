package ir.map.socialnetworkapp.Domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<User> friends = new ArrayList<>();

    private String password;

    public User(String firstName, String lastName) throws NoSuchAlgorithmException {
        this.firstName = firstName;
        this.lastName = lastName;

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update("implicit".getBytes());

        StringBuilder hashedPassword = new StringBuilder();
        for (byte b : md.digest()) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hashedPassword.append('0');
            }
            hashedPassword.append(hex);
        }

        this.password = hashedPassword.toString();
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\n' +
                ", lastName='" + lastName + '\n' +
                ", friends=" + friends + '\n' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, friends);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
