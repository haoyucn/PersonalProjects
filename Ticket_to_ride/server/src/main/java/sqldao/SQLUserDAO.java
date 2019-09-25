package sqldao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import communication.Result;
import communication.Serializer;
import dao.IUserDao;
import dao.User;

public class SQLUserDAO implements IUserDao {
    SQLDBConnection connection = new SQLDBConnection();

    public static void main (String[] argv) {
        User user = new User("abab", "werweer", "dfadxccc");
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        sqlUserDAO.addUser(user.getId(),user.getPassword(),user.getAuthtoken());
        List<User> users = sqlUserDAO.getUsers();
        for(User u : users) {
            if (u.equals(user)) {
                System.out.println("user found");
            }
        }
    }

    @Override
    public boolean addUser(String id, String password, String authtoken) {
        Result result = null;
        String statement = "INSERT INTO users (id, password, authtoken) " +
                "VALUES (?, ?, ?)";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,id);
            ps.setString(2, password);
            ps.setString(3, authtoken);
            result = connection.executeUpdateStatement(ps);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (result == null) {
            return false;
        }
        return false;
    }

    @Override
    public boolean removeUser(String id) {
        Result result = null;
        String statement = "DELETE FROM users WHERE id = ?";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,id);
            result = connection.executeUpdateStatement(ps);


        } catch (SQLException e) {
            return false;
        }

        if (result == null) {
            return false;
        }
        return result.isSuccess();    }

    @Override
    public boolean updateAuthToken(String id, String authtoken) {
        Result result = null;
        String statement = "UPDATE users SET authtoken = ? WHERE id = ?";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        try {
            ps.setString(1,authtoken);
            ps.setString(2,id);
            result = connection.executeUpdateStatement(ps);

        } catch (SQLException e) {
            return false;
        }

        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        Result result = null;
        String statement = "SELECT * FROM users";
//        PreparedStatement ps = connection.getPreparedStatment(statement);
        Statement ps = connection.getStatement();
        try {
            ResultSet result1 = ps.executeQuery(statement);
            System.out.println("PRINTING");
            System.out.println("FINISHED");
//            while (result1.)
            result = new Result(true, result1,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (result == null) {
            return null;
        }
        else if (result.isSuccess()){
            System.out.println(result.toString());
            ResultSet rs = (ResultSet) result.getData();
            try {
                ArrayList<String> lobbyJsons = new ArrayList<String>();
                System.out.println("RESULT SIZE " + rs.getFetchSize());
                while(rs.next()) {
                    //rs.getInt()
//                    System.out.println(rs.getString("id"));
//                    System.out.println(rs.getString("password"));
                    Serializer serializer = new Serializer();

                    users.add(new User(rs.getString("id"), rs.getString("password"), rs.getString("authtoken")));
                    System.out.println(rs.toString());

//                    String gameJson = new String(b.getBytes(1,(int) b.length()));
//                    gameJsons.add(gameJson);
//                    System.out.println(gameJson);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return users;
    }

    @Override
    public boolean clear() {
        Result result = null;
        String statement = "DELETE FROM users";
        PreparedStatement ps = connection.getPreparedStatment(statement);

        result = connection.executeUpdateStatement(ps);

        if (result == null) {
            return false;
        }
        return result.isSuccess();
    }
}
