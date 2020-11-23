package kr.co.minzero.divvyup.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DivvyupUtiltiyTest {

    @Test
    public void splitMoneyTest() {
        long[] splitMoney = DivvyupUtility.splitMoney(10000, 10);
        long sum = 0L;
        for(long split: splitMoney) {
            sum += split;
        }
        assertThat(splitMoney.length).isEqualTo(10);
        assertThat(sum).isEqualTo(10000L);
    }
}
