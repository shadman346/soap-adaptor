package com.practice.soapadaptor.client;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class CustomCharacterEscapeHandler implements CharacterEscapeHandler {
    @Override
    public void escape(char[] buf, int start, int len, boolean isAttValue, Writer out) throws IOException {
        if (buf != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < start + len; i++) {
                char ch = buf[i];

                //by adding these, it prevent the problem happened when unmarshalling
                if (ch == '&') {
                    sb.append("&amp;");
                    continue;
                }

                if (ch == '"' && isAttValue) {
                    sb.append("&quot;");
                    continue;
                }

                if (ch == '\'' && isAttValue) {
                    sb.append("&apos;");
                    continue;
                }


                // otherwise print normally
                sb.append(ch);
            }

            //Make corrections of unintended changes
            String st = sb.toString();

            st = st.replace("&amp;quot;", "&quot;")
                    .replace("&amp;lt;", "&lt;")
                    .replace("&amp;gt;", "&gt;")
                    .replace("&amp;apos;", "&apos;")
                    .replace("&amp;amp;", "&amp;");

            out.write(st);
        }
    }
}

