package io.github.spafka.rpc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import scala.util.control.Exception;

import java.nio.ByteBuffer;
@Slf4j
public class UnsafeTest {

    @Test
    public void byteArray(){

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(Integer.MAX_VALUE);

        byteBuffer.put(Integer.MAX_VALUE-1,(byte) 2);

        byteBuffer.get(Integer.MAX_VALUE-1);


    }

    @Test
    public void byteArray2(){


        log.warn("TotalMemory {}", Runtime.getRuntime().totalMemory()/(1024*1024)+"M");
        log.warn("FreeMemory {}", Runtime.getRuntime().freeMemory()/(1024*1024)+"M");
        log.warn("MaxMemory {}", Runtime.getRuntime().maxMemory()/(1024*1024)+"M");
        log.warn("UsedMemory {}" , (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024)+"M");

        ByteBuffer byteBuffers[]=new ByteBuffer[16];

        for (int i=0;i<5;i++){

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(Integer.MAX_VALUE-1);

            byteBuffer.put(Integer.MAX_VALUE-2,(byte) 2);

            byteBuffer.get(Integer.MAX_VALUE-2);
            byteBuffers[i]=byteBuffer;
        }


        System.out.println();

    }
}
