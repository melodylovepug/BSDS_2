package Core;

/**
 * Created by Yuga on 10/17/16.
 */
/*
public class TimeOutRemover  extends  Thread implements  Runnable{
    private CAServer pub;

        TimeOutRemover (CAServer pubserver)
        {
            pub = pubserver;
        }

        public  void run(){
            while(true){
                try
                {sleep(100);} catch(InterruptedException e){}

                System.out.println("In the remove progress");



                //peek, take the first elemet for exam but not reomve
                CAServer.timestamp_Id found = pub.timerqueue.peek();
                while(found != null)
                {
                     try{sleep(2000);} catch(InterruptedException e){}

                CAServer.timestamp_Id cur = found;
                long cur_time = System.currentTimeMillis();
                long cur_timestamp = cur.TimeStamp;

                Integer cur_id = cur.id;
                System.out.println("trying");

                System.out.println(cur_timestamp);
                if(cur_timestamp <= cur_time)
                {

                    Boolean result =  pub.remove_everything(found);
                    if(result) System.out.println("Succesed remove message with ID: " + cur_id);

                    try{
                        pub.timerqueue.take();
                    }catch (InterruptedException e) {}


                }

                // why break?
                    else continue;
                found = pub.timerqueue.peek();
            }

        }




    }


}


*/




