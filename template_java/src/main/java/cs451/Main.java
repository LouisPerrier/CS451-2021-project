package cs451;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Main {

    private static String outputPath;
    public static Queue<String> outputBuffer = new LinkedList<>();

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");

        //write/flush output file if necessary
        System.out.println("Writing output.");

        try {
            File outputFile = new File(outputPath);
            FileOutputStream s = new FileOutputStream(outputFile);
            OutputStreamWriter w = new OutputStreamWriter(s);
            while (outputBuffer.peek() != null) {
                w.write(outputBuffer.poll());
                w.write("\n");
            }
            w.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID: " + pid + "\n");
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

        System.out.println("My ID: " + parser.myId() + "\n");

        Host myHost = null;

        System.out.println("List of resolved hosts is:");
        System.out.println("==========================");
        for (Host host: parser.hosts()) {
            System.out.println(host.getId());
            System.out.println("Human-readable IP: " + host.getIp());
            System.out.println("Human-readable Port: " + host.getPort());
            System.out.println();

            if (host.getId()==parser.myId() && myHost==null) {
                myHost = host;
            }
        }
        System.out.println();

        int nbMessages = 0;
Host receiver = null;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File(parser.config())));
               String[] s = reader.readLine().split(" ");
                nbMessages = Integer.parseInt(s[0]);
                int receiverId = Integer.parseInt(s[1]);
                for (Host h : parser.hosts()) {
                    if (h.getId() == receiverId) receiver = h;
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (myHost != null && receiver  != null  ) {
                myHost.init(parser.hosts(), nbMessages, receiver);
            } else {
                System.out.println("Invalid id");
            }
        

        System.out.println("Path to output:");
        System.out.println("===============");
        System.out.println(parser.output() + "\n");
outputPath = parser.output();

        System.out.println("Path to config:");
        System.out.println("===============");
        System.out.println(parser.config() + "\n");

        System.out.println("Doing some initialization\n");

        System.out.println("Broadcasting and delivering messages...\n");

        if (myHost != null) myHost.broadcast();

        // After a process finishes broadcasting,
        // it waits forever for the delivery of messages.
        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }
    }
}
