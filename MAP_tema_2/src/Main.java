import factory.Strategy;
import model.BubbleSort;
import model.QuickSort;
import model.SortingTask;
import Tests.DelayTaskRunnerTest;
import Tests.MessageTaskTest;
import Tests.PrinterTaskRunnerTest;
import Tests.StrategyTaskRunnerTest;
import model.SortStrategy;
import model.MessageTask;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello!");

        System.out.println("Test task - BUBBLESORT");
        SortingTask task1 = new SortingTask("1", "Hello", new int[]{1, 4, 3, 2}, SortStrategy.BUBBLESORT);

        task1.execute();

        System.out.println("Test task - QUICKSORT");
        SortingTask task2 = new SortingTask("2", "Hello", new int[]{4, 3, 2, 1}, SortStrategy.QUICKSORT);
        task2.execute();

        int[] n1 = {4, 3, 2, 1};

        BubbleSort bubbleSort = new BubbleSort();
        bubbleSort.sort(n1);

        for (int j : n1) {
            System.out.print(j + " ");
        }
        System.out.println("\n");

        int[] n2 = {4, 3, 2, 1};

        QuickSort quickSort = new QuickSort();
        quickSort.sort(n2);

        for (int j : n2) {
            System.out.print(j + " ");
        }
        System.out.println("\n");

        System.out.println("Test task-uri\n");
        MessageTaskTest.testMessageTask();
        System.out.println("\n");


        System.out.println("Test DelayTaskRunner\n");
        System.out.println("LIFO");
        DelayTaskRunnerTest.delayTaskRunnerTest(Strategy.LIFO, MessageTaskTest.getMessageTasks());
        System.out.println("\n");
        System.out.println("FIFO");
        DelayTaskRunnerTest.delayTaskRunnerTest(Strategy.FIFO, MessageTaskTest.getMessageTasks());
        System.out.println("\n");

        System.out.println("Test StrategyTaskRunner\n");
        System.out.println("LIFO");
        StrategyTaskRunnerTest.strategyTaskRunnerTest(Strategy.LIFO, MessageTaskTest.getMessageTasks());
        System.out.println("\n");
        System.out.println("FIFO");
        StrategyTaskRunnerTest.strategyTaskRunnerTest(Strategy.FIFO, MessageTaskTest.getMessageTasks());
        System.out.println("\n");

        System.out.println("Test PrinterTaskRunner\n");
        System.out.println("LIFO");
        PrinterTaskRunnerTest.printerTaskRunnerTest(Strategy.LIFO, MessageTaskTest.getMessageTasks());
        System.out.println("\n");
        System.out.println("FIFO");
        PrinterTaskRunnerTest.printerTaskRunnerTest(Strategy.FIFO, MessageTaskTest.getMessageTasks());
        System.out.println("\n");


        MessageTask task3 = new MessageTask("1", "First Task", "Hello", "Alice", "Bob", LocalDateTime.now());
        MessageTask task4 = new MessageTask("2", "Second Task", "Hi", "Charlie", "Dave", LocalDateTime.now().plusMinutes(5));
        MessageTask task5 = new MessageTask("3", "Third Task", "Hey", "Eve", "Frank", LocalDateTime.now().plusMinutes(10));

        MessageTask[] messageTasks = {task3, task4, task5};

        Strategy chosenStrategy = Strategy.FIFO;
        StrategyTaskRunnerTest.strategyTaskRunnerTest(chosenStrategy, messageTasks);



    }

}