package org.dk.test;

public class changeSubCharacter {
    /* 보조 캐릭터 변경 */
    public static String[] make()
    {
        String select = "캐릭터선택_5번"; //혈귀
        String returnHome = "9번키"; //혈귀
        String texts[]={
                "메뉴선택",
                "버튼_재시작",
                select,
                "캐릭터선택_입장",
                "대기_30초",
                returnHome,
                "대기_5초"
        };

        return texts;
    }

}
