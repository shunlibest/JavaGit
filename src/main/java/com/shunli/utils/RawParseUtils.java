package com.shunli.utils;


import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RawParseUtils {
    private static final byte[] digits10;

    static {
        digits10 = new byte['9' + 1];
        Arrays.fill(digits10, (byte) -1);
        for (char i = '0'; i <= '9'; i++)
            digits10[i] = (byte) (i - '0');
    }

    /**
     * 找到父对象的位置 parent开头
     */
    public static int matchParents(byte[] buf, int start) {
        final int end = buf.length;
        while (start < end) {
            if (buf[start] == 'p' && buf[start + 1] == 'a' && buf[start + 2] == 'r' &&
                    buf[start + 3] == 'e' && buf[start + 4] == 'n' && buf[start + 5] == 't' && buf[start + 6] == ' ') {
                return start + 7;
            }
            start = nextLF(buf, start);
        }
        return 0;
    }

    /**
     * 找到提交消息正文的位置。
     *
     * @param b   缓冲区数据
     * @param ptr 缓冲区中开始扫描的位置。一般情况都是从0开始
     * @return 用户消息缓冲区的位置
     */
    public static int commitMessage(byte[] b, int ptr) {
        final int sz = b.length;
        if (ptr == 0)
            ptr += 46; // skip the "tree ..." line.
        while (ptr < sz && b[ptr] == 'p')
            ptr += 48; // skip this parent.

        // 跳过任何剩余的标题行
        return tagMessage(b, ptr);
    }

    // 找到标签消息体的位置。
    public static int tagMessage(byte[] b, int ptr) {
        final int sz = b.length;
        if (ptr == 0)
            ptr += 48; // skip the "object ..." line.
        // Assume the rest of the current paragraph is all headers.
        while (ptr < sz && b[ptr] != '\n')
            ptr = nextLF(b, ptr);
        if (ptr < sz && b[ptr] == '\n')
            return ptr + 1;
        return -1;
    }

    // 找到下一个 \n 后停止
    public static int nextLF(byte[] b, int ptr) {
        return next(b, ptr, '\n');
    }

    // 找到给定字符后的第一个位置
    public static int next(byte[] b, int ptr, char chrA) {
        final int sz = b.length;
        while (ptr < sz) {
            if (b[ptr++] == chrA)
                return ptr;
        }
        return ptr;
    }

    /**
     * 解码缓冲区数据, 使用UTF-8编码
     */
    public static String decode(final byte[] buffer, final int start, final int end) {
        ByteBuffer b = ByteBuffer.wrap(buffer, start, end - start);
        b.mark();
        return decode(b, UTF_8);
    }

    private static int parseTimeZoneOffset(final byte[] b, int ptr, MutableInteger ptrResult) {
        final int v = parseBase10(b, ptr, ptrResult);
        final int tzMins = v % 100;
        final int tzHours = v / 100;
        return tzHours * 60 + tzMins;
    }

    private static int parseBase10(final byte[] b, int ptr, final MutableInteger ptrResult) {
        int r = 0;
        int sign = 0;
        try {
            final int sz = b.length;
            while (ptr < sz && b[ptr] == ' ')
                ptr++;
            if (ptr >= sz)
                return 0;

            switch (b[ptr]) {
                case '-':
                    sign = -1;
                    ptr++;
                    break;
                case '+':
                    ptr++;
                    break;
            }

            while (ptr < sz) {
                final byte v = digits10[b[ptr]];
                if (v < 0)
                    break;
                r = (r * 10) + v;
                ptr++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Not a valid digit.
        }
        if (ptrResult != null)
            ptrResult.value = ptr;
        return sign < 0 ? -r : r;
    }


    private static String decode(ByteBuffer b, Charset charset) {
        final CharsetDecoder d = charset.newDecoder();
        d.onMalformedInput(CodingErrorAction.REPORT);
        d.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return d.decode(b).toString();
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static final class MutableInteger {
        public int value;
    }


}
