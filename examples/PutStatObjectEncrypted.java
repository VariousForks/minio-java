import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.ServerSideEncryption;
import io.minio.errors.MinioException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import org.xmlpull.v1.XmlPullParserException;

public class PutStatObjectEncrypted {
  /** MinioClient.putObject() and MinioClient.statObject() example for SSE_C. */
  public static void main(String[] args)
      throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException {
    try {
      /* play.min.io for test and development. */
      MinioClient minioClient =
          new MinioClient(
              "https://play.min.io",
              "Q3AM3UQ867SPQQA43P2F",
              "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");

      /* Amazon S3: */
      // MinioClient minioClient = new MinioClient("https://s3.amazonaws.com", "YOUR-ACCESSKEYID",
      //

      // Create some content for the object.
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < 10; i++) {
        builder.append(
            "Sphinx of black quartz, judge my vow: Used by Adobe InDesign to display font samples. ");
        builder.append("(29 letters)\n");
        builder.append(
            "Jackdaws love my big sphinx of quartz: Similarly, used by Windows XP for some fonts. ");
        builder.append("(31 letters)\n");
        builder.append(
            "Pack my box with five dozen liquor jugs: According to Wikipedia, this one is used on ");
        builder.append("NASAs Space Shuttle. (32 letters)\n");
        builder.append(
            "The quick onyx goblin jumps over the lazy dwarf: Flavor text from an Unhinged Magic Card. ");
        builder.append("(39 letters)\n");
        builder.append(
            "How razorback-jumping frogs can level six piqued gymnasts!: Not going to win any brevity ");
        builder.append("awards at 49 letters long, but old-time Mac users may recognize it.\n");
        builder.append(
            "Cozy lummox gives smart squid who asks for job pen: A 41-letter tester sentence for Mac ");
        builder.append("computers after System 7.\n");
        builder.append(
            "A few others we like: Amazingly few discotheques provide jukeboxes; Now fax quiz Jack! my ");
        builder.append("brave ghost pled; Watch Jeopardy!, Alex Trebeks fun TV quiz game.\n");
        builder.append("---\n");
      }

      // Create a InputStream for object upload.
      ByteArrayInputStream bais = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));

      // Generate a new 256 bit AES key - This key must be remembered by the client.
      KeyGenerator keyGen = KeyGenerator.getInstance("AES");
      keyGen.init(256);
      // To test SSE-C
      ServerSideEncryption sse = ServerSideEncryption.withCustomerKey(keyGen.generateKey());

      PutObjectOptions options = new PutObjectOptions(bais.available(), -1);
      options.setSse(sse);
      minioClient.putObject("my-bucketname", "my-objectname", bais, options);

      bais.close();

      System.out.println("my-objectname is encrypted and uploaded successfully");

      // Get the metadata of the object.
      ObjectStat objectStat = minioClient.statObject("my-bucketname", "my-objectname", sse);

      System.out.println("my-objectname metadata: ");
      System.out.println(objectStat);

    } catch (MinioException e) {
      System.out.println("Error occurred: " + e);
    }
  }
}
