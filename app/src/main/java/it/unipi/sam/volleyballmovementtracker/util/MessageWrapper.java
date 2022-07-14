package it.unipi.sam.volleyballmovementtracker.util;

import android.os.Message;

public class MessageWrapper {
    public Message msg;
    public boolean oscillatore;

    public MessageWrapper(Message msg, boolean oscillatore) {
        this.msg = msg;
        this.oscillatore = oscillatore;
    }
}
