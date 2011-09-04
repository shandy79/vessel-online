package org.vesselonline.ai.util;

import java.io.PrintStream;

public class Instrumentation {
  private boolean print;
  private PrintStream stream;
  private long startTime;
  private boolean printElapsedTime;
  private long cpuTime;
  private int workUnits, secondaryWorkUnits;

  public Instrumentation(boolean print, PrintStream stream) {
    this.print = print;
    this.stream = stream;

    startTime = System.currentTimeMillis();
    printElapsedTime = false;

    cpuTime = 0;
    workUnits = 0;
    secondaryWorkUnits = 0;
  }

  public boolean isPrint() { return print; }
  public void setPrint(boolean print) { this.print = print; }

  public PrintStream getStream() { return stream; }
  public void setStream(PrintStream stream) { this.stream = stream; }

  public long getStartTime() { return startTime; }
  public long getElapsedTime() { return System.currentTimeMillis() - getStartTime(); }

  public boolean isPrintElapsedTime() { return printElapsedTime; }
  public void setPrintElapsedTime(boolean printElapsedTime) { this.printElapsedTime = printElapsedTime; }

  public long getCPUTime() { return cpuTime; }
  public void addCPUTime(long cpuTime) { this.cpuTime += cpuTime; }

  public int getWorkUnits() { return workUnits; }
  public void addWorkUnits(int workUnits) { this.workUnits += workUnits; }

  public int getSecondaryWorkUnits() { return secondaryWorkUnits; }
  public void addSecondaryWorkUnits(int secondaryWorkUnits) { this.secondaryWorkUnits += secondaryWorkUnits; }

  public void print(String message) {
    if (isPrint()) {
      if (isPrintElapsedTime()) {
        stream.print("[" + (System.currentTimeMillis() - getStartTime()) + "] ");
      }
      stream.println(message);
    }
  }

  public void printStatus(String workUnitLbl, String secondaryWorkUnitLbl) {
    stream.println("  CPU Time: " + getCPUTime() + ", " + getWorkUnits() + " " + workUnitLbl + ", " +
                   getSecondaryWorkUnits() + " " + secondaryWorkUnitLbl);
  }
}
