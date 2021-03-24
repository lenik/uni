package net.bodz.uni.catme;

public interface ICommand {

    boolean isGreedy();

    void execute(Object... args);

}
