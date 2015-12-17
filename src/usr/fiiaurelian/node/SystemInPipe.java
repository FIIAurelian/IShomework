package usr.fiiaurelian.node;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.SelectableChannel;
import java.io.InputStream;
import java.io.IOException;


public class SystemInPipe {
   
	Pipe pipe;
	CopyThread copyThread;

	public SystemInPipe (InputStream in) throws IOException {
	      pipe = Pipe.open();
	      copyThread = new CopyThread (in, pipe.sink());
	   }
	
   public SystemInPipe() throws IOException {
      this (System.in);
   }

   public void start() {
      copyThread.start();
   }

   public SelectableChannel getStdinChannel() throws IOException {
      SelectableChannel channel = pipe.source();
      channel.configureBlocking (false);
      return (channel);
   }

   protected void finalize() {
      copyThread.shutdown();
   }
   
   public static class CopyThread extends Thread {
      boolean keepRunning = true;
      byte [] bytes = new byte [128];
      ByteBuffer buffer = ByteBuffer.wrap (bytes);
      InputStream in;
      WritableByteChannel out;

      CopyThread (InputStream in, WritableByteChannel out) {
         this.in = in;
         this.out = out;
         this.setDaemon (true);
      }

      public void shutdown() {
         keepRunning = false;
         this.interrupt();
      }

      public void run() {
         try {
            while (keepRunning) {
               int count = in.read (bytes);

               if (count < 0) {
                  break;
               }

               buffer.clear().limit (count);

               out.write (buffer);
            }

            out.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}