import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;


public class Main {

    public static ObjectMapper mapper = new ObjectMapper();
    public static final String REMOTE_URL =
            "https://api.nasa.gov/planetary/apod?api_key=6SuHMEDfnWBEhtTMttVBcFWSiE5EYqRR8r3so5qs";

    public static void main(String[] args) throws IOException {

        final CloseableHttpClient httpClient = HttpClients.createDefault();

        final HttpGet request1 = new HttpGet(REMOTE_URL);

        CloseableHttpResponse response1 = httpClient.execute(request1);
        ApiNasaFile apiNasaFile = mapper.readValue(
                response1.getEntity().getContent(), new TypeReference<>() {
                });

        System.out.println(apiNasaFile.getUrl());

        String[] splitUrl = apiNasaFile.getUrl().split("/");
        String fileName = splitUrl[splitUrl.length - 1];

        final HttpGet request2 = new HttpGet(apiNasaFile.getUrl());

        CloseableHttpResponse response2 = httpClient.execute(request2);

        try (InputStream is = new DataInputStream(response2.getEntity().getContent());
             FileOutputStream fos = new FileOutputStream(fileName)) {

            byte[] buffer = is.readAllBytes();

            fos.write(buffer, 0, buffer.length);
            System.out.println("The file " + fileName + " has been written");

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        response1.close();
        response2.close();
        httpClient.close();
    }
}
