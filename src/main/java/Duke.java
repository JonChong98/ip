import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Duke {
    private static String gap = "____________________________________________________________\n";
    private static HashSet<String> validCommands;
    private static ArrayList<Task> tasks;

    public static void main(String[] args) {
        initialSetup();
        String logo = " ____        _        \n"
                    + "|  _ \\ _   _| | _____ \n"
                    + "| | | | | | | |/ / _ \\\n"
                    + "| |_| | |_| |   <  __/\n"
                    + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println(logo);
        System.out.println(gap + "Greetings! I am Aegis.\n"
                         + "How can I assist you?\n" + gap);
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                String input = sc.nextLine();
                StringTokenizer st = new StringTokenizer(input);
                String identifier = st.nextToken().toLowerCase();
                if (validCommands.contains(identifier)) {
                    executeCommand(input);
                } else {
                    throw new DukeException("I do not recognize that command.\n"
                            + "Please enter a valid command.");
                }
            } catch (DukeException e) {
                System.out.println(gap + e.getMessage() + "\n" + gap);
            }
        }
    }

    private static void initialSetup() {
        tasks = new ArrayList<Task>();
        validCommands = new HashSet<String>();
        validCommands.addAll(Arrays.asList("bye",
                "list",
                "mark",
                "unmark",
                "todo",
                "deadline",
                "event",
                "delete"));
        try {
            readTaskListData();
        } catch (FileNotFoundException e) {
            System.out.println("FNFE occurred.");
        } catch (IOException e) {
            System.out.println("IOE occurred.");
        }
    }

    private static void executeCommand(String command) throws DukeException {
        StringTokenizer st = new StringTokenizer(command);
        String identifier = st.nextToken().toLowerCase();
        String arguments = "";
        while(st.hasMoreTokens()) {
            arguments += st.nextToken() + " ";
        }
        arguments = arguments.trim();
        switch (identifier) {
        case "bye":
            exitProgram();
            break;
        case "list":
            printTaskList(arguments);
            break;
        case "mark":
            markTask(arguments);
            break;
        case "unmark":
            unmarkTask(arguments);
            break;
        case "todo":
            createToDoTask(arguments);
            break;
        case "deadline":
            createDeadlineTask(arguments);
            break;
        case "event":
            createEventTask(arguments);
            break;
        case "delete":
            deleteTask(arguments);
            break;
        default:
            System.out.println("なに？！");
            break;
        }
        try {
            if (!identifier.equals("list")) {
                writeTaskListData();
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
        System.out.println("Total task count: " + tasks.size() + ".\n" + gap);
    }

    private static void exitProgram() {
        System.out.println(gap + "Goodbye! Have a pleasant day!\n" + gap);
        System.exit(0);
    }

    private static void printTaskList(String arguments) throws DukeException {
        if (!arguments.isEmpty()) {
            throw new DukeException("list command does not accept arguments.\n"
                    + "Enter 'list' to view the current list of tasks");
        }
        String taskList = "";
        for (int i = 0; i < tasks.size(); i++) {
            Task curr = tasks.get(i);
            taskList += ((i+1) + ". " + curr.toString() + "\n");
        }
        System.out.println(gap + "Here are your tasks:\n" + taskList + gap);
    }

    private static void markTask(String arguments) throws DukeException {
        int taskNum = Integer.parseInt(arguments) - 1;
        Task selectedTask = tasks.get(taskNum);
        selectedTask.markDone();
        System.out.println(gap + "Well done, task marked as completed.\n"
                + selectedTask.toString() + "\n" + gap);
    }

    private static void unmarkTask(String arguments) throws DukeException {
        int taskNum = Integer.parseInt(arguments) - 1;
        Task selectedTask = tasks.get(taskNum);
        selectedTask.markNotDone();
        System.out.println(gap + "Understood, task marked as uncompleted.\n"
                + selectedTask.toString() + "\n" + gap);
    }

    private static void createToDoTask(String arguments) throws DukeException {
        if (!arguments.isEmpty()) {
            ToDo newToDo = new ToDo(arguments);
            tasks.add(newToDo);
            System.out.println(gap + "Confirmed. New task added:\n"
                    + newToDo.toString() + "\n");
        } else {
            throw new DukeException("todo command requires a description for the task."
                    + "\n\nPlease leave a space after 'todo' and enter"
                    + " the task description.");
        }
    }

    private static void createDeadlineTask(String arguments) throws DukeException {
        if (!arguments.isEmpty()) {
            String[] deadlineInfo = arguments.split("/");
            StringTokenizer st = new StringTokenizer(deadlineInfo[0].trim());
            String deadlineDesc = "";
            while(st.hasMoreTokens()) {
                deadlineDesc += st.nextToken() + " ";
            }
            deadlineDesc = deadlineDesc.trim();
            st = new StringTokenizer(deadlineInfo[1].trim());
            st.nextToken();
            String by = "";
            while(st.hasMoreTokens()) {
                by += st.nextToken() + " ";
            }
            by = by.trim();
            Deadline newDeadline = new Deadline(deadlineDesc, by);
            tasks.add(newDeadline);
            System.out.println(gap + "Confirmed. New task added:\n"
                    + newDeadline.toString() + "\n");
        } else {
            throw new DukeException("deadline command requires a description for the task"
                    + " and a deadline. \n\nPlease leave a space after 'deadline'"
                    + " and enter the task description, \nfollowed by a space and a"
                    + " forward slash then the deadline of the task.");
        }
    }

    private static void createEventTask(String arguments) throws DukeException {
        if (!arguments.isEmpty()) {
            String[] eventInfo = arguments.split("/");
            StringTokenizer st = new StringTokenizer(eventInfo[0].trim());
            String eventDesc = "";
            while(st.hasMoreTokens()) {
                eventDesc += st.nextToken() + " ";
            }
            eventDesc = eventDesc.trim();
            st = new StringTokenizer(eventInfo[1].trim());
            st.nextToken();
            String from = "";
            while (st.hasMoreTokens()) {
                from += st.nextToken() + " ";
            }
            from = from.trim();
            st = new StringTokenizer(eventInfo[2].trim());
            st.nextToken();
            String end = "";
            while (st.hasMoreTokens()) {
                end += st.nextToken() + " ";
            }
            end = end.trim();
            Event newEvent = new Event(eventDesc, from, end);
            tasks.add(newEvent);
            System.out.println(gap + "Confirmed. New task added:\n"
                    + newEvent.toString() + "\n");
        } else {
            throw new DukeException("event command requires a description for the task,"
                    + " start time and end time. \n\nPlease leave a space after 'event'"
                    + " and enter the task description, \nfollowed by a space and forward slash"
                    + " before the start time, \nfollowed by another space and forward slash"
                    + " before the end time.");
        }
    }

    private static void deleteTask(String arguments) throws DukeException {
        int delIndex = Integer.parseInt(arguments) - 1;
        Task toDelete = tasks.remove(delIndex);
        System.out.println(gap + "Acknowledged. The following task has been removed:\n"
                + toDelete.toString());
        System.out.println("\nTasks remaining: " + tasks.size() + ".\n" + gap);
    }

    private static void writeTaskListData() throws IOException {
        String filePath = "./src/main/data/duke.txt";
        File save = new File(filePath);
        try {
            if (!save.exists()) {
                save.createNewFile();
            }
            FileWriter fw = new FileWriter(filePath);
            for (int i = 0; i < tasks.size(); i++) {
                fw.write(tasks.get(i).toTaskSaveString() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("An error has occurred.");
            e.getStackTrace();
        }
    }

    private static void readTaskListData() throws FileNotFoundException, IOException {
        String filePath = "./src/main/data/duke.txt";
        File read = new File(filePath);
        try {
            if (!read.exists()) {
                read.createNewFile();
            }
            Scanner sc = new Scanner(read);
            while (sc.hasNextLine()) {
                String data = sc.nextLine();
                Task savedTask = reconstructTask(data);
                tasks.add(savedTask);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred locating the file.");
        } catch (IOException e) {
            System.out.println("An error has occurred.");
        }
    }

    private static Task reconstructTask(String saveString) {
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
}