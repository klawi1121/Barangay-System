package data_storage_security;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SecureFileHandler {

    private static final String FILE_PATH = "residents.dat";

    public static void saveResidents(List<String> dataList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String data : dataList) {
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadResidents() {
        List<String> dataList = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return dataList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}
