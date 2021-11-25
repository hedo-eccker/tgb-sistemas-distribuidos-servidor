import model.WeatherReport;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class WeatherClient {

    static final String serverHost = "tcp://localhost:5556";

    public static void main(String[] args) throws ParseException {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect(serverHost);

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            WeatherReport sapReport = new WeatherReport("Sapiranga", 22.1, df.parse("23/11/2021"));
            WeatherReport estanciaReport = new WeatherReport("Estância Velha", 22.1, df.parse("23/11/2021"));
            WeatherReport ivotiReport = new WeatherReport("Ivoti", 22.1, df.parse("23/11/2021"));

            socket.send(sapReport.toJsonString().getBytes(ZMQ.CHARSET), 0);
            String response = new String(socket.recv(0), ZMQ.CHARSET);
            System.out.println(response);

            socket.send(estanciaReport.toJsonString().getBytes(ZMQ.CHARSET), 0);
            response = new String(socket.recv(0), ZMQ.CHARSET);
            System.out.println(response);

            socket.send(ivotiReport.toJsonString().getBytes(ZMQ.CHARSET), 0);
            response = new String(socket.recv(0), ZMQ.CHARSET);
            System.out.println(response);

            socket.close();
        }
    }
}
