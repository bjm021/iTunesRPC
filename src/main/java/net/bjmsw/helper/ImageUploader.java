package net.bjmsw.helper;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ImageUploader {

    public static String uploadImage(BufferedImage img, String name) throws  IOException {
        String url = "https://rpc.bjmsw.net/";

        // upload image via form-data
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] bytes = baos.toByteArray();

            builder.addBinaryBody(
                    "image",
                    bytes,
                    org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM,
            name + ".png"
            );

            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            CloseableHttpResponse response = httpClient.execute(uploadFile);

            String res = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
            System.out.println("[ImageUploader] Uploaded " + res);

            return res;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage img = ImageIO.read(new File(".\\tmp.png"));
        uploadImage(img, "Teds`!^stAüä ü2lbum");
    }


}
