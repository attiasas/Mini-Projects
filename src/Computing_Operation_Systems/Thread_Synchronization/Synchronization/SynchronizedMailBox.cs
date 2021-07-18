using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace ThreadSynchronization
{
    //this class extends the MailBox, by overriding the read and write methods.
    //the override must call the original implementation, which contain races, protecting the critical sections from races.
    //you cannot define a new message array or any other data structue for messages here.

    class SynchronizedMailBox : MailBox
    {
        private Mutex changeMutex;
        private Semaphore waitPoll;

        public SynchronizedMailBox() : base()
        {
            changeMutex = new Mutex();
            waitPoll = new Semaphore(0, 1000);
        }

        public override Message Read()
        {
            waitPoll.WaitOne();
            changeMutex.WaitOne();

            Message message = base.Read();

            changeMutex.ReleaseMutex();
            return message;
        }

        public override void Write(Message msg)
        {
            changeMutex.WaitOne();
            base.Write(msg);
            changeMutex.ReleaseMutex();

            waitPoll.Release();
        }
    }
}
