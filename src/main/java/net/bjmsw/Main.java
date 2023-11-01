package net.bjmsw;

import net.bjmsw.helper.Config;
import net.bjmsw.helper.Setup;
import net.bjmsw.model.TrackInfo;

import java.io.IOException;
import java.time.Instant;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class Main extends Thread {

    public static Queue<TrackInfo> trackInfoQueue;
    public static Queue<String> artworkQueue;

    public static int playerStatus = 0;
    public static Instant endTime = Instant.now();

    public static void main(String[] args) {
        try {
            new Setup();
        } catch (IOException e) {
            System.err.println("[Setup] Error while setting up!");
            System.err.println("[Setup] Failed to create tools directory!");
            System.exit(0);
        }
        Config.getInstance();

        trackInfoQueue = new ArrayBlockingQueue<>(100);
        artworkQueue = new ArrayBlockingQueue<>(100);

        iTunesBridge iTunesBridge = new iTunesBridge();
        iTunesBridge.start();

        DiscordRP discordRP = new DiscordRP(iTunesBridge);
        discordRP.start();

    }
}