package org.dk.test;

public class changeMainCharacter {
    /* 본캐릭 13번째 선택 */
    public static String[] make()
    {
        String returnHome = "button_9"; //혈귀
        String texts[]={
                "choose_menu",
                "button_restart",
                "changeChar_scrolldown",
                "changeChar_13",
                "changeChar_enter",
                "wait_sec_30",
                returnHome,
                "wait_sec_5"
        };

        return texts;
    }

}
