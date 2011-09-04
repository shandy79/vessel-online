package org.vesselonline.semantic.ws.ols;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class BioMartRESTClient {
  public static final String CHARSET = "UTF-8";

  private String url;
  private String xmlStr;

  public BioMartRESTClient(String url) {
    setURL(url);
  }

  public String getURL() { return url; }
  public void setURL(String url) { this.url = url; }

  public String getXMLStr() { return xmlStr; }
  public void setXMLStr(String xmlStr) { this.xmlStr = xmlStr; }

  // URLConnection usage based on code from:
  // http://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests
  public String runQuery(String xmlStr) {
    setXMLStr(xmlStr);
    StringBuilder result = new StringBuilder();

    try {
      String query = "query=" + URLEncoder.encode(getXMLStr(), CHARSET);

      URLConnection connection = new URL(getURL()).openConnection();
      connection.setDoOutput(true);  // Triggers POST
      connection.setRequestProperty("Accept-Charset", CHARSET);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
      OutputStream output = null;

      try {
        output = connection.getOutputStream();
        output.write(query.getBytes(CHARSET));
      } finally {
        if (output != null) try { output.close(); } catch (IOException logOrIgnore) { ; }
      }

      InputStream response = connection.getInputStream();

      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new InputStreamReader(response, CHARSET));
        for (String line; (line = reader.readLine()) != null;) {
          if (! (line.contains("</table>") || line.contains("</body>") || line.contains("</html>"))) {
            line = line.replaceAll("&", "&amp;");
            result.append(line + "\n");
          }
        }
      } finally {
        if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) { ; }
      }
    } catch (UnsupportedEncodingException uee) {
      uee.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (Throwable t) {
      t.printStackTrace();
    }

    return result.toString();
  }
}
