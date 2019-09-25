package dao;

import java.sql.Blob;

/**
 * Created by jalton on 12/10/18.
 */

public class GameBlob {
    private Blob game;
    private Blob cmdList;

    public GameBlob(Blob game, Blob cmdList) {
        this.game = game;
        this.cmdList = cmdList;
    }

    public Blob getGame() {
        return game;
    }

    public void setGame(Blob game) {
        this.game = game;
    }

    public Blob getCmdList() {
        return cmdList;
    }

    public void setCmdList(Blob cmdList) {
        this.cmdList = cmdList;
    }
}
