import java.io.*;
import java.net.*;
import java.util.*;

public final class server {
    public static void main(String argv[]) throws IOException {

        System.out.println("Starting the server.");
        ServerSocket serverSocket = new ServerSocket(7899);
        Socket socket = serverSocket.accept();

        while (true) {
            try {

                socket = serverSocket.accept();
                System.out.println("Socket connected.");
                HttpRequest req = new HttpRequest(socket);
                Thread thread = new Thread(req);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception r) {
                r.printStackTrace();
            }
        }
    }
}
final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;


    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }


    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {

        DataInputStream datais = new DataInputStream(socket.getInputStream());
        DataOutputStream dataos = new DataOutputStream(socket.getOutputStream());

        System.out.println("Hostname: " + socket.getInetAddress().getHostName());
        System.out.println("Port: " + socket.getPort());
        System.out.println("Socket Family: AF_INET"); //Java only supports this
        System.out.println("Socket Type: SOCK_STREAM"); // From DataOutputStream.
        System.out.println("Protocol: IPROTO_TCP"); // From DataOutputStream, UDP does not support stream.
        BufferedReader br = new BufferedReader(new InputStreamReader(datais));

        String requestLine = br.readLine();


        System.out.println();
        System.out.println("Request Line is :"+ requestLine);


        StringTokenizer tokenizer= new StringTokenizer(requestLine);
        tokenizer.nextToken();
        String filename = tokenizer.nextToken();

        System.out.println(filename);


        if(filename.length()>4 && filename.substring(0,5).equals("/src/"))
        {
            filename = filename.substring(4);
            System.out.println(filename);

        }

        else if (filename.equals("/"))
        {
            filename ="/default.html";
        }

        FileInputStream fis = null;
        boolean Fexists = true;
        try
        {
            fis = new FileInputStream("./src"+filename);

        }
        catch(FileNotFoundException f){

            Fexists = false;
        }

        String statusline = null;
        String ContentTypeLine = null;
        String EntityBody = null;

        if(Fexists){
            System.out.println("file found");
            statusline = "HTTP/1.1 200 OK";
            ContentTypeLine = "Content-type: "+ contentType(filename)+ CRLF;
        }
        else{
            statusline = "HTTP/1.1 404 Not Found";
            ContentTypeLine = "No such file exists";
            EntityBody = "404 NOT FOUND";
        }

        dataos.writeBytes(statusline);
        dataos.writeBytes(CRLF);
        dataos.writeBytes(ContentTypeLine);
        dataos.writeBytes(CRLF);
        dataos.writeBytes(CRLF);


        if (Fexists){

            sendBytes(fis, dataos);
            fis.close();
        }
        else{
            dataos.writeBytes(EntityBody);
        }
        dataos.writeBytes(CRLF);

        dataos.close();
        br.close();
        socket.close();
        System.out.println("socket closed");
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception
    {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }


    private static String contentType (String Fname){
        if (Fname.endsWith(".htm") || Fname.endsWith(".html")) {
            return "text/html";
        }
        return "application/octet-stream";
    }
}

// WEB REFERENCES:
// https://github.com/samruddhikapileshwar/Building-a-Simple-Web-Client-and-a-Multithreaded-Web-Server
// https://www.net.t-labs.tu-berlin.de/teaching/computer_networking/ap01.htm
// https://www.youtube.com/watch?v=bhskF8Dl8Ns
// https://www.youtube.com/watch?v=ZIzoesrHHQo&t=264s
// https://stackoverflow.com/questions/32002172/how-to-programmatically-send-http-request-to-inner-webpage-link-with-java

