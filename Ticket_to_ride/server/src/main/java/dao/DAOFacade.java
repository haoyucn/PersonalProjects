package dao;

import com.obfuscation.server.GenericCommand;

import java.util.ArrayList;
import java.util.List;

import communication.GameServer;
import communication.LobbyGame;

/**
 * Created by jalton on 12/5/18.
 */

public class DAOFacade implements IGameDao, ILobbyDao, IUserDao {

    private IDaoFactory daoFactory = null;
    private static DAOFacade instance = null;

    private IUserDao userDao;
    private ILobbyDao lobbyDao;
    private IGameDao gameDao;

    private DAOFacade() {

    }

    public static DAOFacade getInstance() {
        if (instance == null) {
            instance = new DAOFacade();
        }

        return instance;
    }

    public void setDaoFactory(IDaoFactory factory) {
        daoFactory = factory;
        userDao = daoFactory.getUserDao();
        lobbyDao = daoFactory.getLobbyDao();
        gameDao = daoFactory.getGameDao();
    }

    @Override
    public boolean addGame(String gameID, GameServer game) {
        return gameDao.addGame(gameID, game);
    }

    @Override
    public boolean addLobby(String id, LobbyGame lobby) {
        return lobbyDao.addLobby(id, lobby);
    }

    @Override
    public boolean addUser(String id, String password, String authtoken) {
        return userDao.addUser(id, password, authtoken);
    }

    @Override
    public boolean removeLobby(String id) {
        return lobbyDao.removeLobby(id);
    }

    @Override
    public boolean removeGame(String gameID) {
        return gameDao.removeGame(gameID);
    }

    @Override
    public boolean updateLobby(String id, LobbyGame lobby) {
        return lobbyDao.updateLobby(id, lobby);
    }

    @Override
    public boolean updateGame(String gameID, GameServer game) {
        return gameDao.updateGame(gameID, game);
    }

    @Override
    public boolean removeUser(String id) {
        return userDao.removeUser(id);
    }

    @Override
    public List<LobbyGame> getLobbies() {
        System.out.println(lobbyDao);
        return lobbyDao.getLobbies();
    }

    @Override
    public boolean updateCmdList(String gameID, ArrayList<GenericCommand> cmdlist) {
        return gameDao.updateCmdList(gameID, cmdlist);
    }

    @Override
    public boolean updateAuthToken(String id, String authtoken) {
        return userDao.updateAuthToken(id, authtoken);
    }

    @Override
    public List<User> getUsers() {
        return userDao.getUsers(); }

    @Override
    public List<GameServer> getGames() {
        return gameDao.getGames();
    }

    @Override
    public List<GenericCommand> getCommands() {
        return gameDao.getCommands();
    }

    @Override
    public boolean clear() {
        gameDao.clear();
        lobbyDao.clear();
        userDao.clear();
        return true;
    }
}
