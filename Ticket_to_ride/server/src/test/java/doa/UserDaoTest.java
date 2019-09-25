package doa;

import org.junit.Test;

import java.util.List;

import dao.IUserDao;
import dao.User;
import tsvdao.TSVDaoFactory;
import sqldao.SQLFactory;

public class UserDaoTest {

    @Test
    public void TSVTest() {
        System.out.println("Start");

        IUserDao dao = new TSVDaoFactory().getUserDao();
        dao.clear();

        List<User> users;
        User user = new User("bob", "password", "authtoken");
        dao.addUser(user.getId(), user.getPassword(), user.getAuthtoken());
        users = dao.getUsers();
        assert(users.size() == 1);
        assert(users.get(0).equals(user));

        user.setAuthtoken("authtoken");
        dao.updateAuthToken(user.getId(), user.getAuthtoken());
        users = dao.getUsers();
        assert(users.size() == 1);
        assert(users.get(0).equals(user));

        dao.removeUser(user.getId());
        users = dao.getUsers();
        assert(users.size() == 0);

        System.out.println("No problems");
    }

    @Test
    public void SQLTest() {
        System.out.println("Start");

        IUserDao dao = new SQLFactory().getUserDao();
        dao.clear();

        List<User> users;
        User user = new User("bob", "password", "authtoken");
        dao.addUser(user.getId(), user.getPassword(), user.getAuthtoken());
        users = dao.getUsers();
        assert(users.size() == 1);
        assert(users.get(0).equals(user));

        user.setAuthtoken("authtoken");
        dao.updateAuthToken(user.getId(), user.getAuthtoken());
        users = dao.getUsers();
        assert(users.size() == 1);
        assert(users.get(0).equals(user));

        dao.removeUser(user.getId());
        users = dao.getUsers();
        assert(users.size() == 0);

        System.out.println("No problems");
    }

}
