package it.unipi.sam.volleyballmovementtracker.util;

public class BtAndServiceStatesWrapper {
    private int btState;
    private int serviceState;

    public BtAndServiceStatesWrapper(int btState, int serviceState) {
        this.btState = btState;
        this.serviceState = serviceState;
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
}
