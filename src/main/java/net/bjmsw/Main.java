package net.bjmsw;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.bjmsw.model.TrackInfo;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Main extends Thread {

    public static Queue<TrackInfo> trackInfoQueue;
    public static Queue<String> artworkQueue;

    public static void main(String[] args) {
        trackInfoQueue = new ArrayBlockingQueue<>(100);
        artworkQueue = new ArrayBlockingQueue<>(100);

        iTunesBridge iTunesBridge = new iTunesBridge();
        iTunesBridge.start();

        DiscordRP discordRP = new DiscordRP(iTunesBridge);
        discordRP.start();

    }
}