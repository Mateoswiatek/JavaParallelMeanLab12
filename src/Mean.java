import java.util.Arrays;
import java.util.Locale;

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
            System.out.printf(Locale.US,"%d-%d mean=%f\n",start,end,mean);
        }
    }

    static double[] array;
    static void initArray(int size){
        array = new double[size];
        for(int i=0;i<size;i++){
            array[i]= Math.random()*size/(i+1);
        }
    }

    public static void main(String[] args) {
        System.out.println("siea");
        initArray(100000000);
    }

}

// take i put to funkcje blokujące,
// put czeka jesli kolejka jest plena
// take czeka jesli nic nie ma na stosie.


// offer -  nie blokuje
/*
pula watkow, executor - zarządza
ograniczenie się do pewnej ilosci wątków. te wątki dostają zadania do wykonania. gdy wykkonają

zadanie - albo jakko wątek, albo jako obietk który robi runnable. - mega. przy tym moim skanowaniu.

trzeba shoud down, aby zatrzymac

 */



