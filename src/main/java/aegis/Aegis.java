package aegis;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Aegis class contains the main() method that initiates and runs the Aegis assistant program.
 * Also contains private functions called by main() to execute commands.
 */
public class Aegis extends Application {
    private static Parser parser;
    private static Storage storage;
    private static TaskList taskList;
    private static Ui ui;
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private Image aegis = new Image(this.getClass().getResourceAsStream("/images/aegis.png"));

    /**
     * Constructor for creating an Aegis object.
     */
    public Aegis() {}

    /**
     * Starts the execution of the Aegis assistant program.
     * Initializes required objects and contains the main while loop that repeatedly prompts
     * the user for input and executes commands based on the input.
     *
     * @param args Default parameter for main method.
     */
    public static void main(String[] args) {
        initialSetup();

        ui.printLogo();
        ui.printGreeting();
        ui.printDivider();

        while (true) {
            try {
                String input = ui.getUserInput();
                if (parser.checkValidCommand(input)) {
                    executeCommand(input);
                } else {
                    throw new AegisException("I do not recognize that command.\n"
                            + "Please enter a valid command.\n");
                }
            } catch (AegisException e) {
                ui.printDivider();
                System.out.println(e.getMessage());
                ui.printDivider();
            }
        }
    }

    /**
     * Overridden method from javafx.application.Application that implements own custom stage.
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage stage) {
        //Step 1. Setting up required components

        //The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);

        //Step 2. Formatting the window to look as expected
        stage.setTitle("Aegis");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogContainer.setSpacing(10.0);
        dialogContainer.setPadding(new Insets(10, 10, 10, 10));


        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        stage.show();

        //Step 3. Add functionality to handle user input.
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

    }

    private Label getDialogLabel(String text) {
        Label textToAdd = new Label(text);
        textToAdd.setWrapText(true);

        return textToAdd;
    }

    private void handleUserInput() {
        Label userText = new Label(userInput.getText());
        Label dukeText = new Label(getResponse(userInput.getText()));
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, new ImageView(user)),
                DialogBox.getAegisDialog(dukeText, new ImageView(aegis))
        );
        userInput.clear();
    }

    private String getResponse(String input) {
        return input;
    }

    private static void initialSetup() {
        parser = new Parser();
        storage = new Storage("./src/main/data",
                "./src/main/data/aegis.txt");
        taskList = new TaskList();
        ui = new Ui();

        try {
            ArrayList<String> tasksFromFile = storage.readTaskListData();
            for (int i = 0; i < tasksFromFile.size(); i++) {
                Task reconstructedTask = taskList.reconstructTask(tasksFromFile.get(i));
                taskList.addTask(reconstructedTask);
            }
        } catch (FileNotFoundException e) {
            ui.printFileNotFoundError();
        } catch (IOException e) {
            ui.printIoException();
        }

    }

    private static void executeCommand(String input) throws AegisException {
        String identifier = parser.parseCommand(input);
        String arguments = parser.parseArguments(input);

        switch (identifier) {
        case "bye":
            exitProgram();
            break;
        case "list":
            printTasks();
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
        case "find":
            printIdentifiedTasks(arguments);
            break;
        default:
            System.out.println("なに？！");
            break;
        }

        try {
            if (!identifier.equals("list")) {
                storage.writeTaskListData(taskList);
            }
        } catch (IOException e) {
            ui.printIoException();
        }
    }

    private static void exitProgram() {
        ui.printDivider();
        ui.printFarewell();
        ui.printDivider();
        System.exit(0);
    }

    private static void printTasks() {
        ui.printDivider();
        taskList.printTaskList();
        taskList.printTaskCount();
        ui.printDivider();
    }

    private static void markTask(String arguments) {
        try {
            int taskNum = parser.parseTaskIndex(arguments);
            taskList.markTask(taskNum);

            ui.printDivider();
            ui.printMarkTaskSuccess();
            System.out.println(taskList.getTask(taskNum).toString() + "\n");
            ui.printDivider();
        } catch (IndexOutOfBoundsException e) {
            ui.printDivider();
            System.out.println("Invalid task index provided.\nPlease provide a valid task index.\n");
            ui.printDivider();
        }
    }

    private static void unmarkTask(String arguments) {
        try {
            int taskNum = parser.parseTaskIndex(arguments);
            taskList.unmarkTask(taskNum);

            ui.printDivider();
            ui.printUnmarkTaskSuccess();
            System.out.println(taskList.getTask(taskNum).toString() + "\n");
            ui.printDivider();
        } catch (IndexOutOfBoundsException e) {
            ui.printDivider();
            System.out.println("Invalid task index provided.\nPlease provide a valid task index.\n");
            ui.printDivider();
        }
    }

    private static void createToDoTask(String arguments) throws AegisException {
        if (!arguments.isEmpty()) {
            ToDo newToDo = new ToDo(arguments);
            taskList.addTask(newToDo);

            ui.printDivider();
            ui.printCreateTaskSuccess();
            System.out.println(newToDo.toString() + "\n");
            taskList.printTaskCount();
            ui.printDivider();
        } else {
            throw new AegisException("todo command requires a description for the task."
                    + "\n\nPlease leave a space after 'todo' and enter"
                    + " the task description.");
        }
    }

    private static void createDeadlineTask(String arguments) {
        try {
            String[] deadlineArgs = parser.parseDeadlineArguments(arguments);

            if (deadlineArgs.length == 2) {
                Deadline newDeadline = new Deadline(deadlineArgs[0], deadlineArgs[1]);
                taskList.addTask(newDeadline);

                ui.printDivider();
                ui.printCreateTaskSuccess();
                System.out.println(newDeadline.toString() + "\n");
                taskList.printTaskCount();
                ui.printDivider();
            }
        } catch (IndexOutOfBoundsException | DateTimeParseException e) {
            ui.printDivider();
            System.out.println("Invalid command given for creating Deadline task."
                    + "\nPlease ensure the command uses the following format:"
                    + "\ndeadline <Task Description> /by <Date in YYYY-MM-DD>\n");
            ui.printDivider();
        }
    }

    private static void createEventTask(String arguments) {
        try {
            String[] eventArgs = parser.parseEventArguments(arguments);

            if (!arguments.isEmpty()) {
                Event newEvent = new Event(eventArgs[0], eventArgs[1], eventArgs[2]);
                taskList.addTask(newEvent);

                ui.printDivider();
                ui.printCreateTaskSuccess();
                System.out.println(newEvent.toString() + "\n");
                taskList.printTaskCount();
                ui.printDivider();
            }
        } catch (IndexOutOfBoundsException | DateTimeParseException e) {
            ui.printDivider();
            System.out.println("Invalid command given for creating Event task."
                    + "\nPlease ensure the command uses the following format:"
                    + "\nevent <Task Description> /from <Date in YYYY-MM-DD> /to <Date in YYYY-MM-DD>\n");
            ui.printDivider();
        }
    }

    private static void deleteTask(String arguments) {
        try {
            int delIndex = parser.parseTaskIndex(arguments);
            Task toDelete = taskList.getTask(delIndex);
            taskList.deleteTask(delIndex);

            ui.printDivider();
            ui.printDeleteTaskSuccess();
            System.out.println(toDelete.toString() + "\n");
            taskList.printTaskCount();
            ui.printDivider();
        } catch (IndexOutOfBoundsException e) {
            ui.printDivider();
            System.out.println("Invalid task index provided.\nPlease provide a valid task index.\n");
            ui.printDivider();
        }
    }

    /**
     * Prints all tasks that have been identified to have the keyword in their descriptions.
     *
     * @param keyword Keyword to look for in task descriptions.
     */
    private static void printIdentifiedTasks(String keyword) {
        ui.printDivider();
        taskList.printTasksWithKeyword(keyword);
        ui.printDivider();
    }
}