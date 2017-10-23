package org.mhealth.open.data.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

/**
 * for DataImport
 *
 * @author {USER} on 17-10-21
 */
public class LogParser {
    public static void main(String[] args) throws IOException {
//        BufferedWriter writer = new BufferedWriter(new FileWriter("./write-count"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("./read-count"));
//        String write = /home/just/IdeaProjects/DataImport/logs/blood-pressure.log /home/just/IdeaProjects/DataImport/logs/heart-rate.log /home/just/IdeaProjects/DataImport/logs/body-fat.log
        TreeMap<String, Long> sortedCount = new TreeMap<>();
        String[] fileName = args;
        for (int i = 0; i < fileName.length; i++) {


            Path path = Paths.get(fileName[i]);
            Map<String, Long> count = Files.lines(path).map(line -> new AbstractMap.SimpleEntry<>(line.split(" ")[1], 1))
                    .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, counting()));

            count.forEach((k, v) -> {
                sortedCount.compute(k, (present, num) -> num == null ? v : num + v);
            });


        }
        sortedCount.forEach((k, v) -> {
            try {
                writer.write(String.format("%s,%d \n", k, v));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }
}
