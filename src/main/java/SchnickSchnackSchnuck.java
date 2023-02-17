import classes.helper.Type;
import classes.init.Init;
import classes.result.Result;
import classes.round.Round;
import com.google.gson.Gson;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;

import java.net.URI;
import java.util.concurrent.ThreadLocalRandom;

public class SchnickSchnackSchnuck {
    public static final String SECRET = "07d48760-769f-4040-99d6-25e5ca7b9e61"; //Das Secret des Bot
    public static final String GAMESERVER = "https://games.uhno.de"; //URL zum Gameserver

    //Verschiedene Arten von Rueckgaben
    public static final String INIT = "INIT";
    public static final String ROUND = "ROUND";
    public static final String RESULT = "RESULT";

    //Parser fuer JSON-String -> Java-Objekte
    public static final Gson gson = new Gson();


    public static void main(String... args) {
        if(SECRET == null || SECRET.isEmpty()) {
            System.err.println("Was mit Secret?");
            return;
        }


        URI uri = URI.create(GAMESERVER);
        IO.Options options = IO.Options.builder()
                .setTransports(new String[]{WebSocket.NAME})
                .build();

        Socket socket = IO.socket(uri, options);

        //ab hier ist der Socket erstellt, aber noch nichts mit gemacht worden
        //wir registrieren als naechstes die entsprechenden events

        socket.on("connect", (event) -> {
            System.out.println("connect");

            socket.emit("authenticate", new Object[]{SECRET}, (response) -> {
                System.out.println("success? ");
                Boolean success = (Boolean) response[0];
                System.out.println("\t" + success);
            });
        });

        socket.on("disconnect", (event) -> {
            System.out.println("disconnect");
        });

        socket.on("data", (data) -> {
            //der aufbau von data ist hier wie folgt:
            //in data[0] ist immer der json-response
            //in data[1] ist der "Ack", den wir brauchen zum antworten

            String json = String.valueOf(data[0]);

            Type type = gson.fromJson(json, Type.class);

            if (type.getType().equalsIgnoreCase(INIT)) {
                System.out.println("Neue Runde!");
                Init object = gson.fromJson(json, Init.class);
            } else if (type.getType().equalsIgnoreCase(ROUND)) {
                //bei ROUND muessen wir ja antworten, also holen wir uns das "Ack"
                //in anderen worten: callback, rueckkanal, "der wo die antwort hin muss", ...
                Ack ack = (Ack) data[data.length - 1];
                Round object = gson.fromJson(json, Round.class);

                int randomNum = ThreadLocalRandom.current().nextInt(1, 3 + 1);

                String result = "";

                switch (randomNum) {
                    case 1:
                        result = "SCHERE";
                        break;
                    case 2:
                        result = "STEIN";
                        break;
                    case 3:
                        result = "PAPIER";
                        break;
                }

                System.out.println("\tWir schicken: " + result);

                //hier rufen wir dann auf dem Ack entsprechend unser ergebnis auf
                ack.call(result);
            } else if (type.getType().equalsIgnoreCase(RESULT)) {
                System.out.println("Runde vorbei!");
                Result object = gson.fromJson(json, Result.class);
            } else {
                System.out.println("unbekannter typ:");
                System.out.println(json);
            }
        });

        //jetzt sind alle events angelegt, wir oeffnen den socket, los gehts!

        socket.open();
    }
}
