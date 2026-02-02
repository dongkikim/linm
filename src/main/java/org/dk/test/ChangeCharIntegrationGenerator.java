package org.dk.test;

import org.dk.JsonMergerMultiple;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeCharIntegrationGenerator {

    private static final String MACRO_PATH = "src/main/resources/macro/";

    public static void main(String[] args) {
        try {
            for (int i = 1; i <= 13; i++) {
                String[] scripts = makeScripts(i);
                String outputFileName = "changeChar_" + i + "_integration";
                String title = "캐릭터변경 " + i + "번캐릭선택 통합";
                String outputFile = MACRO_PATH + outputFileName + ".json";

                List<String> pathList = Arrays.stream(scripts)
                    .map(JsonMergerMultiple::makePath)
                    .collect(Collectors.toList());

                JsonMergerMultiple.mergeJsonFiles(pathList, outputFile, title, "1");
                System.out.println("생성 완료: " + outputFileName + ".json (title: " + title + ")");
            }
            System.out.println("\n=== 총 13개 integration 파일 생성 완료 ===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] makeScripts(int charNum) {
        String returnHome = "button_9";
        List<String> scripts = new ArrayList<>();

        scripts.add("choose_menu");
        scripts.add("button_restart");

        scripts.add("changeChar_scrollup");
        // 스크롤 조건 분기
        if (charNum >= 1 && charNum <= 5) {
            // 1~5: 스크롤 없음
        } else if (charNum >= 6 && charNum <= 8) {
            // 6~8: scrollmiddle
            scripts.add("changeChar_scrollmiddle");
        } else {
            // 9~13: scrolldown
            scripts.add("changeChar_scrolldown");
        }

        scripts.add("changeChar_" + charNum);
        scripts.add("changeChar_enter");
        scripts.add("wait_sec_30");
        scripts.add(returnHome);
        scripts.add("wait_sec_5");

        return scripts.toArray(new String[0]);
    }
}
