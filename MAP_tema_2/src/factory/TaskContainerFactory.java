package factory;

import container.*;

import factory.Strategy;

public class TaskContainerFactory implements Factory {
    public TaskContainerFactory() {
    }

    private static final TaskContainerFactory instance = new TaskContainerFactory();
//instanta unica

    public static TaskContainerFactory getInstance() {
        return instance;
    }

    @Override
    public Container createContainer(Strategy strategy) {
        switch (strategy) {
            case LIFO -> {
                return new StackContainer();
            }
            case FIFO -> {
                return new QueueContainer();
            }
        }
        return null;
    }
}
