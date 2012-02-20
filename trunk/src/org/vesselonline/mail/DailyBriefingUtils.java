package org.vesselonline.mail;

import java.awt.Font;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.tasks.Tasks;

public enum DailyBriefingUtils {
  INSTANCE;

  // Copied from the API Access tab on the Google APIs Console
  private final String clientID = "<client_id>";
  private final String clientSecret = "<client_secret>";
  // Redirect for installed applications
  private final String redirectURL = "urn:ietf:wg:oauth:2.0:oob";
  // Read-only OAuth 2.0 scope for Google APIs
  private final String tasksScope = "https://www.googleapis.com/auth/tasks";  //.readonly";
  private final String calendarScope = "https://www.googleapis.com/auth/calendar";  //.readonly";
  // Regex for RFC 3339 Date/Time strings "2011-06-03T10:00:00.000-07:00"
  private final Pattern rfc3339Pattern = Pattern.compile("(\\d{4}-(\\d{2}-\\d{2}))T(\\d{2}:\\d{2}):\\d{2}\\.\\d{3}-(\\d{2}:\\d{2})");

  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;

  private final String applicationName;
  private final int oneMinute;
  private final Font smallFont;

  private GoogleAccessProtectedResource googleAccessProtectedResource;
  private Tasks tasksService;
  private Calendar calendarService;

  private DailyBriefingUtils() {
    httpTransport = new NetHttpTransport();
    jsonFactory = new JacksonFactory();

    applicationName = "Daily Briefing";
    oneMinute = 60000;
    smallFont = new Font("SansSerif", Font.PLAIN, 11);
  }

  public HttpTransport getHttpTransport() { return httpTransport; }
  public JsonFactory getJsonFactory() { return jsonFactory; }

  public String getApplicationName() { return applicationName; }
  public int getOneMinute() { return oneMinute; }
  public Font getSmallFont() { return smallFont; }

  private String getScope() { return tasksScope + " " + calendarScope; }

  public GoogleAccessProtectedResource getGoogleAccessProtectedResource() throws DailyBriefingException {
    if (googleAccessProtectedResource == null) {    
      // Step 1: Authorize -->
      String authorizationUrl = new GoogleAuthorizationRequestUrl(clientID, redirectURL, getScope()).build();

      JTextArea authUrlTextArea = new JTextArea("Visit this URL in your web browser:\n\n" + authorizationUrl +
          "\n\nEnter the Authorization Code:", 6, 80);
      authUrlTextArea.setLineWrap(true);
      authUrlTextArea.setEditable(false);

      // Point or redirect your user to the authorizationUrl and read auth code from the JOptionPane
      String code = JOptionPane.showInputDialog(authUrlTextArea);
      // End of Step 1 <--

      // Step 2: Exchange -->
      AccessTokenResponse tokenResponse;
      try {
        tokenResponse = new GoogleAuthorizationCodeGrant(getHttpTransport(), getJsonFactory(), clientID, clientSecret, code,
            redirectURL).execute();
      } catch (IOException ioe) {
        throw new DailyBriefingException(ioe);
      } catch (Throwable t) {
        throw new DailyBriefingException(t);
      }

      googleAccessProtectedResource = new GoogleAccessProtectedResource(tokenResponse.accessToken, getHttpTransport(), getJsonFactory(),
          clientID, clientSecret, tokenResponse.refreshToken);
      // End of Step 2 <--
    }

    return googleAccessProtectedResource;
  }

  public Tasks getGoogleTasksService() throws DailyBriefingException {
    if (tasksService == null) {
      tasksService = Tasks.builder(getHttpTransport(), getJsonFactory()).setApplicationName(getApplicationName())
          .setHttpRequestInitializer(getGoogleAccessProtectedResource()).build();
    }

    return tasksService;
  }

  public Calendar getGoogleCalendarService() throws DailyBriefingException {
    if (calendarService == null) {
      calendarService = Calendar.builder(getHttpTransport(), getJsonFactory()).setApplicationName(getApplicationName())
          .setHttpRequestInitializer(getGoogleAccessProtectedResource()).build();
    }

    return calendarService;
  }

  /** Construct formatted string for current date and time. */
  public String getCurrentTime() {
    java.util.Calendar now = java.util.Calendar.getInstance();
    String nowStr = "as of " + (now.get(java.util.Calendar.MONTH) + 1) + "/" + now.get(java.util.Calendar.DAY_OF_MONTH) +
                    "/" + now.get(java.util.Calendar.YEAR) + " " + now.get(java.util.Calendar.HOUR_OF_DAY) + ":";
    if (now.get(java.util.Calendar.MINUTE) < 10) nowStr += "0";
    nowStr += now.get(java.util.Calendar.MINUTE) + ":";
    if (now.get(java.util.Calendar.SECOND) < 10) nowStr += "0";
    nowStr += now.get(java.util.Calendar.SECOND);
    
    return nowStr;
  }

// "start": {
//   "dateTime": "2011-06-03T10:00:00.000-07:00",
//   "timeZone": "America/Los_Angeles"
// }
  public String eventDateTimeToString(EventDateTime eventDateTime) {
    String dateTimeStr = "";

    if (eventDateTime != null) {
      // For timed events
      if (eventDateTime.getDateTime() != null) {
        dateTimeStr = eventDateTime.getDateTime().toStringRfc3339();

        Matcher m = rfc3339Pattern.matcher(dateTimeStr);
        if (m.matches()) {
          dateTimeStr = m.group(2) + " " + m.group(3);
        }

      // For all-day events
      } else if (eventDateTime.getDate() != null) {
        dateTimeStr = eventDateTime.getDate();
        dateTimeStr = dateTimeStr.substring(5);
      }
    }

    return dateTimeStr.replace('-', '/');
  }

  public String eventTimeToString(EventDateTime eventDateTime) {
    String dateTimeStr = eventDateTimeToString(eventDateTime);

    // For timed events, remove the date portion of the string, e.g. "02-10 "
    if (dateTimeStr.matches(".*\\d{2}:\\d{2}")) {
      dateTimeStr = dateTimeStr.substring(6);
    
    // For all-day events
    } else {
      dateTimeStr = "";
    }

    return dateTimeStr;
  }

  public String eventDateTimeRangeToString(Event event) {
    String dateTimeStr = eventDateTimeToString(event.getStart());
    String endTimeStr = eventTimeToString(event.getEnd());
    if (! endTimeStr.equals("")) dateTimeStr += " - " + endTimeStr;

    return dateTimeStr;
  }
}
