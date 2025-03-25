package Tests;

import model.MessageTask;
import model.Task;
import decorator.StrategyTaskRunner;
import decorator.TaskRunner;
import factory.Strategy;

public class StrategyTaskRunnerTest {

    public static void strategyTaskRunnerTest(Strategy containerStrategy, MessageTask[] messageTasks) {
        TaskRunner taskRunner = new StrategyTaskRunner(containerStrategy);
        taskRunner.addTask(messageTasks[0]);
        taskRunner.addTask(messageTasks[1]);
        taskRunner.addTask(messageTasks[2]);
        taskRunner.executeAll();
    }
}