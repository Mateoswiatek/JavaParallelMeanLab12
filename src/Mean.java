import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mean {
    static class MeanCalc extends Thread {
        private final int start;
        private final int end;
        double mean = 0;

        MeanCalc(int start, int end){
            this.start = start;
            this.end=end;
        }
        public void run(){
            mean = Arrays.stream(Arrays.copyOfRange(array, start, end)).average().orElse(Double.NaN);
//            System.out.printf(Locale.US,"%d-%d mean=%f\n",start,end,mean);
            try {
                results.put(mean);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    static double[] array;
    static BlockingQueue<Double> results = new ArrayBlockingQueue<>(100);

    static void initArray(int size){
        array = new double[size];
        for(int i=0;i<size;i++){
            array[i]= Math.random()*size/(i+1);
        }
    }

    public static void main(String[] args) {
        System.out.println("siea");
//        initArray(100);
        initArray(10000);
        parallelMean1(8);
        parallelMean2(8);
        parallelMean3(8);
//        test();
    }

    public static void test() {
        initArray(128000000);
        for(int cnt:new int[]{1,2,4,8,16,32,64,128}){
            parallelMean3(cnt);
        }
    }

    /**
     * Oblicza średnią wartości elementów tablicy array uruchamiając równolegle działające wątki.
     * Wypisuje czasy operacji
     * @param cnt - liczba wątków
     */
    static void parallelMean1(int cnt) {
        // utwórz tablicę wątków
        MeanCalc threads[]= new MeanCalc[cnt];
        // utwórz wątki, podziel tablice na równe bloki i przekaż indeksy do wątków
        // załóż, że array.length dzieli się przez cnt)
        int elementow =  array.length / cnt;
        int thr = 0;
        for(int i = 0; i<array.length; i+=elementow, thr++){
            int koniec = Math.min(i + elementow, array.length);
            threads[thr] =  new MeanCalc(i, koniec);
        }

        double t1 = System.nanoTime()/1e6;
        for(MeanCalc mc : threads){
            mc.start();
        }
        double t2 = System.nanoTime()/1e6;

        // czekaj na ich zakończenie używając metody ''join''
        try {
            for (MeanCalc mc : threads) {
                mc.join();
            }
        } catch (InterruptedException e){
            System.out.println(e);
        }

        // oblicz średnią ze średnich
        double mean = 0;
        for(MeanCalc mc:threads) {
            mean+=mc.mean;
        }
        mean /= cnt;
        double t3 = System.nanoTime()/1e6;
        System.out.printf(Locale.US," 1 size = %d cnt=%d >  t2-t1=%f t3-t1=%f mean=%f\n",
                array.length,
                cnt,
                t2-t1,
                t3-t1,
                mean);
    }

    static void parallelMean2(int cnt) {

        //TODO zmienic sposb podzialu na taki jak w 1 i 3
        MeanCalc threads[]= new MeanCalc[cnt];

        int elementow =  array.length / cnt;
        int thr = 0;
        for(int i = 0; i<array.length; i+=elementow, thr++){
            int koniec = Math.min(i + elementow, array.length);
            threads[thr] =  new MeanCalc(i, koniec);
        }
        double t1 = System.nanoTime()/1e6;
        for(MeanCalc mc : threads){
            mc.start();
        }
        double t2 = System.nanoTime()/1e6;

        double mean = 0;
        try {
            for(int i =0; i<cnt; i++){
                mean+=results.take();
            }
        } catch (InterruptedException e){
            System.out.println(e);
        }
        mean /= cnt;

        double t3 = System.nanoTime()/1e6;
        System.out.printf(Locale.US," 2 size = %d cnt=%d >  t2-t1=%f t3-t1=%f mean=%f\n",
                array.length,
                cnt,
                t2-t1,
                t3-t1,
                mean);
    }

    static void parallelMean3(int cnt) {

        ExecutorService executor = Executors.newFixedThreadPool(cnt);

        int elementow =  array.length / cnt;
        int thr = 0;
        double t1 = System.nanoTime()/1e6;
        for(int i = 0; i<array.length; i+=elementow, thr++){
            int koniec = Math.min(i + elementow, array.length);
            executor.execute(new MeanCalc(i, koniec));
        }
        executor.shutdown();

        double t2 = System.nanoTime()/1e6;

        double mean = 0;
        try {
            for(int i =0; i<cnt; i++){
                mean+=results.take();
            }
        } catch (InterruptedException e){
            System.out.println(e);
        }
        mean /= cnt;

        double t3 = System.nanoTime()/1e6;
        System.out.printf(Locale.US," 3 size = %d cnt=%d >  t2-t1=%f t3-t1=%f mean=%f\n",
                array.length,
                cnt,
                t2-t1,
                t3-t1,
                mean);
    }

}

// take i put to funkcje blokujące,
// put czeka jesli kolejka jest plena
// take czeka jesli nic nie ma na stosie.

/*
pula watkow, executor - zarządza
ograniczenie się do pewnej ilosci wątków. te wątki dostają zadania do wykonania. gdy wykkonają
zadanie - albo jakko wątek, albo jako obietk który robi runnable. - mega. przy tym moim skanowaniu.
kolejka do wynikow, tak mozna tez zrobic ze skanowaniem protow
 */



