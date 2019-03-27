package com.company;

import java.io.Serializable;

public class MapMessage implements Serializable {

    public MsgType type;
    public String key;
    public Integer value;

    public MapMessage(MsgType type, String key, Integer value){
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public MapMessage(MsgType type, String key){
        this.type = type;
        this.key = key;
    }
}
