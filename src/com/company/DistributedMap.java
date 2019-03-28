package com.company;

import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DistributedMap implements Receiver, SimpleStringMap {
    private JChannel jchannel;
    private HashMap<String, Integer> localMap;
    public DistributedMap(String channelId) throws Exception {
        jchannel = new JChannel();
        jchannel.setReceiver(this);
        jchannel.connect(channelId);
        localMap = new HashMap<>();
        jchannel.getState(null, 0);
    }

    @Override
    public boolean containsKey(String key) {
        return localMap.containsKey(key);
    }


    public void logMapState(){
        System.out.println("Current map state");
        for (Map.Entry<String,Integer> item:localMap.entrySet()
             ) {
            System.out.println(item.getKey()+": "+item.getValue());
        }
    }

    @Override
    public Integer get(String key) {
        return localMap.get(key);
    }

    @Override
    public void put(String key, Integer value) {
        try {
            jchannel.send(new Message(null, new MapMessage(MsgType.PUT, key, value)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer remove(String key) {
        try {
            jchannel.send(new Message(null, new MapMessage(MsgType.REMOVE, key)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localMap.get(key);
    }

    public void close(){

        jchannel.close();
    }

    @Override
    public void receive(Message msg) {
        MapMessage mapMsg = msg.getObject();
        if(mapMsg.type == MsgType.PUT){
            localMap.put(mapMsg.key, mapMsg.value);
        } else
        if(mapMsg.type == MsgType.REMOVE){
            localMap.remove(mapMsg.key);
        }
        logMapState();
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (localMap) {
            Util.objectToStream(localMap, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        synchronized (localMap) {
            localMap = Util.objectFromStream(new DataInputStream(input));
        }
    }

    @Override
    public void viewAccepted(View view) {
        handleView(jchannel, view);
    }
    // http://www.jgroups.org/manual/index.html#HandlingNetworkPartitions
    private static void handleView(JChannel ch, View new_view) {
        if(new_view instanceof MergeView) {
            ViewHandler handler = new ViewHandler(ch, (MergeView)new_view);
            handler.start();
        }
    }

    private static class ViewHandler extends Thread {
        JChannel ch;
        MergeView view;

        private ViewHandler(JChannel ch, MergeView view) {
            this.ch = ch;
            this.view = view;
        }

        public void run() {
            Vector<View> subgroups = (Vector<View>) view.getSubgroups();
            View tmp_view = subgroups.firstElement();
            Address local_addr = ch.getAddress();
            if(!tmp_view.getMembers().contains(local_addr)) {
                System.out.println("dropping own state");
                try {
                    ch.getState(null, 10000);
                }
                catch(Exception ex) {
                }
            }
            else {
                System.out.println("doing nothing");
            }
        }
    }
}
