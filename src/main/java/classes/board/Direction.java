package classes.board;

public enum Direction {
    HORIZONTAL("h"),
    VERTICAL("v");

    private String alias;

    Direction(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return alias;
    }
}

