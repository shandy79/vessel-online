package org.vesselonline.draftroom.util;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.vesselonline.draftroom.api.FantasyTeam;
import org.vesselonline.draftroom.api.Player;

public class DraftRoomUtil {
  // Singleton instance of this class
  private static DraftRoomUtil draftRoomUtil = new DraftRoomUtil();

  // Properties file containing application settings
  private static final String PROPS_FILE = "/draftroom.properties";

  // "Constants" available to the application
  private String serverPrefix;
  private String serverDir;
  private String siteName;
  private String infoEmail;
  private String adminEmail;
  private String copyright;
  private SimpleDateFormat dateFormat;
  private int recentItemCount;

  private String leagueName;
  private String leagueAbbr;
  private URL leagueLogo;
  private int teamCount;
  private int roundCount;
  private int timeLimit;
  private String timeDirection;
  private boolean snakeDraft;
  private String sport;
  private String proLeague;
  private URL proLogo;

  // Application variables for use during draft
  private int pickNumber = 1;
  private FantasyTeam currentTeamOnClock;
  private Date currentClockStart;
  private List<FantasyTeam> orderedTeamList;
//  private FantasyTeamDAO fantasyTeamDAO;

  private DraftRoomUtil() {
    // Initialization of fields from properties file
    Properties props = new Properties();
    try {
      props.load(DraftRoomUtil.class.getResourceAsStream(PROPS_FILE));

      serverPrefix = props.getProperty("SERVER_PREFIX");
      serverDir = props.getProperty("SERVER_DIR");
      siteName = props.getProperty("SITE_NAME");
      infoEmail = props.getProperty("INFO_EMAIL");
      adminEmail = props.getProperty("ADMIN_EMAIL");
      copyright = props.getProperty("COPYRIGHT");
      dateFormat = new SimpleDateFormat(props.getProperty("DATE_FORMAT"));
      recentItemCount = Integer.parseInt(props.getProperty("RECENT_ITEM_COUNT"));

      leagueName = props.getProperty("LEAGUE_NAME");
      leagueAbbr = props.getProperty("LEAGUE_ABBR");
      leagueLogo = new URL(props.getProperty("LEAGUE_LOGO"));
      teamCount = Integer.parseInt(props.getProperty("TEAM_COUNT"));
      roundCount = Integer.parseInt(props.getProperty("ROUND_COUNT"));
      timeLimit = Integer.parseInt(props.getProperty("TIME_LIMIT"));
      timeDirection = props.getProperty("TIME_DIRECTION");
      snakeDraft = Boolean.parseBoolean(props.getProperty("SNAKE_DRAFT"));
      sport = props.getProperty("SPORT");
      proLeague = props.getProperty("PRO_LEAGUE");
      proLogo = new URL(props.getProperty("PRO_LOGO"));

    } catch (IOException ioe) {
      serverPrefix = "http://www.swimshower.com/";
      serverDir = "/home/shandy79/public_html";
      siteName = "Draft Room";
      infoEmail = "agbadza79@gmail.com";
      adminEmail = "agbadza79@gmail.com";
      dateFormat = new SimpleDateFormat("M/d/yyyy h:mm a");
      recentItemCount = 5;

      timeLimit = 5;
      timeDirection = "down";
      snakeDraft = true;

      ioe.printStackTrace();
    } finally {
      props = null;
    }

    // Retrieve all teams from database for easy access during draft
//    fantasyTeamDAO = new FantasyTeamDAO();
//    orderedTeamList = fantasyTeamDAO.findAllDraftSorted();
  }

  public static DraftRoomUtil getInstance() { return draftRoomUtil; }

  public String getServerPrefix() { return serverPrefix; }
  public String getServerDir() { return serverDir; }
  public String getSiteName() { return siteName; }
  public String getInfoEmail() { return infoEmail; }
  public String getAdminEmail() { return adminEmail; }
  public String getCopyright() { return copyright; }
  public SimpleDateFormat getDateFormat() { return dateFormat; }
  public int getRecentItemCount() { return recentItemCount; }

  public String getLeagueName() { return leagueName; }
  public String getLeagueAbbr() { return leagueAbbr; }
  public URL getLeagueLogo() { return leagueLogo; }
  public int getTeamCount() { return teamCount; }
  public int getRoundCount() { return roundCount; }
  public int getTimeLimit() { return timeLimit; }
  public String getTimeDirection() { return timeDirection; }
  public boolean isSnakeDraft() { return snakeDraft; }
  public String getSport() { return sport; }
  public String getProLeague() { return proLeague; }
  public URL getProLogo() { return proLogo; }

  public int getPickNumber() { return pickNumber; }
  public FantasyTeam getCurrentTeamOnClock() { return currentTeamOnClock; }
  public Date getCurrentClockStart() { return currentClockStart; }

  public int getRoundNumber() { return (pickNumber / teamCount) + 1; }

  public boolean draftPlayer(Player player) {
    boolean result = currentTeamOnClock.addPlayer(player);

    pickNumber++;
    currentTeamOnClock = getFantasyTeamFromPickNumber(pickNumber);
    resetCurrentClockStart();

    return result;
  }

  public void gotoPickNumber(int pickNumber) {
    this.pickNumber = pickNumber;
    currentTeamOnClock = getFantasyTeamFromPickNumber(pickNumber);
    resetCurrentClockStart();
  }

  private FantasyTeam getFantasyTeamFromPickNumber(int pickNumber) {
    int index = 0;

    if (teamCount == 1) {
      index = 0;
    } else if (! isSnakeDraft() || (getRoundNumber() % 2) != 0) {
      index = (pickNumber % teamCount) - 1;
      if (index == -1) index = teamCount - 1;
    } else {
      index = teamCount - (pickNumber % teamCount);
      if (index == teamCount) index = 0;
    }

    return orderedTeamList.get(index);
  }

  private void resetCurrentClockStart() { this.currentClockStart = new Date(); }
}
