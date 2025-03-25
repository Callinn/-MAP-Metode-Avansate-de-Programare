package Tests;

import factory.Strategy;
import model.MessageTask;
import decorator.DelayTaskRunner;
import decorator.PrinterTaskRunner;
import decorator.StrategyTaskRunner;

public class DelayTaskRunnerTest {

    public static void delayTaskRunnerTest(Strategy containerStrategy, MessageTask[] messageTasks) {
        StrategyTaskRunner strategyTaskRunner = new StrategyTaskRunner(containerStrategy);
        DelayTaskRunner delayTaskRunner = new DelayTaskRunner(strategyTaskRunner);
        PrinterTaskRunner printerTaskRunner = new PrinterTaskRunner(delayTaskRunner);
        printerTaskRunner.addTask(messageTasks[0]);
        printerTaskRunner.addTask(messageTasks[1]);
        printerTaskRunner.addTask(messageTasks[2]);
        printerTaskRunner.executeAll();
    }
}