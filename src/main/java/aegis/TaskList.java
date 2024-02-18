package aegis;

import java.util.ArrayList;

/**
 * TaskList class represents a container that stores and keeps track of tasks.
 * Also contains methods to manipulate stored tasks.
 */
public class TaskList {
    private ArrayList<Task> taskList;

    /**
     * Constructor for creating a TaskList object.
     */
    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    /**
     * Returns the task stored at the specified index.
     *
     * @param index Index of a stored task in the task list.
     * @return Task at specified index.
     */
    public Task getTask(int index) {
        return taskList.get(index);
    }

    /**
     * Returns the number of tasks currently stored in the task list.
     *
     * @return Number of tasks in task list.
     */
    public int getTaskCount() {
        return taskList.size();
    }

    /**
     * Adds a task to the task list.
     *
     * @param newTask Task to be added.
     */
    public void addTask(Task newTask) {
        taskList.add(newTask);
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param index Index of task to be deleted.
     */
    public void deleteTask(int index) {
        taskList.remove(index);
    }

    /**
     * Changes the status of a task to completed.
     *
     * @param index Index of task to be marked complete.
     */
    public void markTask(int index) {
        Task selectedTask = taskList.get(index);
        selectedTask.setDone();
    }

    /**
     * Changes the status of a task to not completed.
     *
     * @param index Index of task to be marked not completed.
     */
    public void unmarkTask(int index) {
        Task selectedTask = taskList.get(index);
        selectedTask.setNotDone();
    }

    /**
     * Prints all tasks in the task list.
     * Each task is printed on a separate line and contains all details of the task such as
     * its task type, completion status, description and other fields specific to the task type.
     */
    public void printTaskList() {
        System.out.println("Here are your tasks: \n");

        for (int i = 0; i < this.taskList.size(); i++) {
            Task currTask = taskList.get(i);
            System.out.println((i + 1) + currTask.toString());
        }
        System.out.println();
    }

    /**
     * Prints the total number of tasks in a string format for better readability.
     */
    public void printTaskCount() {
        System.out.println("Total task count: " + taskList.size() + ".\n");
    }

    /**
     * Returns a Task that is constructed from the String input provided.
     * The task created and its details depend on the task type specified in the String input.
     *
     * @param saveString String containing task details.
     * @return Task created using parsed details.
     */
    public Task reconstructTask(String saveString) {
        String[] taskArgs = saveString.split("\\|");
        String identifier = taskArgs[0];
        Task reconstructedTask = null;

        switch (identifier) {
        case "T":
            reconstructedTask = new ToDo(taskArgs[2]);
            break;
        case "D":
            reconstructedTask = new Deadline(taskArgs[2], taskArgs[3]);
            break;
        case "E":
            reconstructedTask = new Event(taskArgs[2], taskArgs[3], taskArgs[4]);
            break;
        default:
            System.out.println("Unable to reconstruct task.");
            break;
        }

        Boolean isDone = taskArgs[1].equals("1");
        if (isDone) {
            reconstructedTask.setDone();
        }

        return reconstructedTask;
    }

    /**
     * Prints out a list of tasks that have descriptions containing a particular keyword.
     *
     * @param keyword Keyword to look for in task descriptions.
     */
    public void printTasksWithKeyword(String keyword) {
        ArrayList<Task> identifiedTasks = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            Task currTask = taskList.get(i);

            if (currTask.checkDescription(keyword)) {
                identifiedTasks.add(currTask);
            }
        }

        if (identifiedTasks.isEmpty()) {
            System.out.println("There were no tasks matching your keyword.\n");
            return;
        }

        System.out.println("These tasks match the keyword you provided:\n");
        for (int i = 0; i < identifiedTasks.size(); i++) {
            System.out.println(identifiedTasks.get(i).toString());
        }
        System.out.println();
    }
}