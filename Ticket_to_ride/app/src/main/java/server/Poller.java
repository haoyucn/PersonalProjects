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
 * Created by hao on 10/5/18.
 */

public class Poller {

    private static Integer gameListVersion = 0;
    private static boolean running = false;
    private static ScheduledExecutorService scheduledExecutorService;
    private static ScheduledFuture scheduledFuture;
    private static final int checkTime = 1;

    private static final Runnable CheckUpdates = new Runnable() {
        @Override
        public void run() {
            if (running) {
//                System.out.println("***********************************************" + ModelRoot.newInstance().getDisplayState());
                switch (ModelRoot.getInstance().getDisplayState()) {
                    case GAMELOBBYLIST:
                        CheckandUpdateGameList();
                        break;
                    case LOBBY:
//                        System.out.println("about to check lobby");
                        CheckandUpdateGameLobby();
                        break;
                    case GAME:
//                        System.out.println("in the state of game");
                        CheckandUpdateGame();
                    default:
                        break;
                }
            }
            else {
                scheduledExecutorService.shutdown();
            }
        }
    };

    public static void StartPoller () {
        running = true;
//        System.out.println("Poller Starting");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        ScheduledFuture scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(CheckUpdates,1,1, TimeUnit.SECONDS);

    }

    public static void EndPoller () {
        running = false;
    }

    private static void CheckandUpdateGameList(Integer versionNum) {
        ServerProxy serverProxy = new ServerProxy();


        if (!versionNum.equals(gameListVersion)) {
            ModelFacade.getInstance().updateGameList();
        }
    }

//    Note: this is just a copy of last phase
    private static void CheckandUpdateGameLobby() {
        ServerProxy serverProxy = new ServerProxy();
        Result result = serverProxy.CheckGameLobby(ModelRoot.getInstance().getAuthToken(),ModelRoot.getInstance().getLobbyGame().getGameID());
//        System.out.println("In Check and update game lobby");
//        System.out.println(result.toString());
        if (result.isSuccess()) {
            Integer versionNum = (Integer) result.getData();
            LobbyGame lobbyGame = ModelRoot.getInstance().getLobbyGame();
//            System.out.println(versionNum + " : " + lobbyGame.getVersionNum());
            if (!versionNum.equals(lobbyGame.getVersionNum())) {
                ModelFacade.getInstance().UpdateLobby();
                lobbyGame.setVersionNum(versionNum);
            }
        }
        else {
//            System.out.println("poller failure");
        }
    }

