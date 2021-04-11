

import java.io.*;
import java.net.*;
import java.util.*;

public class client {

    public static void main(String[] args)
    {
        try{

            long timeStart = System.nanoTime();
            Socket socket = new Socket("localhost", 7899);
            System.out.println("Server connection established");
            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());


            BufferedReader b = new BufferedReader(new InputStreamReader(din));

            System.out.println("");
            System.out.println("Hostname: " + socket.getInetAddress().getHostName());
            System.out.println("Port: " + socket.getPort());
            System.out.println("Socket Family: AF_INET"); //Java only supports this
            System.out.println("Socket Type: SOCK_STREAM"); // From DataOutputStream.
            System.out.println("Protocol: IPROTO_TCP"); // From DataOutputStream, Stream happens only on TCP.
            dout.writeBytes("GET /index.html HTTP/1.1\n");

            String l  = b.readLine();

            while(l!=null)
            {
                System.out.println(l);
                l = b.readLine();
            }

            dout.flush();

            socket.close();
            long timeEnd = System.nanoTime();
            System.out.println("RTT=" +  (double)Math.round(((timeEnd - timeStart) / 1000000) * 100)/100+" ms");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
