package com.example.sobollab2;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class WelcomeController {
    @FXML
    private TextField nicknameField;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    private void connectServer() {
        try {
            clientSocket = new Socket("localhost", 8080);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("СЕРВЕР НЕ ЗАПУЩЕН!");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Server is not available.");
            alert.showAndWait();
        }
    }

    private void loginGame() {
        try {
            if (nicknameField.getText() == null || nicknameField.getText().equals("")){
                Alert alert = new Alert(Alert.AlertType.WARNING, "Введите никнейм");
                alert.showAndWait();
                return;
            }
            out.writeUTF(nicknameField.getText());
            String response = in.readUTF();
            if (response.equals("OK")) {
                FXMLLoader fxmlLoader = new FXMLLoader(MarksmanGame.class.getResource("marksman-game-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = (Stage) nicknameField.getScene().getWindow();
                stage.setScene(scene);

                GameClient client = fxmlLoader.getController();
                client.connectServer(clientSocket, in, out);
            } else {
                System.out.println("Cервер уже запущен!");
//                Alert alert = new Alert(Alert.AlertType.WARNING, response);
//                alert.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onConnectButtonClick() {
        if (clientSocket == null) {
            connectServer();
        }
        if (clientSocket != null) {
            loginGame();
        }
    }
}
