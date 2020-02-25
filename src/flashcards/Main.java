package flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    private static Map<String, String> cardToDefinition = new LinkedHashMap<>();
    private static Map<String, String> definitionToCard = new LinkedHashMap<>();
    private static Map<String, Integer> statistics = new HashMap<>();
    private static boolean isEnabled = true;
    private final static Scanner sc = new Scanner(System.in);
    private static Random random = new Random();
    private static List<String> log = new LinkedList<>();


    private static void chooseAction() {
        logPrintln("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        String action = sc.nextLine().trim();
        switch (action) {
            case "add":
                add();
                break;
            case "remove":
                remove();
                break;
            case "import":
                importPrompt();
                break;
            case "export":
                exportPrompt();
                break;
            case "ask":
                ask();
                break;
            case "log":
                log();
                break;
            case "hardest card":
                hardest();
                break;
            case "reset stats":
                reset();
                break;
            case "exit":
                logPrintln("Bye bye!");
                isEnabled = false;
                break;
            default:
                logPrintln("Sorry, I cannot understand.");
                break;
        }
        logPrintln("");
    }


    private static void add() {
        Scanner scanner = new Scanner(System.in);
        logPrintln("The card:");
        String card = scanner.nextLine();
        if (cardToDefinition.containsKey(card)) {
            logPrint(String.format("The card \"%s\" already exists.\n\n", card));
        } else {
            logPrintln("The definition of the card:");
            String definition = scanner.nextLine();
            if (definitionToCard.containsKey(definition)) {
                logPrint(String.format("The definition \"%s\" already exists.\n\n", definition));
            } else {
                cardToDefinition.put(card, definition);
                definitionToCard.put(definition, card);
                statistics.put(card, 0);
                logPrint(String.format("The pair (\"%s\":\"%s\") has been added.\n\n", card, definition));
            }
        }
    }


    private static void remove() {
        logPrintln("The card:");
        String card = sc.nextLine();

        if (cardToDefinition.containsKey(card)) {
            definitionToCard.remove(cardToDefinition.get(card));
            cardToDefinition.remove(card);
            statistics.remove(card);
            logPrintln("The card \"" + card +"\" has been removed.");
        } else {
            logPrintln("Can't remove \"" + card + "\":  there is no such card.");
        }

    }


    private static void importPrompt() {
        logPrintln("File name:");
        String fileName = sc.nextLine();
        importCards(fileName);
    }

    private static void importCards(String fileName){
        File file = new File(fileName);
        try (Scanner reader = new Scanner(file)) {
            int amount = 0;
            while (reader.hasNextLine()) {
                String card = reader.nextLine();
                String definition = reader.nextLine();
                int mistakes = Integer.parseInt(reader.nextLine());
                if(cardToDefinition.containsKey(card)){
                    definitionToCard.remove(cardToDefinition.get(card));
                    cardToDefinition.remove(card);
                }
                cardToDefinition.put(card, definition);
                definitionToCard.put(definition, card);
                statistics.put(card, mistakes);
                amount++;
            }
            logPrintln(amount + " cards have been loaded.");

        } catch (IOException e) {
            logPrintln("File not found.");
        }
    }


    private static void exportPrompt() {
        logPrintln("File name:");
        String fileName = sc.nextLine();
        exportCards(fileName);
    }

    private static void exportCards(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
        for (Map.Entry<String, String> couple: cardToDefinition.entrySet()) {
            writer.write(couple.getKey() + "\n");
            writer.write(couple.getValue() + "\n");
            writer.write(statistics.get(couple.getKey()) + "\n");
        }
        logPrintln(cardToDefinition.size() + " cards have been saved.");

    } catch (IOException e) {
        logPrintln("File not found.");
    }
    }


    private static void ask() {
        logPrintln("How many times to ask?");
        int amount = Integer.parseInt(sc.nextLine());
        String answer, key, value;

        for (int i = 0; i < amount; i++) {
            key = randomKey();
            value = cardToDefinition.get(key);
            logPrintln("Print the definition of \"" + key + "\":");
            answer = sc.nextLine();
            if (value.equals(answer)) {
                logPrint("Correct answer. ");
                logPrintln("");
            } else if (definitionToCard.containsKey(answer)) {
                logPrint("Wrong answer. The correct one is \"" + value +
                        "\", you've just written the definition of \"" + definitionToCard.get(answer) + "\" card.");
                logPrintln("");
                statistics.replace(key, statistics.get(key) + 1);
            } else {
                logPrint("Wrong answer. The correct one is \"" + value + "\".");
                logPrintln("");
                statistics.replace(key, statistics.get(key) + 1);
            }
        }
    }

    private static void logPrint(String s) {
        log.add(s);
        System.out.print(s);
    }

    private static void logPrintln(String s) {
        log.add(s + '\n');
        System.out.println(s);
    }

    private static String randomKey() {
        int number = random.nextInt(cardToDefinition.size());
        int i = 0;

        for (String key : cardToDefinition.keySet()) {
            if (i == number) {
                return key;
            }
            i++;
        }
        return null;
    }

    private static void log() {
        logPrintln("File name:");
        try {
            FileWriter fileWriter = new FileWriter(sc.nextLine());
            for (String s :
                    log) {
                fileWriter.append(s);
            }
            fileWriter.close();
            logPrintln("The log has been saved.");
        } catch (IOException e) {
            logPrintln("File not found");
        }
    }

    private static void hardest() {
        int max = 0;
        List<String> list = new LinkedList<>();
        for (Map.Entry<String, Integer> entry :
                statistics.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                list.clear();
                list.add(entry.getKey());
            }
            else if (entry.getValue() == max && max != 0) {
                list.add(entry.getKey());
            }
        }
        if (list.size() == 0) {
            logPrintln("There are no cards with errors.");
        }
        else if (list.size() == 1) {
            logPrintln(String.format("The hardest card is \"%s\". You have %d errors answering it.", list.get(0), max));
        }
        else {
            logPrint("The hardest cards are");
            for (String s :
                    list) {
                logPrint(String.format(" \"%s\"", s));
            }
            logPrintln(String.format(". You have %d errors answering them.", max));
        }
    }

    private static void reset() {
        for (Map.Entry<String, Integer> entry:
                statistics.entrySet()){
            entry.setValue(0);
        }
        logPrintln("Card statistics has been reset.");
    }

    public static void main(String[] args) {
        String export = null;
        for (int i = 0; i < args.length - 1; i += 2) {
            if (args[i].equals("-import")) {
                importCards(args[i + 1]);
            }
            else if (args[i].equals("-export")) {
                export = args[i + 1];
            }
        }
        while (isEnabled) {
            chooseAction();
        }
        if (export != null) {
            exportCards(export);
        }
    }
}