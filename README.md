# backendTest

The application consists 1 controller with four main methods. For local testing, please use port 9090.

Requirments:

1.For the initial run, you have to run a put request,  /monitorApp/configuration first. Note: the parameters in requestbody should meet following rules:
 Interval is a  postive integer, and url meets the format as : https://api.[a-zA-Z0-9]{2,}.paysafe.com/alternatepayments/v[1-9]/monitor
 For example: {
  "interval": 0,
  "url": "https://api.test.paysafe.com/alternatepayments/v1/monitor"
}
"interval" is monitoring period, measure unit is "minute".
"START" means start monitoring and "STOP" means STOP monitoring.

2. All the methods need URL for processing, incorrect url will return different mesaages.

3. A monitor process can be started only if: 1) This server is not being monitored currently
  2) The parameters are set correctly as step 1 mentioned.
4. Every re-start monitoring will cause the previous records removed.

5. The application only ingest the value which is not the same the last ingested value.
    Eg. The last record is "READY": "Tue Nov 19 20:44:22 EST 2019",
    if the status of server is still "READY", then no new records will be taken. If the status is "DOWN", then, one more records, "DOWN": "CURRENT FETCHING TIME",  will be ingested.
6. Delete history data of a existing server is allowed, however, you have to stop monitoring it before you try to delete it.

7. GET /monitorApp/configuration api is simply return whether the server is being monitored or not.

8. The /monitorApp/test api is used for testing multiple server monitoring. Replace the url in step 1 with http://localhost:9090/monitorApp/test , it will start monitoring a dummy server. This server will return "READY","DOWN" randomly. Compareed with Paysaft server, it can show the change of server more easily. NOTE: You have to disable URL validation in restful controller, it has to be donw by changing code, unfortunetly.
