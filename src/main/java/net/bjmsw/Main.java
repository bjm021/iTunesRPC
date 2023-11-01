package net.bjmsw;

import net.bjmsw.helper.Config;
import net.bjmsw.model.TrackInfo;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Main extends Thread {

    public static Queue<TrackInfo> trackInfoQueue;
    public static Queue<String> artworkQueue;

    public static int playerStatus = 0;
    public static Instant endTime = Instant.now();

    public static void main(String[] args) {
        Config.getInstance();

        trackInfoQueue = new ArrayBlockingQueue<>(100);
        artworkQueue = new ArrayBlockingQueue<>(100);

        iTunesBridge iTunesBridge = new iTunesBridge();
        iTunesBridge.start();

        DiscordRP discordRP = new DiscordRP(iTunesBridge);
        discordRP.start();

    }
}