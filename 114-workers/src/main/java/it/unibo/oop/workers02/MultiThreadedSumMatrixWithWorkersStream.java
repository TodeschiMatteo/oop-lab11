package it.unibo.oop.workers02;

import java.util.stream.IntStream;

/**
 * Implementation of SumMatrix that use a parallel stream of Workers.
 */
public class MultiThreadedSumMatrixWithWorkersStream implements SumMatrix {

    private final int nthread;

    /**
     * Constructor in order to use stream of workers.
     * @param nthread are the required threads
     */
    public MultiThreadedSumMatrixWithWorkersStream(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double result;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        @SuppressWarnings("PMD.ArrayIsStoredDirectly")
        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            double result = 0;
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (final double j : matrix[i]) {
                    result += j;
                }
            }
            this.result = result;
        }

        /**
         * Returns the result of summing up the doubles within the matrix.
         * 
         * @return the sum of every element in the matrix
         */
        public double getResult() {
            return this.result;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        /*
         * Build a stream of workers
         */
        return IntStream
                .iterate(0, start -> start + size)
                .limit(nthread)
                .mapToObj(start -> new Worker(matrix, start, size))
                .peek(Thread::start)
                .peek(MultiThreadedSumMatrixWithWorkersStream::joinUninterruptibly)
                .mapToDouble(Worker::getResult)
                .sum();
    }

    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
