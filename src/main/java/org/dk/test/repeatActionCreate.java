package org.dk.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class repeatActionCreate {
//반복부만들기
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode events = mapper.createArrayNode();

        // 1번 세트 설정 (시작 시간)
        long startMs1 = 11900;  // type 513
        long startMs2 = 12000;  // type 514
        
        // 간격 (3초)
        long interval = 3000;

        // 2번 세트의 기준 시간 (이 시간을 넘으면 안됨)
        long targetLimit = 110000;

        long currentMs1 = startMs1;
        long currentMs2 = startMs2;

        // 1번 세트의 마지막(currentMs2)이 targetLimit보다 작은 동안 반복
        while (currentMs2 < targetLimit) {
            // 첫 번째 이벤트 (Type 513)
            ObjectNode event1 = mapper.createObjectNode();
            ObjectNode pos1 = mapper.createObjectNode();
            pos1.put("x", "0.66078186");
            pos1.put("y", "0.863228679");
            
            event1.set("position", pos1);
            event1.put("ms", String.valueOf(currentMs1));
            event1.put("type", "513");
            event1.put("detail", "1374304");
            event1.put("data", "0");
            
            events.add(event1);

            // 두 번째 이벤트 (Type 514)
            ObjectNode event2 = mapper.createObjectNode();
            ObjectNode pos2 = mapper.createObjectNode();
            pos2.put("x", "0.66078186");
            pos2.put("y", "0.863228679");
            
            event2.set("position", pos2);
            event2.put("ms", String.valueOf(currentMs2));
            event2.put("type", "514");
            event2.put("detail", "1374304");
            event2.put("data", "0");
            
            events.add(event2);

            // 시간 증가
            currentMs1 += interval;
            currentMs2 += interval;
        }

        try {
            // 결과 출력
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
