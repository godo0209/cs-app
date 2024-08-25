package com.example;

//conexion
    import java.net.URI;
    import org.apache.http.HttpEntity;
    import org.apache.http.client.methods.CloseableHttpResponse;
    import org.apache.http.client.methods.HttpDelete;
    import org.apache.http.client.methods.HttpGet;
    import org.apache.http.client.methods.HttpPost;
    import org.apache.http.client.methods.HttpPut;
    import org.apache.http.entity.StringEntity;
    import org.apache.http.impl.client.CloseableHttpClient;
    import org.apache.http.impl.client.HttpClients;
//excepciones
    import java.net.URISyntaxException;
    import java.io.IOException;

//lectura
    import java.io.InputStreamReader;
    import java.io.Reader;
//aux
    import org.json.JSONObject;
    import org.json.JSONArray;

public class Conexion {
    private final String url;
    
    public Conexion(String url){
        this.url = url;
    }
    public JSONObject sendGet() {
        JSONObject res = null;

        try{
            CloseableHttpClient client = HttpClients.createDefault();
            
            HttpGet e = new HttpGet(new URI(this.url));
            CloseableHttpResponse response = client.execute(e);
            
            res = getJsonResponse(response);
            client.close();
        }catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }
        return res;
    }
    public JSONArray sendGet2(){
        JSONArray res = null;

        try{
            CloseableHttpClient client = HttpClients.createDefault();
            
            HttpGet e = new HttpGet(new URI(this.url));
            CloseableHttpResponse response = client.execute(e);
            
            res = getJsonResponse2(response);
            client.close();
        }catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }
        return res;
    }
    public JSONObject sendPost(JSONObject json){
        JSONObject res = null;

        try{
            CloseableHttpClient client = HttpClients.createDefault();
            
            HttpPost e = new HttpPost(new URI(this.url));
            e.setEntity(new StringEntity(json.toString()));
            e.setHeader("Accept", "application/json");
            e.setHeader("Content-type", "application/json");
            
            CloseableHttpResponse response = client.execute(e);
            
            res = getJsonResponse(response);
            client.close();
        }catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }
        return res;
    }
    public JSONObject sendPut(JSONObject json){
        JSONObject res = null;

        try{
            CloseableHttpClient client = HttpClients.createDefault();
            
            HttpPut e = new HttpPut(new URI(this.url));
            e.setEntity(new StringEntity(json.toString()));
            e.setHeader("Accept", "application/json");
            e.setHeader("Content-type", "application/json");
            
            CloseableHttpResponse response = client.execute(e);
            
            res = getJsonResponse(response);
            client.close();
        }catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }
        return res;
    }
    public JSONObject sendDelete(){
        JSONObject res = null;

        try{
            CloseableHttpClient client = HttpClients.createDefault();
            
            HttpDelete e = new HttpDelete(new URI(this.url));
            CloseableHttpResponse response = client.execute(e);
            
            res = getJsonResponse(response);
            client.close();
        }catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }
        return res;
    }
    private JSONObject getJsonResponse(CloseableHttpResponse response) {
        JSONObject res = new JSONObject();
        HttpEntity responseAux = response.getEntity();

        String readResponse = "";

        if(responseAux != null){
            try{
                Reader reader = new InputStreamReader(responseAux.getContent());
                while(true){
                    int chr = reader.read();
                    if(chr == -1)
                        break;
                    readResponse += (char) chr;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        if(readResponse != null)
            res = new JSONObject(readResponse);
        return res;
    }
    private JSONArray getJsonResponse2(CloseableHttpResponse response){
        JSONArray res = new JSONArray();
        HttpEntity responseAux = response.getEntity();

        String readResponse = "";

        if(responseAux != null){
            try{
                Reader reader = new InputStreamReader(responseAux.getContent());
                while(true){
                    int chr = reader.read();
                    if(chr == -1)
                        break;
                    readResponse += (char) chr;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        if(readResponse != null)
            res = new JSONArray(readResponse);
        return res;
    }
}
