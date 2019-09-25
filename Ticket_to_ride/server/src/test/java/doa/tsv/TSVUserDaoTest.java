package doa.tsv;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dao.IUserDao;
import dao.User;
import tsvdao.TSVDaoFactory;

public class TSVUserDaoTest {


        @Test
        public void CompareTest() {
//            String[] header = new String[1];
//            header[0] = "string";
//            TSVReaderWriter rw = new TSVReaderWriter(header);
//            rw.writeHeader();
//            rw.writeLine(header);
//
//            List<String[]> lines = rw.readAll();
//            assert(lines.size() == 1);
//            String[] tokens = lines.get(0);
//            assert(tokens.length == 1);
//            assert(tokens[0].equals("string"));


            List<User> firstUsers = new ArrayList<>();
            firstUsers.add( new User("user1", "pass1", "auth1"));
            firstUsers.add( new User("user2", "pass2", "auth2"));
            firstUsers.add( new User("user3", "pass3", "auth3"));

            TSVDaoFactory factory = new TSVDaoFactory();

            IUserDao dao = factory.getUserDao();

            for (User user : firstUsers) {
                dao.addUser(user.getId(), user.getPassword(), user.getAuthtoken());
            }

            List<User> users = dao.getUsers();

            for (int i = 0; i < users.size(); i++) {
                assert (isUserSame(users.get(i), firstUsers.get(i)));
            }

        }

        private boolean isUserSame(User u1, User u2) {
            if (u1.getId().equals(u2.getId())
                    && u1.getPassword().equals(u2.getPassword())
                    && u1.getAuthtoken().equals(u2.getAuthtoken())) {
                return true;
            }

            return false;
        }
}
