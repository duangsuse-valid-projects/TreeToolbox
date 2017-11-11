package bsh;

public class ClassIdentifier {
    Class clas;

    public ClassIdentifier(Class clas) {
        this.clas = clas;
    }

    // Can't call it getClass()
    public Class getTargetClass() {
        return clas;
    }

    public String toString() {
        return "类标识符: " + clas.getName();
    }
}
