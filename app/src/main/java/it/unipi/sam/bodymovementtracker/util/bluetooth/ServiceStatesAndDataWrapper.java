package it.unipi.sam.bodymovementtracker.util.bluetooth;

import it.unipi.sam.bodymovementtracker.util.MessageWrapper;

public class ServiceStatesAndDataWrapper {
    private int btState;
    private int serviceState;
    private MessageWrapper data;

    public ServiceStatesAndDataWrapper(int btState, int serviceState, MessageWrapper msg) {
        this.btState = btState;
        this.serviceState = serviceState;
        this.data = msg;
    }

    public int getBtState() {
        return btState;
    }
    public void setBtState(int btState) {
        this.btState = btState;
    }

    public int getServiceState() {
        return serviceState;
    }
    public void setServiceState(int serviceState) {
        this.serviceState = serviceState;
    }

    public MessageWrapper getMsg() {
        return data;
    }
    public void setMsg(MessageWrapper msg) {
        this.data = msg;
    }
}
