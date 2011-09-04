package com.swimshower.model;

import org.vesselonline.metadata.XMLMetadata;

public class Song extends SwimShowerResource implements XMLMetadata {
  private float sizeInMegabytes;
  private String lyrics;

  public Song() { super(); }

  public float getSizeInMegabytes() { return sizeInMegabytes; }
  public void setSizeInMegabytes(float sizeInMegabytes) { this.sizeInMegabytes = sizeInMegabytes; }

  public String getLyrics() { return lyrics; }
  public void setLyrics(String lyrics) { this.lyrics = lyrics; }

  public String toDublinCoreRDF() {
    // TODO Auto-generated method stub
    return null;
  }
}
