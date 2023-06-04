package fr.insset.ccm.m1.sag.travelogue.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class NetworkConnectivityCheck {

    private static final String IP_ADDRESS = "194.57.107.117";
    private static final int PORT = 80;
    private static final int TIMEOUT_MS = 1500;

    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected && isServerReachable();
    }

    private static boolean isServerReachable() {
        try (Socket socket = new Socket()) {
            SocketAddress socketAddress = new InetSocketAddress(IP_ADDRESS, PORT);
            socket.connect(socketAddress, TIMEOUT_MS);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}


