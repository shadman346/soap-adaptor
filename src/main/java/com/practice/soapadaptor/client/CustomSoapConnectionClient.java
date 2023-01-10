package com.practice.soapadaptor.client;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import lombok.extern.slf4j.Slf4j;

import javax.xml.soap.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.StringTokenizer;


@Slf4j
public class CustomSoapConnectionClient extends SOAPConnection {
    private MessageFactory messageFactory = null;
    private boolean closed = false;

    public CustomSoapConnectionClient() throws SOAPException {
        try {
            messageFactory = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
        } catch (NoSuchMethodError ex) {
            //fallback to default SOAP 1.1 in this case for backward compatibility
            messageFactory = MessageFactory.newInstance();
        } catch (Exception ex) {
            throw new SOAPExceptionImpl("Unable to create message factory", ex);
        }
    }
    private java.net.HttpURLConnection createConnection(URL endpoint)
            throws IOException {
        return (HttpURLConnection) endpoint.openConnection();
    }
    private byte[] readFully(InputStream istream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num = 0;

        while ((num = istream.read(buf)) != -1) {
            bout.write(buf, 0, num);
        }

        byte[] ret = bout.toByteArray();

        return ret;
    }
    SOAPMessage post(SOAPMessage message, URL endPoint) throws SOAPException, IOException{
        boolean isFailure = false;

        URL url = null;
        HttpURLConnection httpConnection = null;

        int responseCode = 0;
        try {
            // Process the URL
            URI uri = new URI(endPoint.toString());

            url = endPoint;


            // TBD
            //    Will deal with https later.
            if (!url.getProtocol().equalsIgnoreCase("http")
                    && !url.getProtocol().equalsIgnoreCase("https")) {
                log.warn("SAAJ0052.p2p.protocol.mustbe.http.or.https");
                throw new IllegalArgumentException(
                        "Protocol "
                                + url.getProtocol()
                                + " not supported in URL "
                                + url);
            }
            httpConnection = createConnection(url);

            httpConnection.setRequestMethod("POST");

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setInstanceFollowRedirects(true);
//            httpConnection.setConnectTimeout(CONNECT_TIMEOUT);
//            httpConnection.setReadTimeout(READ_TIMEOUT);

            if (message.saveRequired())
                message.saveChanges();

            MimeHeaders headers = message.getMimeHeaders();

            Iterator<?> it = headers.getAllHeaders();
            boolean hasAuth = false; // true if we find explicit Auth header
            while (it.hasNext()) {
                MimeHeader header = (MimeHeader) it.next();

                String[] values = headers.getHeader(header.getName());
                if (values.length == 1)
                    httpConnection.setRequestProperty(
                            header.getName(),
                            header.getValue());
                else {
                    StringBuilder concat = new StringBuilder();
                    int i = 0;
                    while (i < values.length) {
                        if (i != 0)
                            concat.append(',');
                        concat.append(values[i]);
                        i++;
                    }

                    httpConnection.setRequestProperty(
                            header.getName(),
                            concat.toString());
                }

                if ("Authorization".equals(header.getName())) {
                    hasAuth = true;
//                    if (log.isLoggable(Level.FINE))
//                        log.fine("SAAJ0091.p2p.https.auth.in.POST.true");
                }
            }

            OutputStream out = httpConnection.getOutputStream();
            ByteArrayOutputStream streamFilter = new ByteArrayOutputStream();
            StringWriter sw = new StringWriter();
            CustomCharacterEscapeHandler customCharacterEscapeHandler = new CustomCharacterEscapeHandler();
            try {
                message.writeTo(streamFilter);
                String streamFilterStr = streamFilter.toString();
                char[] buff = streamFilterStr.toCharArray();
                customCharacterEscapeHandler.escape(buff,0,streamFilterStr.length(),false,sw);
                out.write(sw.toString().getBytes(Charset.forName("UTF-8")));
                out.flush();
            } finally {
                out.close();
                sw.close();
                streamFilter.close();
            }
            log.info("Request soap message after additional parsing that sent to soap client:\n {}", out.toString());
            httpConnection.connect();

            try {

                responseCode = httpConnection.getResponseCode();

                // let HTTP_INTERNAL_ERROR (500) and HTTP_BAD_REQUEST (400) through because it is used for SOAP faults
                if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR || responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    isFailure = true;
                }
                //else if (responseCode != HttpURLConnection.HTTP_OK)
                //else if (!(responseCode >= HttpURLConnection.HTTP_OK && responseCode < 207))
                else if ((responseCode / 100) != 2) {
                    throw new SOAPExceptionImpl(
                            "Bad response: ("
                                    + responseCode
                                    + httpConnection.getResponseMessage());

                }
            } catch (IOException e) {
                // on JDK1.3.1_01, we end up here, but then getResponseCode() succeeds!
                responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR || responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    isFailure = true;
                } else {
                    throw e;
                }

            }

        } catch (SOAPException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SOAPExceptionImpl("Message send failed", ex);
        }

        SOAPMessage response = null;
        InputStream httpIn = null;
        if (responseCode == HttpURLConnection.HTTP_OK || isFailure) {
            try {

                MimeHeaders headers = new MimeHeaders();

                String key, value;

                // Header field 0 is the status line so we skip it.

                int i = 1;

                while (true) {
                    key = httpConnection.getHeaderFieldKey(i);
                    value = httpConnection.getHeaderField(i);

                    if (key == null && value == null)
                        break;

                    if (key != null) {
                        StringTokenizer values =
                                new StringTokenizer(value, ",");
                        while (values.hasMoreTokens())
                            headers.addHeader(key, values.nextToken().trim());
                    }
                    i++;
                }

                httpIn =
                        (isFailure
                                ? httpConnection.getErrorStream()
                                : httpConnection.getInputStream());

                byte[] bytes = readFully(httpIn);

                int length =
                        httpConnection.getContentLength() == -1
                                ? bytes.length
                                : httpConnection.getContentLength();

                // If no reply message is returned,
                // content-Length header field value is expected to be zero.
                if (length == 0) {
                    response = null;
                    log.error("SAAJ0014.p2p.content.zero");
                } else {
                    ByteInputStream in = new ByteInputStream(bytes, length);
                    response = messageFactory.createMessage(headers, in);
                }

            } catch (SOAPException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new SOAPExceptionImpl(
                        "Unable to read response: " + ex.getMessage());
            } finally {
                if (httpIn != null)
                    httpIn.close();
                httpConnection.disconnect();
            }
        }
        return response;
    }

    @Override
    public SOAPMessage call(SOAPMessage message, Object endPoint) throws SOAPException {
        if (closed) {
            log.error("SAAJ0003.p2p.call.already.closed.conn");
            throw new SOAPExceptionImpl("Connection is closed");
        }

        if (endPoint instanceof java.lang.String) {
            try {
                endPoint = new URL((String) endPoint);
            } catch (MalformedURLException mex) {
                throw new SOAPExceptionImpl("Bad URL: " + mex.getMessage());
            }
        }

        if (endPoint instanceof URL)
            try {
                SOAPMessage response = post(message, (URL)endPoint);
                return response;
            } catch (Exception ex) {
                // TBD -- chaining?
                throw new SOAPExceptionImpl(ex);
            } else {
            throw new SOAPExceptionImpl("Bad endPoint type " + endPoint);
        }
    }

    @Override
    public void close() throws SOAPException {
        if (closed) {
            log.error("SAAJ0002.p2p.close.already.closed.conn");
            throw new SOAPExceptionImpl("Connection already closed");
        }

        messageFactory = null;
        closed = true;
    }
}
