public class LockShow {
    public static void main(String[] args) throws InterruptedException {
        Fei f=new Fei();
        T t1=new T(f),t2=new T(f);
        Thread a=new Thread(t1);
        Thread b=new Thread(t2);
        a.start();
        b.start();
        while (!(t1.finished&&t2.finished)){
            System.out.println("waiting");
            Thread.sleep(500);
        }
        System.out.println(f.val);
    }

}
class T implements Runnable{
    Fei fei;
    boolean finished;
    public T(Fei f){
        fei=f;
    }

    @Override
    public void run() {
        fei.increase();
        finished=true;
    }
}
class Fei{
    int val=0;
    public synchronized void increase(){
        for (int i = 0; i < 10000000; i++) {
            val++;
        }
    }
}