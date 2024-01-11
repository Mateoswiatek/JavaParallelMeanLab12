import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class AsyncMean {
    static double[] array;
    static class MeanCalcSupplier implements Supplier<Double> {
        private final int start;
        private final int end;

        MeanCalcSupplier(int start, int end){
            this.start = start;
            this.end=end;
        }

        @Override
        public Double get() {
            return Arrays.stream(Arrays.copyOfRange(array, start, end)).average().orElse(Double.NaN);
//            double mean = ...
//            System.out.printf(Locale.US,"%d-%d mean=%f\n",start,end,mean);
//            reutr mean
        }
    }

    public static void main(String[] args) {
        int size = 100_000_000;
        initArray(size);

        asyncMeanv1(1000);
        asyncMeanv2(1000);
    }


    static void initArray(int size){
        array = new double[size];
        for(int i=0;i<size;i++){
            array[i]= Math.random()*size/(i+1);
        }
    }

    public static void asyncMeanv1(int kubelki) {
//        int size = 100_000_000;
//        initArray(size);
        ExecutorService executor = Executors.newFixedThreadPool(16);

        int diff = array.length / kubelki;
        int start = 0;

        // Utwórz listę future
        List<CompletableFuture<Double>> partialResults = new ArrayList<>();
        for(int i=0;i<kubelki;i++){
            CompletableFuture<Double> partialMean = CompletableFuture.supplyAsync(
                    new MeanCalcSupplier(start, (start+=diff)),executor);
            partialResults.add(partialMean);
        }

        // zagreguj wyniki
        double avg = partialResults.stream().mapToDouble(cf -> {
            try {
                return cf.get();
            } catch (InterruptedException | ExecutionException e){
                return 0.0;
            }
        }).average().orElse(Double.NaN);

        for(var pr:partialResults){
            pr.join();
            // wywołaj pr.join() aby odczytać wartość future;
            // join() zawiesza wątek wołający
        }
        System.out.printf(Locale.US,"mean1=%f%n",avg);

        executor.shutdown();
    }

    static void asyncMeanv2(int kubelki) {
//        int size = 100_000_000;
//        initArray(size);
        ExecutorService executor = Executors.newFixedThreadPool(16);

        int diff = array.length / kubelki;
        int start = 0;


        BlockingQueue<Double> queue = new ArrayBlockingQueue<>(kubelki);

        for (int i = 0; i < kubelki; i++) {
            CompletableFuture.supplyAsync(
                    new MeanCalcSupplier(start, (start+=diff)),executor)
            .thenApply(queue::offer); // dodajemy
        }

        double mean=0;
        int x = kubelki;
        double sum =0;
        while(x-- > 0){
            try {
                sum+=queue.take();
            } catch (InterruptedException e){
                System.out.println("Exception: " + e);
            }
        }
        mean = sum / kubelki;

        System.out.printf(Locale.US,"mean2=%f%n", mean);

        executor.shutdown();
    }

}

