package com.dji.sdk.sample.internal.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Reader {

    private int count;

    public String[][] readInput() {

        //Hardcode
        File file = new File("/home/cornier/Mobile-SDK-Android/Sample Code/app/Input/input.csv");

        StringBuilder text = new StringBuilder();
        count = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            line = br.readLine();
            line = br.readLine();

            while ((line = br.readLine()) != null) {
                count++;
            }
            br.close();
        }
        catch (IOException e) {

        }

        String[][] coordinates = new String[count][4];

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            line = br.readLine();
            line = br.readLine();
            int i = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                coordinates[i][0] = parts[0];
                coordinates[i][1] = parts[1];
                coordinates[i][2] = parts[2];
                coordinates[i][3] = parts[3];

            }
            br.close();
        }
        catch (IOException e) {

        }

        return coordinates;

    }

    public int size() {
        return count;
    }

}