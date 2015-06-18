package org.junit.runners;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class FenixFrameworkRunner extends BlockJUnit4ClassRunner {

    public FenixFrameworkRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    @Atomic(mode = TxMode.WRITE)
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        // TODO Auto-generated method stub
        super.runChild(method, notifier);
    }
}
