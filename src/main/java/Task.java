public class Task {
    protected String description;
    protected Boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    public String toTaskSaveString() {
        return this.getStatusInt() + "|" + this.description;
    }

    public int getStatusInt() {
        return this.isDone? 1 : 0;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public void markDone() {
        this.isDone = true;
    }

    public void markNotDone() {
        this.isDone = false;
    }
}