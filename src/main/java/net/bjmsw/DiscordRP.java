package net.bjmsw;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.bjmsw.model.TrackInfo;

import java.io.File;
import java.time.Instant;

public class DiscordRP extends Thread {

    private iTunesBridge iTunesBridge;

    public DiscordRP(iTunesBridge iTunesBridge) {
        this.iTunesBridge = iTunesBridge;
    }

    private boolean checkUpdate = true;

    public void run() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        File discordLibrary = new File("./discord_game_sdk.dll");
        Core.init(discordLibrary);

        try (CreateParams params = new CreateParams()) {
            params.setClientID(1169073481737064488L);
            params.setFlags(CreateParams.getDefaultFlags());

            try (Core core = new Core(params)) {


                try (Activity activity = new Activity()) {
                    while (true) {
                        if (!Main.trackInfoQueue.isEmpty()) {
                            System.out.println("[DiscordRP] Queue not empty, updating RPC");
                            TrackInfo trackInfo = Main.trackInfoQueue.poll();
                            System.out.println("[DiscordRP] Updating RPC");
                            activity.setDetails(trackInfo.getTrackName() + " by " + trackInfo.getArtist());
                            activity.setState("on " + trackInfo.getAlbum());
                            activity.assets().setLargeText(trackInfo.getAlbum());
                            //activity.assets().setSmallText(trackInfo.getArtist());
                            activity.timestamps().setStart(Instant.ofEpochSecond(trackInfo.getStartTime()));
                            core.activityManager().updateActivity(activity);
                        }
                        if (!Main.artworkQueue.isEmpty()) {
                            System.out.println("[DiscordRP] Artwork queue not empty, updating RPC");
                            String artwork = Main.artworkQueue.poll();
                            System.out.println("[DiscordRP] Updating RPC with artwork (" + artwork + ")");
                            activity.assets().setLargeImage(artwork);
                            //activity.assets().setSmallImage(artwork);
                            core.activityManager().updateActivity(activity);
                        }
                        if (Main.playerStatus == 0) {
                            core.activityManager().clearActivity();
                        }
                        core.runCallbacks();
                        try {
                            // Sleep a bit to save CPU
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
