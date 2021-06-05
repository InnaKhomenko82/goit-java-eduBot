package com.TelegramBot.GoogleApi;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

// https://docs.google.com/spreadsheets/d/1JAV0NxS2ZNH6IzrFLJumwTdU6uA_Fs37cX6Nu31W9fY/edit#gid=0
//   1JAV0NxS2ZNH6IzrFLJumwTdU6uA_Fs37cX6Nu31W9fY
public class GoogleApiToJson {
    private static final String CREDENTIALS_FILE = "/credential.json";
    private static final String PROPERTIES_FILE = "/application.properties";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Properties appProp;
    static List<Object> values2 = new LinkedList<>();
    static List<Object> rows = new LinkedList<>();
    static List<List<Object>> values = new LinkedList<>();

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadSheetId = getProperties().getProperty("spreadsheet_id");


        Sheets service = new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials())
                .setApplicationName("Google Api")
                .build();


        Spreadsheet spreadsheetMetadata = service.spreadsheets().get(spreadSheetId).execute();

        List<Sheet> sheets = spreadsheetMetadata.getSheets();
        sheets.forEach(sheet -> System.out.println(((SheetProperties) (sheet.get("properties"))).get("title")));

        ValueRange response = service
                .spreadsheets()
                .values()
                .get(spreadSheetId, "JavaScrip!A4:C")
                .execute();

        List<List<Object>> values = response.getValues();
        // List<List<Object>> values = new LinkedList<>();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found");
            return;
        }

        for (List row : values) {
            if (row.isEmpty()) {
                continue;
            }
            String vi = "Video: ";

            String r = (String) row.get(0);
            String r1 = (String) row.get(1);
            String r2 = (String) row.get(2);
            String allR = "\nВопрос: " + r + " \nОтвет: " + r1 + " \nВидео: " + r2 + "\n";
            String all2 = " Вопрос: " + r + " Ответ: " + r1 + " Видео: " + r2;
            rows.add(row);
            values2.add(all2);

            //System.out.printf("Вопрос: %s \n Ответ: %s \n Видео: %s \n\n", row.get(0), row.get(1), row.get(2));
        }
        // System.out.println(range);
        WriteToJson.Writer1ToArray(rows);
        WriteToJson.Writer2ToString(values2);
        WriteToJson.Writer3ToObject(values);
        //ReadToJson.Read1();
    }

    private static HttpRequestInitializer getCredentials() throws IOException {
        InputStream in = GoogleApiToJson.class.getResourceAsStream(CREDENTIALS_FILE);
        if (in == null) {
            throw new FileNotFoundException("No File" + CREDENTIALS_FILE);
        }
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Lists.newArrayList(Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)));
        return new HttpCredentialsAdapter(credentials);
    }

    private static Properties getProperties() throws IOException {
        if (appProp != null) {
            return appProp;
        }
        InputStream in = GoogleApiToJson.class.getResourceAsStream(PROPERTIES_FILE);
        if (in == null) {
            throw new FileNotFoundException("No FileAgain" + PROPERTIES_FILE);
        }
        appProp = new Properties();
        appProp.load(in);
        return appProp;
    }
}
