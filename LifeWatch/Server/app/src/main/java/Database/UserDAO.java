package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Database.Model.UserModel;

/**
 * Created by haoyucn on 5/23/19.
 */

public class UserDAO {

    public int insert (UserModel user) {
        SQLDBConnection connection = new SQLDBConnection();
        int result = -1;
        String statement = "INSERT INTO user (username, password, email, age, profession) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setInt(4, user.getAge());
            ps.setString(5,user.getProfession());
            result = connection.executeUpdateStatement(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.closeConnection();
        return result;
    }

    public UserModel getUserByUserName(String username) {
        SQLDBConnection connection = new SQLDBConnection();
        UserModel userModel = null;
        String statement = "Select username, password, email, age, profession from user where username = ?;";
        PreparedStatement ps = connection.getPreparedStatment(statement);
        try {
            ps.setString(1,username);
            ResultSet resultSet = connection.executeQueryStatement(ps);
            if (resultSet == null) {
                System.out.println("reached a null statement");
            }
            if (resultSet.next()) {
                userModel = new UserModel(resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getInt("age"),
                        resultSet.getString("profession"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userModel = null;
        }
        connection.closeConnection();
        return userModel;
    }

    public int update(UserModel userModel){
        SQLDBConnection connection = new SQLDBConnection();
        int result = -1;
        String statement =
                "Update user Set password = ?, " +
                        "email = ?, " +
                        "age = ?, " +
                        "profession = ? " +
                        "Where username = ?" +
                        ";";
        PreparedStatement ps = connection.getPreparedStatment(statement);
        try {
            ps.setString(1, userModel.getPassword());
            ps.setString(2, userModel.getEmail());
            ps.setInt(3, userModel.getAge());
            ps.setString(4, userModel.getProfession());
            ps.setString(5, userModel.getUsername());
            result = connection.executeUpdateStatement(ps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.closeConnection();
        return result;
    }

    public int delete(String username) {
        SQLDBConnection connection = new SQLDBConnection();
        int result = -1;
        String stmt = "Delete from user where username = ?";
        PreparedStatement ps = connection.getPreparedStatment(stmt);
        try{
            ps.setString(1, username);
            result = connection.executeUpdateStatement(ps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.closeConnection();
        return result;
    }

    public void clear() {
        SQLDBConnection connection = new SQLDBConnection();
        String sql = "DELETE FROM user";
        PreparedStatement stmt = connection.getPreparedStatment(sql);
        connection.executeUpdateStatement(stmt);
        connection.closeConnection();
    }
}
