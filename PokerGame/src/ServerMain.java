import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {

    public static List<Player> onlinePlayers;
    public static List<Table> tables;
    public static List<Game> ongoingGames;
    private final static int port = 8888;

    public static void main(String[] args) {
        onlinePlayers = new ArrayList<>();
        tables = new ArrayList<>();
        ongoingGames = new ArrayList<>();

        tables.add(new Table(5, 0, 6));
        tables.add(new Table(6, 1, 6));
        tables.add(new Table(10, 2, 6));
        tables.add(new Table(3, 3, 4));

        //start server
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Game server running");
            while (true){
                Socket socket = serverSocket.accept();
                new Thread(new Handler(socket)).start();
            }
        }catch (IOException x){
            x.printStackTrace();
        }

    }

}
