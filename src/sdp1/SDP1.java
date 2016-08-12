package sdp1;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

class D {

    Map<Integer,String> map = new HashMap<Integer,String>();
    boolean flag=false;
    void generatePassword(Map m) {
        map = m;
        for (int key : map.keySet()) {
            if(!map.get(key).equals("password revoked"))
                 map.put(key, Integer.toString(key)+"XYZ"); 
        }
        flag=true;
    }
    String getPassword(int id){
        if(!flag)
            return "not yet";
        return map.get(id);
    }
}

class B implements Runnable {

    static BlockingQueue<Integer> Q = new LinkedBlockingQueue<Integer>();
    int totalStudent;
    Thread t;
    Map<Integer, String> students = new HashMap<Integer, String>();
    D d;

    B(int x, D y) {
        totalStudent = x;
        d = y;
        students = new HashMap();
        t = new Thread(this, "B");
        t.start();
    }

    void setTotal(int x) {
        totalStudent = x;
    }

    void addToQueue(int x) {
        Q.add(x);
    }

    boolean isFull() {
        if (Q.size() >= totalStudent) {
            return true;
        }
        return false;
    }

    public void run() {
        while (Q.size() < totalStudent) {
            try {
                t.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(B.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        totalStudent=0;
        while (!Q.isEmpty()) {
            int temp = Q.poll();
            if (students.containsKey(temp)) {
                students.put(temp, "password revoked");
            } else {
                students.put(temp, "not yet");
            }
        }
        d.generatePassword(students);

    }
}

class Teacher {

    BlockingQueue<Integer> Q;
    String ID;
    boolean flag = true;
    B b;

    Teacher(BlockingQueue<Integer> q, String s, B x) {
        Q = q;
        ID = s;
        b = x;
    }
}

class A extends Teacher implements Runnable {

    Thread t;

    A(BlockingQueue<Integer> q, String s, B b) {
        super(q, s, b);
        Thread t = new Thread(this, s);
        t.start();
    }

    public void run() {
        while (true) {

            if (Q.size() > 0) {
                try {
                    b.addToQueue(Q.remove());
                } catch (Exception e) {
                }
            }

            if (b.isFull()) {
                break;
            }
            try {
                t.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(E.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class C extends Teacher implements Runnable {

    Thread t;

    C(BlockingQueue<Integer> q, String s, B b) {
        super(q, s, b);
        t = new Thread(this, s);
        t.start();
    }

    public void run() {
        while (true) {

            if (Q.size() > 0) {
                try {
                    b.addToQueue(Q.remove());
                } catch (Exception e) {
                }
            }

            if (b.isFull()) {
                break;
            }
            try {
                t.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(E.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class E extends Teacher implements Runnable {

    Thread t;

    E(BlockingQueue<Integer> q, String s, B b) {
        super(q, s, b);
        t = new Thread(this, s);
        t.start();
    }

    public void run() {
        while (true) {

            if (Q.size() > 0) {
                try {
                    b.addToQueue(Q.remove());
                } catch (Exception e) {
                }
            }

            if (b.isFull()) {
                break;
            }
            try {
                t.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(E.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Student implements Runnable {

    String ID;
    Thread t;
    BlockingQueue<Integer>[] q;
    D d;

    Student(String s, BlockingQueue<Integer>[] Q, D y) {
        ID = s;
        q = Q;
        d = y;
        t = new Thread(this, ID);
        t.start();
    }

    public void run() {
        System.out.println("Student with ID " + ID + " arrived at " + new Date());
        Random rand = new Random();
        int value = rand.nextInt(3);
        q[value].add(Integer.parseInt(ID));
        String pass;
        while(true){
            pass=d.getPassword(Integer.parseInt(ID));
            if(pass.equals("not yet"))
                try {
                    t.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            }
            else
                break;
        }
        System.out.println("Student with ID " + ID + " got " +pass+" at "+ new Date());
    }
}

class timedTask extends TimerTask {

    String IDs[];
    boolean flag = false;
    Timer t;
    BlockingQueue<Integer>[] q;
    D d;

    timedTask(String recieved[], BlockingQueue<Integer>[] Q, D y) {
        IDs = recieved;
        q = Q;
        d = y;
    }

    timedTask(Timer tm) {
        flag = true;
        t = tm;
    }

    public void run() {
        if (flag) {
            t.cancel();
            return;
        }
        for (int i = 1; i < IDs.length; i++) {
            new Student(IDs[i], q, d);
        }

    }
}

public class SDP1 {

    public static void main(String[] args) throws FileNotFoundException {
        D d = new D();
        B b = new B(100, d);
        BlockingQueue<Integer>[] Q = createTeacher(b);
        int count = createStudent(Q, d);
        b.setTotal(count);
    }

    static BlockingQueue<Integer>[] createTeacher(B d) {
        BlockingQueue<Integer>[] Q = new LinkedBlockingQueue[3];
        for (int i = 0; i < Q.length; i++) {
            Q[i] = new LinkedBlockingQueue<Integer>();
        }
        new A(Q[0], "A1", d);
        new A(Q[0], "A2", d);
        new C(Q[1], "C1", d);
        new C(Q[1], "C2", d);
        new E(Q[2], "E1", d);
        new E(Q[2], "E2", d);
        return Q;
    }

    static int createStudent(BlockingQueue<Integer>[] Q, D d) throws FileNotFoundException {
        Calendar highest = null, t;
        int i = 0, count = 0;
        Scanner S = new Scanner(new File("temp.txt"));
        Timer timer = new Timer();
        while (S.hasNextLine()) {
            String IDs[] = S.nextLine().split(" ");
            count = count + IDs.length - 1;
            String time[] = IDs[0].split(":");
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            date.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            date.set(Calendar.SECOND, Integer.parseInt(time[2]));
            if (i == 0) {
                highest = date;
                i++;
            }
            if (date.compareTo(highest) > 0) {
                highest = date;
            }
            timer.schedule(new timedTask(IDs, Q, d), date.getTime());
        }
        highest.add(Calendar.MINUTE, 1);
        timer.schedule(new timedTask(timer), highest.getTime());
        return count;
    }
}
