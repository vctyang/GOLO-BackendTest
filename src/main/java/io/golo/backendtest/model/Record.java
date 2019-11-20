package io.golo.backendtest.model;

// Hold the status and the time it was set.
public class Record {
    public String status;
    public String timeStamp;
    public  Record(String status,String timeStamp){
        this.status = status;
        this.timeStamp = timeStamp;
    }
    @Override
    public String toString() {
        return  "{".concat("\"").concat(status).concat("\":").concat("\"").concat(timeStamp).concat("\"}");
    }

}
