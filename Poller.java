package server;

import com.obfuscation.server.GenericCommand;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import communication.LobbyGame;
import communication.Result;
import communication.Serializer;
import model.ModelFacade;
import model.ModelRoot;

/**
 * This is a poller which periodically poll the updates from the server into client
 * @author Hao Yu <haoyu_cn@hotmail.com>
 */

public class Poller {
    /**
     * the numbers of times that the game has been updated (command pattern)
     * it represents the version of the object needed for update.
     * if the number differs from the number sent by client, it will trigger an update base on the current app state
     */
    private static Integer gameListVersion = 0;

    /**
     * if the poller is started
     */
    private static boolean running = false;

    /**
     * scheduler for multi threading
     */
    private static ScheduledExecutorService scheduledExecutorService;
    private static ScheduledFuture scheduledFuture;

    /**
     * run the poller every 1 sec
     */
    private static final int checkTime = 1;

    /**
     * poller runs this function to get the update from the server
     */
    private static final Runnable CheckUpdates = new Runnable() {
        @Override
        public void run() {
            if (running) {
                // this check in which state the mobile app is currently on
                switch (ModelRoot.getInstance().getDisplayState()) {
                    case GAMELOBBYLIST: // user is looking at the list of lobbys
                        CheckandUpdateGameList();
                        break;
                    case LOBBY: // user is in lobby, game is not started yet
                        CheckandUpdateGameLobby();
                        break;
                    case GAME: // user is in game
                        CheckandUpdateGame();
                    default:
                        break;
                }
            }
            else {
                // stop the threads from running it
                scheduledExecutorService.shutdown();
            }
        }
    };

    /**
     * start the poller
     */
    public static void StartPoller () {
        running = true;
        // set up to 5 thread in the pool
        scheduledExecutorService = Executors.newScheduledThreadPool(5);
        // set a thread to run the runnable func CheckUpdates(), for every 1 sec
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(CheckUpdates,1,1, TimeUnit.SECONDS);

    }

    /**
     * stop the poller
     */
    public static void EndPoller () {
        running = false;
        scheduledExecutorService.shutdown();
    }

    /**
     * update the list of lobbies in the client model
     */
    private static void CheckandUpdateGameList() {
        ServerProxy serverProxy = new ServerProxy();

        // get server's lobby list version numer, result contains a single integer as version number
        Result result = serverProxy.CheckGameList(ModelRoot.getInstance().getAuthToken());
        if (result.isSuccess()) {
            Integer versionNum = (Integer) result.getData();
            if (!versionNum.equals(gameListVersion)) {

                // if differs, grab the list of lobbies from server
                ModelFacade.getInstance().updateGameList();
                gameListVersion = versionNum;
            }
        }
        else {
            System.out.println("poller failure: lobby list");
        }

    }

    /**
     * check and update the lobby which user is currently in
     */
    private static void CheckandUpdateGameLobby() {
        ServerProxy serverProxy = new ServerProxy();

        // get the lobby's content from server
        Result result = serverProxy.CheckGameLobby(ModelRoot.getInstance().getAuthToken(),ModelRoot.getInstance().getLobbyGame().getGameID());
        if (result.isSuccess()) {

            //  if success, get the lobby version number
            Integer versionNum = (Integer) result.getData();
            LobbyGame lobbyGame = ModelRoot.getInstance().getLobbyGame();

            // check if version number from client and server match
            if (!versionNum.equals(lobbyGame.getVersionNum())) {

                // get update if 2 doesnt match
                ModelFacade.getInstance().UpdateLobby();
                lobbyGame.setVersionNum(versionNum);
            }
        }
        else {
           System.out.println("poller failure: lobby content");
        }
    }


