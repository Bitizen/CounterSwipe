package com.bitizen.counterswipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SocketService extends Service {

	private Boolean setup = false;
	private String result;
	private String message;
	private Socket socket;
	private InputStreamReader isr;
	private BufferedReader reader;
	private PrintWriter out;
	private ExecutorService es;
	private Runnable updateRunnable;

    private InetAddress serverAddr;
	private final IBinder myBinder = new LocalBinder();
	//private TCPClient mTcpClient = new TCPClient();
	private static final int SERVERPORT = 5559;
	private static final String SERVERIP = "192.168.0.16";   

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("I am in Ibinder onBind method");
	    return myBinder;
	}

	public class LocalBinder extends Binder {
		public SocketService getService() {
			System.out.println("I am in Localbinder ");
			return SocketService.this;
		}
	}

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("I am in on create");     
    }

    public void IsBoundable() {
        //Toast.makeText(this,"I bind like butter", Toast.LENGTH_LONG).show();
    	System.out.println("BOUNDABLE");
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            System.out.println("in sendMessage"+message);
            out.println(message);
            out.flush();
        }
    }

    public String receiveMessage() {
        if (message != null) {
        	return result;
        } else {
        	return "result is null";
        }
    }
    
    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        //TRIAL
    	
        Runnable read = new readSocket();
        new Thread(read).start();
        return START_STICKY;
    }

    /*
    class connectSocket implements Runnable {
        @Override
        public void run() {
    		try {
    			serverAddr = InetAddress.getByName(SERVERIP);
    			socket = new Socket(serverAddr, SERVERPORT);
    			isr = new InputStreamReader(socket.getInputStream());
    			reader = new BufferedReader(isr);
    			out = new PrintWriter(socket.getOutputStream());
    		} catch (UnknownHostException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
    }
    */

	private void setupNetworking() {
		try {
			socket = new Socket(SERVERIP, SERVERPORT);
			isr = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(isr);
			out = new PrintWriter(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    class readSocket implements Runnable {
        @Override
        public void run() {
			if (!setup) {
				result = new String();
				setupNetworking();
				setup = true;
			}
			
        	try {
				while((result=reader.readLine())!=null) {
					System.out.println("server: " + result);
					//UIHandler.post(updateRunnable);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
        	socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;
    }
}
