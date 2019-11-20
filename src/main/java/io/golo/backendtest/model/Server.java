package io.golo.backendtest.model;

import java.util.ArrayList;
import java.util.List;

// Server class which holds the information of a server.
public class Server {
    private String URL;
    private String actionStatus;
    private List<Record> historyItem ;
    public Server(String URL, String status){
        this.URL = URL;
        this.actionStatus =status;
        historyItem = new ArrayList<>();

    }
    public void setStaus(String status){
        this.actionStatus = status;
    }
    public void removeHistory(){
        historyItem.clear();
    }
    public List<Record> getRecords(){
        return historyItem;
    }
    public String getActionStatus() {
        return actionStatus;
    }
    public String getURL (){
        return URL;
    }
}
