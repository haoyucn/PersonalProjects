package sqldao;

import dao.IDaoFactory;
import dao.IGameDao;
import dao.ILobbyDao;
import dao.IUserDao;

public class SQLFactory implements IDaoFactory {

    public SQLFactory() {
    }

    @Override
    public IUserDao getUserDao() {
        return new SQLUserDAO();
    }

    @Override
    public ILobbyDao getLobbyDao() {
        return new SQLLobbyDAO();
    }

    @Override
    public IGameDao getGameDao() {
        return new SQLGameDAO();
    }

}