    /**
     * update the list of lobby in the client model
     */
    private static void CheckandUpdateGame() {
        ServerProxy serverProxy = new ServerProxy();
        // get server command to update game, result contains commands
        // commands are formed by java reflection methods
        // reflection: call a function, given the function's name and package name.

        // this also uses command pattern
        // example: 
        //      server keeps a list of N commands has been runned in the past as [c0, c1, c2, ..., cN]
        //      if client sent its current command number as N-2
        //      server will return a list of command as [cN-1, cN] to make sure server updates enough
        Result result = serverProxy.CheckGame(ModelRoot.getInstance().getAuthToken(),ModelRoot.getInstance().getGame().getGameID(), ModelRoot.getInstance().getGame().getState());
        // check if this http request is a success
        if (result.isSuccess()) {
            // ready the serializer to parse the command
            Serializer serializer = new Serializer();
            ArrayList<Object> commands = (ArrayList<Object>)result.getData();

            for (int i = 0 ; i < commands.size(); i++) {
                try {

                    // parse the execute command
                    GenericCommand c = (GenericCommand) serializer.deserializeCommand(commands.get(i).toString());
                    c.execute();

                    // increment the game state for each command runned, the idea is the same as version number
                    ModelRoot.getInstance().getGame().stateIncreament();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        else {
            System.out.println("poller failure: game");
        }
    }


    
}
//    Command type: class java.util.ArrayList{ true, [{className=server.ClientFacade, methodName=initializeGame, parameterType=[communication.Game], parameterValue=[{mGameID=retre, mHost=rtrtyrtyrt, mMaxPlayers=2.0, mPlayers=[{id=P56a7c402-fe85-457f-82bd-14409ce5959d, playerName=rtrtyrtyrt, point=0.0, cards=[], claimedRoutesID=[], ticketToChoose=[{city1={name=Chicago, lat=0.0, lng=0.0}, city2={name=Santa_Fe, lat=0.0, lng=0.0}, value=9.0}, {city1={name=New_York, lat=0.0, lng=0.0}, city2={name=Atlanta, lat=0.0, lng=0.0}, value=6.0}, {city1={name=Toronto, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=10.0}], ticketNum=3.0, trainCarNum=40.0, cardNum=0.0, playerColor=PLAYER_BLUE}, {id=P696f8522-626a-4166-9abe-734a843226ff, playerName=wert, point=0.0, cards=[], claimedRoutesID=[], ticketToChoose=[], ticketNum=3.0, trainCarNum=40.0, cardNum=0.0, playerColor=PLAYER_RED}], mAbsentPlayers=[], messages=[], misStarted=true, trainCards=[{color=GREEN}, {color=WHITE}, {color=ORANGE}, {color=WHITE}, {color=WHITE}, {color=BLACK}, {color=WHITE}, {color=BLACK}, {color=RED}, {color=GREEN}, {color=BLUE}, {color=PURPLE}, {color=BLACK}, {color=PURPLE}, {color=PURPLE}, {color=BLACK}, {color=RED}, {color=ORANGE}, {color=GREEN}, {color=PURPLE}, {color=YELLOW}, {color=YELLOW}, {color=GREEN}, {color=PURPLE}, {color=ORANGE}, {color=ORANGE}, {color=RED}, {color=BLACK}, {color=PURPLE}, {color=BLUE}, {color=BLUE}, {color=YELLOW}, {color=RED}, {color=BLACK}, {color=BLACK}, {color=WHITE}, {color=YELLOW}, {color=WHITE}, {color=YELLOW}, {color=WHITE}, {color=BLUE}, {color=RED}, {color=BLACK}, {color=PURPLE}, {color=YELLOW}, {color=GREEN}, {color=RED}, {color=BLUE}, {color=PURPLE}, {color=BLUE}, {color=ORANGE}, {color=GREEN}, {color=YELLOW}, {color=BLUE}, {color=YELLOW}, {color=PURPLE}, {color=ORANGE}, {color=RED}, {color=ORANGE}, {color=GREEN}, {color=RED}, {color=ORANGE}, {color=RED}, {color=WHITE}, {color=BLUE}, {color=BLACK}, {color=ORANGE}, {color=GREEN}, {color=BLUE}, {color=YELLOW}, {color=YELLOW}, {color=PURPLE}, {color=WHITE}, {color=YELLOW}, {color=ORANGE}, {color=GREEN}, {color=BLUE}, {color=BLUE}, {color=GREEN}, {color=RED}, {color=GREEN}, {color=GREEN}, {color=PURPLE}, {color=RED}, {color=RED}, {color=WHITE}, {color=BLACK}, {color=ORANGE}, {color=PURPLE}, {color=BLACK}, {color=ORANGE}], tickets=[{city1={name=Kansas_City, lat=0.0, lng=0.0}, city2={name=Houston, lat=0.0, lng=0.0}, value=5.0}, {city1={name=Toronto, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=10.0}, {city1={name=Seattle, lat=0.0, lng=0.0}, city2={name=New_York, lat=0.0, lng=0.0}, value=22.0}, {city1={name=Calgary, lat=0.0, lng=0.0}, city2={name=Salt_Lake_City, lat=0.0, lng=0.0}, value=7.0}, {city1={name=Duluth, lat=0.0, lng=0.0}, city2={name=El_Paso, lat=0.0, lng=0.0}, value=10.0}, {city1={name=Montreal, lat=0.0, lng=0.0}, city2={name=Atlanta, lat=0.0, lng=0.0}, value=9.0}, {city1={name=Denver, lat=0.0, lng=0.0}, city2={name=El_Paso, lat=0.0, lng=0.0}, value=4.0}, {city1={name=Montreal, lat=0.0, lng=0.0}, city2={name=New_Orleans, lat=0.0, lng=0.0}, value=13.0}, {city1={name=Helena, lat=0.0, lng=0.0}, city2={name=Los_Angeles, lat=0.0, lng=0.0}, value=8.0}, {city1={name=Boston, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=12.0}, {city1={name=Denver, lat=0.0, lng=0.0}, city2={name=Pittsburgh, lat=0.0, lng=0.0}, value=11.0}, {city1={name=Sault_Ste_Marie, lat=0.0, lng=0.0}, city2={name=Nashville, lat=0.0, lng=0.0}, value=8.0}, {city1={name=Chicago, lat=0.0, lng=0.0}, city2={name=New_Orleans, lat=0.0, lng=0.0}, value=7.0}, {city1={name=Los_Angeles, lat=0.0, lng=0.0}, city2={name=Chicago, lat=0.0, lng=0.0}, value=16.0}, {city1={name=Vancouver, lat=0.0, lng=0.0}, city2={name=Santa_Fe, lat=0.0, lng=0.0}, value=13.0}, {city1={name=Winnipeg, lat=0.0, lng=0.0}, city2={name=Houston, lat=0.0, lng=0.0}, value=12.0}, {city1={name=Los_Angeles, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=20.0}, {city1={name=Vancouver, lat=0.0, lng=0.0}, city2={name=Montréal, lat=0.0, lng=0.0}, value=20.0}, {c
