package com.NImble.phoney_mouse;

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {

    private String serverMessage;
    public static final String SERVERIP = Global.stringToPass; //your computer IP address
    public static final int SERVERPORT = 8080;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent.");
                Log.e("TCP Client", "C: Done.");

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (mRun) {
                    serverMessage = in.readLine();
                    Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
                    if (serverMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;
                }
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                socket.close();
            }
        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);
        }
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}