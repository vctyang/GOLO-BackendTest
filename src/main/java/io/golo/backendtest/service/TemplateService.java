package io.golo.backendtest.service;

import io.golo.backendtest.model.ActionCode;
import io.golo.backendtest.model.Record;
import io.golo.backendtest.model.Server;
import io.golo.backendtest.model.ServerStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Service for operations
 */
@Service
public class TemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateService.class);
    private HashMap<String, Server> serverCollection = new HashMap<>();
    private  ScheduledThreadPoolExecutor EXECUTOR = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
    private HashMap<String, ScheduledFuture> schedulers = new HashMap<>();

    public boolean deleteServer (String url){
        if (serverCollection.containsKey(url)){
            serverCollection.get(url).getRecords().clear();
            serverCollection.remove(url);
                    return true;
        }
        else{
            return false;
        }
    }
    public List<Record> getHistory(String url){
        if (serverCollection.containsKey(url)){
            return serverCollection.get(url).getRecords();
        }
        else{
            return null;
        }
    }

    public String getServerStatus(String url){
        if(serverCollection.containsKey(url))
            return serverCollection.get(url).getActionStatus();
        else
            return "No such a server.";
    }
    public ActionCode updateTasks(String url, int interval, ActionCode action){
        if (action.equals(ActionCode.START)){
            //Try to start a monitoring process
            if (serverCollection.containsKey(url)){
                if (serverCollection.get(url).getActionStatus().equals(ActionCode.START.name()))
                    return ActionCode.RUNNING;
                else {
                    serverCollection.get(url).setStaus(action.name());
                    serverCollection.get(url).getRecords().clear();
                    schedulers.put(url, EXECUTOR.scheduleAtFixedRate(new CustomerRunnalbe(url,serverCollection.get(url).getRecords()),0,interval, TimeUnit.MINUTES));
                }
            }
            // No such a server is monitored, add directly
            else {
                serverCollection.put(url,new Server(url, action.name()));
                schedulers.put(url, EXECUTOR.scheduleAtFixedRate(new CustomerRunnalbe(url,serverCollection.get(url).getRecords()),0,interval, TimeUnit.MINUTES));
            }
        }
        else {
            //Try to stop monitoring
           if (!serverCollection.containsKey(url))
               return ActionCode.UNEXIST;
           schedulers.get(url).cancel(true);
           schedulers.remove(url);
           serverCollection.get(url).getRecords().add(new Record(ActionCode.STOP.name(), new Date().toString()));
           serverCollection.get(url).setStaus(ActionCode.STOP.name());
        }
        return ActionCode.UPDATED;
    }


    //Customer runnable, created with the scheduler service. Used to read information of server.
    class CustomerRunnalbe implements Runnable{

        private List<Record> entries;
        private String url;
        public CustomerRunnalbe(String url,List<Record> records) {
            this.entries = records;
            this.url = url;
        }
        private void addEntry(String status,Date date){
            if (entries.isEmpty()) {
                entries.add(new Record(status, date.toString()));
                return;
            }

            int index =  entries.size()-1;
            String lastStatus = entries.get(index) == null? null: entries.get(index).status;
            if (status.equals(lastStatus))
                return;
            else
                entries.add(new Record(status, date.toString()));
        }
        @Override
        public void run(){
            RestTemplate restTemplate = new RestTemplate();
            String status = null;
            Date date = new Date();
            ResponseEntity<String> response;
            try {
               response = restTemplate.exchange(url, HttpMethod.GET,null,String.class);
            }
            catch (Exception e){
                status = ServerStatus.UNKNOWN.name();
                addEntry(status,date);
                return;

            }
            if (!response.getStatusCode().is2xxSuccessful())
                addEntry(ServerStatus.UNACCEPTABLE.name(),date);
            else {
                JSONParser parser = new JSONParser();
                try {
                    JSONObject message = (JSONObject) parser.parse(response.getBody());
                     status = (String) message.get("status");
                } catch (ParseException e) {
                    addEntry(ServerStatus.UNRECONGNIZED.name(),date);
                }
            }
            if (status==null || status.isEmpty())
                addEntry(ServerStatus.UNRECONGNIZED.name(),date);
            else
                addEntry(status,date);
        }
    }


}
