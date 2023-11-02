package net.bjmsw;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.bjmsw.helper.Setup;
import net.bjmsw.model.TrackInfo;

import java.io.File;

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
        File discordLibrary = null;
        switch (Main.currentOS) {
            case WINDOWS ->
                    discordLibrary = new File("./tools/discord_game_sdk.dll");
            case MAC ->
                    discordLibrary = new File("./tools/discord_game_sdk.dylib");
            case LINUX ->
                    discordLibrary = new File("./tools/discord_game_sdk.so");


        }


        assert discordLibrary != null;
        Core.init(discordLibrary);

        try (CreateParams params = new CreateParams()) {
            if (Main.currentOS == Setup.OS.MAC) params.setClientID(1169615516168634398L);
            else params.setClientID(1169073481737064488L);
            params.setFlags(CreateParams.getDefaultFlags());

            try (Core core = new Core(params)) {


                try (Activity activity = new Activity()) {
                    while (true) {
                        if (!Main.trackInfoQueue.isEmpty()) {
                            System.out.println("[DiscordRP] Queue not empty, updating RPC");
                            TrackInfo trackInfo = Main.trackInfoQueue.poll();
                            System.out.println("[DiscordRP] Updating RPC");


                            activity.setDetails(trackInfo.getTrackName());

                            if (!trackInfo.getArtist().isEmpty()) activity.setState("by " + trackInfo.getArtist());
                            else activity.setState("iTunesRPC by b.jm021");


                            if (trackInfo.getAlbum().isEmpty()) {
                                activity.assets().setLargeText("iTunesRPC by b.jm021");
                                activity.assets().setSmallText("");
                                activity.assets().setSmallImage("");
                            }
                            else {
                                activity.assets().setLargeText(trackInfo.getAlbum());
                                activity.assets().setSmallImage(Main.getDefaultImageUrl());
                                activity.assets().setSmallText("iTunesRPC by b.jm021");
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
