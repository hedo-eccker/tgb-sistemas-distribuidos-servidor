import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.WeatherAverage;
import model.WeatherReport;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeatherReportServer {

    static final String host = "tcp://*:5556";
    static HashMap<String, ArrayList<WeatherReport>> reportsForCities;

    public static void main(String[] args) throws ParseException {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind(host);

            reportsForCities = new HashMap<>();

            System.out.println("Servidor rodando em " + host);

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            reportsForCities.put(
                    "São Leopoldo",
                    new ArrayList<>(
                            List.of(
                                    new WeatherReport("São Leopoldo", 28.9, df.parse("24/11/2021")),
                                    new WeatherReport("São Leopoldo", 22.1, df.parse("23/11/2021")),
                                    new WeatherReport("São Leopoldo", 32.0, df.parse("22/11/2021")),
                                    new WeatherReport("São Leopoldo", 18.5, df.parse("21/11/2021"))
                            )
                    )
            );

            reportsForCities.put(
                    "Novo Hamburgo",
                    new ArrayList<>(
                            List.of(
                                    new WeatherReport("Novo Hamburgo", 18.9, df.parse("24/11/2021")),
                                    new WeatherReport("Novo Hamburgo", 12.1, df.parse("23/11/2021")),
                                    new WeatherReport("Novo Hamburgo", 22.0, df.parse("22/11/2021")),
                                    new WeatherReport("Novo Hamburgo", 8.5, df.parse("21/11/2021"))
                            )
                    )
            );

            reportsForCities.put(
                    "Canoas",
                    new ArrayList<>(
                            List.of(
                                    new WeatherReport("Canoas", 29.2, df.parse("24/11/2021")),
                                    new WeatherReport("Canoas", 23.4, df.parse("23/11/2021")),
                                    new WeatherReport("Canoas", 27.5, df.parse("22/11/2021")),
                                    new WeatherReport("Canoas", 25.1, df.parse("21/11/2021"))
                            )
                    )
            );

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Aguardando conexões...\n");
                byte[] reply = socket.recv(0);
                String receivedMessage = new String(reply, ZMQ.CHARSET);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("dd/MM/yyyy");
                Gson gson = gsonBuilder.create();

                ArrayList<WeatherAverage> averages = new ArrayList<>();

                if (receivedMessage.equals("::fetchReports")) {
                    for (String city: reportsForCities.keySet()) {
                        double average = generalAverageForCity(city);
                        averages.add(new WeatherAverage(average, city));
                    }

                    String jsonResponse = gson.toJson(averages);
                    socket.send(jsonResponse.getBytes(ZMQ.CHARSET), 0);
                    continue;
                }

                WeatherReport receivedReport = gson.fromJson(receivedMessage, WeatherReport.class);

                if (reportsForCities.containsKey(receivedReport.cityName)) {
                    reportsForCities.get(receivedReport.cityName).add(receivedReport);
                } else {
                    reportsForCities.put(receivedReport.cityName, new ArrayList<>(List.of(receivedReport)));
                }

                String response = "Informe meteorológico para a cidade de " + receivedReport.cityName + " registrado com sucesso.";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }
    }

    static double generalAverageForCity(String city) {
        double degreesSum = 0;
        ArrayList<WeatherReport> reports = reportsForCities.get(city);

        for(WeatherReport report: reports) {
            degreesSum += report.degreesInCelsius;
        }

        return degreesSum / reports.size();
    }

}
