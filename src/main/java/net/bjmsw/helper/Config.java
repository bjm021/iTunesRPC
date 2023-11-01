package net.bjmsw.helper;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Config {

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public Config() {
        readConfig();
    }

    JSONObject config;

    private void readConfig() {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            try {
                createConfig();
            } catch (IOException e) {
                System.err.println("[Config] Could not create config file!");
                return;
            }
        }

        try {
            config = new JSONObject(IOUtils.toString(new FileInputStream(configFile), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            System.err.println("[Config] Could not find config file!");
        } catch (IOException e) {
            System.err.println("[Config] Could not read config file!");
        }

    }

    private void createConfig() throws IOException {
        System.out.println("[Config] Creating config file...");
        System.out.println("[Config] Please take a moment to configure the application.");
        JSONObject root = new JSONObject();
        JSONObject upload = new JSONObject();
        upload.put("url", "<host-url>");
        upload.put("password", "<password>");
        root.put("cover-upload-config", upload);

        try (FileWriter file = new FileWriter("config.json")) {
            file.write(root.toString(4));
        }
    }

    private void writeConfig() {
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(config.toString(4));
        } catch (IOException e) {
            System.err.println("[Config] Could not write config file!");
        }
    }

    public String getUploadURL() {
        readConfig();
        return config.getJSONObject("cover-upload-config").getString("url");
    }

    public String getUploadPassword() {
        readConfig();
        return config.getJSONObject("cover-upload-config").getString("password");
    }

    public void setUploadURL(String url) {
        readConfig();
        config.getJSONObject("cover-upload-config").put("url", url);
        writeConfig();
    }

    public void setUploadPassword(String password) {
        readConfig();
        config.getJSONObject("cover-upload-config").put("password", password);
        writeConfig();
    }

}
