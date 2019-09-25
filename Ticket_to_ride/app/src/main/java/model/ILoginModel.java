package model;

import java.util.ArrayList;
import java.util.List;

import communication.Game;
import communication.Message;
import communication.Ticket;

public interface ILoginModel {

    public void Login(String userName, String password);

    public void Register(String userName, String password);

    public void JoinGame(Game game);

    public void CreateGame(Game game);

    public void LeaveGame(Game game);

    public void StartGame(Game game);

    public void UpdateGameList();

    public void UpdateGame();

    public void CheckGameList();

    public void CheckGame();

    public void sendMessage(Message message);

    public void chooseTickets(List<Ticket> tickets);

    public void chooseCard(int index);

    public boolean UpdateState(DisplayState displayState);

    public ArrayList<Game> GetGameList();

    public Game GetCurrentGame();

    public String GetUserName();

}