    private static void CheckandUpdateGame() {
        ServerProxy serverProxy = new ServerProxy();
//        System.out.println("check and update gamem is called");
//        System.out.print("current gamestate is: ");
//        System.out.print(ModelRoot.newInstance().getGame().getState());
//        System.out.print('\n');

        Result result = serverProxy.CheckGame(ModelRoot.getInstance().getAuthToken(),ModelRoot.getInstance().getGame().getGameID(), ModelRoot.getInstance().getGame().getState());
//        System.out.println(result.toString());
        if (result.isSuccess()) {
//            System.out.println(result.toString());
            Serializer serializer = new Serializer();
            ArrayList<Object> commands = (ArrayList<Object>)result.getData();

//            System.out.print("get a list of of command with size" );
//
//            System.out.print(commands.size());
//            System.out.print('\n');
//            System.out.print("Command type: " + result.getData().getClass().toString());
//            System.out.println(result.toString());

            for (int i = 0 ; i < commands.size(); i++) {
//                System.out.println("get into this loop");
                try {
//                    System.out.println(commands.get(i).toString());
                    GenericCommand c = (GenericCommand) serializer.deserializeCommand(commands.get(i).toString());
//                    System.out.println("Command Detail");
//                    System.out.println("command class: " + c.className);
//                    System.out.println("command method: " + c.methodName);
//                    System.out.println("command number: " + i);
//                System.out.println("command method: " + c.methodName);

                    c.execute();
                    CommandTask commandTask = new CommandTask();
                    commandTask.execute();
                    ModelRoot.getInstance().getGame().stateIncreament();
//                    System.out.print("New game state after : ");
//                    System.out.print(ModelRoot.newInstance().getGame().getState());
//                    System.out.print("\n");
//                    System.out.println("number of train card: ");

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        else {
//            System.out.println("poller failure");
//            System.out.println(result.getErrorInfo());
        }
    }

    private static void CheckandUpdateGameList() {
//        System.out.println("Poller working");
        ServerProxy serverProxy = new ServerProxy();
        Result result = serverProxy.CheckGameList(ModelRoot.getInstance().getAuthToken());
        if (result.isSuccess()) {
            Integer versionNum = (Integer) result.getData();
//            System.out.println(versionNum + " " + gameListVersion);
            if (!versionNum.equals(gameListVersion)) {
                ModelFacade.getInstance().updateGameList();
                gameListVersion = versionNum;
            }
        }

    }
}
//    Command type: class java.util.ArrayList{ true, [{className=server.ClientFacade, methodName=initializeGame, parameterType=[communication.Game], parameterValue=[{mGameID=retre, mHost=rtrtyrtyrt, mMaxPlayers=2.0, mPlayers=[{id=P56a7c402-fe85-457f-82bd-14409ce5959d, playerName=rtrtyrtyrt, point=0.0, cards=[], claimedRoutesID=[], ticketToChoose=[{city1={name=Chicago, lat=0.0, lng=0.0}, city2={name=Santa_Fe, lat=0.0, lng=0.0}, value=9.0}, {city1={name=New_York, lat=0.0, lng=0.0}, city2={name=Atlanta, lat=0.0, lng=0.0}, value=6.0}, {city1={name=Toronto, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=10.0}], ticketNum=3.0, trainCarNum=40.0, cardNum=0.0, playerColor=PLAYER_BLUE}, {id=P696f8522-626a-4166-9abe-734a843226ff, playerName=wert, point=0.0, cards=[], claimedRoutesID=[], ticketToChoose=[], ticketNum=3.0, trainCarNum=40.0, cardNum=0.0, playerColor=PLAYER_RED}], mAbsentPlayers=[], messages=[], misStarted=true, trainCards=[{color=GREEN}, {color=WHITE}, {color=ORANGE}, {color=WHITE}, {color=WHITE}, {color=BLACK}, {color=WHITE}, {color=BLACK}, {color=RED}, {color=GREEN}, {color=BLUE}, {color=PURPLE}, {color=BLACK}, {color=PURPLE}, {color=PURPLE}, {color=BLACK}, {color=RED}, {color=ORANGE}, {color=GREEN}, {color=PURPLE}, {color=YELLOW}, {color=YELLOW}, {color=GREEN}, {color=PURPLE}, {color=ORANGE}, {color=ORANGE}, {color=RED}, {color=BLACK}, {color=PURPLE}, {color=BLUE}, {color=BLUE}, {color=YELLOW}, {color=RED}, {color=BLACK}, {color=BLACK}, {color=WHITE}, {color=YELLOW}, {color=WHITE}, {color=YELLOW}, {color=WHITE}, {color=BLUE}, {color=RED}, {color=BLACK}, {color=PURPLE}, {color=YELLOW}, {color=GREEN}, {color=RED}, {color=BLUE}, {color=PURPLE}, {color=BLUE}, {color=ORANGE}, {color=GREEN}, {color=YELLOW}, {color=BLUE}, {color=YELLOW}, {color=PURPLE}, {color=ORANGE}, {color=RED}, {color=ORANGE}, {color=GREEN}, {color=RED}, {color=ORANGE}, {color=RED}, {color=WHITE}, {color=BLUE}, {color=BLACK}, {color=ORANGE}, {color=GREEN}, {color=BLUE}, {color=YELLOW}, {color=YELLOW}, {color=PURPLE}, {color=WHITE}, {color=YELLOW}, {color=ORANGE}, {color=GREEN}, {color=BLUE}, {color=BLUE}, {color=GREEN}, {color=RED}, {color=GREEN}, {color=GREEN}, {color=PURPLE}, {color=RED}, {color=RED}, {color=WHITE}, {color=BLACK}, {color=ORANGE}, {color=PURPLE}, {color=BLACK}, {color=ORANGE}], tickets=[{city1={name=Kansas_City, lat=0.0, lng=0.0}, city2={name=Houston, lat=0.0, lng=0.0}, value=5.0}, {city1={name=Toronto, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=10.0}, {city1={name=Seattle, lat=0.0, lng=0.0}, city2={name=New_York, lat=0.0, lng=0.0}, value=22.0}, {city1={name=Calgary, lat=0.0, lng=0.0}, city2={name=Salt_Lake_City, lat=0.0, lng=0.0}, value=7.0}, {city1={name=Duluth, lat=0.0, lng=0.0}, city2={name=El_Paso, lat=0.0, lng=0.0}, value=10.0}, {city1={name=Montreal, lat=0.0, lng=0.0}, city2={name=Atlanta, lat=0.0, lng=0.0}, value=9.0}, {city1={name=Denver, lat=0.0, lng=0.0}, city2={name=El_Paso, lat=0.0, lng=0.0}, value=4.0}, {city1={name=Montreal, lat=0.0, lng=0.0}, city2={name=New_Orleans, lat=0.0, lng=0.0}, value=13.0}, {city1={name=Helena, lat=0.0, lng=0.0}, city2={name=Los_Angeles, lat=0.0, lng=0.0}, value=8.0}, {city1={name=Boston, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=12.0}, {city1={name=Denver, lat=0.0, lng=0.0}, city2={name=Pittsburgh, lat=0.0, lng=0.0}, value=11.0}, {city1={name=Sault_Ste_Marie, lat=0.0, lng=0.0}, city2={name=Nashville, lat=0.0, lng=0.0}, value=8.0}, {city1={name=Chicago, lat=0.0, lng=0.0}, city2={name=New_Orleans, lat=0.0, lng=0.0}, value=7.0}, {city1={name=Los_Angeles, lat=0.0, lng=0.0}, city2={name=Chicago, lat=0.0, lng=0.0}, value=16.0}, {city1={name=Vancouver, lat=0.0, lng=0.0}, city2={name=Santa_Fe, lat=0.0, lng=0.0}, value=13.0}, {city1={name=Winnipeg, lat=0.0, lng=0.0}, city2={name=Houston, lat=0.0, lng=0.0}, value=12.0}, {city1={name=Los_Angeles, lat=0.0, lng=0.0}, city2={name=Miami, lat=0.0, lng=0.0}, value=20.0}, {city1={name=Vancouver, lat=0.0, lng=0.0}, city2={name=Montréal, lat=0.0, lng=0.0}, value=20.0}, {c
