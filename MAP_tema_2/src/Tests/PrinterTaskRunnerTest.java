package Tests;

import model.MessageTask;
import decorator.PrinterTaskRunner;
import decorator.StrategyTaskRunner;
import factory.Strategy;

public class PrinterTaskRunnerTest {

    public static void printerTaskRunnerTest(Strategy containerStrategy, MessageTask[] messageTasks) {
        StrategyTaskRunner strategyTaskRunner = new StrategyTaskRunner(containerStrategy);
        PrinterTaskRunner printerTaskRunner = new PrinterTaskRunner(strategyTaskRunner);
        printerTaskRunner.addTask(messageTasks[0]);
        printerTaskRunner.addTask(messageTasks[1]);
        printerTaskRunner.addTask(messageTasks[2]);
        printerTaskRunner.executeAll();
    }
}