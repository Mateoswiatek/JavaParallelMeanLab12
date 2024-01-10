import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class AsyncMean {
    static class MeanCalcSupplier implements Supplier<Double> {
        private final int start;
        private final int end;
        double mean = 0;

        MeanCalcSupplier(int start, int end){
            this.start = start;
            this.end=end;
        }

        @Override
        public Double get() {
            double mean = Arrays.stream(Arrays.copyOfRange(array, start, end)).average().orElse(Double.NaN);
            System.out.printf(Locale.US,"%d-%d mean=%f\n",start,end,mean);
            return mean;
        }
    }

    static double[] array;
    static void initArray(int size){
        array = new double[size];
        for(int i=0;i<size;i++){
            array[i]= Math.random()*size/(i+1);
        }
    }

    public static void asyncMeanv1(int kubelki) {
        int size = 100_000_000;
        initArray(size);
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

        try {
            // Pobierz wyniki częściowe z obiektów CompletableFuture i policz sumę
            double sum = partialResults.stream()
                    .mapToDouble(cf -> {
                        try {
                            return cf.get(); // Pobierz wynik z CompletableFuture
                        } catch (InterruptedException | ExecutionException e) {
                            return 0.0; // Obsłuż błąd lub zwróć domyślną wartość
                        }
                    })
                    .sum();

            // Oblicz średnią arytmetyczną
            double average = sum / partialResults.size();
            System.out.println("Średnia wynosi: " + average);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double mean = 0;
        for(var pr:partialResults){
            // wywołaj pr.join() aby odczytać wartość future;
            // join() zawiesza wątek wołający
        }
        System.out.printf(Locale.US,"mean=%f\n",mean);

        executor.shutdown();
    }


}

