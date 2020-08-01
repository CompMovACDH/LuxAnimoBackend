
package com.lsdi.social.mhealth.util;

import com.lsdi.social.mhealth.handler.EventHandler;
import com.lsdi.social.mhealth.model.SocialEvent;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import org.json.simple.JSONArray; 
//import org.json.simple.JSONObject; 
//import org.json.simple.parser.*;

import java.util.Arrays;
import org.json.JSONObject;
import java.time.*;


//@Component
public class StreamReceiver implements MqttCallback {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(StreamReceiver.class);

    //private int day =50;

    private ExecutorService xrayExecutor = Executors.newSingleThreadExecutor();

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    private String topic;      //  = "social";
    private int qos = 2;
    //private String broker       = "tcp://iot.eclipse.org:1883";
    //private String broker       = "tcp://lsdi.ufma.br:1883";
    private String broker;       //= "tcp://127.0.0.1:1883"; //
    private String clientId = "ivan.rodrigues";
    private MemoryPersistence persistence = new MemoryPersistence();
    public static  MqttClient client;
    //@Autowired
    private EventHandler eventHandler = EventHandler.getInstance();

    public void receiverStream(){
        try {
            client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            System.out.println("Connecting to broker: "+broker);
            client.connect(options);
            System.out.println("Connected");
            client.setCallback(this);
            client.subscribe(topic);
            //String conteudo = "1900-07-23 00:05:14";
            //MqttMessage messagem = new MqttMessage(conteudo.getBytes());
            //client.publish(topic, messagem);
            
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }

    }

    @Override
    public void connectionLost(Throwable throwable) {}
    
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {    	 	
    	
    	/*String mqttJson = mqttMessage.toString(); 
		String[] arrOfStr = mqttJson.split("[\":,]");     
		Long timeUnix = Long.valueOf(arrOfStr[Arrays.asList(arrOfStr).indexOf("publicationTimestamp")+2]);
		String timeUnix = arrOfStr[Arrays.asList(arrOfStr).indexOf("publicationTimestamp")+2];
		Date date = new Date(timeUnix);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String timeStamp = formatter.format(date);
   		System.out.println(timeStamp);*/   
    	
        xrayExecutor.submit(new Runnable() { 
            public void run() {
            	Date date = null;           
                SocialEvent socialEvent;
                /*DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");         
                try {                
                    start_date = formatter.parse(String.valueOf(mqttMessage));
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                /*try { 
                	start_date = formatter.parse(String.valueOf(mqttMessage));
	        		//System.out.println(start_date);        
                } catch (ParseException e) {
                	e.printStackTrace();
                }*/         
                try {
                	String mqttJson = mqttMessage.toString(); 
            		String[] arrOfStr = mqttJson.split("[\":,]");     
            		Long timeUnix = Long.valueOf(arrOfStr[Arrays.asList(arrOfStr).indexOf("publicationTimestamp")+2]);
        	  	    date = new Date(timeUnix);
        	    }catch(Exception e) {	    
        	    	e.printStackTrace(System.out);
        	    }
                //socialEvent = new SocialEvent("u08","Interaction","", start_date ,new Date(),(double)0.0);
                socialEvent = new SocialEvent("u08","Interaction","", date,new Date(),(double)0.0);
                eventHandler.handle(socialEvent);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    LOG.error("Thread Interrupted", e);
                }
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
    //read data u00
    public void startSending() {
        //receiverStream();
    /*
        ExecutorService xrayExecutor = Executors.newSingleThreadExecutor();

        xrayExecutor.submit(new Runnable() {
            public void run() {

               try (BufferedReader br = new BufferedReader(new FileReader(ParametersUtil.PATH))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        Date start_date = null;
                        SocialEvent socialEvent;
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            start_date = formatter.parse(String.valueOf(line));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        socialEvent = new SocialEvent("u08","Interaction","",start_date,new Date(),(double)0.0);

                        eventHandler.handle(socialEvent);

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });*/
    }

    /**
     *  Convert unix for date
     * @param unix_timestamp
     * @return date
    private Date unixToDate(String unix_timestamp) throws ParseException {
    Date date = new java.util.Date(Long.parseLong(unix_timestamp)*1000L);
    SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    sdf.setTimeZone(TimeZone.getTimeZone("UTF-5"));
    String formattedDate = sdf.format(date);
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date startDate = formatter.parse(formattedDate);
    return startDate;
    }*/
}
