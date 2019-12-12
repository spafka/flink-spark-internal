package io.github.spafka.rpc;

import io.github.spafka.spark.util.Utils;
import scala.runtime.AbstractFunction0;

public class CpuTests {


    public static void main(String[] args) {


        System.out.println( Utils.timeTakenMs(new AbstractFunction0<Void>() {
            @Override
            public Void apply() {

                long a=0;
                for (int i=0;i<Integer.MAX_VALUE;i++){

                    a++;
                }
                return null;
            }
        }));


    }
}
