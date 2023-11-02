package net.bjmsw.itunesrpc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.bjmsw.itunesrpc.helper.Config;
import net.bjmsw.itunesrpc.helper.Setup;
import net.bjmsw.itunesrpc.mocel.TrackInfo;

import java.io.IOException;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Main extends Application {

    public static Queue<TrackInfo> trackInfoQueue;
    public static Queue<String> artworkQueue;
    public static boolean rpcEnabled = true;
    public static boolean rpcRunning = false;

    public static int playerStatus = 0;
    public static Instant endTime = Instant.now();
    public static iTunesBridge iBridge;

    private static DiscordRP discordRP;

    public static Setup.OS currentOS = Setup.OS.OTHER;

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

        iBridge = new iTunesBridge();
        iBridge.start();

        discordRP = new DiscordRP(iBridge);
        discordRP.start();

        launch(args);

    }

    public static String getDefaultImageUrl() {
        switch (currentOS) {
            case MAC -> {
                return "https://cdn.bjmsw.net/img/AppleMusic_Logo.png";
            }
            default -> {
                return "https://cdn.bjmsw.net/img/itunes_logo.png";
            }
        }
    }

    private static Label statusLabel;
    private static Image artwork;
    private static ImageView imageView;
    private static AnchorPane rootPane;
    private static Label nowPlayingLabel;
    private static boolean uiInitialized = false;
    @Override
    public void start(Stage stage) throws Exception {
        rootPane = new AnchorPane();
        Scene scene = new Scene(rootPane, 400, 500);

        Label label = new Label("iTunesRPC");
        label.setFont(javafx.scene.text.Font.font(30));
        label.setStyle("-fx-alignment: center;");
        label.setPrefWidth(200);
        label.setPrefHeight(50);
        label.setLayoutX(100);
        label.setLayoutY(20);
        rootPane.getChildren().add(label);

        statusLabel = new Label("Status: " + playerStatus);
        statusLabel.setFont(javafx.scene.text.Font.font(20));
        statusLabel.setStyle("-fx-alignment: center;");
        statusLabel.setPrefWidth(200);
        statusLabel.setPrefHeight(50);
        statusLabel.setLayoutX(100);
        statusLabel.setLayoutY(80);
        rootPane.getChildren().add(statusLabel);

        nowPlayingLabel = new Label("Now Playing: " + iBridge.getTrackInfo().getTrackName());
        nowPlayingLabel.setFont(javafx.scene.text.Font.font(20));
        nowPlayingLabel.setStyle("-fx-alignment: center;");
        nowPlayingLabel.setPrefWidth(350);
        nowPlayingLabel.setPrefHeight(50);
        nowPlayingLabel.setLayoutX(25);
        nowPlayingLabel.setLayoutY(120);
        rootPane.getChildren().add(nowPlayingLabel);

        Button toggleRPCButton = new Button("Turn off RPC");
        toggleRPCButton.setPrefWidth(200);
        toggleRPCButton.setPrefHeight(50);
        toggleRPCButton.setLayoutX(100);
        toggleRPCButton.setLayoutY(410);
        toggleRPCButton.setOnAction(event -> {
            if (toggleRPCButton.getText().equals("Turn off RPC")) {
                toggleRPCButton.setText("Turn on RPC");
                discordRP.interrupt();
                rpcEnabled = false;
            } else {
                toggleRPCButton.setText("Turn off RPC");
                iBridge.reset();
                discordRP = new DiscordRP(iBridge);
                discordRP.start();
                rpcEnabled = true;
                iBridge.reset();
            }
        });
        rootPane.getChildren().add(toggleRPCButton);

        uiInitialized = true;
        updateUI();

        stage.setOnCloseRequest(event -> {
            System.out.println("[Main] Closing...");
            iBridge.interrupt();
            System.exit(0);
        });

        UpdateUI updateUI = new UpdateUI();
        updateUI.start();

        stage.setScene(scene);
        stage.show();

    }

    public static void updateUI() {
        if (!uiInitialized) return;
        if (iBridge.getTrackInfo().getArtwork() != null) {
            artwork = SwingFXUtils.toFXImage(iBridge.getTrackInfo().getArtwork(), null);
        } else {
            artwork = new Image(getDefaultImageUrl());
        }
        if (imageView != null) rootPane.getChildren().remove(imageView);
        imageView = new ImageView(artwork);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setLayoutX(100);
        imageView.setLayoutY(180);
        rootPane.getChildren().add(imageView);
        nowPlayingLabel.setText("Now Playing: " + iBridge.getTrackInfo().getTrackName());

    }

    public static void checkRunning() {
        if (rpcRunning) {
            statusLabel.setText("Status: RPC running");
            statusLabel.setStyle("-fx-text-fill: green; -fx-alignment: center;");
        } else {
            statusLabel.setText("Status: RPC not running");
            statusLabel.setStyle("-fx-text-fill: red; -fx-alignment: center;");
        }
    }

    class UpdateUI extends Thread {
        public void run() {
            while (true) {
                Platform.runLater(Main::checkRunning);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}