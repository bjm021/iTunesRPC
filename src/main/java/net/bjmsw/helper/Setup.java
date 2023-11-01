package net.bjmsw.helper;

import net.bjmsw.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Setup {

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

        toolsDir.mkdir();
        System.out.println("[Setup] Exporting resources...");
        exportResource("tools/discord_game_sdk.dll", "tools/discord_game_sdk.dll");
        exportResource("tools/extractArtwork.vbs", "tools/extractArtwork.vbs");
        exportResource("tools/getTrackInfo.vbs", "tools/getTrackInfo.vbs");

        FileWriter fw = new FileWriter("tools/version.txt");
        fw.write(VERSION);
        fw.close();

        System.out.println("[Setup] Done!");
    }

    public static void main(String[] args) throws Exception {
        new Setup();
    }


}
