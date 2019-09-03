package io.github.spafka.stream;


import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class internal {

    public static void main(String[] args) {

        List<String> strings = Arrays.asList("deep dark deep dark van shit");

        Map<String, List<Tuple2<String, Integer>>> listMap = strings.stream().
                flatMap(x -> Stream.of(x.split(" ")))
                .map(x -> new Tuple2<String, Integer>(x, 1))
                .collect(Collectors.groupingBy(x -> x.f0));

        listMap.forEach((k, v) -> {
            Tuple2<String, Integer> x = v.stream().reduce((a, b) -> new Tuple2<>(a.f0, a.f1 + b.f1)).get();
            // emit
            System.out.println(x);
        });


    }
}
