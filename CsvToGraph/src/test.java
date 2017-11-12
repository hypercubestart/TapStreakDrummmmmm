import java.nio.ByteBuffer;

/**
 * Created by Andy on 11/12/2017.
 */
public class test {
    public static void main(String[] args) {

        byte[] bytes = ByteBuffer.allocate(4).putInt(1000).array();

        for (byte b : bytes) {
            System.out.format("0x%x ", b);
        }
    }
}
