import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.WeatherAverage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.List;

public class WeatherReportPublisher {

    static final String host = "tcp://*:5557";
    static final String serverHost = "tcp://localhost:5556";

    public static void main(String[] args) throws InterruptedException {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket reqSocket = context.createSocket(SocketType.REQ);
            reqSocket.connect(serverHost);

            ZMQ.Socket pubSocket = context.createSocket(SocketType.PUB);
            pubSocket.bind(host);

            while (!Thread.currentThread().isInterrupted()) {
                reqSocket.send("::fetchReports", 0);
                String availableAveragesJson = new String(reqSocket.recv(0), ZMQ.CHARSET);

                System.out.println("Recebendo " + availableAveragesJson);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("dd/MM/yyyy");
                Gson gson = gsonBuilder.create();

                List<WeatherAverage> averages = gson.fromJson(availableAveragesJson, new TypeToken<List<WeatherAverage>>(){}.getType());

                for (WeatherAverage average: averages) {
                    String averageJson = gson.toJson(average);
                    System.out.println("Publicando " + averageJson);
                    pubSocket.send(averageJson);
                }

                Thread.sleep(10000);
            }
        }
    }

}
