package com.dillon.clapp.catchphrase;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Dillon Clapp on 7/17/2015.
 */
public class ArrayActions {

    int size = 0;

    public String[] getNewWordSet(String wordLong){

        //Splits the long string of many words into an array of each individual words
        String[] words = wordLong.split(" ");

        //Returns the array of Strings
        return words;
    }

    public String chooseSetType(String clickType){

        String file = "stock1.txt";

        if(clickType.equals("standard")){

            Random r = new Random();

            int randomInt = r.nextInt(4);

            switch(randomInt){
                case 0: file = "stock1.txt";
                    break;
                case 1: file = "stock2.txt";
                    break;
                case 2: file = "stock3.txt";
                    break;
                case 3: file = "stock4.txt";
                    break;
                case 4: file = "stock5.txt";
                    break;
                default: return "stock1.txt";
            }
        }

        return file;
    }

    public int getSize(String wordLong){
        String[] words = wordLong.split(" ");

        return words.length;
    }

    public List<String> getWordsFromFile(String textFileName, Context context){
        List<String> wordList = new ArrayList<>();
        String textStr = "";
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(textFileName);
            textStr = getStringFromInputStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(textStr !=null){
            String[] words = textStr.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("[^\\w]", "");
                wordList.add(words[i]);
            }
        }
        return wordList;
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}
