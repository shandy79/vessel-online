package org.vesselonline.mail;

import java.io.IOException;
import java.util.Vector;
import javax.swing.JTable;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.Tasks;

public class TasksDisplay extends BriefingDisplay {
  /**
   * For Serializable interface, value generated by Eclipse.
   */
  private static final long serialVersionUID = 277077991045826293L;

  private TaskList taskList;
  private Tasks tasks;

  private static final Vector<String> COLUMN_NAMES = new Vector<String>(2);
  static {
    COLUMN_NAMES.add("#");
    COLUMN_NAMES.add("Title");
  }

  public TasksDisplay(int tabIndex, int delay, TaskList taskList) {
    super(tabIndex, taskList.getTitle(), delay, COLUMN_NAMES);
    this.taskList = taskList;
  }

  public Tasks getTasks() { return tasks; }
  public String getTaskListID() { return taskList.getId(); }

  @Override
  protected JTable createTable() {
    return new TasksDialogTable(this);
  }

  @Override
  protected void refresh() throws DailyBriefingException {
    Vector<String> row;
    int taskCount = 0;

    try {
      tasks = DailyBriefingUtils.INSTANCE.getGoogleTasksService().tasks().list(getTaskListID()).execute();

      // Empty the current table data, then reload with retrieved tasks
      getTableModel().setRowCount(0);
      for (Task task : getTasks().getItems()) {
        taskCount++;
        row = new Vector<String>(2);
        row.add(0, Integer.toString(taskCount));
        row.add(1, task.getTitle());

        getTableModel().addRow(row);
      }

      // Update time and task count labels
      getTimeLabel().setText(DailyBriefingUtils.INSTANCE.getCurrentTime());
      getMessageLabel().setText(taskCount + " tasks");
    } catch (IOException ioe) {
      throw new DailyBriefingException(ioe);
    }
  }

  @Override
  protected void handleException(DailyBriefingException dbe) {
    getMessageLabel().setText(dbe.getCause().getMessage());
  }
}
