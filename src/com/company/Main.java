package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
private static DistributedMap map;
    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack","true");
        map = new DistributedMap("new-channel-name");
        eventLoop();
        map.close();
    }


    private static void eventLoop() throws IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
                System.out.print("> "); System.out.flush();
                String line=in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    return;
                if(line.startsWith("put")){
                    map.put(line.split(" ")[1], Integer.parseInt(line.split(" ")[2]));
                } else
                if(line.startsWith("get")){
                    System.out.println("result: "+map.get(line.split(" ")[1]));
                } else
                if(line.startsWith("containsKey")){
                    System.out.println("result: "+map.containsKey(line.split(" ")[1]));
                } else
                if(line.startsWith("remove")){
                    map.remove(line.split(" ")[1]);
                }
            if(line.startsWith("map")){
                map.logMapState();
            }
        }
    }
}
