package kr.co.minzero.divvyup.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * 공통적으로 사용되는 유틸리티 모음
 */
public class DivvyupUtility {

    /**
     * 3자리 토큰을 생성하기 위한 로직 apache-common3 의 RandomStringUtils 를 이용
     *
     * @return a-z,A-Z,0-9 를 조합한 3자리 문자열 값
     */
    public static String createToken() {
        return RandomStringUtils.randomAlphanumeric(3);
    }

    /**
     * 금액을 나눠주는 로직
     *
     * @param money 뿌린 금액
     * @param count 분배 인원수
     * @return 나눠진 금액 배
     */
    public static long[] splitMoney(long money, int count) {
        long[] moneyList = new long[count];
        for(int i = 0; i < count - 1; i++) {
            // 랜덤으로 나누고 10원 단위 절삭
            long split = (Math.round(money / (new Random().nextInt(count)+1.1F)) / 10) * 10 ;
            money -= split;
            moneyList[i] = split;
        }
        moneyList[count-1] = money;

        return moneyList;
    }
}
