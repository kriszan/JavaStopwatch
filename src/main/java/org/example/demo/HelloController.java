package org.example.demo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.Duration;
import java.time.Instant;

public class HelloController {

    @FXML
    private Button startButton;
    @FXML
    private Button resetButton;
    @FXML
    private Label clockTime;
    @FXML
    private ListView<String> myListView;

    private boolean isRunning = false;
    private Instant startInstant;
    private Task<Void> timerTask;

    @FXML
    private void initialize() {
        startButton.setOnAction(event -> handleStartStop());
        resetButton.setOnAction(event -> handleReset());
    }

    private void handleStartStop() {
        if (isRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void handleReset() {
        if (resetButton.getText().equals("Reset")) {
            clockTime.setText("00:00:00");
        } else {
            recordCurrentTime();
        }
    }

    private void startTimer() {
        isRunning = true;
        if (startInstant == null) {
            startInstant = Instant.now();
        }
        //startInstant = Instant.now();

        timerTask = new Task<>() {
            @Override
            protected Void call() {
                while (!isCancelled()) {
                    Duration elapsed = Duration.between(startInstant, Instant.now());
                    updateMessage(formatDuration(elapsed));

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        //nélküle hibára fut :(
                        Thread.currentThread().interrupt();
                    }
                }
                return null;
            }
        };


        clockTime.textProperty().bind(timerTask.messageProperty());

        new Thread(timerTask).start();

        startButton.setText("Stop");
        resetButton.setText("Lap Time");
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        isRunning = false;
        startButton.setText("Start");
        resetButton.setText("Reset");
        clockTime.textProperty().unbind();
    }

    private void recordCurrentTime() {
        Duration lapTime = Duration.between(startInstant, Instant.now());
        myListView.getItems().add(formatDuration(lapTime));
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        long milliseconds = duration.toMillis() % 1000;
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds);
    }
}