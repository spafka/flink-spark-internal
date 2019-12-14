package io.github.spafka.mesos;

import java.nio.ByteBuffer;

public class MemoryTest {

    public static void main(String[] args) {

        int allocated = Integer.MAX_VALUE - 3;

        if (args == null || args.length == 0) {


        }else {
            String arg = args[0];

            if (arg.endsWith("m")){
                String[] mb = arg.split("m");
                allocated= Integer.parseInt(mb[0]) * 1024 * 1024;
            }
        }

        ByteBuffer.allocateDirect(allocated);


    }
}
