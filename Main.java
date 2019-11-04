
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    static HashMap<String, String> idToTitle = new HashMap<String, String>();
    static HashMap<String, ArrayList<String>> citations = new HashMap<String, ArrayList<String>>();

    static void tierSearch(String id, int tier, int count) {
        if (tier == 0)
            return;
        if (citations.get(id) == null)
            return;

        citations.get(id).forEach(i -> {
            System.out.println("TIER: " + count + ": " + idToTitle.get(i) + " (" + i + ")");
        });
        citations.get(id).forEach(i -> {
            tierSearch(i, tier - 1, count + 1);
        });
    }

    public static void main(String[] args) throws Exception {
        String file = args[0];
        String keyword = args[1];
        int tier = Integer.parseInt(args[2]);

        Stream<String> lines = Files.lines(Paths.get(file));
        List<String> list = lines.filter(i -> i.startsWith("#*") || i.startsWith("#index") || i.startsWith("#%") || i.length() == 0)
                .collect(Collectors.toList());

        HashMap<String, String> matches = new HashMap<String, String>();
        String title = "";
        String index = "";
        for (int x = 0; x < list.size(); x++) {
            String i = list.get(x);

            if (i.startsWith("#*"))
                title = i.substring(2);
            if (i.startsWith("#index"))
                index = i.substring(6);
            if (i.startsWith("#%")) {
                String citedBy = i.substring(2);

                if (!citations.containsKey(citedBy))
                    citations.put(citedBy, new ArrayList<String>());
                citations.get(citedBy).add(index);
            }

            if (i.length() == 0) {
                idToTitle.put(index, title);

                if (title.contains(keyword))
                    matches.put(title, index);
            }
        }

        matches.forEach((i, j) -> {
            System.out.println(i);
            System.out.println("==========");

            tierSearch(j, tier, 1);
            System.out.println();
        });
    }

}
