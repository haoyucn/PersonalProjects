package tsvdao;

import com.obfuscation.server.GenericCommand;

import java.util.ArrayList;
import java.util.List;

import communication.GameServer;
import communication.Serializer;
import dao.IGameDao;


public class TSVGameDao implements IGameDao {

    private TSVReaderWriter rw;

    private static int ARRAY_SIZE = 4;

    private static int i_TYPE = 0;
    private static int i_ID = 1;
    private static int i_GAME = 2;
    private static int i_CMDL = 3;

    TSVGameDao() {
        String[] header = new String[ARRAY_SIZE];
        header[i_TYPE] = "type";
        header[i_ID] = "id";
        header[i_GAME] = "game";
        header[i_CMDL] = "cmdlist";
        rw = new TSVReaderWriter(header);
    }

    @Override
    public boolean addGame(String gameID, GameServer game) {
        String[] row = new String[ARRAY_SIZE];
        row[i_TYPE] = DATA_TYPES.GAME;
        row[i_ID] = gameID;
        row[i_GAME] = new Serializer().serializeGameServer(game);
        row[i_CMDL] = "";
        rw.writeLine(row);

        return true;
    }

    @Override
    public boolean removeGame(String gameID) {
        List<String[]> rows = rw.readAll();
        for(int i = 0; i < rows.size(); i++) {
            if(rows.get(i)[i_TYPE].equals(DATA_TYPES.GAME)) {
                String[] row = rows.get(i);
                if(row[i_ID].equals(gameID)) {
                    rows.remove(i);
                    break;
                }
            }

        }
        rw.writeLines(rows);

        return true;
    }

    @Override
    public boolean updateGame(String gameID, GameServer game) {
        List<String[]> rows = rw.readAll();
        for(int i = 0; i < rows.size(); i++) {
            if(rows.get(i)[i_TYPE].equals(DATA_TYPES.GAME)) {
                String[] row = rows.get(i);
                if (row[i_ID].equals(gameID)) {
                    row[i_GAME] = new Serializer().serializeGameServer(game);
                    break;
                }
            }
        }
        rw.writeLines(rows);

        return true;
    }

    @Override
    public boolean updateCmdList(String gameID, ArrayList<GenericCommand> cmdlist) {
        List<String[]> rows = rw.readAll();
        for(int i = 0; i < rows.size(); i++) {
            if(rows.get(i)[i_TYPE].equals(DATA_TYPES.GAME)) {
                String[] row = rows.get(i);
                if (row.equals(gameID)) {
                    String[] r = new String[ARRAY_SIZE];
                    r[i_TYPE] = DATA_TYPES.GAME;
                    r[i_ID] = row[i_ID];
                    r[i_GAME] = row[i_GAME];
                    r[i_CMDL] = serializeCmdList(cmdlist);
                    rows.set(i, r);
                    break;
                }
            }
        }
        rw.writeLines(rows);

        return true;
    }

    private String serializeCmdList(ArrayList<GenericCommand> cmdlist) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Serializer serializer = new Serializer();
        for (GenericCommand c : cmdlist) {
            sb.append(serializer.serializeCommand(c));
        }
        if(sb.charAt(sb.length() -1) == ',') {
            sb.setCharAt(sb.charAt(sb.length() - 1),']');
        }
        return sb.toString();
    }

    @Override
    public List<GameServer> getGames() {
        List<GameServer> games = new ArrayList<>();
        List<String[]> rows = null;

        rows = rw.readAll();

        if (rows == null) {
            return games;
        }

        for(String[] row : rows) {
            if(row[i_TYPE].equals(DATA_TYPES.GAME)) {
                try {
                    Serializer serializer = new Serializer();
                    games.add(serializer.deserializeGameServer(row[i_GAME]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return games;
    }

    @Override
    public List<GenericCommand> getCommands() {
        List<GenericCommand> commands = new ArrayList<>();
        List<String[]> rows = null;

        rows = rw.readAll();

        if (rows == null) {
            return commands;
        }

        if (rows == null) {
            return commands;
        }

        for(String[] row : rows) {
            if(row[i_TYPE].equals(DATA_TYPES.GAME)) {
                if (row.length < ARRAY_SIZE) {
                    return commands;
                }
                try {
                    Serializer serializer = new Serializer();
                    ArrayList<Object> list = serializer.deserializeList(row[i_CMDL]);

                    for (Object o : list) {
                        System.out.println("adding command");
                        System.out.println(o.toString());
                        commands.add(serializer.deserializeGenericCommand(o.toString()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return commands;
    }

    @Override
    public boolean clear() {
        String[] header = new String[ARRAY_SIZE];
        header[i_TYPE] = "type";
        header[i_ID] = "id";
        header[i_GAME] = "game";
        header[i_CMDL] = "cmdlist";
        rw = new TSVReaderWriter(header);
        rw.clear();
        rw.writeHeader();
        return true;
    }

}
