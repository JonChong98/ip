package duke;

public class ToDo extends Task {
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toTaskSaveString() {
        return "T|" + this.getStatusInt() + "|" + this.description;
    }
}