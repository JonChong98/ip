package duke;

import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> taskList;

    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    public Task getTask(int index) {
        return taskList.get(index);
    }

    public int getTaskCount() {
        return taskList.size();
    }

    public void addTask(Task newTask) {
        taskList.add(newTask);
    }

    public void deleteTask(int index) {
        taskList.remove(index);
    }

    public void markTask(int index) {
        Task selectedTask = taskList.get(index);
        selectedTask.markDone();
    }

    public void unmarkTask(int index) {
        Task selectedTask = taskList.get(index);
        selectedTask.markNotDone();
    }

    public void printTaskList() {
        System.out.println("Here are your tasks: \n");
        for (int i = 0; i < this.taskList.size(); i++) {
            Task currTask = taskList.get(i);
            System.out.println((i + 1) + currTask.toString());
        }
        System.out.println();
    }

    public void printTaskCount() {
        System.out.println("Total task count: " + taskList.size() + ".\n");
    }

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
            reconstructedTask.markDone();
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
