package dao;

public interface IDaoFactory {

    IUserDao getUserDao();
    ILobbyDao getLobbyDao();
    IGameDao getGameDao();

}
