package com.goodsun.goodsunbackend.data;

import com.goodsun.goodsunbackend.model.calculation.FileData;
import com.goodsun.goodsunbackend.model.request.GpsCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileDataCollector {

    private static final String FILE_LOCATION = "weatherData/";
    private static final String FILE_ENDING = ".csv";
    public List<FileData> loadFileData(GpsCoordinates gpsCoordinates, int year){
        List<FileData> fileData = new ArrayList<>();
        String filePath = FILE_LOCATION + gpsCoordinates.longitude() + " " + gpsCoordinates.latitude() + "_" + year + FILE_ENDING;
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            assert inputStream != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (Objects.equals(values[0], "") || Objects.equals(values[0], "0") || Objects.equals(values[0], "1")){
                        continue;
                    }
                    LocalDateTime ldt = getLocalDateTimeFromCSVLine(values);
                    fileData.add(new FileData(gpsCoordinates, ldt, Double.parseDouble(values[6]),Double.parseDouble(values[7]), Double.parseDouble(values[8]),Double.parseDouble(values[9]),Double.parseDouble(values[10])));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileData;
    }

    public LocalDateTime getLocalDateTimeFromCSVLine(String[] values) {
        return LocalDateTime.of(Integer.parseInt(values[1]),Integer.parseInt(values[2]),Integer.parseInt(values[3]),Integer.parseInt(values[4]),Integer.parseInt(values[5]));
    }
}
