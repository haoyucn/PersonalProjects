package dao;

import java.sql.Blob;
import java.util.List;

import communication.PlayerUser;

public interface IUserDao {

    boolean addUser(String id, String password, String authtoken);
    boolean removeUser(String id);
    boolean updateAuthToken(String id, String authtoken);
    List<User> getUsers();
    boolean clear();
}
