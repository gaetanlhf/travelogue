package fr.insset.ccm.m1.sag.travelogue.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import fr.insset.ccm.m1.sag.travelogue.activity.MainActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.NoConnection;

public class NetworkConnectivityCheck {

    private static boolean wasConnect;

    public static boolean isOnline() {
        try {
            int timeoutMs = 5000;
            Socket sock = new Socket();
            SocketAddress sockAddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockAddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

    public static void checkConnection(Callback callback){
        AtomicBoolean isConnected = new AtomicBoolean(false);
        isConnected.set(isOnline());
        callback.onCallback(isConnected);
    }

    public interface Callback {
        void onCallback(AtomicBoolean isConnected);
    }
}