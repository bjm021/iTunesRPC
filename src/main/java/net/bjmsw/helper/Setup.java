package net.bjmsw.helper;

import net.bjmsw.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Setup {

    enum OS {
        WINDOWS, MAC, LINUX, OTHER
    }

    private static final String VERSION = "1.0";

    static public void exportResource(String resourceName, String dest) throws IOException {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(resourceName)) {
            Files.copy(is, Paths.get(dest), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // An error occurred copying the resource
            e.printStackTrace();
        }
    }


    public Setup() throws IOException {
        File toolsDir = new File("tools");
        if (toolsDir.exists()) {
            File versionFile = new File("tools/version.txt");
            if (versionFile.exists()) {
                FileInputStream fis = new FileInputStream("tools/version.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String line = br.readLine();
                br.close();
                if (line.equals(VERSION)) {
                    System.out.println("[Setup] Tools are up to date!");
                    return;
                }
                System.out.println("[Setup] Tools are outdated! Updating...");
            }

        }

        String osName = System.getProperty("os.name").toLowerCase();
        OS currentOS = OS.OTHER;
        if (osName.contains("windows")) {
            currentOS = OS.WINDOWS;
        } else if (osName.contains("mac")) {
            currentOS = OS.MAC;
        } else if (osName.contains("linux")) {
            currentOS = OS.LINUX;
        }

        toolsDir.mkdir();
        System.out.println("[Setup] Exporting resources...");

        switch (currentOS) {
            case WINDOWS -> {
                exportResource("tools/discord_game_sdk.dll", "tools/discord_game_sdk.dll");
                exportResource("tools/extractArtwork.vbs", "tools/extractArtwork.vbs");
                exportResource("tools/getTrackInfo.vbs", "tools/getTrackInfo.vbs");
            }
            case MAC -> {
                // not suuported yet
                exportResource("tools/libdiscord_game_sdk.dylib", "tools/libdiscord_game_sdk.dylib");
                exportResource("tools/extractArtwork.scpt", "tools/extractArtwork.scpt");
                exportResource("tools/getTrackInfo.scpt", "tools/getTrackInfo.scpt");
            }
            case LINUX -> {
                // not suuported yet
                exportResource("tools/libdiscord_game_sdk.so", "tools/libdiscord_game_sdk.so");
                exportResource("tools/extractArtwork.sh", "tools/extractArtwork.sh");
                exportResource("tools/getTrackInfo.sh", "tools/getTrackInfo.sh");
            }
        }

        FileWriter fw = new FileWriter("tools/version.txt");
        fw.write(VERSION);
        fw.close();

        System.out.println("[Setup] Done!");
    }

    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("os.name"));
    }


}
