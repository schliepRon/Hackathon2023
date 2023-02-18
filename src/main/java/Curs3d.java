import classes.Coordinate;
import classes.board.Direction;
import classes.board.Furniture;
import classes.helper.Type;
import classes.init.Init;
import classes.result.Result;
import classes.result.ResultPlayer;
import classes.round.Round;
import com.google.gson.Gson;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Curs3d {
    public static final String SECRET = "bddbfb09-62b6-426f-b712-b19a52f72dcb"; //Das Secret des Bot
    public static final String GAMESERVER = "https://games.uhno.de"; //URL zum Gameserver

    //Verschiedene Arten von Rueckgaben
    public static final String INIT = "INIT";

    public static final String SET = "SET";
    public static final String ROUND = "ROUND";
    public static final String RESULT = "RESULT";

    //Parser fuer JSON-String -> Java-Objekte
    public static final Gson gson = new Gson();


    public static void main(String... args) {
        if (SECRET == null || SECRET.isEmpty()) {
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

        String[][] myBoard = new String[10][10];
        String[][] enemyBoard = new String[10][10];
        List<String[][]> boards = new ArrayList<>();


        Map<String, List<Coordinate>> leftCoordsToShoot = new HashMap<>();
        Map<String, List<Coordinate>> schachbrett = new HashMap<>();
        Map<String, Coordinate> lastShot = new HashMap<>();
        Map<String, Integer> winLoss = new HashMap<>();
        Map<String, Furniture[]> furnitureArr = new HashMap<>();

        Map<String, List<Coordinate>> angehittet = new HashMap<>();
        Map<String, List<Coordinate>> tot = new HashMap<>();

        winLoss.put("WIN", 0);
        winLoss.put("LOSS", 0);

        boards.add(myBoard);
        boards.add(enemyBoard);

        socket.on("data", (data) -> {
            //der aufbau von data ist hier wie folgt:
            //in data[0] ist immer der json-response
            //in data[1] ist der "Ack", den wir brauchen zum antworten

            String json = String.valueOf(data[0]);

            Type type = gson.fromJson(json, Type.class);

            if (type.getType().equalsIgnoreCase(INIT)) {
                System.out.println("Neue Runde!");
                Init object = gson.fromJson(json, Init.class);
                boards.set(0, new String[10][10]);
                boards.set(1, new String[10][10]);


                List<Furniture> furs = null;
                int panikin = 0;
                int PANIK = 5;
                do {
                    furs = placeFurniture(panikin++ > PANIK);
                }while (furs == null);

                Furniture[] furArr = new Furniture[5];
                furArr[0] = furs.get(0);
                furArr[1] = furs.get(1);
                furArr[2] = furs.get(2);
                furArr[3] = furs.get(3);
                furArr[4] = furs.get(4);

                furnitureArr.put(object.getId(), furArr);
                List<Coordinate> allCords = new ArrayList<>();
                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 10; y++) {
                        allCords.add(new Coordinate(x, y));
                    }
                }
                List<Coordinate> halfCoords = new ArrayList<>();
                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 10; y++) {
                        if (y % 2 == 0 && x % 2 == 0
                                || y % 2 == 1 && x % 2 == 1) {
                            halfCoords.add(new Coordinate(x, y));
                        }
                    }
                }

                Collections.shuffle(allCords);
                Collections.shuffle(halfCoords);

                lastShot.put(object.getId(), new Coordinate(0, 0));

                schachbrett.put(object.getId(), halfCoords);
                leftCoordsToShoot.put(object.getId(), allCords);
            } else if (type.getType().equalsIgnoreCase(SET)) {

                System.out.println("\tSet");
                Ack ack = (Ack) data[data.length - 1];
                Init object = gson.fromJson(json, Init.class);


                JSONArray jsonArray = new JSONArray();

                Arrays.stream(furnitureArr.get(object.getId())).forEach(teil -> {
                    try {
                        JSONObject mapObject = new JSONObject();
                        mapObject.put("start", teil.getStart());
                        mapObject.put("direction", teil.getDirection());
                        mapObject.put("size", teil.getSize());
                        jsonArray.put(mapObject);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                System.out.println(jsonArray);
                ack.call(jsonArray);

            } else if (type.getType().equalsIgnoreCase(ROUND)) {
                //bei ROUND muessen wir ja antworten, also holen wir uns das "Ack"
                //in anderen worten: callback, rueckkanal, "der wo die antwort hin muss", ...
                Ack ack = (Ack) data[data.length - 1];
                Round object = gson.fromJson(json, Round.class);


                if (tot.containsKey(object.getId())) {
                    tot.replace(object.getId(), new ArrayList<>());
                } else {
                    tot.put(object.getId(), new ArrayList<>());
                }
                if (angehittet.containsKey(object.getId())) {
                    angehittet.replace(object.getId(), new ArrayList<>());
                } else {
                    angehittet.put(object.getId(), new ArrayList<>());
                }
                Coordinate move;

                int index = object.getPlayers().get(0).getId().equals(object.getSelf()) ? 1 : 0;
                List<List<String>> board = object.getBoards().get(index);

                AtomicInteger count = new AtomicInteger();
                AtomicInteger x = new AtomicInteger();
                AtomicInteger y = new AtomicInteger();
                board.forEach(list -> {
                    list.forEach(string -> {
                        if (string.equalsIgnoreCase("X")) {
                            count.getAndIncrement();
                        }
                        if (string.equals("x")) {
                            angehittet.get(object.getId()).add(new Coordinate(y.get(), x.get()));
                        } else if (string.equals("X")) {
                            tot.get(object.getId()).add(new Coordinate(y.get(), x.get()));
                        }
                        x.getAndIncrement();
                    });
                    x.set(0);
                    y.getAndIncrement();
                });

                List<Coordinate> sinnvolleZiele = new ArrayList<>();
                //Tote & anliegende löschen
                tot.get(object.getId()).forEach(entry -> leftCoordsToShoot.get(object.getId()).removeAll(getCoordsAround(entry)));

                if (angehittet.get(object.getId()).size() > 0) {
//
//                    System.out.println("OFFEN");
//                    leftCoordsToShoot.get(object.getId()).forEach(coordinate -> {
//                        System.out.println(coordinate.getX() + " " + coordinate.getY());
//                    });
//                    System.out.println("TOT");
//                    tot.get(object.getId()).forEach(coordinate -> {
//                        System.out.println(coordinate.getX() + " " + coordinate.getY());
//                    });

                    if (angehittet.get(object.getId()).size() > 1) {
                        Direction dir = getDirectionOfAngehittet(angehittet.get(object.getId()));
                        if (dir == Direction.VERTICAL) {
                            List<Coordinate> finalSinnvolleZiele2 = sinnvolleZiele;
                            angehittet.get(object.getId()).forEach(hit -> finalSinnvolleZiele2.addAll(getCoordsAroundVertical(hit)));
                            sinnvolleZiele.addAll(finalSinnvolleZiele2);
//                            System.out.println("Vertical");
                        } else {
                            List<Coordinate> finalSinnvolleZiele1 = sinnvolleZiele;
                            angehittet.get(object.getId()).forEach(hit -> finalSinnvolleZiele1.addAll(getCoordsAroundHorizontal(hit)));
                            sinnvolleZiele.addAll(finalSinnvolleZiele1);
//                            System.out.println("Horizontal");
                        }
                    } else {
                        List<Coordinate> finalSinnvolleZiele = sinnvolleZiele;
                        angehittet.get(object.getId()).forEach(hit -> finalSinnvolleZiele.addAll(getCoordsAroundWithoutDiagonal(hit)));
                        sinnvolleZiele.addAll(finalSinnvolleZiele);
//                        System.out.println("Unbekannte Richtung");
                    }
                    sinnvolleZiele =
                            sinnvolleZiele.stream().distinct().filter(around -> leftCoordsToShoot.get(object.getId()).contains(around))
                                    .collect(Collectors.toList());

//                    System.out.println("SINNVOLL");
                    sinnvolleZiele.forEach(coordinate -> {
//                        System.out.println(coordinate.getX() + " " + coordinate.getY());
                    });

                    Collections.shuffle(sinnvolleZiele);
//                    System.out.println("\tanliegendes abschiessen");
                    move = sinnvolleZiele.stream().findAny().orElse(leftCoordsToShoot.get(object.getId()).get(0));
                } else {
                    if (leftCoordsToShoot.size() == 100) {
                        move = new Coordinate(6, 6);
                    } else if (leftCoordsToShoot.size() == 99) {
                        move = new Coordinate(4, 4);
                    } else {
                        move = schachbrett.get(object.getId()).stream().filter(point -> leftCoordsToShoot.get(object.getId()).contains(point))
                                .findFirst().orElse(leftCoordsToShoot.get(object.getId()).get(0));
                    }
                }

                leftCoordsToShoot.get(object.getId()).remove(move);
                schachbrett.get(object.getId()).remove(move);
                lastShot.replace(object.getId(), move);


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(move.getX());
                jsonArray.put(move.getY());
//                System.out.println("\tWir schicken: " + jsonArray);

                //hier rufen wir dann auf dem Ack entsprechend unser ergebnis auf
                ack.call(jsonArray);
            } else if (type.getType().equalsIgnoreCase(RESULT)) {
//                System.out.println("Runde vorbei!");
                ResultPlayer myself = null;
                try {
                    Result object = gson.fromJson(json, Result.class);
                    myself =
                            object.getPlayers().stream().filter(resultPlayer -> resultPlayer.getId().equals(object.getSelf())).findAny().get();
                } catch (Exception ignore) {

                }
                if (myself == null || myself.getScore() > 0) {
                    System.out.println("WIN");
                    winLoss.replace("WIN", winLoss.get("WIN") + 1);
                } else {
                    winLoss.replace("LOSS", winLoss.get("LOSS") + 1);
                }
                System.out.println(String.format("WIN/LOSS (%s/%s)", winLoss.get("WIN"), winLoss.get("LOSS")));
                System.out.println(json);
            } else {
                System.out.println("unbekannter typ:");
                System.out.println(json);
            }
        });

        //jetzt sind alle events angelegt, wir oeffnen den socket, los gehts!

        socket.open();
    }

    private static List<Furniture> placeFurniture(boolean panikin) {

        int PANIK = 30;
        Furniture fuenfer = new Furniture(5);
        Furniture vierer = new Furniture(4);
        Furniture dreier1 = new Furniture(3);
        Furniture dreier2 = new Furniture(3);
        Furniture zweier = new Furniture(2);

        System.out.println("Place Furniture");

        List<Furniture> furnitures = new ArrayList<>();
//   {"start":[4,3],"direction":"h","size":5},
        //   {"start":[8,6],"direction":"v","size":4},
        //   {"start":[1,5],"direction":"v","size":3},
        //   {"start":[3,5],"direction":"v","size":3},
        //   {"start":[5,5],"direction":"v","size":2}
        if (panikin) {
            fuenfer.setDirection("h");
            fuenfer.setStart(new int[]{4, 3});

            vierer.setDirection("v");
            vierer.setStart(new int[]{8, 6});

            dreier1.setDirection("v");
            dreier1.setStart(new int[]{1, 5});

            dreier2.setDirection("v");
            dreier2.setStart(new int[]{3, 5});

            zweier.setDirection("v");
            zweier.setStart(new int[]{5, 5});

            furnitures.add(fuenfer);
            furnitures.add(vierer);
            furnitures.add(dreier1);
            furnitures.add(dreier2);
            furnitures.add(zweier);
            return furnitures;
        }

        int safetyCount = 0;
        do {
            fuenfer.setDirection(getRandomDirection());
            fuenfer.setStart(getRandomCoord());
            safetyCount++;
        } while (safetyCount < PANIK && !checkPlacement(fuenfer, furnitures));
        furnitures.add(fuenfer);
        System.out.println("5er");
        if (safetyCount >= PANIK) {
            return null;
        }

        safetyCount = 0;
        do {
            vierer.setDirection(getRandomDirection());
            vierer.setStart(getRandomCoord());
            safetyCount++;
        } while (safetyCount < PANIK && !checkPlacement(vierer, furnitures));
        furnitures.add(vierer);
        System.out.println("4er");
        if (safetyCount >= PANIK) {
            return null;
        }
        safetyCount = 0;
        do {
            dreier1.setDirection(getRandomDirection());
            dreier1.setStart(getRandomCoord());
            safetyCount++;
        } while (safetyCount < PANIK && !checkPlacement(dreier1, furnitures));
        furnitures.add(dreier1);
        System.out.println("3er");
        if (safetyCount >= PANIK) {
            return null;
        }
        safetyCount = 0;
        do {
            dreier2.setDirection(getRandomDirection());
            dreier2.setStart(getRandomCoord());
            safetyCount++;
        } while (safetyCount < PANIK && !checkPlacement(dreier2, furnitures));
        furnitures.add(dreier2);
        System.out.println("3er");
        if (safetyCount >= PANIK) {
            return null;
        }
        safetyCount = 0;
        do {
            zweier.setDirection(getRandomDirection());
            zweier.setStart(getRandomCoord());
            safetyCount++;
        } while (safetyCount < PANIK && !checkPlacement(zweier, furnitures));
        furnitures.add(zweier);
        System.out.println("2er");
        if (safetyCount >= PANIK) {
            return null;
        }


        return furnitures;
    }

    private static List<Furniture> identifyEnemyFurniture(List<Coordinate> tote) {
        List<Furniture> furnitureList = new ArrayList<>();
        List<List<Coordinate>> matchedCoordinates = new ArrayList<>();
        tote.forEach(toter -> {
            List<List<Coordinate>> result = new ArrayList<>();
            List<Coordinate> anliegendeCoords = getCoordsAroundWithoutDiagonal(toter);
            matchedCoordinates.forEach(list -> {
                if (anliegendeCoords.stream().anyMatch(list::contains)) {
                    result.set(0, list);
                }
            });
            matchedCoordinates.remove(result.get(0));
            result.get(0).add(toter);
            matchedCoordinates.add(result.get(0));
        });
        matchedCoordinates.forEach(list -> {
            furnitureList.add(new Furniture(list.size()));
        });
        return furnitureList;
    }

    private static int getMinimumSizeOfMissingFurnitures(List<Coordinate> tote) {
        List<Furniture> toteFs = identifyEnemyFurniture(tote);

        return 0;
    }

    private static int[] getRandomCoord() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 9);
        int randomNum2 = ThreadLocalRandom.current().nextInt(0, 9);
        return new int[]{randomNum, randomNum2};
    }

    private static String getRandomDirection() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        if (randomNum == 1) {
            return Direction.HORIZONTAL.getAlias();
        }
        return Direction.VERTICAL.getAlias();
    }

    private static boolean checkPlacement(Furniture furnitureToCheck, List<Furniture> furnitures) {
        if (!checkCoord(furnitureToCheck.getSize(), furnitureToCheck.getStart(), furnitureToCheck.getDirection())) {
            return false;
        }
        if (!checkCollission(furnitureToCheck, furnitures)) {
            return false;
        }
        return true;
    }

    private static boolean checkCoord(int size, int[] coord, String direction) {
        switch (size) {
            case 5:
                if (direction.equals(Direction.HORIZONTAL.getAlias())) {
                    if (coord[0] > 5) {
                        return false;
                    }
                } else {
                    if (coord[1] > 5) {
                        return false;
                    }
                }
                break;
            case 4:
                if (direction.equals(Direction.HORIZONTAL.getAlias())) {
                    if (coord[0] > 6) {
                        return false;
                    }
                } else {
                    if (coord[1] > 6) {
                        return false;
                    }
                }
                break;
            case 3:
                if (direction.equals(Direction.HORIZONTAL.getAlias())) {
                    if (coord[0] > 7) {
                        return false;
                    }
                } else {
                    if (coord[1] > 7) {
                        return false;
                    }
                }
                break;
            case 2:
                if (direction.equals(Direction.HORIZONTAL.getAlias())) {
                    if (coord[0] > 8) {
                        return false;
                    }
                } else {
                    if (coord[1] > 8) {
                        return false;
                    }
                }
                break;
            default:
                System.out.println("wat");
        }
        return true;
    }

    private static boolean checkCollission(Furniture toCheck, List<Furniture> others) {
        if (others.size() < 1) {
            return true;
        }
        List<Coordinate> coordsToCheck = getFullCoordsOfFurniture(toCheck);
        AtomicBoolean match = new AtomicBoolean(false);
        for (Furniture other : others) {
            System.out.println("size of Other " + other.getSize());
            List<Coordinate> otherCoords = getFullCoordsOfFurniture(other);
            List<Coordinate> otherCoordsColls = getFullCollissionZoneOfFurniture(otherCoords);
            otherCoordsColls.forEach(entry -> {
                System.out.println(entry.getX() + " / " + entry.getY());
                if (coordsToCheck.contains(entry)) {
                    System.out.println("collission");
                    match.set(true);
                }
            });
        }
        return !match.get();
    }

    private static List<Coordinate> getFullCoordsOfFurniture(Furniture furniture) {

        List<Coordinate> result = new ArrayList<>();

        if (furniture == null) {
            return result;
        }
        result.add(new Coordinate(furniture.getStart()));

        for (int i = 1; i <= furniture.getSize(); i++) {
            if (furniture.getDirection().equals(Direction.HORIZONTAL.getAlias())) {
                result.add(new Coordinate(furniture.getStart()[0] + i, furniture.getStart()[1]));
            } else {
                result.add(new Coordinate(furniture.getStart()[0], furniture.getStart()[1] + i));
            }
        }
        return result;
    }

    private static List<Coordinate> getFullCollissionZoneOfFurniture(List<Coordinate> coords) {

        List<Coordinate> result = new ArrayList<>();

        for (Coordinate coord : coords) {
            result.addAll(getCoordsAround(coord));
        }
        result = result.stream().distinct().collect(Collectors.toList());
        return result;
    }

    public static List<Coordinate> getCoordsAround(Coordinate coord) {

        List<Coordinate> result = new ArrayList<>();
        int xCoord = coord.getX() - 1;
        int yCoord = coord.getY() - 1;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                result.add(new Coordinate(xCoord + x, yCoord + y));
            }
        }
        result.remove(coord);
        return result;
    }

    public static List<Coordinate> getCoordsAroundWithoutDiagonal(Coordinate coord) {

        List<Coordinate> result = new ArrayList<>();

        result.add(new Coordinate(coord.getX() - 1, coord.getY()));
        result.add(new Coordinate(coord.getX() + 1, coord.getY()));

        result.add(new Coordinate(coord.getX(), coord.getY() - 1));
        result.add(new Coordinate(coord.getX(), coord.getY() + 1));


        result.remove(coord);
        return result;
    }

    public static List<Coordinate> getCoordsAroundHorizontal(Coordinate coord) {

        List<Coordinate> result = new ArrayList<>();

        result.add(new Coordinate(coord.getX() - 1, coord.getY()));
        result.add(new Coordinate(coord.getX() + 1, coord.getY()));


        result.remove(coord);
        return result;
    }

    public static List<Coordinate> getCoordsAroundVertical(Coordinate coord) {

        List<Coordinate> result = new ArrayList<>();

        result.add(new Coordinate(coord.getX(), coord.getY() - 1));
        result.add(new Coordinate(coord.getX(), coord.getY() + 1));


        result.remove(coord);
        return result;
    }

    public static Direction getDirectionOfAngehittet(List<Coordinate> angehittet) {
        if (angehittet.size() <= 1) {
            return Direction.HORIZONTAL;
        }
        return angehittet.get(0).getX() == angehittet.get(1).getX() ? Direction.VERTICAL : Direction.HORIZONTAL;
    }
}
