package it.unibo.oop.workers02;

import java.util.stream.IntStream;

/**
 * Implementation of SumMatrix that use a parallel stream of Double.
 */
public class MultiThreadedSumMatrixWithDoubleStream implements SumMatrix {

    private final int nthread;

    /**
     * Constructor in order to use parallel Stream.
     * @param nthread are the required threads
     */
    public MultiThreadedSumMatrixWithDoubleStream(final int nthread) {
        this.nthread = nthread;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length / nthread + matrix.length % nthread;
        return IntStream
                .iterate(0, start -> start + size)
                .limit(nthread)
                .parallel()
                .mapToDouble(start -> {
                    double result = 0;
                    for (int i = start; i < matrix.length && i < start + size; i++) {
                        for (final double j : matrix[i]) {
                            result += j;
                        }
                    }
                    return result;
                })
                .sum();
    }
}
