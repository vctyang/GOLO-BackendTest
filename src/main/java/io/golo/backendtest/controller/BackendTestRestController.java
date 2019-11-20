package io.golo.backendtest.controller;

import io.golo.backendtest.model.*;
import io.golo.backendtest.service.TemplateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Rest controller exposing all API endpoints
 */
@RestController
@RequestMapping("/monitorApp")
public class BackendTestRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendTestRestController.class);
    private static final String PATTERN = "^https://api.[a-zA-z0-9]{2,}.paysafe.com/alternatepayments/v[1-9]/monitor$";
    private List<Record> records = new ArrayList<>();

    @Autowired
    private TemplateService templateService;
    /**
     * Default constructor
     */
    @Autowired
    public BackendTestRestController() {
    }

    /**
     * Returns API resource
     *
     * @return The response containing the data to retrieve.
     */
    @ApiOperation(value="Retrive the history Data.", notes="Basing on the server URL")
    @GetMapping(value = "/history")
    public ResponseEntity<Object> getData(@RequestParam(name = "Server URL", required = true) String url) {

        if (!validateURL(url))
            return new ResponseEntity<>("Server URL is wrong", HttpStatus.BAD_REQUEST);
        JSONObject response = new JSONObject();
        response.put("Server URL", url);
        response.put("Records", buildRecords(url)); ///

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @ApiOperation(value="Retrive the status of a specified server.", notes="Basing on the server URL")
    @GetMapping(value = "/configuration")
    public ResponseEntity<String> getSetting(@RequestParam(name = "Server", required = true) String url) {

        templateService.getServerStatus(url);
        return new ResponseEntity<>(templateService.getServerStatus(url), HttpStatus.OK);

    }

    //Dummy Server.
    @ApiOperation(value="Testing server , used for test multiple servers monitoring.", notes="Basing on the server URL")
    @GetMapping(value = "/test")
    public ResponseEntity<JSONObject> getStatus() {
        String sta = "status";
        String val =  Math.random()>0.5? "READY":"DOWN";
        JSONObject res = new JSONObject();
        res.put(sta,val);

        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    @ApiOperation(value="Set up a monitor. Eg. Stop or Start, and it fails if attempting to start a monitor a server which is being monitored. " +
            "And the interval is basing on minutes. ")
    @PutMapping(value = "/configuration")
    public ResponseEntity<String> setMonitor(@RequestBody( required = true ) Payload parameter,
                                             @RequestParam(name = "Stop or Start monitoring", required = true) ActionOption action) {

        if (parameter.getInterval()<1 )
            return new ResponseEntity<>( "Interval has to be postive.", HttpStatus.BAD_REQUEST);
        if ( !validateURL(parameter.getUrl()))
            return new ResponseEntity<>( "Wrong url format", HttpStatus.BAD_REQUEST);


        return new ResponseEntity<>( templateService.updateTasks( parameter.getUrl(),parameter.getInterval(),
                                          ActionCode.valueOf(action.name())).name(),
                                     HttpStatus.CREATED);
    }
    @ApiOperation(value="Delete history data of a server. ")
    @DeleteMapping(value = "/history")
    public ResponseEntity<String> deleteHistory(@RequestParam(name = "Server URL", required = true) String url) {

        if (templateService.getServerStatus(url).equals(ActionCode.STOP.name())){
            templateService.deleteServer(url);
            return new ResponseEntity<>("The History is deleted.", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("Check server status first!", HttpStatus.BAD_REQUEST);
    }

    //Validate the URL,
    private boolean validateURL(String url){
        //Uncomment the following line if using the dummy server.
        //return true;
        return url.matches(PATTERN);
    }

    private JSONArray buildRecords(String url) {
        List<Record> records = templateService.getHistory(url);
        JSONArray recordObjects = new JSONArray();
        JSONParser parser = new JSONParser();
        Date nowTime = new Date();
        JSONObject tail = new JSONObject();

        if (records != null) {
            records.forEach(x -> {
                try {
                    recordObjects.add(parser.parse(x.toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            tail.put("Checked", nowTime.toString());
        } else {
            tail.put("Wrong server", nowTime.toString());
        }
        recordObjects.add(tail);

        return recordObjects;
    }

}
