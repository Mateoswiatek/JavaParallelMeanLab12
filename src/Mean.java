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
//        initArray(100000);
//        parallelMean3(16, 10);
        test();
    }

    public static void test() {
        initArray(128000000);
        for(int cnt:new int[]{1,2,4,8,16,32,64,128}){
            parallelMean3(cnt, 50);
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
        int diff = array.length / cnt;
        int start = 0;
        for(int i = 0; i<cnt; i++){
            threads[i] =  new MeanCalc(start, (start+=diff));
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
        System.out.printf(Locale.US,"size = %d cnt=%d >  t2-t1=%f t3-t1=%f mean=%f\n",
                array.length,
                cnt,
                t2-t1,
                t3-t1,
                mean);
    }

    static void parallelMean2(int cnt) {
        MeanCalc threads[]= new MeanCalc[cnt];
        int diff = array.length / cnt;
        int start = 0;
        for(int i = 0; i<cnt; i++){
            threads[i] =  new MeanCalc(start, (start+=diff));
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
        System.out.printf(Locale.US,"size = %d cnt=%d >  t2-t1=%f t3-t1=%f mean=%f\n",
                array.length,
                cnt,
                t2-t1,
                t3-t1,
                mean);
    }

    static void parallelMean3(int cnt, int ilosc_podzialow) {

        ExecutorService executor = Executors.newFixedThreadPool(cnt);
        int start = 0;
        int diff = array.length / ilosc_podzialow;

        double t1 = System.nanoTime()/1e6;
        for(int i=0;i<ilosc_podzialow;i++){
            executor.execute(new MeanCalc(start, (start+=diff)));
        }
        executor.shutdown();

        double t2 = System.nanoTime()/1e6;

        double mean = 0;
        try {
            for(int i =0; i<ilosc_podzialow; i++){
                mean+=results.take();
            }
        } catch (InterruptedException e){
            System.out.println(e);
        }
        mean /= ilosc_podzialow;

        double t3 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"size = %d cnt=%d >  t2-t1=%f t3-t1=%f mean=%f\n",
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



