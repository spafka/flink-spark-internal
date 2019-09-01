package io.github.spafka.test;

import io.github.spafka.spark.util.Utils;
import scala.Tuple2;
import scala.runtime.AbstractFunction0;

import java.io.*;

public class GenerateFile {


    public static void main(String[] args) throws IOException {
        int totalLine = Integer.MAX_VALUE;

        String fileName="F://hadoopfile.txt";
        if ((args.length!=0 ) && args[0].equals("")) {
            fileName=args[0];
        }
        File file = new File(fileName);

        Tuple2<Void, Object> timeTakenMs = Utils.timeTakenMs(new AbstractFunction0<Void>() {
            @Override
            public Void apply() {
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(file));

                    for (int i = 0; i < totalLine; i++) {
                        bos.write("1".getBytes());

                    }

                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        });

        System.out.println(timeTakenMs);


    }

}
