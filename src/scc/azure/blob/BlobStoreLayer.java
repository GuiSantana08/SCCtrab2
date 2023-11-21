package scc.azure.blob;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import jakarta.ws.rs.NotFoundException;

import javax.ws.rs.BadRequestException;

public class BlobStoreLayer {

    static String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=scc60353;AccountKey=g5h9LhplKvWgDtLtLyLzfPwxpwaxir3abHxIj/nDh0dZAibIDsEMrpCxdn+PfxrrTAyfobZ50oaS+AStH1uIVw==;EndpointSuffix=core.windows.net";

    private static BlobStoreLayer instance;

    private String path;

    public static synchronized BlobStoreLayer getInstance() {
        if (instance != null)
            return instance;

        String volumePath = "/mnt/vol";
        instance = new BlobStoreLayer(volumePath);
        return instance;
    }

    private BlobContainerClient client;

    public BlobStoreLayer(String path) {
        this.path = path;
    }

    public void upload(String name, byte[] image) {
       try{
        String blobPath = path + "/" + name;
        File file = new File(blobPath);
        file.createNewFile();

           FileOutputStream stream = new FileOutputStream(file);
           stream.write(image);
           stream.close();
       }catch (Exception e){
           throw new BadRequestException("Error uploading image " + name);
       }

      /*  BlobClient blob = client.getBlobClient(key);
        blob.upload(BinaryData.fromBytes(image));*/
    }

    public byte[] download(String name) {
        String blobPath = path + "/" + name;
        File blob = new File(blobPath);
        byte[] data = new byte[(int) blob.length()];
        if (blob.exists()) {
            try {
                data = new byte[(int) blob.length()];
                DataInputStream stream = new DataInputStream(new FileInputStream(blob));
                stream.readFully(data);
                stream.close();
            } catch (Exception e) {
                throw new NotFoundException("Error downloading image: " + name);
            }
        } else
            throw new NotFoundException("Image not found: " + name);

        return data;
    }
      /*  BlobClient blob = client.getBlobClient(key);
        BinaryData data = blob.downloadContent();
        return data.toBytes();*/
    }

    public boolean delete(String name) {
        String filePath = path + "/" + name;
        File fileToDelete = new File(filePath);
        try{
            return fileToDelete.delete();
        }catch (Exception e) {
            throw new BadRequestException("Error deleting image: " + name);
        }

       /* BlobClient blob = client.getBlobClient(key);
        boolean isDeleted = blob.deleteIfExists();
        return isDeleted;*/
    }

    public List<String> list() {
        return Stream.of(new File(path).listFiles())
                .map(File::getName)
                .collect(Collectors.toList());
        /*List<BlobItem> blobs = client.listBlobs().stream().collect(Collectors.toList());
        return blobs.stream().map(BlobItem::getName).collect(Collectors.toList());*/
    }
}
