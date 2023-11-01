package net.bjmsw;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.bjmsw.model.TrackInfo;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

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

                            String details = trackInfo.getTrackName();
                            if (!trackInfo.getArtist().isEmpty()) details += " by " + trackInfo.getArtist();
                            activity.setDetails(details);

                            if (trackInfo.getAlbum().isEmpty()) {
                                activity.setState("iTunesRPC by b.jm021");
                                activity.assets().setLargeText("iTunesRPC by b.jm021");
                            }
                            else {
                                activity.setState("on " + trackInfo.getAlbum());
                                activity.assets().setLargeText(trackInfo.getAlbum());
                            }

                            //activity.assets().setSmallText(trackInfo.getArtist());
                            // calcuilate end time (now + length)
                            long endTime = trackInfo.getStartTime() + trackInfo.getLength();
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
                        } else {
                            activity.timestamps().setEnd(Main.endTime);
                            core.activityManager().updateActivity(activity);
                            core.runCallbacks();
                        }

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
