package util;

import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static util.IndexToken.requestSplit;


class IndexTokenTest {


    @Test
    void splitTest() {
        String line = "GET /index.html HTTP/1.1";
        assertThat(requestSplit(line)).isEqualTo("/index.html");
    }


}