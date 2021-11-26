import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.WeatherAverage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class WeatherAveragesSubscriber {

    static final String serverHost = "tcp://localhost:5557";

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect(serverHost);

            socket.subscribe("".getBytes(ZMQ.CHARSET));

            System.out.println("Recebendo updates do publisher com host " + serverHost);
            while (!Thread.currentThread().isInterrupted()) {
                String receivedAverage = new String(socket.recv(0), ZMQ.CHARSET);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("dd/MM/yyyy");
                Gson gson = gsonBuilder.create();

                WeatherAverage average = gson.fromJson(receivedAverage, WeatherAverage.class);
                System.out.printf("A média de temperatura em %s é de %3.2fºC\n", average.city, average.average);
            }
        }
    }

}
