import model.WeatherReport;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class WeatherReportClient {

    static final String serverHost = "tcp://localhost:5556";

    public static void main(String[] args) throws ParseException {
        boolean shouldContinue = true;

        Scanner input = new Scanner(System.in);

        do {
                System.out.println("Cadastro de informe meteorológico:\n");
                System.out.println("Digite a cidade:");

                String city = input.next();

                System.out.println("Digite a temperatura medida em ºC:");
                double temperature = input.nextDouble();

                System.out.println("Digite a data da medição da temperatura (dd/MM/yyyy):");
                String dateString = input.next();

                Date reportDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);

                WeatherReport report = new WeatherReport(city, temperature, reportDate);

            try (ZContext context = new ZContext()) {
                ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                socket.connect(serverHost);

                socket.send(report.toJsonString().getBytes(ZMQ.CHARSET), 0);
                String response = new String(socket.recv(0), ZMQ.CHARSET);
                System.out.println(response);

                System.out.println("Deseja cadastrar outro informe? (s/n)");
                String responseString = input.next();

                if (responseString.equals("n")) {
                    shouldContinue = false;
                    socket.close();
                }
            }
        } while (shouldContinue);
    }
}
