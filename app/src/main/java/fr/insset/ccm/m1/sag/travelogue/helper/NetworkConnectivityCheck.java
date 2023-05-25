package fr.insset.ccm.m1.sag.travelogue.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import fr.insset.ccm.m1.sag.travelogue.activity.MainActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.NoConnection;

public class NetworkConnectivityCheck {

    private static boolean wasConnect = true;

    public static boolean isOnline() {
        try {
            int timeoutMs = 10000;
            Socket sock = new Socket();
            SocketAddress sockAddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockAddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

    public static void checkConnection(Context context){
        if(!isOnline() && wasConnect){
            wasConnect = false;
            Intent intent = new Intent(context, NoConnection.class);
            ((Activity) context).overridePendingTransition(0, 0);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        else if (isOnline() && !wasConnect){
            wasConnect = true;
            Intent intent = new Intent(context, MainActivity.class);
            ((Activity) context).overridePendingTransition(0, 0);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }
}