package Database.Model;

/**
 * Created by haoyucn on 5/23/19.
 */

public class UserModel {
    private String username;
    private String password;
    private String email;
    private int age;
    private String profession;

    public UserModel(String username, String password, String email, int age, String profession) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.profession = profession;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", profession='" + profession + '\'' +
                '}';
    }
}
