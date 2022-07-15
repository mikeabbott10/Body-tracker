package it.unipi.sam.volleyballmovementtracker.util;

import android.os.Message;

public class MessageWrapper {
    public Message msg;
    public boolean oscillator;

    public MessageWrapper(Message msg, boolean oscillator) {
        this.msg = msg;
        this.oscillator = oscillator;
    }

}
