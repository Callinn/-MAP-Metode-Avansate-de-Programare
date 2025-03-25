package factory;
import container.Container;
import factory.Strategy;

public interface Factory {
    Container createContainer(Strategy strategy);
}