package dao;

import com.obfuscation.server.GenericCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import communication.GameServer;

public interface IGameDao {

    boolean addGame(String gameID, GameServer game);
    boolean removeGame(String gameID);
    boolean updateGame(String gameID, GameServer game);
    boolean updateCmdList(String gameID, ArrayList<GenericCommand> cmdlist);
    List<GameServer> getGames();
    List<GenericCommand> getCommands();
    boolean clear();

}
