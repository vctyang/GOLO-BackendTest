package io.golo.backendtest.model;

// Payload for pass parameters of setting
public class Payload {

    /**
     * Default constructor, empty for Json serializer
     */
    private String url;
    private int interval;
    public  Payload(){
        interval = 1;
        url = "https://api.test.paysafe.com/alternatepayments/v1/monitor";// Default constructor, empty for Json serializer
    }
    public String getUrl() { return url;}
    public int getInterval(){return interval;}
    public void setInterval(int interval){ this.interval = interval;}
    public void setUrl(String url){ this.url = url;}

}
