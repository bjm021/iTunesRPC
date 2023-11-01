package net.bjmsw;

import net.bjmsw.helper.ImageUploader;
import net.bjmsw.model.TrackInfo;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class iTunesBridge extends Thread {

    private TrackInfo trackInfo;
    private String lastTrackName = "notinitalized";

    @Override
    public void run() {

        while (true) {

            try {
                String command = "cscript //NoLogo .\\tools\\getTrackInfo.vbs";
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder output = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                //.out.println(output.toString());

                String base64Output = output.toString();
                base64Output = base64Output.replace("\n", "");
                byte[] decodedBytes = Base64.getDecoder().decode(base64Output);
                String result = new String(decodedBytes, StandardCharsets.UTF_8);


                result = result.substring(1);
                //System.out.println(result);




                JSONObject trackInfoJSON = new JSONObject(result);
                int playerStatus = trackInfoJSON.getInt("state");
                trackInfo = new TrackInfo(trackInfoJSON.getString("title"), trackInfoJSON.getString("artist"), trackInfoJSON.getString("album"), trackInfoJSON.getInt("duration"));
                if (lastTrackName.equals("notinitalized")) {
                    lastTrackName = trackInfo.getTrackName();
                    System.out.println("[iTunesBridge] First track: " + trackInfo.getTrackName());
                    System.out.println("[iTunesBridge] First artist: " + trackInfo.getArtist());
                    System.out.println("[iTunesBridge] First album: " + trackInfo.getAlbum());
                    System.out.println("[iTunesBridge] is empty?: " + trackInfo.isEmpty());
                    if (!trackInfo.isEmpty()) extractArtwork();
                    Main.trackInfoQueue.add(trackInfo);
                } else if (!lastTrackName.equals(trackInfo.getTrackName())) {
                    lastTrackName = trackInfo.getTrackName();
                    System.out.println("[iTunesBridge] New track: " + trackInfo.getTrackName());
                    System.out.println("[iTunesBridge] New artist: " + trackInfo.getArtist());
                    System.out.println("[iTunesBridge] New album: " + trackInfo.getAlbum());
                    System.out.println("[iTunesBridge] is empty?: " + trackInfo.isEmpty());
                    if (!trackInfo.isEmpty()) extractArtwork();
                    Main.trackInfoQueue.add(trackInfo);
                }

                if (playerStatus != Main.playerStatus) {
                    Main.playerStatus = playerStatus;
                    System.out.println("[iTunesBridge] Player status changed to " + playerStatus);

                    if (playerStatus == 1) Main.trackInfoQueue.add(trackInfo);
                }

                int position = trackInfoJSON.getInt("position");
                int duration = trackInfoJSON.getInt("duration");

                // calculate the end timestamp
                // now + (duration - position)
                long endTime = System.currentTimeMillis() / 1000L + (duration - position);
                // to EpochSecond
                endTime = Instant.ofEpochSecond(endTime).getEpochSecond();
                Main.endTime = Instant.ofEpochSecond(endTime);



                // Wait for the process to complete
                int exitCode = process.waitFor();
                if (exitCode != 0) System.out.println("Exit code: " + exitCode);



                Thread.sleep(1000);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void extractArtwork() {
        try {
            String command = "cscript //NoLogo .\\tools\\extractArtwork.vbs";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();


            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            var format = new JSONObject(output.toString()).getString("format");

            if (format.equals("none")) {
                Main.artworkQueue.add("https://cdn.bjmsw.net/img/itunes_logo.png");
                return;
            }

            BufferedImage artwork = ImageIO.read(new File("./tools/tmp." + format));
            trackInfo.setArtwork(artwork);
            System.out.println("[ArtworkExtractor] Read artwork (Format: " + format + ")");

            String url = ImageUploader.uploadImage(artwork, trackInfo.getAlbum());
            Main.artworkQueue.add(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TrackInfo getTrackInfo() {
        return trackInfo;
    }
}
